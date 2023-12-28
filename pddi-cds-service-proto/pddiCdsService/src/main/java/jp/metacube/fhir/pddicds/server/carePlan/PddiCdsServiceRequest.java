package jp.metacube.fhir.pddicds.server.carePlan;

import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.ActivityDefinition;
import org.hl7.fhir.r4.model.ActivityDefinition.RequestIntent;
import org.hl7.fhir.r4.model.ActivityDefinition.RequestPriority;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.ServiceRequest.ServiceRequestIntent;
import org.hl7.fhir.r4.model.ServiceRequest.ServiceRequestPriority;
import org.hl7.fhir.r4.model.ServiceRequest.ServiceRequestStatus;
import org.hl7.fhir.r4.model.Type;

/**
 * ActivityDefinitionから作成されたServiceRequestを扱う
 *
 * @author takaha
 */
public class PddiCdsServiceRequest extends PddiCdsResource {

  /** コンストラクタ */
  public PddiCdsServiceRequest() {}

  /**
   * ActivityDefinitionからServiceRequestを作成する
   *
   * @param ad ActivityDefinition
   * @return ServiceRequest
   */
  public ServiceRequest create(ActivityDefinition ad) {
    ServiceRequest sr = new ServiceRequest();

    // id
    sr.setId(UUID.randomUUID().toString());
    // identifier
    sr.setIdentifier(ad.getIdentifier());
    // status: draftに設定
    sr.setStatus(ServiceRequestStatus.DRAFT);
    // intent
    RequestIntent intent = ad.getIntent();
    if (intent != null) {
      String ic = intent.toCode();
      if (!StringUtils.isEmpty(ic)) {
        sr.setIntent(ServiceRequestIntent.fromCode(ic));
      }
    }
    // priority
    RequestPriority priority = ad.getPriority();
    if (priority != null) {
      String pc = priority.toCode();
      if (!StringUtils.isEmpty(pc)) {
        sr.setPriority(ServiceRequestPriority.fromCode(pc));
      }
    }

    // doNotPerform
    sr.setDoNotPerform(ad.getDoNotPerform());
    // code
    CodeableConcept code = ad.getCode();
    if (code != null && !code.isEmpty()) {
      sr.setCode(code);
    }
    // quantity
    Quantity qt = ad.getQuantity();
    if (qt != null && !qt.isEmpty()) {
      sr.setQuantity(qt);
    }

    // occurrence
    Type tm = ad.getTiming();
    if (tm != null && !tm.isEmpty()) {
      sr.setOccurrence(tm);
    }
    // locationReference
    Reference loc = ad.getLocation();
    if (loc != null && !loc.isEmpty()) {
      sr.addLocationReference(loc);
    }
    // bodySite
    List<CodeableConcept> bss = ad.getBodySite();
    if (bss != null && !bss.isEmpty()) {
      sr.setBodySite(bss);
    }

    return sr;
  }
}
