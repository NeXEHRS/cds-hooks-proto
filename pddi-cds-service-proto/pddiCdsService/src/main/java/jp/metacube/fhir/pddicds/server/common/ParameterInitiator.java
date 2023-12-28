/** */
package jp.metacube.fhir.pddicds.server.common;

import java.util.Properties;
import org.apache.velocity.app.Velocity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.metacube.fhir.pddicds.server.config.PddiCdsProperties;

/**
 * PDDI-CDSサーバを駆動するうえで必要となる各種パラメータの初期化を行う
 *
 * @author takaha
 */
public class ParameterInitiator {
  public ObjectMapper objectMapper = null;
  private static ParameterInitiator pi = new ParameterInitiator();

  /** コンストラクタ */
  private ParameterInitiator() {
    // ObjectMapper
    objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

    // Velocity
    // Velocity初期設定
    Properties p = new Properties();
    p.setProperty(Velocity.RESOURCE_LOADER, "file");
    // URL vmurl = this.getClass().getClassLoader().getResource("resources/js/");
    // String vmbase = vmurl.getPath();
    String serverHome = PddiCdsProperties.getInstance().getServerHome();
    String vmbase = serverHome + "/src/main/java/resources/js";
    p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, vmbase);
    Velocity.init(p);
  }

  /**
   * インスタンスを返す
   *
   * @return ParameterInitiatorインスタンス
   */
  public static ParameterInitiator getInstance() {
    if (pi == null) {
      pi = new ParameterInitiator();
    }
    return pi;
  }
}
