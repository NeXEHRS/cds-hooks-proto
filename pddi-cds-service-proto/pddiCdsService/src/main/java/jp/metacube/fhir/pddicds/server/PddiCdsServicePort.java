/** */
package jp.metacube.fhir.pddicds.server;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/** @author takaha */
public class PddiCdsServicePort {

  /** */
  // public PddiCdsServicePort() { }
  @POST
  @Path("/{id}")
  public Response analysis(
      @Context HttpHeaders headers,
      @Context HttpServletRequest request,
      @PathParam("id") String id,
      String body) {

    ServiceDriver sd = new ServiceDriver();
    Response resp = sd.driver(id, body, request);
    return resp;
  }
}
