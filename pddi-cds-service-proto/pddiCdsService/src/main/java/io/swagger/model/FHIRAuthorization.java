package io.swagger.model;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class FHIRAuthorization {

  @ApiModelProperty(required = true, value = "")
  private String accessToken = null;

  @XmlType(name = "TokenTypeEnum")
  @XmlEnum(String.class)
  public enum TokenTypeEnum {
    @XmlEnumValue("Bearer")
    BEARER(String.valueOf("Bearer"));

    private String value;

    TokenTypeEnum(String v) {
      value = v;
    }

    public String value() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static TokenTypeEnum fromValue(String v) {
      for (TokenTypeEnum b : TokenTypeEnum.values()) {
        if (String.valueOf(b.value).equals(v)) {
          return b;
        }
      }
      return null;
    }
  }

  @ApiModelProperty(required = true, value = "")
  private TokenTypeEnum tokenType = null;

  @ApiModelProperty(required = true, value = "")
  private Integer expiresIn = null;

  @ApiModelProperty(required = true, value = "")
  private String scope = null;

  @ApiModelProperty(required = true, value = "")
  private String subject = null;
  /**
   * Get accessToken
   *
   * @return accessToken
   */
  @JsonProperty("access_token")
  @NotNull
  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public FHIRAuthorization accessToken(String accessToken) {
    this.accessToken = accessToken;
    return this;
  }

  /**
   * Get tokenType
   *
   * @return tokenType
   */
  @JsonProperty("token_type")
  @NotNull
  public String getTokenType() {
    if (tokenType == null) {
      return null;
    }
    return tokenType.value();
  }

  public void setTokenType(TokenTypeEnum tokenType) {
    this.tokenType = tokenType;
  }

  public FHIRAuthorization tokenType(TokenTypeEnum tokenType) {
    this.tokenType = tokenType;
    return this;
  }

  /**
   * Get expiresIn
   *
   * @return expiresIn
   */
  @JsonProperty("expires_in")
  @NotNull
  public Integer getExpiresIn() {
    return expiresIn;
  }

  public void setExpiresIn(Integer expiresIn) {
    this.expiresIn = expiresIn;
  }

  public FHIRAuthorization expiresIn(Integer expiresIn) {
    this.expiresIn = expiresIn;
    return this;
  }

  /**
   * Get scope
   *
   * @return scope
   */
  @JsonProperty("scope")
  @NotNull
  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public FHIRAuthorization scope(String scope) {
    this.scope = scope;
    return this;
  }

  /**
   * Get subject
   *
   * @return subject
   */
  @JsonProperty("subject")
  @NotNull
  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public FHIRAuthorization subject(String subject) {
    this.subject = subject;
    return this;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FHIRAuthorization {\n");

    sb.append("    accessToken: ").append(toIndentedString(accessToken)).append("\n");
    sb.append("    tokenType: ").append(toIndentedString(tokenType)).append("\n");
    sb.append("    expiresIn: ").append(toIndentedString(expiresIn)).append("\n");
    sb.append("    scope: ").append(toIndentedString(scope)).append("\n");
    sb.append("    subject: ").append(toIndentedString(subject)).append("\n");
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
