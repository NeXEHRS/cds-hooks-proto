/** */
package jp.metacube.fhir.pddicds.server.cards;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.model.CDSResponse;
import io.swagger.model.Card;
import jp.metacube.fhir.pddicds.server.common.PddiCdsException;
import jp.metacube.fhir.pddicds.server.common.ReadWriteFile;

/** @author takaha */
class PddiCdsCardTest {

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

  @Test
  void testCreate1() throws PddiCdsException, JsonProcessingException, FileNotFoundException {
    // 準備
    ReadWriteFile rfr = new ReadWriteFile();
    IBaseResource bundle =
        rfr.getResource("./src/test/resources/Bundle-CarePlan-warfarin-nsaids-order-sign.json");

    // 実行
    PddiCdsCard pcc = new PddiCdsCard();
    ArrayList<Card> cards = pcc.createCards((Bundle) bundle);

    CDSResponse cdsResp = new CDSResponse();
    cdsResp.cards(cards);

    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    String jsonData = mapper.writeValueAsString(cdsResp);
    System.out.println(jsonData);

    // 検証
    assertEquals(4, cards.size());

    //    assertEquals("warning", rginfo.indicator);
    //    RequestGroup rg = rginfo.requestGroup;
    //    rg.getIdentifierFirstRep().getValue();
    //    assertEquals(rg.getIdentifierFirstRep().getValue(), hookInstance);
    //    assertEquals(rg.getInstantiatesUri().get(0).asStringValue(), requestUrl);
    //    assertEquals(rg.getAuthoredOn().toString(), requestDate.toString());
    //    assertEquals(rg.getSubject().getIdentifier().getValue(), patientId);
    //    assertEquals(rg.getIdentifierFirstRep().getValue(), hookInstance);
    //    List<RequestGroupActionComponent> racts = rg.getAction();
    //    assertEquals(racts.size(), 1);
    //    RequestGroupActionComponent ract = racts.get(0);
    //    // assertEquals(ract.getPrefix(), "");
    //    assertEquals(
    //        ract.getTitle(),
    //        "Potential Drug-Drug Interaction between warfarin (Warfarin Sodium 0.5 MG Oral Tablet)
    // and NSAID (Ketorolac Tromethamine 10 MG Oral Tablet).");
  }
}
