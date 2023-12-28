package jp.metacube.fhir.pddicds.server.crinicalReasoning;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.hl7.fhir.r4.model.Attachment;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.Library;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.PlanDefinition;
import ca.uhn.fhir.parser.IParser;
import jp.metacube.fhir.pddicds.server.common.FhirParser;
import jp.metacube.fhir.pddicds.server.common.PddiCdsException;
import jp.metacube.fhir.pddicds.server.common.ReadWriteFile;
// import jp.metacube.fhir.pddicds.server.common.ReadFHIRResource;
import jp.metacube.fhir.pddicds.server.config.PddiCdsProperties;
import jp.metacube.fhir.pddicds.server.preparation.CqlPreprocessing;

/**
 * CDSHookRequestに含まれるFHIRリソースをもとにCQL解析を実行する
 *
 * @author takaha
 */
public class CqlAnalysis {
  private ReadWriteFile rfr = null;
  private CqlPreprocessing cpp = null;
  /** コンストラクタ */
  public CqlAnalysis(CqlPreprocessing cpp) {
    rfr = new ReadWriteFile();
    this.cpp = cpp;
    //    // Velocity初期設定
    //    Properties p = new Properties();
    //    p.setProperty(Velocity.RESOURCE_LOADER, "file");
    //    // String base=ap.getProperty("registry.home")+"/xdstools/storedquery/templates";
    //    URL vmurl = this.getClass().getClassLoader().getResource("resources/js/");
    //    String base = vmurl.getPath();
    //    p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, base);
    //    Velocity.init(p);
  }

  /**
   * @param cpp
   * @return
   * @throws PddiCdsException
   */
  public String exec() throws PddiCdsException {
    String cqlResult = null;

    // ストレージからPlanDefinitionを取得

    try {
      cpp.planDefinition = (PlanDefinition) rfr.getResource("PlanDefinition", cpp.id);
    } catch (FileNotFoundException e2) {
      e2.printStackTrace();
      throw new PddiCdsException(
          500,
          IssueSeverity.FATAL,
          IssueType.NULL,
          "ストレージからPlanDefinition取得時の例外: " + e2.getMessage());
    }

    // PlanDefinitionからLibrary取得
    List<CanonicalType> pdlibs = cpp.planDefinition.getLibrary();
    if (pdlibs == null || pdlibs.isEmpty() || pdlibs.size() == 0) {
      throw new PddiCdsException(
          500,
          IssueSeverity.FATAL,
          IssueType.NULL,
          "PlanDefinition.libraryに要素が存在しません: id = " + cpp.id);
    }
    // もし複数ある場合は最初のものを選ぶ
    CanonicalType pdlib = pdlibs.get(0);
    String libRef = pdlib.asStringValue();

    // ストレージからLibraryを取得
    Library lib;
    try {
      String storage = PddiCdsProperties.getInstance().getStorageFolderPath();
      String librsc = storage + "/" + libRef;
      lib = (Library) rfr.getResource(librsc);
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
      throw new PddiCdsException(
          500,
          IssueSeverity.FATAL,
          IssueType.EXCEPTION,
          "ストレージからLibrary取得時に発生した例外: " + e1.getMessage());
    }

    // CQL解析に使うELMを取得
    List<Attachment> cnts = lib.getContent();
    Attachment content = null;
    if (cnts != null && cnts.size() > 0) {
      for (int i = 0; i < cnts.size(); i++) {
        Attachment cnt = cnts.get(i);
        String ct = cnt.getContentType();
        if ("application/elm+json".equals(ct)) {
          content = cnt;
        }
      }
    }
    if (content == null) {
      throw new PddiCdsException(
          500,
          IssueSeverity.FATAL,
          IssueType.NULL,
          "contentType = application/elm+json であるLibrary.contentが存在しません.");
    }
    String uri = content.getUrl();
    // InputStream is = this.getClass().getClassLoader().getResourceAsStream(uri);
    String elm = null;
    try {
      String storage = PddiCdsProperties.getInstance().getStorageFolderPath();
      String fp = storage + "/" + uri;
      FileInputStream is = new FileInputStream(fp);
      elm = rfr.readFile(is, null);
    } catch (Exception e) {
      e.printStackTrace();
      throw new PddiCdsException(
          500,
          IssueSeverity.FATAL,
          IssueType.EXCEPTION,
          "ELM読み込み中に例外発生: uri = " + uri + ", exception = " + e.getMessage());
    }
    if (StringUtils.isEmpty(elm)) {
      throw new PddiCdsException(500, IssueSeverity.FATAL, IssueType.NULL, "ELMがありません: " + uri);
    }

    // 実行用JavaScriptテンプレート読み込み
    // テンプレートに入力Bundle、ELMなどを書き入れる
    String[] workfile = this.createJavaScript(cpp.bundle, elm);

    // JavaScript実行し、CQL解析実行
    cqlResult = this.execCqlAnalysis(workfile[2]);

    return cqlResult;
  }

  private String[] createJavaScript(Bundle bundle, String elm) throws PddiCdsException {
    String[] workfile = new String[3];

    // JavaScriptテンプレート読み込み
    // URL vmurl = this.getClass().getClassLoader().getResource("resources/js/cql-exec.js.vm");
    // String vmpath = vmurl.getPath();
    Template jsTemp = Velocity.getTemplate("cql-exec.js.vm", "UTF8");

    // テンプレートに埋め込むデータを設定する
    PddiCdsProperties pcp = PddiCdsProperties.getInstance();
    VelocityContext vc = new VelocityContext();
    vc.put("cqlExecFhir", pcp.getCqlExecFhirPath());
    vc.put("cqlExecution", pcp.getCqlExecutionPath());
    vc.put("fhirSupportTools", pcp.getFhirSupportToolsPath());
    vc.put("fhirHelpersJson", pcp.getFhirSupportToolsPath() + "/json");

    // elmを作業フォルダへファイル化して格納
    String fuuid = cpp.uuid;
    String elmName = "elm-" + fuuid + ".json";
    // String workpath = this.getClass().getClassLoader().getResource("works/").getPath();
    String workpath = pcp.getWorkFolderPath();
    String elmpath = workpath + "/" + elmName;
    workfile[0] = rfr.writeFile(elmpath, elm);
    workfile[0] = workfile[0].replaceAll("\\\\", "/");
    // String elmss = this.adjustSimpleJson(elm);
    vc.put("elmJsonFile", workfile[0]);

    // 入力リソースをまとめたbundleを作業フォルダへファイル化して格納
    IParser jp = FhirParser.getInstance().getJSONParser();
    String bundleStr = jp.encodeResourceToString(bundle);
    String bdName = "cql-bundle-" + fuuid + ".json";
    String bdpath = workpath + "/" + bdName;
    workfile[1] = rfr.writeFile(bdpath, bundleStr);
    workfile[1] = workfile[1].replaceAll("\\\\", "/");
    vc.put("inputBundleFile", workfile[1]);

    // テンプレートにデータを埋め込む
    StringWriter sw = new StringWriter();
    jsTemp.merge(vc, sw);
    // Velocity.mergeTemplate(jsTemp,"Shift_JIS",vc,sw);
    sw.flush();

    // 埋め込んだ結果作成されたJavascript
    String createjs = sw.toString();

    // 一時的なJavaScriptファイルを作成
    // ファイル名は新たに作成したUUIDを基に作成
    String jsName = "cqlexec-" + fuuid + ".js";
    String jspath = workpath + "/" + jsName;
    workfile[2] = rfr.writeFile(jspath, createjs);
    workfile[2] = workfile[2].replaceAll("\\\\", "/");

    //
    //
    //    File wf = null;
    //    try {
    //      wf = new File(workpath + "/" + jsName);
    //      FileWriter fw = new FileWriter(wf);
    //      fw.write(createjs);
    //      fw.close();
    //    } catch (IOException e) {
    //      e.printStackTrace();
    //      throw new PddiCdsException(500, IssueSeverity.FATAL, IssueType.EXCEPTION, e);
    //    }
    //
    return workfile;
  }

  private String execCqlAnalysis(String jsPath) throws PddiCdsException {
    String result = null;

    try {
      String nodejsExecPath = PddiCdsProperties.getInstance().getNodejsExecPath();
      ProcessBuilder pb = new ProcessBuilder(nodejsExecPath, jsPath);
      // pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
      // pb.redirectError(ProcessBuilder.Redirect.INHERIT);
      Process process = pb.start();
      InputStreamThread ist = new InputStreamThread(process.getInputStream());
      InputStreamThread iet = new InputStreamThread(process.getErrorStream());
      ist.start();
      iet.start();
      process.waitFor();
      ist.join();
      iet.join();

      // InputStreamThread実行時に発生した例外を処理
      List<Exception> el1 = ist.getExceptionList();
      if (el1 != null && el1.size() > 0) {
        String elstr = "";
        for (int i = 0; i < el1.size(); i++) {
          Exception err = el1.get(i);
          elstr += err.getLocalizedMessage();
        }
        throw new PddiCdsException(
            500, IssueSeverity.FATAL, IssueType.EXCEPTION, "CQL解析実行中に例外発生 :" + elstr);
      }

      List<Exception> el2 = iet.getExceptionList();
      if (el2 != null && el2.size() > 0) {
        String elstr = "";
        for (int i = 0; i < el2.size(); i++) {
          Exception err = el2.get(i);
          elstr += err.getLocalizedMessage();
        }
        throw new PddiCdsException(
            500, IssueSeverity.FATAL, IssueType.EXCEPTION, "CQL解析実行中に例外発生: " + elstr);
      }

      // 標準出力は文字列リストとなっているので、これをつないで一つの文字列にする
      result = this.changeStringList2String(ist.getStringList());
      String error = this.changeStringList2String(iet.getStringList());
      // System.out.println("< ======= >");
      // System.out.println("=== > " + result);
      // System.out.println("--- > " + error + "===/");

      // CQL解析結果を作業フォルダへファイル化して格納（結果確認のため）
      String fuuid = cpp.uuid;
      String crName = "cql-result-" + fuuid + ".json";
      PddiCdsProperties pcp = PddiCdsProperties.getInstance();
      String workpath = pcp.getWorkFolderPath();
      String crpath = workpath + "/" + crName;
      rfr.writeFile(crpath, result);

      if ((result == null || "".equals(result.trim()))
          && (error != null && !"".equals(error.trim()))) {

        // エラー出力
        throw new PddiCdsException(
            500, IssueSeverity.FATAL, IssueType.EXCEPTION, "CQL解析実行中に例外発生: " + error);
      }

    } catch (InterruptedException | IOException e) {
      e.printStackTrace();
      throw new PddiCdsException(
          500, IssueSeverity.FATAL, IssueType.EXCEPTION, "CQL解析実行中に例外発生: " + e.getMessage());
    }

    return result;
  }

  private String changeStringList2String(List<String> list) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < list.size(); i++) {
      String elem = list.get(i).trim();
      sb.append(elem);
    }
    return sb.toString();
  }

  //  private PddiCdsException makePddiCdsException(List<Exception> exceps) {
  //    if (exceps.size() > 0) {
  //      Exception excep = exceps.get(0);
  //      return new PddiCdsException(500, IssueSeverity.FATAL, IssueType.EXCEPTION, excep);
  //    }
  //    return null;
  //  }

  // PrettyStringスタイルのJSON文字列をSimpleStringスタイルのJSON文字列に変換する
  //  private String adjustSimpleJson(String json) throws PddiCdsException {
  //
  //    ObjectMapper mapper = ParameterInitiator.getInstance().objectMapper;
  //    JsonNode jobj = null;
  //    try {
  //      jobj = mapper.readTree(json);
  //    } catch (JsonProcessingException e) {
  //      e.printStackTrace();
  //      throw new PddiCdsException(500, IssueSeverity.FATAL, IssueType.EXCEPTION, e);
  //    }
  //
  //    return jobj.toString();
  //  }

  private class InputStreamThread extends Thread {
    private InputStream in;
    private List<String> list = new ArrayList<String>();
    private List<Exception> exceps = new ArrayList<Exception>();

    public InputStreamThread(InputStream in) {
      this.in = in;
    }

    @Override
    public void run() {
      BufferedReader br = null;
      InputStreamReader isr = null;
      try {
        isr = new InputStreamReader(in, "UTF8");
        br = new BufferedReader(isr);
        String line = null;
        while ((line = br.readLine()) != null) {
          // ログなど出力する
          // System.out.println(type + ">" + line);
          list.add(line);
        }
      } catch (IOException e) {
        e.printStackTrace();
        exceps.add(e);
      } finally {
        try {
          if (br != null) {
            br.close();
          }
          if (isr != null) {
            isr.close();
          }
        } catch (IOException e) {
          e.printStackTrace();
          exceps.add(e);
        }
      }
    }

    public List<String> getStringList() {
      return list;
    }

    public List<Exception> getExceptionList() {
      return exceps;
    }
  }
}
