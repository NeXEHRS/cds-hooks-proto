package io.swagger.model;

import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class CDSRequest {

  @ApiModelProperty(required = true, value = "The hook that triggered this CDS Service call.")
  /** The hook that triggered this CDS Service call. */
  private String hook = null;

  @ApiModelProperty(required = true, value = "")
  private UUID hookInstance = null;

  @ApiModelProperty(value = "")
  private String fhirServer = null;

  @ApiModelProperty(value = "")
  @Valid
  private FHIRAuthorization fhirAuthorization = null;

  @ApiModelProperty(required = true, value = "")
  private Object context = null;

  @ApiModelProperty(value = "")
  private Object prefetch = null;
  /**
   * The hook that triggered this CDS Service call.
   *
   * @return hook
   */
  @JsonProperty("hook")
  @NotNull
  public String getHook() {
    return hook;
  }

  public void setHook(String hook) {
    this.hook = hook;
  }

  public CDSRequest hook(String hook) {
    this.hook = hook;
    return this;
  }

  /**
   * Get hookInstance
   *
   * @return hookInstance
   */
  @JsonProperty("hookInstance")
  @NotNull
  public UUID getHookInstance() {
    return hookInstance;
  }

  public void setHookInstance(UUID hookInstance) {
    this.hookInstance = hookInstance;
  }

  public CDSRequest hookInstance(UUID hookInstance) {
    this.hookInstance = hookInstance;
    return this;
  }

  /**
   * Get fhirServer
   *
   * @return fhirServer
   */
  @JsonProperty("fhirServer")
  public String getFhirServer() {
    return fhirServer;
  }

  public void setFhirServer(String fhirServer) {
    this.fhirServer = fhirServer;
  }

  public CDSRequest fhirServer(String fhirServer) {
    this.fhirServer = fhirServer;
    return this;
  }

  /**
   * Get fhirAuthorization
   *
   * @return fhirAuthorization
   */
  @JsonProperty("fhirAuthorization")
  public FHIRAuthorization getFhirAuthorization() {
    return fhirAuthorization;
  }

  public void setFhirAuthorization(FHIRAuthorization fhirAuthorization) {
    this.fhirAuthorization = fhirAuthorization;
  }

  public CDSRequest fhirAuthorization(FHIRAuthorization fhirAuthorization) {
    this.fhirAuthorization = fhirAuthorization;
    return this;
  }

  /**
   * Get context
   *
   * @return context
   */
  @JsonProperty("context")
  @NotNull
  public Object getContext() {
    return context;
  }

  public void setContext(Object context) {
    this.context = context;
  }

  public CDSRequest context(Object context) {
    this.context = context;
    return this;
  }

  /**
   * Get prefetch
   *
   * @return prefetch
   */
  @JsonProperty("prefetch")
  public Object getPrefetch() {
    return prefetch;
  }

  public void setPrefetch(Object prefetch) {
    this.prefetch = prefetch;
  }

  public CDSRequest prefetch(Object prefetch) {
    this.prefetch = prefetch;
    return this;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CDSRequest {\n");

    sb.append("    hook: ").append(toIndentedString(hook)).append("\n");
    sb.append("    hookInstance: ").append(toIndentedString(hookInstance)).append("\n");
    sb.append("    fhirServer: ").append(toIndentedString(fhirServer)).append("\n");
    sb.append("    fhirAuthorization: ").append(toIndentedString(fhirAuthorization)).append("\n");
    sb.append("    context: ").append(toIndentedString(context)).append("\n");
    sb.append("    prefetch: ").append(toIndentedString(prefetch)).append("\n");
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
