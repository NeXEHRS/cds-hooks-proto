package io.swagger.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class CDSService {

  @ApiModelProperty(
      required = true,
      value = "short id for this service, unique with the CDS Provider (will be used in URL paths)")
  /** short id for this service, unique with the CDS Provider (will be used in URL paths) */
  private String id = null;

  @ApiModelProperty(required = true, value = "The hook this service should be invoked on.")
  /** The hook this service should be invoked on. */
  private String hook = null;

  @ApiModelProperty(
      value = "Human-readable name for the CDS Service (e.g. \"CMS Drug Pricing Service\")")
  /** Human-readable name for the CDS Service (e.g. \"CMS Drug Pricing Service\") */
  private String title = null;

  @ApiModelProperty(required = true, value = "Longer-form description of what the service offers")
  /** Longer-form description of what the service offers */
  private String description = null;

  @ApiModelProperty(value = "")
  @Valid
  private Prefetch prefetch = null;
  /**
   * short id for this service, unique with the CDS Provider (will be used in URL paths)
   *
   * @return id
   */
  @JsonProperty("id")
  @NotNull
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public CDSService id(String id) {
    this.id = id;
    return this;
  }

  /**
   * The hook this service should be invoked on.
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

  public CDSService hook(String hook) {
    this.hook = hook;
    return this;
  }

  /**
   * Human-readable name for the CDS Service (e.g. \&quot;CMS Drug Pricing Service\&quot;)
   *
   * @return title
   */
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public CDSService title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Longer-form description of what the service offers
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

  public CDSService description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get prefetch
   *
   * @return prefetch
   */
  @JsonProperty("prefetch")
  public Prefetch getPrefetch() {
    return prefetch;
  }

  public void setPrefetch(Prefetch prefetch) {
    this.prefetch = prefetch;
  }

  public CDSService prefetch(Prefetch prefetch) {
    this.prefetch = prefetch;
    return this;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CDSService {\n");

    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    hook: ").append(toIndentedString(hook)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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
