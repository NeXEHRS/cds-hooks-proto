/** */
package jp.metacube.fhir.pddicds.server.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import jp.metacube.fhir.pddicds.server.common.FhirParser;
import jp.metacube.fhir.pddicds.server.common.ParameterInitiator;

/** @author takaha */
public class PddiCdsServiceListener implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    try {
      PddiCdsProperties.getInstance();
      ParameterInitiator.getInstance();
      FhirParser.getInstance();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {}
}
