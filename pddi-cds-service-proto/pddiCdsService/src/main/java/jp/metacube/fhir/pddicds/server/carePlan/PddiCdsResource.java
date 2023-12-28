/** */
package jp.metacube.fhir.pddicds.server.carePlan;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Expression;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionConditionComponent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.metacube.fhir.pddicds.server.common.ParameterInitiator;
import jp.metacube.fhir.pddicds.server.common.PddiCdsException;

/** @author takaha */
public class PddiCdsResource {
  protected String cqlResults = null;
  protected JsonNode cqlResultObject = null;

  /** */
  public PddiCdsResource() {}

  /** @param cro */
  public PddiCdsResource(JsonNode cro) {
    this.cqlResultObject = cro;
  }

  /**
   * @param cqlResults
   * @throws PddiCdsException
   */
  public PddiCdsResource(String patientId, String cqlResults) throws PddiCdsException {
    this.cqlResults = cqlResults;

    // this.cqlResults: JSONString -> JSONObject
    ObjectMapper mapper = ParameterInitiator.getInstance().objectMapper;
    try {
      JsonNode cresults = mapper.readTree(cqlResults);
      cqlResultObject = cresults.get("patientResults").get(patientId);

    } catch (JsonProcessingException e) {
      e.printStackTrace();
      throw new PddiCdsException(400, IssueSeverity.FATAL, IssueType.EXCEPTION, e.getMessage());
    }
  }

  /**
   * PlanDefinition.action.conditionの判定を実施する
   *
   * @param conditions PlanDefinition.action.condition
   * @return 判定結果(true, false). 判定不能の時はnullを返す
   * @throws PddiCdsException
   */
  protected boolean getActionCondition(List<PlanDefinitionActionConditionComponent> conditions)
      throws PddiCdsException {
    // conditions==null または　conditions.size()==0 の場合はtrueを返し、当該actionをRequestGroupへセット
    if (conditions == null) {
      return true;
    }
    int csize = conditions.size();
    if (csize == 0) {
      return true;
    }

    // conditions.size()>=1の場合は全てのconditionがtrueである場合にtrueを返し、当該action実行
    boolean condResult = true;
    for (int i = 0; i < csize; i++) {
      PlanDefinitionActionConditionComponent cd = conditions.get(i);
      Expression exp = cd.getExpression();
      if (exp == null) {
        // condition.expressionを持たない場合はこのconditionはtrueとみなす
        condResult = condResult && true;
      }
      String expExp = exp.getExpression();
      if (StringUtils.isEmpty(expExp)) {
        // condition.expression.expressionを持たない場合はこのconditionはtrueとみなす
        condResult = condResult && true;
      } else {
        String bv = this.cqlResultObject.get(expExp).asText();
        if ("true".equals(bv)) {
          condResult = condResult && true;
        } else if ("false".equals(bv)) {
          condResult = condResult && false;
          i = csize + 1;
        } else {
          // 内部エラー：PlanDefinition.actionがないため処理できない
          throw new PddiCdsException(
              500,
              IssueSeverity.FATAL,
              IssueType.INVALID,
              "action.conditionがnull、またはtrue、false以外の値を持っています");
        }
      }
    }

    return condResult;
  }
}
