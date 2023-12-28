package jp.metacube.fhir.pddicds.server.carePlan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.InputStream;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.hl7.fhir.r4.model.PlanDefinition;
import org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionComponent;
import org.hl7.fhir.r4.model.RelatedArtifact;
import org.hl7.fhir.r4.model.RequestGroup;
import org.hl7.fhir.r4.model.RequestGroup.RequestGroupActionComponent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.metacube.fhir.pddicds.server.carePlan.PddiCdsRequestGroup.RequestGroupInfo;
import jp.metacube.fhir.pddicds.server.common.ParameterInitiator;
import jp.metacube.fhir.pddicds.server.common.ReadWriteFile;

class PddiCdsRequestGroupTest {

  @BeforeAll
  static void setUpBeforeClass() throws Exception {}

  @AfterAll
  static void tearDownAfterClass() throws Exception {}

  @BeforeEach
  void setUp() throws Exception {}

  @AfterEach
  void tearDown() throws Exception {}

  // @Test
  // void testPddiCdsRequestGroup() {
  //  fail("まだ実装されていません");
  // }

  @Test
  void testCreate1() throws Exception {
    // 準備
    String patientId = "f101";
    ReadWriteFile rfr = new ReadWriteFile();
    PlanDefinition pd =
        (PlanDefinition) rfr.getResource("./storage/PlanDefinition-warfarin-nsaids-cds-sign.json");
    InputStream is =
        this.getClass()
            .getClassLoader()
            .getResourceAsStream("resources/cql-result-warfarin-nsaids-order-sign.json");
    String cqlResult = rfr.readFile(is, null);
    ObjectMapper mapper = ParameterInitiator.getInstance().objectMapper;
    JsonNode cresults = mapper.readTree(cqlResult);
    JsonNode cqlResultObject = cresults.get("patientResults").get(patientId);
    String hookInstance = "f24dfe5e-9480-429f-9b27-2af9c655c42a";
    String requestUrl = "http://localhost:8080/pddiCdsServer/warfarin-nsaids-cds-sign";
    Instant nowInst = Instant.now();
    Date requestDate = Date.from(nowInst);
    List<RelatedArtifact> ras = pd.getRelatedArtifact();
    PlanDefinitionActionComponent pdac = pd.getActionFirstRep().getAction().get(1);

    // 実行
    PddiCdsRequestGroup pcrg =
        new PddiCdsRequestGroup(
            hookInstance, requestUrl, patientId, requestDate, ras, cqlResultObject);
    RequestGroupInfo rginfo = pcrg.create(pdac);

    // 検証
    assertEquals("warning", rginfo.indicator);
    RequestGroup rg = rginfo.requestGroup;
    rg.getIdentifierFirstRep().getValue();
    assertEquals(rg.getIdentifierFirstRep().getValue(), hookInstance);
    assertEquals(rg.getInstantiatesUri().get(0).asStringValue(), requestUrl);
    assertEquals(rg.getAuthoredOn().toString(), requestDate.toString());
    assertEquals(rg.getSubject().getIdentifier().getValue(), patientId);
    assertEquals(rg.getIdentifierFirstRep().getValue(), hookInstance);
    List<RequestGroupActionComponent> racts = rg.getAction();
    assertEquals(racts.size(), 1);
    RequestGroupActionComponent ract = racts.get(0);
    // assertEquals(ract.getPrefix(), "");
    assertEquals(
        ract.getTitle(),
        "Potential Drug-Drug Interaction between warfarin (Warfarin Sodium 0.5 MG Oral Tablet) and NSAID (Ketorolac Tromethamine 10 MG Oral Tablet).");

    // String desc1 = ract.getDescription();
    // String desc2 =
    //    "Increased risk of bleeding. \nBleeding is a serious potential clinical consequence
    // because it can result in death, life-threatening hospitalization, and disability.
    // \nNon-steroidal anti-inflammatory drugs (NSAIDs) have antiplatelet effects which increase the
    // bleeding risk when combined with oral anticoagulants such as warfarin. The antiplatelet
    // effect of NSAIDs lasts only as long as the NSAID is present in the circulation, unlike
    // aspirin’s antiplatelet effect, which lasts for up to 2 weeks after aspirin is discontinued.
    // NSAIDs also can cause peptic ulcers and most of the evidence for increased bleeding risk with
    // NSAIDs plus warfarin is due to upper gastrointestinal bleeding (UGIB). \nunknown. \n
    // unknown.";
    // assertEquals(desc1, desc2);
    List<RequestGroupActionComponent> rracts = ract.getAction();
    assertEquals(rracts.size(), 3);

    //    assertTrue(rsc instanceof MedicationRequest);
    //    if (rsc instanceof MedicationRequest) {
    //      MedicationRequest mr = (MedicationRequest) rsc;
    //      Coding mcc = mr.getMedicationCodeableConcept().getCodingFirstRep();
    //      String mcode = mcc.getCode();
    //      String msys = mcc.getSystem();
    //      String mdisp = mcc.getDisplay();
    //      assertEquals(mcode, "313782");
    //      assertEquals(msys, "http://www.nlm.nih.gov/research/umls/rxnorm");
    //      assertEquals(mdisp, "Acetaminophen 325 MG Oral Tablet");
    //      String pid = mr.getSubject().getIdentifier().getValue();
    //      assertEquals(pid, "f101");
    //    } else {
    //      fail("Created resource is not MedicationRequest");
    //    }

    // fail("まだ実装されていません");
  }
}
