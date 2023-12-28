/** */
package jp.metacube.fhir.pddicds.server.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

/**
 * 設定パラメータの読み込み
 *
 * @author takaha
 */
public class PddiCdsProperties {
  private Properties prop = null;
  private String systemUUID = null;
  private String serverHome = null;
  private String cqlExecutionPath = null;
  private String cqlExecFhirPath = null;
  private String fhirSupportToolsPath = null;
  private String workFolder = null;
  private String storageFolder = null;
  private String nodejsExec = null;

  private static PddiCdsProperties pcp = new PddiCdsProperties();

  /** コンストラクタ pddi-cds.properties読み込みを実行 */
  private PddiCdsProperties() {
    this.readProperties();
  }

  private void readProperties() {
    InputStream is = null;
    try {
      prop = new Properties();
      is = this.getClass().getClassLoader().getResourceAsStream("pddi-cds.properties");
      prop.load(is);
      System.out.println("pddi-cds.properties読み込みに成功しました");
    } catch (Exception e) {
      e.printStackTrace();
      prop = null;
      System.out.println("pddi-cds.properties読み込みに失敗しました");
    } finally {
      try {
        is.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    // ServerUUID読み込み
    systemUUID = prop.getProperty("pddi-cds.server.id");
    if (systemUUID == null) {
      systemUUID = "urn:uuid:" + UUID.randomUUID().toString();
      System.out.println("pddi-cdsサーバのシステムUUIDが設定されていなかったので以下のように設定します:");
      System.out.println("systemUUID = " + systemUUID);
    }
    // Server Home
    this.serverHome = prop.getProperty("pddi.cds.server.home");

    // cql-execution home folder
    this.cqlExecutionPath = prop.getProperty("cql.execution.home");
    // cql-exec-fhir home folder
    this.cqlExecFhirPath = prop.getProperty("cql.exec.fhir.home");
    // pddi-cds-fhir-support-tools home folder
    this.fhirSupportToolsPath = prop.getProperty("pddi.cds.fhir.support.tools.home");
    // work folder
    this.workFolder = prop.getProperty("pddi.cds.server.works.folder");
    // storage folder
    this.storageFolder = prop.getProperty("pddi.cds.server.storage");
    // Node.js home
    this.nodejsExec = prop.getProperty("nodejs.exec.path");
  }

  /**
   * インスタンスを返す
   *
   * @return インスタンス
   */
  public static PddiCdsProperties getInstance() {
    return pcp;
  }

  /**
   * 指定のパラメータを返す
   *
   * @param key キー
   * @return パラメータ
   */
  public String getProperty(String key) {
    return this.prop.getProperty(key);
  }

  /**
   * 本システムのUUIDを返す
   *
   * @return 本システムのUUID
   */
  public String getSystemUUID() {
    return systemUUID;
  }

  public String getServerHome() {
    return this.serverHome;
  }

  public String getCqlExecutionPath() {
    return this.cqlExecutionPath;
  }

  public String getCqlExecFhirPath() {
    return this.cqlExecFhirPath;
  }

  public String getFhirSupportToolsPath() {
    return this.fhirSupportToolsPath;
  }

  public String getStorageFolderPath() {
    return this.storageFolder;
  }

  public String getWorkFolderPath() {
    return this.workFolder;
  }

  public String getNodejsExecPath() {
    return this.nodejsExec;
  }
}
