package io.swagger.model;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class Card {

  @ApiModelProperty(required = true, value = "")
  private String summary = null;

  @ApiModelProperty(value = "")
  private String detail = null;

  @XmlType(name = "IndicatorEnum")
  @XmlEnum(String.class)
  public enum IndicatorEnum {
    @XmlEnumValue("info")
    INFO(String.valueOf("info")),
    @XmlEnumValue("warning")
    WARNING(String.valueOf("warning")),
    @XmlEnumValue("critical")
    CRITICAL(String.valueOf("critical"));

    private String value;

    IndicatorEnum(String v) {
      value = v;
    }

    public String value() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static IndicatorEnum fromValue(String v) {
      for (IndicatorEnum b : IndicatorEnum.values()) {
        if (String.valueOf(b.value).equals(v)) {
          return b;
        }
      }
      return null;
    }
  }

  @ApiModelProperty(required = true, value = "")
  private IndicatorEnum indicator = null;

  @ApiModelProperty(required = true, value = "")
  @Valid
  private Source source = null;

  @ApiModelProperty(value = "")
  @Valid
  private List<Suggestion> suggestions = null;

  @XmlType(name = "SelectionBehaviorEnum")
  @XmlEnum(String.class)
  public enum SelectionBehaviorEnum {
    @XmlEnumValue("at-most-one")
    AT_MOST_ONE(String.valueOf("at-most-one")),
    @XmlEnumValue("any")
    ANY(String.valueOf("any")),
    @XmlEnumValue("all")
    ALL(String.valueOf("all")),
    @XmlEnumValue("all-or-none")
    ALL_OR_NONE(String.valueOf("all-or-none")),
    @XmlEnumValue("exactly-one")
    EXACTLY_ONE(String.valueOf("exactly-one")),
    @XmlEnumValue("one-or-more")
    ONE_OR_MORE(String.valueOf("one-or-more"));

    private String value;

    SelectionBehaviorEnum(String v) {
      value = v;
    }

    public String value() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static SelectionBehaviorEnum fromValue(String v) {
      for (SelectionBehaviorEnum b : SelectionBehaviorEnum.values()) {
        if (String.valueOf(b.value).equals(v)) {
          return b;
        }
      }
      return null;
    }
  }

  @ApiModelProperty(value = "")
  private SelectionBehaviorEnum selectionBehavior = null;

  @ApiModelProperty(value = "")
  @Valid
  private List<Link> links = null;
  /**
   * Get summary
   *
   * @return summary
   */
  @JsonProperty("summary")
  @NotNull
  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public Card summary(String summary) {
    this.summary = summary;
    return this;
  }

  /**
   * Get detail
   *
   * @return detail
   */
  @JsonProperty("detail")
  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  public Card detail(String detail) {
    this.detail = detail;
    return this;
  }

  /**
   * Get indicator
   *
   * @return indicator
   */
  @JsonProperty("indicator")
  @NotNull
  public String getIndicator() {
    if (indicator == null) {
      return null;
    }
    return indicator.value();
  }

  public void setIndicator(IndicatorEnum indicator) {
    this.indicator = indicator;
  }

  public Card indicator(IndicatorEnum indicator) {
    this.indicator = indicator;
    return this;
  }

  /**
   * Get source
   *
   * @return source
   */
  @JsonProperty("source")
  @NotNull
  public Source getSource() {
    return source;
  }

  public void setSource(Source source) {
    this.source = source;
  }

  public Card source(Source source) {
    this.source = source;
    return this;
  }

  /**
   * Get suggestions
   *
   * @return suggestions
   */
  @JsonProperty("suggestions")
  public List<Suggestion> getSuggestions() {
    return suggestions;
  }

  public void setSuggestions(List<Suggestion> suggestions) {
    this.suggestions = suggestions;
  }

  public Card suggestions(List<Suggestion> suggestions) {
    this.suggestions = suggestions;
    return this;
  }

  public Card addSuggestionsItem(Suggestion suggestionsItem) {
    this.suggestions.add(suggestionsItem);
    return this;
  }

  /**
   * Get selectionBehavior
   *
   * @return selectionBehavior
   */
  @JsonProperty("selectionBehavior")
  public String getSelectionBehavior() {
    if (selectionBehavior == null) {
      return null;
    }
    return selectionBehavior.value();
  }

  public void setSelectionBehavior(SelectionBehaviorEnum selectionBehavior) {
    this.selectionBehavior = selectionBehavior;
  }

  public Card selectionBehavior(SelectionBehaviorEnum selectionBehavior) {
    this.selectionBehavior = selectionBehavior;
    return this;
  }

  /**
   * Get links
   *
   * @return links
   */
  @JsonProperty("links")
  public List<Link> getLinks() {
    return links;
  }

  public void setLinks(List<Link> links) {
    this.links = links;
  }

  public Card links(List<Link> links) {
    this.links = links;
    return this;
  }

  public Card addLinksItem(Link linksItem) {
    this.links.add(linksItem);
    return this;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Card {\n");

    sb.append("    summary: ").append(toIndentedString(summary)).append("\n");
    sb.append("    detail: ").append(toIndentedString(detail)).append("\n");
    sb.append("    indicator: ").append(toIndentedString(indicator)).append("\n");
    sb.append("    source: ").append(toIndentedString(source)).append("\n");
    sb.append("    suggestions: ").append(toIndentedString(suggestions)).append("\n");
    sb.append("    selectionBehavior: ").append(toIndentedString(selectionBehavior)).append("\n");
    sb.append("    links: ").append(toIndentedString(links)).append("\n");
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
