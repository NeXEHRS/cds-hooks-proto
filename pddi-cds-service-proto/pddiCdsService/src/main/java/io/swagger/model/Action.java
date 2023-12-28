package io.swagger.model;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class Action {

  @XmlType(name = "TypeEnum")
  @XmlEnum(String.class)
  public enum TypeEnum {
    @XmlEnumValue("create")
    CREATE(String.valueOf("create")),
    @XmlEnumValue("update")
    UPDATE(String.valueOf("update")),
    @XmlEnumValue("delete")
    DELETE(String.valueOf("delete"));

    private String value;

    TypeEnum(String v) {
      value = v;
    }

    public String value() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static TypeEnum fromValue(String v) {
      for (TypeEnum b : TypeEnum.values()) {
        if (String.valueOf(b.value).equals(v)) {
          return b;
        }
      }
      return null;
    }
  }

  @ApiModelProperty(required = true, value = "")
  private TypeEnum type = null;

  @ApiModelProperty(required = true, value = "")
  private String description = null;

  @ApiModelProperty(value = "")
  private Object resource = null;

  @ApiModelProperty(value = "")
  private String resourceId = null;

  /**
   * Get type
   *
   * @return type
   */
  @JsonProperty("type")
  @NotNull
  public String getType() {
    if (type == null) {
      return null;
    }
    return type.value();
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }

  public Action type(TypeEnum type) {
    this.type = type;
    return this;
  }

  /**
   * Get description
   *
   * @return description
   */
  @JsonProperty("description")
  @NotNull
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Action description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get resource
   *
   * @return resource
   */
  @JsonProperty("resource")
  public Object getResource() {
    return resource;
  }

  public void setResource(Object resource) {
    this.resource = resource;
  }

  public Action resource(Object resource) {
    this.resource = resource;
    return this;
  }

  /**
   * Get resourceId (added by Metacube)
   *
   * @return resourceId
   */
  @JsonProperty("resourceId")
  public String getResourceId() {
    return resourceId;
  }

  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
  }

  public Action resourceId(String resourceId) {
    this.resourceId = resourceId;
    return this;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Action {\n");

    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    resource: ").append(toIndentedString(resource)).append("\n");
    sb.append("    resourceId: ").append(toIndentedString(resourceId)).append("\n");
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
