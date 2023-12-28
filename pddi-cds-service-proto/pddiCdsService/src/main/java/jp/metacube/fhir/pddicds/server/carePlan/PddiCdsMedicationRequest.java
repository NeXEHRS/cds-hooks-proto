/** */
package jp.metacube.fhir.pddicds.server.carePlan;

import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.ActivityDefinition;
import org.hl7.fhir.r4.model.ActivityDefinition.RequestIntent;
import org.hl7.fhir.r4.model.ActivityDefinition.RequestPriority;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Dosage;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestIntent;
import org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestPriority;
import org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestStatus;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
// import jp.metacube.sugarcube.v3.pddiCdsServer.common.PddiCdsException;
import org.hl7.fhir.r4.model.Reference;
import jp.metacube.fhir.pddicds.server.common.PddiCdsException;

/**
 * ActivityDefinitionから作成されたMedicationRequestを扱う
 *
 * @author takaha
 */
public class PddiCdsMedicationRequest extends PddiCdsResource {

  /** コンストラクタ */
  public PddiCdsMedicationRequest() {
    super();
  }

  /**
   * ActivityDefinitionから作成されたMedicationRequestを作成する
   *
   * @param ad ActivityDefinition
   * @return MedicationRequest
   * @throws PddiCdsException
   */
  public MedicationRequest create(String patientId, ActivityDefinition ad) throws PddiCdsException {
    MedicationRequest mr = new MedicationRequest();
    // id
    mr.setId(UUID.randomUUID().toString());
    // identifier
    mr.setIdentifier(ad.getIdentifier());
    // status: draftに設定
    mr.setStatus(MedicationRequestStatus.DRAFT);
    // intent
    RequestIntent intent = ad.getIntent();
    if (intent != null) {
      String ic = intent.toCode();
      if (!StringUtils.isEmpty(ic)) {
        mr.setIntent(MedicationRequestIntent.fromCode(ic));
      }
    }
    // priority
    RequestPriority priority = ad.getPriority();
    if (priority != null) {
      String pc = priority.toCode();
      if (!StringUtils.isEmpty(pc)) {
        mr.setPriority(MedicationRequestPriority.fromCode(pc));
      }
    }
    // doNotPerform
    mr.setDoNotPerform(ad.getDoNotPerform());
    // medication
    CodeableConcept cc = ad.getProductCodeableConcept();
    if (cc != null && !cc.isEmpty()) {
      mr.setMedication(cc);
    } else {
      Reference pr = ad.getProductReference();
      if (pr != null && !pr.isEmpty()) {
        mr.setMedication(pr);
      }
    }
    // subject
    if (!StringUtils.isEmpty(patientId)) {
      Reference ref = new Reference();
      Identifier ident = new Identifier();
      ident.setValue(patientId);
      ref.setIdentifier(ident);
      mr.setSubject(ref);
    } else {
      throw new PddiCdsException(400, IssueSeverity.FATAL, IssueType.INVALID, "患者IDがありません.");
    }

    // dosageInstruction
    List<Dosage> dsg = ad.getDosage();
    if (dsg != null && !dsg.isEmpty() && dsg.size() > 0) {
      mr.setDosageInstruction(dsg);
    }

    return mr;
  }
}
