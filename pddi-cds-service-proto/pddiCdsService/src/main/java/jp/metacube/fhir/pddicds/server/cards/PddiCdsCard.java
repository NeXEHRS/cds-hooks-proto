/** */
package jp.metacube.fhir.pddicds.server.cards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.CarePlan.CarePlanActivityComponent;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.RelatedArtifact;
import org.hl7.fhir.r4.model.RequestGroup;
import org.hl7.fhir.r4.model.RequestGroup.ActionSelectionBehavior;
import org.hl7.fhir.r4.model.RequestGroup.RequestGroupActionComponent;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ca.uhn.fhir.parser.IParser;
import io.swagger.model.Action;
import io.swagger.model.Action.TypeEnum;
import io.swagger.model.Card;
import io.swagger.model.Card.IndicatorEnum;
import io.swagger.model.Card.SelectionBehaviorEnum;
import io.swagger.model.Link;
import io.swagger.model.Source;
import io.swagger.model.Suggestion;
import jp.metacube.fhir.pddicds.server.common.FhirParser;
import jp.metacube.fhir.pddicds.server.common.ParameterInitiator;
import jp.metacube.fhir.pddicds.server.common.PddiCdsException;
import jp.metacube.fhir.pddicds.server.config.PddiCdsProperties;

/**
 * PDDI-CDSサーバで作成するCardを扱う
 *
 * @author takaha
 */
public class PddiCdsCard {
  private String systemUUID = null;

  /** コンストラクタ */
  public PddiCdsCard() {
    this.systemUUID = PddiCdsProperties.getInstance().getSystemUUID();
  }

  public ArrayList<Card> createCards(Bundle bundle) throws PddiCdsException {
    CarePlan cp = null;
    HashMap<String, IBaseResource> rscs = new HashMap<String, IBaseResource>();

    // レスポンス前準備
    ArrayList<Card> cards = new ArrayList<Card>();

    // BundleからFHIRリソースを抽出
    List<BundleEntryComponent> entries = bundle.getEntry();
    boolean hasRequestGroup = false;
    int esize = entries.size();
    if (esize == 0) {
      throw new PddiCdsException(
          200,
          IssueSeverity.INFORMATION,
          IssueType.PROCESSING,
          "カードが作成されませんでした: size of bundle.entry is 0.");
    }
    for (int i = 0; i < esize; i++) {
      BundleEntryComponent entry = entries.get(i);
      Resource ersc = entry.getResource();
      if (ersc instanceof CarePlan) {
        cp = (CarePlan) ersc;
      } else {
        if (ersc instanceof RequestGroup) {
          hasRequestGroup = true;
        }
        String uri = entry.getFullUrl();
        if (uri == null || uri.isEmpty()) {
          uri = "urn:uuid:" + ersc.getId();
        }
        rscs.put(uri, ersc);
      }
    }
    if (cp == null || cp.getActivity().size() == 0 || !hasRequestGroup) {
      String str = "";
      if (cp == null) {
        str += "カードが作成されませんでした: CarePlan is not in Bundle. ";
      } else if (cp.getActivity().size() == 0) {
        str += "カードが作成されませんでした: CarePlan has no activity. ";
      } else if (!hasRequestGroup) {
        str = "カードが作成されませんでした: RequestGroup is not in Bundle.";
      }
      throw new PddiCdsException(200, IssueSeverity.INFORMATION, IssueType.PROCESSING, str);
    }

    // Card作成
    List<CarePlanActivityComponent> acts = cp.getActivity();
    for (int i = 0; i < acts.size(); i++) {
      CarePlanActivityComponent act = acts.get(i);
      Reference aref = act.getReference();
      if (aref != null && !aref.isEmpty()) {
        String uri = aref.getReference();
        IBaseResource ibr = rscs.get(uri);
        if (ibr instanceof RequestGroup) {
          Card card = this.createCard(act, (RequestGroup) ibr, rscs);
          cards.add(card);
        }
      }
    }

    return cards;
  }

  // Card1個作成
  private Card createCard(
      CarePlanActivityComponent cpac, RequestGroup rg, HashMap<String, IBaseResource> rscs) {
    // レスポンス準備
    Card card = new Card();

    // Card情報
    RequestGroupActionComponent act0 = rg.getActionFirstRep();
    if (act0 != null && !act0.isEmpty()) {
      // Summary
      // String prx = act0.getPrefix();
      String ttl = act0.getTitle();
      if (!StringUtils.isEmpty(ttl)) {
        card.setSummary(ttl);
      }

      // detail
      String dcp = act0.getDescription();
      if (!StringUtils.isEmpty(dcp)) {
        card.setDetail(dcp);
      }

      // indicator
      Extension ext =
          cpac.getExtensionByUrl("http://terminology.hl7.org/CodeSystem/cdshooks-indicator");
      if (ext != null && !ext.isEmpty()) {
        StringType value = (StringType) ext.getValue();
        if (value != null && !value.isEmpty()) {
          String sv = value.asStringValue();
          card.setIndicator(IndicatorEnum.fromValue(sv));
        }
      }

      // Source, Link
      List<RelatedArtifact> docs = act0.getDocumentation();
      if (docs != null && docs.size() > 0) {
        SourceAndLinks sl = this.getSourceAndLink(docs);
        if (sl.source != null) {
          card.setSource(sl.source);
        }
        if (sl.links.size() > 0) {
          card.setLinks(sl.links);
        }
      }

      // suggestion
      ArrayList<Suggestion> suggs = new ArrayList<Suggestion>();
      List<RequestGroupActionComponent> act1s = act0.getAction();
      for (int i = 0; i < act1s.size(); i++) {
        // RequestGroup.action.action
        RequestGroupActionComponent act1 = act1s.get(i);
        Suggestion sugg = this.getSuggestion(act1, rscs);
        if (sugg != null) {
          // set=true;
          suggs.add(sugg);
        }
      }
      if (suggs.size() > 0) {
        card.setSuggestions(suggs);
      }

      // selectionBehavior
      ActionSelectionBehavior sb = act0.getSelectionBehavior();
      if (sb != null) {
        card.setSelectionBehavior(SelectionBehaviorEnum.fromValue(sb.toCode()));
      }
    }
    return card;
  }

  // Card.source, Card.link
  private SourceAndLinks getSourceAndLink(List<RelatedArtifact> docs) {
    SourceAndLinks sl = new SourceAndLinks();

    for (int i = 0; i < docs.size(); i++) {
      RelatedArtifact doc = docs.get(i);
      Extension ext = doc.getExtensionByUrl(this.systemUUID);
      String sv = null;
      if (ext != null && !ext.isEmpty()) {
        StringType value = (StringType) ext.getValue();
        if (value != null && !value.isEmpty()) {
          sv = value.asStringValue();
        }
      }

      if ("PlanDefinition.relatedArtifact".equals(sv)) {
        // Source
        sl.source = new Source();
        boolean set = false;
        // Source.label
        String disp = doc.getDisplay();
        if (!StringUtils.isEmpty(disp)) {
          set = true;
          sl.source.setLabel(disp);
        }
        // Source.Url
        String url = doc.getUrl();
        if (!StringUtils.isEmpty(url)) {
          set = true;
          sl.source.setUrl(url);
        }
        // Source.label、Source.Urlの両方がセットされていない場合は
        // source=nullとする
        if (!set) {
          sl.source = null;
        }
      } else {
        // Links
        // if (links == null) {
        //  links = new ArrayList<Link>();
        // }
        Link link = new Link();

        boolean set = false;
        // Link.label
        String disp = doc.getDisplay();
        if (!StringUtils.isEmpty(disp)) {
          set = true;
          link.setLabel(disp);
        }
        // Link.Url
        String url = doc.getUrl();
        if (!StringUtils.isEmpty(url)) {
          set = true;
          link.setUrl(url);
        }

        // Link.type:「absolute」固定
        // またset=trueの場合はlinkをリストに追加
        if (set) {
          // set=true
          link.setType("absolute");
          sl.links.add(link);
        }
      }
    }
    return sl;
  }

  // Card.suggestion
  private Suggestion getSuggestion(
      RequestGroupActionComponent act1, HashMap<String, IBaseResource> rscs) {
    // suggestion初期化
    Suggestion suggestion = new Suggestion();
    boolean set = false;

    // suggestion.label
    String pfx = act1.getPrefix();
    if (!StringUtils.isEmpty(pfx)) {
      set = true;
      suggestion.setLabel(pfx);
    }

    // suggestion.uuid
    String id = act1.getId();
    if (!StringUtils.isEmpty(id)) {
      try {
        UUID uuid = UUID.fromString(id);
        suggestion.setUuid(uuid);
        set = true;
      } catch (IllegalArgumentException e) {
        // 何もしない
        e.printStackTrace();
      }
    }

    // suggestion.action
    ArrayList<Action> actions = new ArrayList<Action>();
    Action action = this.getAction(act1, rscs);
    if (action != null) {
      set = true;
      actions.add(action);
    }

    //    List<RequestGroupActionComponent> act2s = act1.getAction();
    //    for (int i = 0; i < act2s.size(); i++) {
    //      RequestGroupActionComponent act2 = act2s.get(i);
    //      Action action = this.getAction(act2, rscs);
    //      if (action != null) {
    //        set = true;
    //        actions.add(action);
    //      }
    //    }
    if (actions.size() > 0) {
      suggestion.setActions(actions);
    }

    // set=falseの場合nullを返す
    if (!set) {
      suggestion = null;
    }

    return suggestion;
  }

  // Card.suggestion.action
  private Action getAction(RequestGroupActionComponent act2, HashMap<String, IBaseResource> rscs) {
    // action初期化
    Action action = new Action();
    boolean set = false;

    // type
    CodeableConcept type = act2.getType();
    String tcode = null;
    if (type != null && !type.isEmpty()) {
      // PlanDefinitionからRequestGroupを作成するとtype.codingは必ず1つのみ
      Coding tcoding = type.getCodingFirstRep();
      tcode = tcoding.getCode();
      if ("remove".equals(tcode)) {
        // RequestGroup.action.type=create | update | remove | fire-event
        // 一方、Card.suggestion.action.type=create | update | delete
        // 前者のremoveは後者のdeleteに対応.
        // よってtcode=removeの場合tcode=deleteに置き換える、
        tcode = "delete";
      } else if ("fire-event".equals(tcode)) {
        // 対応なし
        return null;
      }
      set = true;
      action.setType(TypeEnum.fromValue(tcode));
    }

    // description
    String dcp = act2.getDescription();
    if (!StringUtils.isEmpty(dcp)) {
      set = true;
      action.setDescription(dcp);
    }

    // resource
    if ("create".equals(tcode) || "update".equals(tcode)) {
      Reference arsc = act2.getResource();
      if (arsc != null && !arsc.isEmpty()) {
        String ref = arsc.getReference();
        if (!StringUtils.isEmpty(ref)) {
          // Card.suggestion.action.resourceにRequestGroup.action...action.resource.referenceで指定したFHIRリソースを貼り付ける
          // 貼り付けるリソースは入力Bundleに含まれる
          // 貼り付けるFHIRリソース -> JSON文字列 -> JSONTreeの順に変換し、最後のJSONTreeを
          // Card.suggestion.action.resourceにセットする。
          IBaseResource resource = rscs.get(ref);
          IParser ip = FhirParser.getInstance().getJSONParser();
          String rscStr = ip.encodeResourceToString(resource);

          ObjectMapper mapper = ParameterInitiator.getInstance().objectMapper;

          JsonNode jn = null;
          try {
            jn = mapper.readTree(rscStr);
            set = true;
          } catch (JsonProcessingException e) {
            // エラーログを出すだけで何もしない
            // 本来貼り付けるべきFHIRリソースは貼り付けない
            e.printStackTrace();
            jn = null;
          }

          // System.out.println(rscStr);
          if (resource != null && jn != null) {
            set = true;
            action.setResource(jn);
          }
        }
      }
    }

    // resourceId
    if ("delete".equals(tcode)) {
      Reference arsc = act2.getResource();
      if (arsc != null && !arsc.isEmpty()) {
        String ref = arsc.getReference();
        if (!StringUtils.isEmpty(ref)) {
          // TODO Card出力要確認
          set = true;
          action.setResourceId(ref);
        }
      }
    }

    if (!set) {
      // インスタンス定義のみで何もセットされていない
      action = null;
    }

    return action;
  }

  // Card.sourceとCard.linkをセットにするローカルクラス
  private class SourceAndLinks {
    public Source source = null;
    public List<Link> links = null;

    public SourceAndLinks() {
      links = new ArrayList<Link>();
    }
  }
}
