package jp.metacube.fhir.pddicds.server.carePlan;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import org.hl7.fhir.r4.model.ActivityDefinition;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jp.metacube.fhir.pddicds.server.common.PddiCdsException;
import jp.metacube.fhir.pddicds.server.common.ReadWriteFile;

class PddiCdsActivityDefinitionTest {

  @BeforeAll
  static void setUpBeforeClass() throws Exception {}

  @AfterAll
  static void tearDownAfterClass() throws Exception {}

  @BeforeEach
  void setUp() throws Exception {}

  @AfterEach
  void tearDown() throws Exception {}

  @Test
  void testExchangeResource1() throws PddiCdsException, FileNotFoundException {
    // 準備
    ReadWriteFile rfr = new ReadWriteFile();
    ActivityDefinition ad =
        (ActivityDefinition) rfr.getResource("./storage/ActivityDefinition-ad101.json");
    String patientId = "f101";

    // 実行
    PddiCdsActivityDefinition pcad = new PddiCdsActivityDefinition();
    Resource rsc = pcad.exchangeResource(patientId, ad);

    // 検証
    assertTrue(rsc instanceof MedicationRequest);
    if (rsc instanceof MedicationRequest) {
      MedicationRequest mr = (MedicationRequest) rsc;
      Coding mcc = mr.getMedicationCodeableConcept().getCodingFirstRep();
      String mcode = mcc.getCode();
      String msys = mcc.getSystem();
      String mdisp = mcc.getDisplay();
      assertEquals(mcode, "313782");
      assertEquals(msys, "http://www.nlm.nih.gov/research/umls/rxnorm");
      assertEquals(mdisp, "Acetaminophen 325 MG Oral Tablet");
      String pid = mr.getSubject().getIdentifier().getValue();
      assertEquals(pid, "f101");
    } else {
      fail("Created resource is not MedicationRequest");
    }
  }

  @Test
  void testExchangeResource2() throws PddiCdsException, FileNotFoundException {
    // 準備
    ReadWriteFile rfr = new ReadWriteFile();
    String patientId = "f101";
    ActivityDefinition ad =
        (ActivityDefinition) rfr.getResource("./storage/ActivityDefinition-ad206.json");
    String[] acode = {"271236005", "312475002", "390963002"};
    String[] adisp = {"Serum potassium level", "Plasma magnesium level", "Plasma calcium level"};
    List<String> lcode = Arrays.asList(acode);
    List<String> ldisp = Arrays.asList(adisp);

    // 実行
    PddiCdsActivityDefinition pcad = new PddiCdsActivityDefinition();
    Resource rsc = pcad.exchangeResource(patientId, ad);

    // 検証
    assertTrue(rsc instanceof ServiceRequest);
    if (rsc instanceof ServiceRequest) {
      ServiceRequest sr = (ServiceRequest) rsc;
      List<Coding> sccs = sr.getCode().getCoding();
      assertEquals(sccs.size(), 3);
      for (int i = 0; i < sccs.size(); i++) {
        Coding scc = sccs.get(i);
        String scode = scc.getCode();
        String ssys = scc.getSystem();
        String sdisp = scc.getDisplay();

        assertThat(lcode, hasItem(scode));
        assertEquals("http://snomed.info/sct", ssys);
        assertThat(ldisp, hasItem(sdisp));
      }
    } else {
      fail("Created Resource is not ServiceRequest.");
    }
  }
}
