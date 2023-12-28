package jp.metacube.fhir.pddicds.server.carePlan;

import java.util.List;
import java.util.UUID;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.CarePlan.CarePlanActivityComponent;
import org.hl7.fhir.r4.model.CarePlan.CarePlanIntent;
import org.hl7.fhir.r4.model.CarePlan.CarePlanStatus;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionComponent;
import org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionConditionComponent;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.RelatedArtifact;
import org.hl7.fhir.r4.model.Resource;
import jp.metacube.fhir.pddicds.server.carePlan.PddiCdsRequestGroup.RequestGroupInfo;
import jp.metacube.fhir.pddicds.server.common.PddiCdsException;
// import jp.metacube.sugarcube.v3.pddiCdsServer.carePlan.PddiCdsRequestGroup.RequestGroupInfo;
// import jp.metacube.sugarcube.v3.pddiCdsServer.common.PddiCdsException;
import jp.metacube.fhir.pddicds.server.preparation.CqlPreprocessing;

/**
 * CQL解析結果とPlanDefinitionからCarePlanを作成する
 *
 * @author takaha
 */
public class PddiCdsCarePlan extends PddiCdsResource {
  // private HashMap<String, String> cqlResults = new HashMap<String, String>();
  private CqlPreprocessing cpp;
  // private PlanDefinition

  /**
   * @param cqlResults
   * @throws PddiCdsException
   */
  public PddiCdsCarePlan(CqlPreprocessing cpp, String cqlResults) throws PddiCdsException {
    super(cpp.patientId, cqlResults);
    this.cpp = cpp;
  }

  /**
   * @param hooksInstance
   * @param requestUrl
   * @param requestDate
   * @param patientId
   * @param pd
   * @return
   * @throws PddiCdsException
   */
  public Bundle create() throws PddiCdsException {
    // 作成したCarePlan, RequestGroup格納のBundle作成
    Bundle bundle = new Bundle();
    bundle.setType(BundleType.COLLECTION);

    // RequestGroup作成に使うPlanDefinition.action
    // PDDI-CDSにおけるPlanDefintion.actionは１つのみ
    // 複数存在しても、最初の一つのみを扱う
    PlanDefinitionActionComponent topAction = cpp.planDefinition.getActionFirstRep();
    if (topAction == null) {
      // 内部エラー：PlanDefinition.actionがないため処理できない
      throw new PddiCdsException(
          500, IssueSeverity.FATAL, IssueType.INVALID, "PlanDefinition.actionはありません.");
    }

    // PlanDefinition.actionにconditionが存在する場合
    List<PlanDefinitionActionConditionComponent> topConds = topAction.getCondition();
    if (!this.getActionCondition(topConds)) {
      // PlanDefinition.actionにあるconditionが「false」を返す
      // この場合は続行不可能となり、カードは返せない
      // 追加のFHIRリソースを要する
      //
      throw new PddiCdsException(
          200,
          IssueSeverity.INFORMATION,
          IssueType.INVALID,
          "PlanDefinition.action.condition=false");
    }

    // CarePlan作成
    CarePlan cp = new CarePlan();
    // CarePlan.id : uuidで設定
    cp.setId(UUID.randomUUID().toString());
    // CarePlan.identifierにCDSHooksRequestのhookInstanceを指定
    Identifier ident = cp.addIdentifier();
    ident.setId(cpp.hookInstance);
    // CarePlan.instantiatesCanonicalにはPlanDefinitionへのアクセスURLを指定
    String pdUrl = cpp.planDefinition.getUrl();
    if (pdUrl.isEmpty()) {
      // 存在しない場合は相対パスを指定
      pdUrl = "PlanDefinition/" + cpp.planDefinition.getId();
    }
    cp.addInstantiatesCanonical(pdUrl);
    // CarePlan.statusは常時draft
    cp.setStatus(CarePlanStatus.DRAFT);
    // CarePlan.intentは常時order
    cp.setIntent(CarePlanIntent.ORDER);
    // CarePlan.subjectは患者リソースへのアクセスURL
    Identifier pid = new Identifier();
    pid.setValue(cpp.patientId);
    Reference pRef = new Reference();
    pRef.setIdentifier(pid);
    cp.setSubject(pRef);

    // PlanDefinitionのRelatedArtifact
    // RequestGroup作成時に中に入れる
    List<RelatedArtifact> pdra = cpp.planDefinition.getRelatedArtifact();

    // RequestGroup作成
    List<PlanDefinitionActionComponent> pdAction = topAction.getAction();
    PddiCdsRequestGroup pcrg =
        new PddiCdsRequestGroup(
            cpp.hookInstance,
            cpp.requestUrl,
            cpp.patientId,
            cpp.requestDate,
            pdra,
            super.cqlResultObject);
    for (int i = 0; i < pdAction.size(); i++) {
      PlanDefinitionActionComponent act1 = pdAction.get(i);

      // PlanDefinition.action.action.conditionが存在する場合は検証を行う
      // topConds = topAction.getCondition();
      List<PlanDefinitionActionConditionComponent> act1Conds = act1.getCondition();
      if (this.getActionCondition(act1Conds)) {
        // conditionを満たす場合はRequestGroupを作成
        RequestGroupInfo rg = pcrg.create(act1);

        // 作成したRequestGroupをBundleに追加する
        BundleEntryComponent entry = bundle.addEntry();
        entry.setResource(rg.requestGroup);
        String rgUuid = "urn:uuid:" + rg.requestGroup.getId();
        entry.setFullUrl(rgUuid);

        // CarePlan.activityにindicator情報とRequestGroupへのURLを設定
        CarePlanActivityComponent activity = cp.addActivity();
        Extension ext = activity.addExtension();
        ext.setValue(new CodeType(rg.indicator));
        ext.setUrl("http://terminology.hl7.org/CodeSystem/cdshooks-indicator");
        activity.setReference(new Reference(rgUuid));

        // RequestGroupが参照するMedicationRequest,ServiceRequestをBundleに追加
        for (int k = 0; k < rg.resources.size(); k++) {
          Resource rsc = rg.resources.get(k);
          BundleEntryComponent rbnd = bundle.addEntry();
          rbnd.setResource(rsc);
          rbnd.setFullUrl("urn:uuid:" + rsc.getId());
        }
      }
    }

    // CarePlanをBundleへ入れる
    BundleEntryComponent cpEntry = bundle.addEntry();
    cpEntry.setResource(cp);
    cpEntry.setFullUrl("urn:uuid:" + cp.getId());

    return bundle;
  }
}
