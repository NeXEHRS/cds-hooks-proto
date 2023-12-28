/** */
package jp.metacube.fhir.pddicds.server.cards;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.model.CDSResponse;
import io.swagger.model.Card;
import jp.metacube.fhir.pddicds.server.common.ParameterInitiator;
import jp.metacube.fhir.pddicds.server.common.PddiCdsException;
import jp.metacube.fhir.pddicds.server.preparation.CqlPreprocessing;

/** @author takaha */
public class PddiCdsResponse {

  private ParameterInitiator pi = null;

  /** */
  public PddiCdsResponse() {
    pi = ParameterInitiator.getInstance();
  }

  /**
   * 呼び出し元へ返信するためのPDDICDSResponse(正常終了)を作成
   *
   * @param cpp リクエストなどの情報を格納
   * @param cards 返信へ含めるCard
   * @return Response
   * @throws PddiCdsException
   */
  public Response createSuccessResponse(CqlPreprocessing cpp, List<Card> cards)
      throws PddiCdsException {

    ResponseBuilder rb = Response.ok();

    // Date: レスポンス作成日時
    Instant nowInst = Instant.now();
    DateTimeFormatter dtf = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC);
    String nowf = dtf.format(nowInst);
    rb.header("Date", nowf);

    // Content-Type
    rb.header("Content-Type", "application/json");

    // X-Request-Id
    rb.header("X-Request-Id", cpp.hookInstance);

    // X-Correlation-Id
    rb.header("X-Correlation-Id", cpp.uuid);

    // レスポンス本体
    CDSResponse cr = new CDSResponse();
    cr.setCards(cards);
    String jsonStr;
    try {
      jsonStr = pi.objectMapper.writeValueAsString(cr);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      throw new PddiCdsException(
          500, IssueSeverity.FATAL, IssueType.EXCEPTION, "CDSレスポンス作成時に例外発生: " + e.getMessage());
    }
    rb.entity(jsonStr);

    return rb.build();
  }

  /**
   * 呼び出し元へ返信するためのPDDICDSResponse(異常終了)を作成
   *
   * @param cpp リクエストなどの情報を格納
   * @param pce 発生した例外
   * @return Response
   */
  public Response createErrorResponse(CqlPreprocessing cpp, PddiCdsException pce) {

    int sc = pce.getStatusCode();
    ResponseBuilder rb = Response.status(sc);

    // Date: レスポンス作成日時
    Instant nowInst = Instant.now();
    DateTimeFormatter dtf = DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC);
    String nowf = dtf.format(nowInst);
    rb.header("Date", nowf);

    // Content-Type
    // rb.header("Content-Type", "application/json");

    // X-Request-Id
    if (!StringUtils.isEmpty(cpp.hookInstance)) {
      rb.header("X-Request-Id", cpp.hookInstance);
    }
    // X-Correlation-Id
    rb.header("X-Correlation-Id", cpp.uuid);

    return rb.build();
  }
}
