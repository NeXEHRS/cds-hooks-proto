package jp.metacube.fhir.pddicds.server.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import ca.uhn.fhir.parser.IParser;
import jp.metacube.fhir.pddicds.server.config.PddiCdsProperties;

/**
 * ストレージに格納されたFHIRリソースを取得する
 *
 * @author takaha
 */
public class ReadWriteFile {

  /** コンストラクタ */
  public ReadWriteFile() {}

  /**
   * リソースタイプとIDから、内部ストレージにあるFHIRリソースファイルを読み込む
   *
   * @param resourceType リソースタイプ
   * @param id リソースID
   * @return 引数に対応するFHIRリソース。存在しない場合はnullを返す
   * @throws FileNotFoundException
   */
  public IBaseResource getResource(String resourceType, String id) throws FileNotFoundException {
    // 内部ストレージからFHIRリソースファイル(JSON)を取得する
    String storageFolder = PddiCdsProperties.getInstance().getStorageFolderPath();
    String filename = storageFolder + "/" + resourceType + "-" + id + ".json";
    return this.getResource(filename);
  }

  /**
   * 内部ストレージにあるFHIRリソースを取得する
   *
   * @param filename ファイル名(絶対パス)
   * @return 指定したファイルに格納されているFHIRリソース
   * @throws FileNotFoundException
   */
  public IBaseResource getResource(String filename) throws FileNotFoundException {
    IBaseResource rsc = null;
    // File file = new File(filename);
    FileInputStream is = new FileInputStream(filename);
    if (is != null) {
      // Fileの内容をStringとして取り出す
      IParser parser = FhirParser.getInstance().getJSONParser();
      rsc = parser.parseResource(is);
    }

    return rsc;
  }

  public String readFile(String fpath) throws Exception {
    File frsc = new File(fpath);
    String fstr = this.readFile(frsc);
    return fstr;
  }

  public String readFile(File frsc) throws Exception {
    // ファイル読み込み可否
    if (!frsc.canRead()) {
      System.out.println("This file cannot read.");
      return null;
    }
    InputStream is = new FileInputStream(frsc);

    // ファイルで使われる文字コード
    FileCharDetecter fcd = new FileCharDetecter();
    String fstr = fcd.detectorFile(frsc.getAbsolutePath());
    String str = this.readFile(is, fstr);

    return str;
  }

  public String readFile(InputStream is, String ftype) throws Exception {
    String fstr = null;
    // ファイル読み込み可否
    if (is != null) {

      // InputStream -> String
      InputStreamReader isr = null;
      BufferedReader br = null;
      try {
        if (ftype != null) {
          isr = new InputStreamReader(is, ftype);
        } else {
          isr = new InputStreamReader(is);
        }
        br = new BufferedReader(isr);
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
          sb.append(line).append(System.getProperty("line.separator"));
        }
        fstr = sb.toString();
        br.close();
      } finally {
        if (br != null) {
          br.close();
        }
        if (isr != null) {
          isr.close();
        }
      }
    }
    return fstr;
  }

  /**
   * 文字列をファイル書き込み
   *
   * @param fpath 文字列の書き込み先となるファイルパス
   * @param data 書き込む文字列
   * @throws PddiCdsException
   */
  public String writeFile(String fpath, String data) throws PddiCdsException {
    File wf = null;
    try {
      wf = new File(fpath);
      FileWriter fw = new FileWriter(wf);
      fw.write(data);
      fw.close();
    } catch (IOException e) {
      e.printStackTrace();
      throw new PddiCdsException(
          500,
          IssueSeverity.FATAL,
          IssueType.EXCEPTION,
          "ファイル書き込み作業中に例外発生: file = " + fpath + ", exception = " + e.getMessage());
    }

    return wf.getAbsolutePath();
  }
}
