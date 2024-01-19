/** */
package jp.metacube.fhir.pddicds.server.preparation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.hl7.fhir.instance.model.api.IBaseResource;
// import org.hl7.fhir.r4.formats.IParser;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.PlanDefinition;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ca.uhn.fhir.parser.IParser;
import jp.metacube.fhir.pddicds.server.common.FhirParser;
import jp.metacube.fhir.pddicds.server.common.ParameterInitiator;
import jp.metacube.fhir.pddicds.server.common.PddiCdsException;
import jp.metacube.fhir.pddicds.server.config.PddiCdsProperties;

/**
 * CQL解析実行準備
 *
 * @author takaha
 */
public class CqlPreprocessing {
  // リクエストURLのID部
  public String id = null;
  // ユーザID
  public String userId = null;
  // 対象となる患者ID
  public String patientId = null;
  // CDSHookRequest.hookInstance：
  public String hookInstance;
  // CDSHookRequest.hook HookRequestの種類
  public String hook;
  // リクエストURL
  public String requestUrl;
  // リクエスト受付時刻
  public Date requestDate;
  // CDSHookRequestに含まれるFHIRリソースをまとめるBundleリソース
  public Bundle bundle = null;
  // PlanDefinition: CQL解析以降で使用
  public PlanDefinition planDefinition = null;
  // 本リクエスト処理で使用するUUID
  public String uuid = UUID.randomUUID().toString();
  // ローカルにて使用
  private JsonNode creq = null;
  // private String creqStr = null;

  /** コンストラクタ */
  public CqlPreprocessing() {}

  /**
   * コンストラクタ
   *
   * @throws PddiCdsException
   */
  public CqlPreprocessing(String id, String requestBody, HttpServletRequest hsr)
      throws PddiCdsException {
    // CQL解析の前処理を実行する
    this.execute(id, requestBody, hsr);
  }

  public void execute(String id, String requestBody, HttpServletRequest hsr)
      throws PddiCdsException {
    // id
    this.id = id;

    // requestURL
    this.requestUrl = hsr.getRequestURL().toString();

    // requestDate
    Instant nowInst = Instant.now();
    this.requestDate = Date.from(nowInst);

    // requestBody
    // JSON String -> JSONObject
    ObjectMapper mapper = ParameterInitiator.getInstance().objectMapper;
    try {
      creq = mapper.readTree(requestBody);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      throw new PddiCdsException(
          400,
          IssueSeverity.FATAL,
          IssueType.EXCEPTION,
          "CDSHookRequest読み込み処理中の例外: " + e.getMessage());
    }

    // hook, hookInstance
    JsonNode rhook = creq.get("hook");
    if (rhook != null) {
      this.hook = rhook.asText();
    } else {
      throw new PddiCdsException(
          400, IssueSeverity.ERROR, IssueType.REQUIRED, "CDSHookRequestにhookが必要です");
    }

    JsonNode hi = creq.get("hookInstance");
    if (hi != null) {
      this.hookInstance = hi.asText();
    } else {
      throw new PddiCdsException(
          400, IssueSeverity.ERROR, IssueType.REQUIRED, "CDSHookRequestにhookInstanceが必要です");
    }

    // context
    JsonNode context = creq.get("context");
    if (context == null) {
      throw new PddiCdsException(
          400, IssueSeverity.ERROR, IssueType.REQUIRED, "CDSHookRequestにcontextが必要です");
    }

    // context.userId
    JsonNode uid = context.get("userId");
    if (uid != null) {
      this.userId = uid.asText();
    } else {
      throw new PddiCdsException(
          400, IssueSeverity.ERROR, IssueType.REQUIRED, "CDSHookRequestにcontext.userIdが必要です");
    }

    // context.patientId
    JsonNode pid = context.get("patientId");
    if (pid != null) {
      this.patientId = pid.asText();
    } else {
      throw new PddiCdsException(
          400, IssueSeverity.ERROR, IssueType.REQUIRED, "CDSHookRequestにcontext.patientIdが必要です");
    }

    // context.draftOrders
    JsonNode draftOrders = context.get("draftOrders");
    if (draftOrders == null) {
      throw new PddiCdsException(
          400, IssueSeverity.ERROR, IssueType.REQUIRED, "CDSHookRequestにcontext.draftOrdersが必要です");
    }

    // System.out.println(draftOrders.toString());
    // JsonNode -> IBaseResource
    IParser fp = FhirParser.getInstance().getJSONParser();
    ArrayList<BundleEntryComponent> bundleEntries = new ArrayList<BundleEntryComponent>();
    try {
      IBaseResource rsc = fp.parseResource(draftOrders.toString());
      if (rsc instanceof Bundle) {
        List<BundleEntryComponent> entries = ((Bundle) rsc).getEntry();
        // Bundle.entryが0件の場合、エラーを出す
        if (entries.size() == 0) {
          throw new PddiCdsException(
              400,
              IssueSeverity.FATAL,
              IssueType.INVALID,
              "CDSHookRequest.context.draftOrders に FHIR Bundle Resourceが含まれていますが、そのBundle.entryはFHIRリソースがありません.");
        }
        // context.draftOrders以下のBundleリソースに含まれていたことを示す
        // 記号をResource.extensionに追加する
        for (int i = 0; i < entries.size(); i++) {
          BundleEntryComponent entry = entries.get(i);
          if (entry != null) {
            DomainResource ersc = (DomainResource) entry.getResource();
            Extension eext = ersc.addExtension();
            eext.setUrl(PddiCdsProperties.getInstance().getSystemUUID());
            eext.setValue(new StringType("contextPrescriptions"));

            bundleEntries.add(entry);
          }
        }
        // bundleEntries.addAll(entries);
      } else {
        throw new PddiCdsException(
            400,
            IssueSeverity.FATAL,
            IssueType.INVALID,
            "CDSHookRequest.context.draftOrders は FHIR Bundle Resourceである必要があります.");
      }
    } catch (Exception e) {
      if (e instanceof PddiCdsException) {
        throw e;
      } else {
        e.printStackTrace();
        throw new PddiCdsException(
            500,
            IssueSeverity.FATAL,
            IssueType.INVALID,
            "CDSHookRequest.context.draftOrders処理中の例外: " + e.getMessage());
      }
    }

    // prefetch
    JsonNode pfs = creq.get("prefetch");
    if (pfs != null) {
      Iterator<String> pfnames = pfs.fieldNames();
      while (pfnames.hasNext()) {
        String pfname = pfnames.next();
        System.out.println(pfname);
        JsonNode pfvalue = pfs.get(pfname);
        try {
          IBaseResource pfrsc = fp.parseResource(pfvalue.toString());
          if (pfrsc instanceof Bundle) {
            List<BundleEntryComponent> entries = ((Bundle) pfrsc).getEntry();
            for (int i = 0; i < entries.size(); i++) {
              BundleEntryComponent entry = entries.get(i);
              Resource ersc = entry.getResource();
              if (ersc instanceof Bundle) {
                // Bundle.entryがBundleである場合、
                ArrayList<BundleEntryComponent> childs = this.getBundleEntries((Bundle) ersc);
                bundleEntries.addAll(childs);
              } else {
                bundleEntries.add(entry);
              }
            }
          } else {
            throw new PddiCdsException(
                400,
                IssueSeverity.FATAL,
                IssueType.EXCEPTION,
                "CDSHookRequest.prefetchはそれぞれ FHIR Bundle resourceである必要があります");
          }
        } catch (Exception e) {
          if (e instanceof PddiCdsException) {
            throw e;
          } else {
            e.printStackTrace();
            throw new PddiCdsException(
                500,
                IssueSeverity.FATAL,
                IssueType.EXCEPTION,
                "CDSHookRequest.prefetch処理中の例外: " + e.getMessage());
          }
        }
      }
    }
    // hookリクエストに含まれたFHIRリソースをBundleにまとめる
    this.bundle = new Bundle();
    this.bundle.setType(BundleType.COLLECTION);
    this.bundle.setEntry(bundleEntries);

    // 確認用
    // String bstr = fp.encodeResourceToString(bundle);
    // System.out.println(bstr);
  }

  private ArrayList<BundleEntryComponent> getBundleEntries(Bundle bundle) {
    ArrayList<BundleEntryComponent> becs = new ArrayList<BundleEntryComponent>();

    List<BundleEntryComponent> entries = bundle.getEntry();
    for (int i = 0; i < entries.size(); i++) {
      BundleEntryComponent entry = entries.get(i);
      Resource ersc = entry.getResource();
      if (ersc instanceof Bundle) {
        ArrayList<BundleEntryComponent> childBecs = this.getBundleEntries((Bundle) ersc);
        becs.addAll(childBecs);
      } else {
        becs.add(entry);
      }
    }

    return becs;
  }
}
