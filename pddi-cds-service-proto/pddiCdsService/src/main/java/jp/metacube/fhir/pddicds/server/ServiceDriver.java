package jp.metacube.fhir.pddicds.server;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import org.hl7.fhir.r4.model.Bundle;
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

  /** コンストラクタ */
  public ServiceDriver() {}

  /**
   * @param id
   * @param RequestBody
   * @param request
   * @return
   */
  public Response driver(String id, String RequestBody, HttpServletRequest request) {

    PddiCdsResponse pcr = new PddiCdsResponse();
    CqlPreprocessing cpp = null;
    Response response = null;

    try {
      /*
       * CQL解析実行準備
       */
      cpp = new CqlPreprocessing(id, RequestBody, request);

      IParser jp = FhirParser.getInstance().getJSONParser();
      String bstr = jp.encodeResourceToString(cpp.bundle);
      System.out.println(">>>> " + bstr);

      /*
       * CQL解析実行
       */
      // cql結果（JSON）
      // 実行中取り寄せるPlanDefinitionはcppにセットされる
      CqlAnalysis ca = new CqlAnalysis(cpp);
      String cqlResults = ca.exec();

      System.out.println("===> " + cqlResults);

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
      System.out.println(">>>> " + cpbstr);

      /*
       * CarePlanとRequestGroupからCardsを作成する
       */
      PddiCdsCard pcc = new PddiCdsCard();
      ArrayList<Card> cards = pcc.createCards(cpBundle);

      /*
       * CDSResponse作成、呼び出し元へのレスポンス作成
       */
      response = pcr.createSuccessResponse(cpp, cards);

    } catch (PddiCdsException pce) {
      pce.printStackTrace();
      // エラーレスポンス作成
      int sc = pce.getStatusCode();
      // ResponseBuilder rb = Response.status(sc);
      if (sc == 200) {
        // カードなしの結果を返す
        try {
          response = pcr.createSuccessResponse(cpp, new ArrayList<Card>());
        } catch (PddiCdsException e) {
          e.printStackTrace();
          pce = e;
        }
      }
      if (sc != 200) {
        // エラーレスポンス作成
        response = pcr.createErrorResponse(cpp, pce);
      }
    }

    return response;
  }
}
