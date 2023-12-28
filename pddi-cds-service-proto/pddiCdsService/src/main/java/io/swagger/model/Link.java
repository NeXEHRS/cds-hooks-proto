package io.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class Link {

  @ApiModelProperty(value = "")
  private String label = null;

  @ApiModelProperty(value = "")
  private String url = null;

  @ApiModelProperty(value = "")
  private String type = null;

  @ApiModelProperty(value = "")
  private String appContext = null;
  /**
   * Get label
   *
   * @return label
   */
  @JsonProperty("label")
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Link label(String label) {
    this.label = label;
    return this;
  }

  /**
   * Get url
   *
   * @return url
   */
  @JsonProperty("url")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Link url(String url) {
    this.url = url;
    return this;
  }

  /**
   * Get type
   *
   * @return type
   */
  @JsonProperty("type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Link type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get appContext
   *
   * @return appContext
   */
  @JsonProperty("appContext")
  public String getAppContext() {
    return appContext;
  }

  public void setAppContext(String appContext) {
    this.appContext = appContext;
  }

  public Link appContext(String appContext) {
    this.appContext = appContext;
    return this;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Link {\n");

    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    appContext: ").append(toIndentedString(appContext)).append("\n");
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
