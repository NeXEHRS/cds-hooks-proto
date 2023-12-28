/** */
package jp.metacube.fhir.pddicds.server.carePlan;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.InputStream;
import java.util.List;
import org.hl7.fhir.r4.model.PlanDefinition;
import org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionComponent;
import org.hl7.fhir.r4.model.PlanDefinition.PlanDefinitionActionConditionComponent;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jp.metacube.fhir.pddicds.server.common.ReadWriteFile;

/** @author takaha */
class PddiCdsResourceTest {

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
   * jp.metacube.fhir.pddicds.server.carePlan.PddiCdsResource#getActionCondition(java.util.List)}
   * のためのテスト・メソッド。
   *
   * @throws Exception
   */
  @Test
  void testGetActionCondition() throws Exception {
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

    PlanDefinitionActionComponent act0 = pd.getActionFirstRep();
    // Inclusion Criteria
    List<PlanDefinitionActionConditionComponent> cond0 = act0.getCondition();
    // Is context medication topical diclofenac
    List<PlanDefinitionActionComponent> act1 = act0.getAction();
    List<PlanDefinitionActionConditionComponent> cond10 = act1.get(0).getCondition();
    // Is not context medication topical diclofenac
    List<PlanDefinitionActionConditionComponent> cond11 = act1.get(1).getCondition();

    // 実行
    boolean bb0, bb10, bb11;
    PddiCdsResource pcr = new PddiCdsResource(patientId, cqlResult);
    // Inclusion Criteria
    bb0 = pcr.getActionCondition(cond0);
    // Is context medication topical diclofenac
    bb10 = pcr.getActionCondition(cond10);
    // Is not context medication topical diclofenac
    bb11 = pcr.getActionCondition(cond11);

    // 検証
    assertTrue(bb0);
    assertFalse(bb10);
    assertTrue(bb11);
  }
}
