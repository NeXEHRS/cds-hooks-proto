package io.swagger.model;

import java.util.List;
import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class CDSServiceInformation {

  @ApiModelProperty(value = "")
  @Valid
  private List<CDSService> services = null;
  /**
   * Get services
   *
   * @return services
   */
  @JsonProperty("services")
  public List<CDSService> getServices() {
    return services;
  }

  public void setServices(List<CDSService> services) {
    this.services = services;
  }

  public CDSServiceInformation services(List<CDSService> services) {
    this.services = services;
    return this;
  }

  public CDSServiceInformation addServicesItem(CDSService servicesItem) {
    this.services.add(servicesItem);
    return this;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CDSServiceInformation {\n");

    sb.append("    services: ").append(toIndentedString(services)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private static String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
