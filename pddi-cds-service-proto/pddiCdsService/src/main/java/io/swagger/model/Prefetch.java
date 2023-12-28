package io.swagger.model;

import java.util.HashMap;
import io.swagger.annotations.ApiModel;

/** queries that the CDS Service would like the CDS Client to execute before every call */
@ApiModel(
    description =
        "queries that the CDS Service would like the CDS Client to execute before every call")
public class Prefetch extends HashMap<String, String> {

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Prefetch {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
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
