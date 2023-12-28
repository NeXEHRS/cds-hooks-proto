package jp.metacube.fhir.pddicds.server.carePlan;

import org.hl7.fhir.r4.model.ActivityDefinition;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.Resource;
import jp.metacube.fhir.pddicds.server.common.PddiCdsException;
// import jp.metacube.sugarcube.v3.pddiCdsServer.common.PddiCdsException;

/**
 * ActivityDefinitionを取り扱う
 *
 * @author takaha
 */
public class PddiCdsActivityDefinition extends PddiCdsResource {

  /** */
  public PddiCdsActivityDefinition() {
    super();
  }

  /**
   * ActivityDefinitionから内部で指定のFHIRリソースを作成する
   *
   * @param ad ActivityDefinition
   * @return FHIRリソース
   * @throws PddiCdsException
   */
  public Resource exchangeResource(String patientId, ActivityDefinition ad)
      throws PddiCdsException {
    Resource rsc = null;
    String kind = ad.getKind().toCode();
    if ("MedicationRequest".equals(kind)) {
      PddiCdsMedicationRequest pcmr = new PddiCdsMedicationRequest();
      rsc = pcmr.create(patientId, ad);
    } else if ("ServiceRequest".equals(kind)) {
      PddiCdsServiceRequest pcsr = new PddiCdsServiceRequest();
      rsc = pcsr.create(ad);
    } else {
      throw new PddiCdsException(
          500,
          IssueSeverity.FATAL,
          IssueType.EXCEPTION,
          "ActivityDefinition から " + kind + " を作成できませんでした.");
    }
    return rsc;
  }
}
