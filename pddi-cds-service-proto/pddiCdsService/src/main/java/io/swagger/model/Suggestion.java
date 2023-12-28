package io.swagger.model;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class Suggestion {

  @ApiModelProperty(required = true, value = "")
  private String label = null;

  @ApiModelProperty(value = "")
  private UUID uuid = null;

  @ApiModelProperty(value = "")
  @Valid
  private List<Action> actions = null;
  /**
   * Get label
   *
   * @return label
   */
  @JsonProperty("label")
  @NotNull
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Suggestion label(String label) {
    this.label = label;
    return this;
  }

  /**
   * Get uuid
   *
   * @return uuid
   */
  @JsonProperty("uuid")
  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public Suggestion uuid(UUID uuid) {
    this.uuid = uuid;
    return this;
  }

  /**
   * Get actions
   *
   * @return actions
   */
  @JsonProperty("actions")
  public List<Action> getActions() {
    return actions;
  }

  public void setActions(List<Action> actions) {
    this.actions = actions;
  }

  public Suggestion actions(List<Action> actions) {
    this.actions = actions;
    return this;
  }

  public Suggestion addActionsItem(Action actionsItem) {
    this.actions.add(actionsItem);
    return this;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Suggestion {\n");

    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    actions: ").append(toIndentedString(actions)).append("\n");
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
