/** */
package jp.metacube.fhir.pddicds.server.config;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import jp.metacube.fhir.pddicds.server.PddiCdsServicePort;

/** @author takaha */
@ApplicationPath("")
public class PddiCdsServiceApplication extends Application {

  @Override
  public Set<Class<?>> getClasses() {
    Set<Class<?>> sc = new HashSet<Class<?>>();
    sc.add(PddiCdsServicePort.class);
    return sc;
  }
}
