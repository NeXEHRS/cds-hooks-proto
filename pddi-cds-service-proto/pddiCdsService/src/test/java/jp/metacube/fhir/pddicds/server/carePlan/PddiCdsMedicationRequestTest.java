/** */
package jp.metacube.fhir.pddicds.server.carePlan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.FileNotFoundException;
import org.hl7.fhir.r4.model.ActivityDefinition;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jp.metacube.fhir.pddicds.server.common.PddiCdsException;
import jp.metacube.fhir.pddicds.server.common.ReadWriteFile;

/**
 * PddiCdsMedicationRequestテスト
 *
 * @author takaha
 */
class PddiCdsMedicationRequestTest {

  /** @throws java.lang.Exception */
  @BeforeAll
  static void setUpBeforeClass() throws Exception {}

  /** @throws java.lang.Exception */
  @AfterAll
  static void tearDownAfterClass() throws Exception {}

  /** @throws java.lang.Exception */
  @BeforeEach
  void setUp() throws Exception {}

  /** @throws java.lang.Exception */
  @AfterEach
  void tearDown() throws Exception {}

  /**
   * {@link
   * jp.metacube.fhir.pddicds.server.carePlan.PddiCdsMedicationRequest#create(java.lang.String,
   * org.hl7.fhir.r4.model.ActivityDefinition)} のためのテスト・メソッド。
   *
   * @throws PddiCdsException
   * @throws FileNotFoundException
   */
  @Test
  void testCreate1() throws PddiCdsException, FileNotFoundException {
    // 準備
    ReadWriteFile rfr = new ReadWriteFile();
    ActivityDefinition ad =
        (ActivityDefinition) rfr.getResource("./storage/ActivityDefinition-ad101.json");
    String patientId = "f101";

    // 実行
    PddiCdsMedicationRequest pcmr = new PddiCdsMedicationRequest();
    MedicationRequest mr = pcmr.create(patientId, ad);

    // 検証
    Coding mcc = mr.getMedicationCodeableConcept().getCodingFirstRep();
    String mcode = mcc.getCode();
    String msys = mcc.getSystem();
    String mdisp = mcc.getDisplay();
    assertEquals(mcode, "313782");
    assertEquals(msys, "http://www.nlm.nih.gov/research/umls/rxnorm");
    assertEquals(mdisp, "Acetaminophen 325 MG Oral Tablet");
    String pid = mr.getSubject().getIdentifier().getValue();
    assertEquals(pid, "f101");
  }
}
