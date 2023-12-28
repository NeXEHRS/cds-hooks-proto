/** */
package jp.metacube.fhir.pddicds.server.crinicalReasoning;

import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.Date;
import org.hl7.fhir.r4.model.Bundle;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jp.metacube.fhir.pddicds.server.common.PddiCdsException;
import jp.metacube.fhir.pddicds.server.common.ReadWriteFile;
import jp.metacube.fhir.pddicds.server.preparation.CqlPreprocessing;

/** @author takaha */
class CqlAnalysisTest {

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
   * jp.metacube.fhir.pddicds.server.crinicalReasoning.CqlAnalysis#exec(jp.metacube.fhir.pddicds.server.preparation.CqlPreprocessing)}
   * のためのテスト・メソッド。
   *
   * @throws PddiCdsException
   * @throws FileNotFoundException
   */
  @Test
  void testExec1() throws PddiCdsException, FileNotFoundException {
    // 準備
    CqlPreprocessing cpp = new CqlPreprocessing();
    cpp.hookInstance = "f24dfe5e-9480-429f-9b27-2af9c655c42a";
    cpp.requestUrl = "http://localhost:8080/pddiCdsServer/warfarin-nsaids-cds-sign";
    cpp.id = "warfarin-nsaids-cds-sign";
    Instant nowInst = Instant.now();
    cpp.requestDate = Date.from(nowInst);
    cpp.patientId = "f101";

    ReadWriteFile rfr = new ReadWriteFile();
    Bundle bundle =
        (Bundle)
            rfr.getResource(
                "./src/test/resources/Bundle-warfarin-nsaids-order-sign-no-coordination.json");
    cpp.bundle = bundle;
    // ObjectMapper mapper = ParameterInitiator.getInstance().objectMapper;
    // JsonNode cresults = mapper.readTree(cqlResult);
    // JsonNode cqlResultObject = cresults.get("patientResults").get(patientId);

    // 実行
    CqlAnalysis cas = new CqlAnalysis(cpp);
    String result = cas.exec();

    System.out.println(result);
    // ObjectMapper mapper = ParameterInitiator.getInstance().objectMapper;
    // mapper.

    // 検証
    // IBaseResource -> String
    //    IParser ip = FhirParser.getInstance().getJSONParser();
    //    String rscStr = ip.encodeResourceToString(bundle);
    //    System.out.println(rscStr);
    //
    //    // bundleからCarePlan、RequestGroup抽出
    //    List<BundleEntryComponent> entries = bundle.getEntry();
    //    ArrayList<CarePlan> cps = new ArrayList<CarePlan>();
    //    ArrayList<RequestGroup> rgs = new ArrayList<RequestGroup>();
    //    ArrayList<MedicationRequest> mrs = new ArrayList<MedicationRequest>();
    //    ArrayList<ServiceRequest> srs = new ArrayList<ServiceRequest>();
    //    ArrayList<Resource> others = new ArrayList<Resource>();
    //    for (int i = 0; i < entries.size(); i++) {
    //      BundleEntryComponent entry = entries.get(i);
    //      Resource ersc = entry.getResource();
    //      if (ersc instanceof CarePlan) {
    //        cps.add((CarePlan) ersc);
    //      } else if (ersc instanceof RequestGroup) {
    //        rgs.add((RequestGroup) ersc);
    //      } else if (ersc instanceof MedicationRequest) {
    //        mrs.add((MedicationRequest) ersc);
    //      } else if (ersc instanceof ServiceRequest) {
    //        srs.add((ServiceRequest) ersc);
    //      } else {
    //        others.add(ersc);
    //      }
    //    }
    //    assertEquals(1, cps.size());
    //    assertEquals(4, rgs.size());
    //    assertEquals(2, mrs.size());
    //    assertEquals(0, srs.size());
    //    assertEquals(0, others.size());
  }
}
