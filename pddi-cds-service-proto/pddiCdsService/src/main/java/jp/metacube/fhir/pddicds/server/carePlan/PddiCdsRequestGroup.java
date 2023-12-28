/** */
package jp.metacube.fhir.pddicds.server.carePlan;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.ActivityDefinition;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Expression;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionComponent;
import org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionDynamicValueComponent;
import org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionRelatedActionComponent;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.RelatedArtifact;
import org.hl7.fhir.r4.model.RequestGroup;
import org.hl7.fhir.r4.model.RequestGroup.ActionRelationshipType;
import org.hl7.fhir.r4.model.RequestGroup.RequestGroupActionComponent;
import org.hl7.fhir.r4.model.RequestGroup.RequestGroupActionRelatedActionComponent;
import org.hl7.fhir.r4.model.RequestGroup.RequestIntent;
import org.hl7.fhir.r4.model.RequestGroup.RequestStatus;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.UriType;
import com.fasterxml.jackson.databind.JsonNode;
// import jp.metacube.sugarcube.v3.pddiCdsServer.common.PddiCdsException;
// import jp.metacube.sugarcube.v3.pddiCdsServer.common.ReadFHIRResource;
import jp.metacube.fhir.pddicds.server.common.PddiCdsException;
import jp.metacube.fhir.pddicds.server.common.ReadWriteFile;
import jp.metacube.fhir.pddicds.server.config.PddiCdsProperties;

/**
 * RequestGroupを扱う
 *
 * @author takaha
 */
public class PddiCdsRequestGroup extends PddiCdsResource {
  private String hookInstance = null;
  private String requestUrl = null;
  private Date requestDate = null;
  private String patientId = null;
  private ReadWriteFile readFr = null;
  private List<RelatedArtifact> pdRelatedArtifacts = null;
  private String sysUUID = null;
  ;

  // private ArrayList<Resource> resourceList=null;

  /** */
  // public PddiCdsRequestGroup(HashMap<String, String> cqlResults) {
  //    super(cqlResults);
  // }

  /**
   * コンストラクタ
   *
   * @param hookInstance CdsHooksRequest.hooksInstance
   * @param requestUrl アクセスURL
   * @param requestDate アクセス日時
   * @param ras PlanDefinition.relatedArtifact
   * @param cqlReqults CQL解析結果
   */
  public PddiCdsRequestGroup(
      String hookInstance,
      String requestUrl,
      String patientId,
      Date requestDate,
      List<RelatedArtifact> ras,
      JsonNode cqlResultObject) {
    super(cqlResultObject);
    this.hookInstance = hookInstance;
    this.requestUrl = requestUrl;
    this.requestDate = requestDate;
    this.pdRelatedArtifacts = ras;
    this.patientId = patientId;
    // ストレージ保存のFHIRリソースを読みだすためのインスタンス指定
    readFr = new ReadWriteFile();
    // システムUUID取得
    sysUUID = PddiCdsProperties.getInstance().getSystemUUID();
  }

  /**
   * PlanDefinition.action.actionの内容からRequestGroupを作成する
   *
   * @param pdAct PlanDefinition.action.action
   * @return RequestGroup
   * @throws PddiCdsException
   */
  public RequestGroupInfo create(PlanDefinitionActionComponent act) throws PddiCdsException {

    // RequestGroup
    RequestGroup rg = new RequestGroup();
    // idはuuidとする
    rg.setId(UUID.randomUUID().toString());
    // identifierにはHookRequest.hookInstance
    Identifier rgIdent = rg.addIdentifier();
    rgIdent.setValue(hookInstance);
    // instanciatesUriには本システムへのアクセスURL
    rg.addInstantiatesUri(requestUrl);
    // statusは「draft」固定
    rg.setStatus(RequestStatus.DRAFT);
    // intentは「order」固定
    rg.setIntent(RequestIntent.ORDER);
    // subjectは患者IDを設定
    Reference sref = new Reference();
    Identifier sident = new Identifier();
    sident.setValue(patientId);
    sref.setIdentifier(sident);
    rg.setSubject(sref);

    // authoredOnにはHooksリクエスト受付時刻
    rg.setAuthoredOn(this.requestDate);

    /*
     * PlanDefinition.action.actionの内容を
     * RequestGroup.actionへセットする
     */
    RequestGroupActionComponentInfo rgacInfo = this.setRequestGroupAction(act);

    // PlanDefinition.relatedArtifactをRequestGroup.action.documentationにセットする
    // こちらはCard.sourceとして設定するため、extensionを設定
    for (int i = 0; i < this.pdRelatedArtifacts.size(); i++) {
      RelatedArtifact pdra = this.pdRelatedArtifacts.get(i);
      // 元のPlanDefinitionに影響しないため、複製を作る
      RelatedArtifact rgra = pdra.copy();
      // Extension作成：文字列「」を設定
      Extension ext = rgra.addExtension();
      ext.setUrl(this.sysUUID);
      ext.setValue(new StringType("PlanDefinition.relatedArtifact"));
      // RequestGroupにRelatedArtifactを追加
      rgacInfo.rgaComponent.addDocumentation(rgra);
    }

    // RequestGroup.actionをRequestGroupへ追加する
    rg.addAction(rgacInfo.rgaComponent);

    // リターン値準備
    RequestGroupInfo rgi = new RequestGroupInfo();
    rgi.requestGroup = rg;
    rgi.indicator = rgacInfo.indicator;
    rgi.resources.addAll(rgacInfo.resources);
    return rgi;
  }

  private RequestGroupActionComponentInfo setRequestGroupAction(PlanDefinitionActionComponent pdac)
      throws PddiCdsException {
    // RequestGroup.action
    RequestGroupActionComponent rgac = new RequestGroupActionComponent();
    // prefix
    rgac.setPrefix(pdac.getPrefix());
    // title
    rgac.setTitle(pdac.getTitle());
    // description
    String sv = pdac.getDescription();
    if (!StringUtils.isEmpty(sv)) {
      // String sv = sv1.replaceAll("\\", "\\\\").replaceAll("'", "\'");
      rgac.setDescription(sv);
    }
    // textEquivalent
    rgac.setTextEquivalent(pdac.getTextEquivalent());
    // priority
    org.hl7.fhir.r4.model.PlanDefinition.RequestPriority pdp = pdac.getPriority();
    if (pdp != null) {
      org.hl7.fhir.r4.model.RequestGroup.RequestPriority rgp =
          org.hl7.fhir.r4.model.RequestGroup.RequestPriority.fromCode(pdp.toCode());
      rgac.setPriority(rgp);
    }
    // code
    List<CodeableConcept> pdacCodes = pdac.getCode();
    if (pdacCodes != null) {
      for (int i = 0; i < pdacCodes.size(); i++) {
        rgac.addCode(pdacCodes.get(i));
      }
    }
    // documentation
    // PlanDefinitionでのaction.documentationを設定する
    // （PlanDefinition.relatedArtifactのものは後で追加）
    List<RelatedArtifact> pdacDocs = pdac.getDocumentation();
    if (pdacDocs != null) {
      for (int i = 0; i < pdacDocs.size(); i++) {
        RelatedArtifact doc = pdacDocs.get(i);
        rgac.addDocumentation(doc);
      }
    }
    // relatedAction
    List<PlanDefinitionActionRelatedActionComponent> pdacRelatedAction = pdac.getRelatedAction();
    if (pdacRelatedAction != null) {
      for (int i = 0; i < pdacRelatedAction.size(); i++) {
        PlanDefinitionActionRelatedActionComponent ra = pdacRelatedAction.get(i);
        RequestGroupActionRelatedActionComponent rgacRelatedAction =
            new RequestGroupActionRelatedActionComponent();
        // relatedAction.actionId
        rgacRelatedAction.setActionId(ra.getActionId());
        // relatedAction.relationship
        String raCode = ra.getRelationship().toCode();
        rgacRelatedAction.setRelationship(ActionRelationshipType.fromCode(raCode));
        // relationAction.offset
        rgacRelatedAction.setOffset(ra.getOffset());
        // rgacに追加
        rgac.addRelatedAction(rgacRelatedAction);
      }
    }

    // participant
    // type
    rgac.setType(pdac.getType());
    // groupingBehavior
    org.hl7.fhir.r4.model.PlanDefinition.ActionGroupingBehavior gb = pdac.getGroupingBehavior();
    if (gb != null) {
      rgac.setGroupingBehavior(
          org.hl7.fhir.r4.model.RequestGroup.ActionGroupingBehavior.fromCode(gb.toCode()));
    }
    // selectionBehavior
    org.hl7.fhir.r4.model.PlanDefinition.ActionSelectionBehavior sb = pdac.getSelectionBehavior();
    if (sb != null) {
      rgac.setSelectionBehavior(
          org.hl7.fhir.r4.model.RequestGroup.ActionSelectionBehavior.fromCode(sb.toCode()));
    }

    // requiredBehavior
    org.hl7.fhir.r4.model.PlanDefinition.ActionRequiredBehavior rb = pdac.getRequiredBehavior();
    if (rb != null) {
      rgac.setRequiredBehavior(
          org.hl7.fhir.r4.model.RequestGroup.ActionRequiredBehavior.fromCode(rb.toCode()));
    }
    // precheckBehavior
    org.hl7.fhir.r4.model.PlanDefinition.ActionPrecheckBehavior pb = pdac.getPrecheckBehavior();
    if (pb != null) {
      rgac.setPrecheckBehavior(
          org.hl7.fhir.r4.model.RequestGroup.ActionPrecheckBehavior.fromCode(pb.toCode()));
    }
    // cardinalityBehavior
    org.hl7.fhir.r4.model.PlanDefinition.ActionCardinalityBehavior cb =
        pdac.getCardinalityBehavior();
    if (cb != null) {
      rgac.setCardinalityBehavior(
          org.hl7.fhir.r4.model.RequestGroup.ActionCardinalityBehavior.fromCode(cb.toCode()));
    }
    // resource
    Resource ersc = null;
    UriType acDef = pdac.getDefinitionUriType();
    if (acDef != null && !acDef.isEmpty()) {
      String acDefStr = acDef.asStringValue();
      String storage = PddiCdsProperties.getInstance().getStorageFolderPath();
      IBaseResource frsc;
      try {
        frsc = this.readFr.getResource(storage + "/" + acDefStr);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        // 「なし」で先に進める
        frsc = null;
      }
      if (frsc != null && frsc instanceof ActivityDefinition) {
        PddiCdsActivityDefinition pcad = new PddiCdsActivityDefinition();
        ersc = pcad.exchangeResource(patientId, (ActivityDefinition) frsc);

        // 作成したFHIRリソースのreferenceをセット
        Reference ref = new Reference();
        ref.setReference("urn:uuid:" + ersc.getId());
        rgac.setResource(ref);
      }
    }

    // dynamicValueの適用
    String indicator = null;
    List<PlanDefinitionActionDynamicValueComponent> dvs = pdac.getDynamicValue();
    if (dvs != null) {
      for (int i = 0; i < dvs.size(); i++) {
        PlanDefinitionActionDynamicValueComponent dv = dvs.get(i);
        // path
        String path = dv.getPath();
        Expression exp = dv.getExpression();
        if (exp != null && !exp.isEmpty()) {
          // expression.expressionを取得し、対応するCQL解析結果を取得
          String expStr = exp.getExpression();
          String value = super.cqlResultObject.get(expStr).asText();
          if (!StringUtils.isEmpty(value)) {
            if ("action.title".equals(path)) {
              // action.titleを該当のCQL解析結果に置き換える
              rgac.setTitle(value);
            } else if ("action.label".equals(path) || "action.prefix".equals(path)) {
              // action.prefix(FHIRR4)を該当のCQL解析結果に置き換える
              rgac.setPrefix(value);
            } else if ("action.description".equals(path)) {
              // action.descriptionを該当のCQL解析結果に置き換える
              // value = value.replaceAll("\\", "\\\\").replaceAll("'", "\'");
              rgac.setDescription(value);
            } else if ("activity.extension".equals(path)) {
              // 後でCarePlan.activity.extensionに書き込むので、一時的に
              indicator = value;
            }
          }
        }
      }
    }

    // action
    List<PlanDefinitionActionComponent> pdacChildren = pdac.getAction();
    ArrayList<Resource> rscs = new ArrayList<Resource>();
    if (pdacChildren != null) {
      for (int i = 0; i < pdacChildren.size(); i++) {
        PlanDefinitionActionComponent pdacChild = pdacChildren.get(i);
        boolean tf = super.getActionCondition(pdacChild.getCondition());
        if (tf) {
          RequestGroupActionComponentInfo crg = this.setRequestGroupAction(pdacChild);
          rgac.addAction(crg.rgaComponent);
          if (!StringUtils.isEmpty(crg.indicator)) {
            // indicatorは子孫actionで指定（またはある種の条件で書き変わる）されている場合、そちらが優先
            indicator = crg.indicator;
          }
          rscs.addAll(crg.resources);
        }
      }
    }

    // RequestGroupActionComponentInfo:レスポンス用
    RequestGroupActionComponentInfo rgacInfo = new RequestGroupActionComponentInfo();
    rgacInfo.rgaComponent = rgac;
    rgacInfo.indicator = indicator;
    if (ersc != null) {
      rgacInfo.resources.add(ersc);
    }
    if (rscs.size() > 0) {
      rgacInfo.resources.addAll(rscs);
    }
    return rgacInfo;
  }

  /**
   * RequestGroup、indicator（info/warning/critical）およびActivityDefinitionから作成したFHIRリソースをまとめるクラス
   *
   * @author takaha
   */
  public class RequestGroupInfo {
    // RequestGroup
    public RequestGroup requestGroup = null;
    // indicator
    public String indicator = null;
    // ActivityDefinitionから作成したFHIRリソース
    public ArrayList<Resource> resources = new ArrayList<Resource>();

    // コンストラクタ
    public RequestGroupInfo() {}
  }

  /**
   * RequestGroupActionComponentindicator（info/warning/critical）およびActivityDefinitionから作成したFHIRリソースをまとめるクラス
   *
   * @author takaha
   */
  private class RequestGroupActionComponentInfo {
    // RequestGroupActionComponent
    public RequestGroupActionComponent rgaComponent = null;
    // indicator
    public String indicator = null;
    // ActivityDefinitionから作成したFHIRリソース
    public ArrayList<Resource> resources = new ArrayList<Resource>();

    // コンストラクタ
    public RequestGroupActionComponentInfo() {}
  }
}
