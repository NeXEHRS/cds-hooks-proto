package jp.metacube.fhir.pddicds.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.OperationOutcome.OperationOutcomeIssueComponent;
import ca.uhn.fhir.parser.IParser;
import io.swagger.model.Card;
import jp.metacube.fhir.pddicds.server.cards.PddiCdsCard;
import jp.metacube.fhir.pddicds.server.cards.PddiCdsResponse;
import jp.metacube.fhir.pddicds.server.carePlan.PddiCdsCarePlan;
import jp.metacube.fhir.pddicds.server.common.FhirParser;
import jp.metacube.fhir.pddicds.server.common.PddiCdsException;
import jp.metacube.fhir.pddicds.server.crinicalReasoning.CqlAnalysis;
import jp.metacube.fhir.pddicds.server.preparation.CqlPreprocessing;

/** @author takaha */
public class ServiceDriver {
  Logger lm = LogManager.getLogger();

  /** コンストラクタ */
  public ServiceDriver() {}

  /**
   * PDDI-CDSサービス実行本体
   *
   * @param id
   * @param requestBody
   * @param request
   * @return
   */
  public Response driver(String id, String requestBody, HttpServletRequest request) {

    PddiCdsResponse pcr = new PddiCdsResponse();
    CqlPreprocessing cpp = null;
    Response response = null;

    try {
      /*
       * CQL解析実行準備
       */
      // cpp = new CqlPreprocessing(id, RequestBody, request);
      // lm.info(">>> CDSHooksRequest\n" + requestBody);
      cpp = new CqlPreprocessing();
      lm.info(">>> CDSHooksRequest (" + cpp.uuid + ")\n" + requestBody);
      cpp.execute(id, requestBody, request);

      IParser jp = FhirParser.getInstance().getJSONParser();
      String bstr = jp.encodeResourceToString(cpp.bundle);
      // System.out.println(">>>> " + bstr);
      lm.info(">>> CQL解析用入力データ (" + cpp.uuid + ")\n" + bstr);

      /*
       * CQL解析実行
       */
      // cql結果（JSON）
      // 実行中取り寄せるPlanDefinitionはcppにセットされる
      CqlAnalysis ca = new CqlAnalysis(cpp);
      String cqlResults = ca.exec();

      // System.out.println("===> " + cqlResults);
      lm.info(">>> CQL解析結果 (" + cpp.uuid + ")\n" + cqlResults);

      /*
       * CQL解析結果とPlanDefinitionからCarePlanとRequestGroupを作成する
       */
      // ReadFHIRResource rfr = new ReadFHIRResource();
      // PlanDefinition pd =
      //    (PlanDefinition) rfr.fromFile("storage/PlanDefinition-warfarin-nsaids-cds-sign.json");

      PddiCdsCarePlan pccp = new PddiCdsCarePlan(cpp, cqlResults);
      Bundle cpBundle = pccp.create();

      // IParser jp = FhirParser.getInstance().getJSONParser();
      String cpbstr = jp.encodeResourceToString(cpBundle);
      // System.out.println(">>>> " + cpbstr);
      lm.info(">>> CarePlan,RequestGroup Create (" + cpp.uuid + ")\n" + cpbstr);

      /*
       * CarePlanとRequestGroupからCardsを作成する
       */
      PddiCdsCard pcc = new PddiCdsCard();
      ArrayList<Card> cards = pcc.createCards(cpBundle);

      /*
       * CDSResponse作成、呼び出し元へのレスポンス作成
       */
      response = pcr.createSuccessResponse(cpp, cards);

    } catch (Exception e) {
      e.printStackTrace();
      PddiCdsException pce = null;
      if (e instanceof PddiCdsException) {
        pce = (PddiCdsException) e;
      } else {
        // ExceptionがPddiCdsExceptionでない場合
        // ExceptionをPddiCdsExceptionにセット
        pce = new PddiCdsException(500, IssueSeverity.FATAL, IssueType.EXCEPTION, e);
      }

      // pddicdsログにも出す
      this.outputToExceptionLog("PddiCdsException発生", cpp, pce);

      // エラーレスポンス作成
      int sc = pce.getStatusCode();
      // ResponseBuilder rb = Response.status(sc);
      if (sc == 200) {
        // カードなしの結果を返す
        try {
          response = pcr.createSuccessResponse(cpp, new ArrayList<Card>());
        } catch (PddiCdsException pce2) {
          pce2.printStackTrace();
          pce = pce2;
          // pddicdsログにも出す
          this.outputToExceptionLog("カードなしレスポンス作成時にException発生", cpp, pce2);
        }
      }
      if (sc != 200) {
        // エラーレスポンス作成
        response = pcr.createErrorResponse(cpp, pce);
      }
    }

    lm.info(">>> レスポンス (" + cpp.uuid + ")\n" + response.getEntity().toString());

    return response;
  }

  private void outputToExceptionLog(String title, CqlPreprocessing cpp, PddiCdsException pce) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    pce.printStackTrace(pw);
    int esc = pce.getStatusCode();
    OperationOutcomeIssueComponent eiss = pce.getOperationOutcome().getIssueFirstRep();
    String esev = eiss.getSeverity().toCode();
    String ecode = eiss.getCode().toCode();
    lm.error(
        "*** "
            + title
            + " ("
            + cpp.uuid
            + "): statusCode="
            + esc
            + ", severity="
            + esev
            + ", code="
            + ecode
            + "\n"
            + sw.toString()); // ********
  }
}
