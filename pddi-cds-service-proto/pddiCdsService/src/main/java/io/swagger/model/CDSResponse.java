package io.swagger.model;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

public class CDSResponse {

  @ApiModelProperty(required = true, value = "")
  @Valid
  private List<Card> cards = new ArrayList<Card>();
  /**
   * Get cards
   *
   * @return cards
   */
  @JsonProperty("cards")
  @NotNull
  public List<Card> getCards() {
    return cards;
  }

  public void setCards(List<Card> cards) {
    this.cards = cards;
  }

  public CDSResponse cards(List<Card> cards) {
    this.cards = cards;
    return this;
  }

  public CDSResponse addCardsItem(Card cardsItem) {
    this.cards.add(cardsItem);
    return this;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CDSResponse {\n");

    sb.append("    cards: ").append(toIndentedString(cards)).append("\n");
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
