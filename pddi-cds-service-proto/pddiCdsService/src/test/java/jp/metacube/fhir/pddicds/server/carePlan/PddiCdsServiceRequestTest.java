/** */
package jp.metacube.fhir.pddicds.server.carePlan;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import org.hl7.fhir.r4.model.ActivityDefinition;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jp.metacube.fhir.pddicds.server.common.ReadWriteFile;

/** @author takaha */
class PddiCdsServiceRequestTest {

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
   * jp.metacube.fhir.pddicds.server.carePlan.PddiCdsServiceRequest#create(org.hl7.fhir.r4.model.ActivityDefinition)}
   * のためのテスト・メソッド。
   *
   * @throws FileNotFoundException
   */
  @Test
  void testCreate1() throws FileNotFoundException {
    // 準備
    ReadWriteFile rfr = new ReadWriteFile();
    ActivityDefinition ad =
        (ActivityDefinition) rfr.getResource("./storage/ActivityDefinition-ad201.json");
    // String patientId = "f101";

    // 実行
    PddiCdsServiceRequest pcsr = new PddiCdsServiceRequest();
    ServiceRequest sr = pcsr.create(ad);

    // 検証
    Coding scc = sr.getCode().getCodingFirstRep();
    String scode = scc.getCode();
    String ssys = scc.getSystem();
    String sdisp = scc.getDisplay();
    assertEquals(scode, "11429006");
    assertEquals(ssys, "http://snomed.info/sct");
    assertEquals(sdisp, "Consultation");
    // String pid = sr.getSubject().getIdentifier().getValue();
    // assertEquals(pid, "f101");
  }

  /**
   * {@link
   * jp.metacube.fhir.pddicds.server.carePlan.PddiCdsServiceRequest#create(org.hl7.fhir.r4.model.ActivityDefinition)}
   * のためのテスト・メソッド。
   *
   * @throws FileNotFoundException
   */
  @Test
  void testCreate2() throws FileNotFoundException {
    // 準備
    ReadWriteFile rfr = new ReadWriteFile();
    ActivityDefinition ad =
        (ActivityDefinition) rfr.getResource("./storage/ActivityDefinition-ad206.json");
    String[] acode = {"271236005", "312475002", "390963002"};
    String[] adisp = {"Serum potassium level", "Plasma magnesium level", "Plasma calcium level"};
    List<String> lcode = Arrays.asList(acode);
    List<String> ldisp = Arrays.asList(adisp);

    // 実行
    PddiCdsServiceRequest pcsr = new PddiCdsServiceRequest();
    ServiceRequest sr = pcsr.create(ad);

    // 検証
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

    // String pid = sr.getSubject().getIdentifier().getValue();
    // assertEquals(pid, "f101");
  }
}
