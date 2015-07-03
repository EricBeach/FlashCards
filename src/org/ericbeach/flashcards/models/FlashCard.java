package org.ericbeach.flashcards.models;

import java.util.List;

public class FlashCard {
  private final String frontSideContent;
  private final String frontSideLanguage;
  private final String backSideContent;
  private final String backSideDetails;
  private final String backSideLanguage;
  private long flashCardId;
  private List<Label> labels;

  public FlashCard(String frontSideContent, String frontSideLanguage,
      String backSideContent, String backSideDetails, String backSideLanguage) {
    this.frontSideContent = frontSideContent;
    this.frontSideLanguage = frontSideLanguage;
    this.backSideContent = backSideContent;
    this.backSideDetails = backSideDetails;
    this.backSideLanguage = backSideLanguage;
  }

  public FlashCard(String frontSideContent, String frontSideLanguage,
      String backSideContent, String backSideDetails, String backSideLanguage, long id) {
    this.frontSideContent = frontSideContent;
    this.frontSideLanguage = frontSideLanguage;
    this.backSideContent = backSideContent;
    this.backSideDetails = backSideDetails;
    this.backSideLanguage = backSideLanguage;
    this.flashCardId = id;
  }

  public void setLabels(List<Label> labels) {
    this.labels = labels;
  }

  public List<Label> getLabels() {
    return labels;
  }

  public String getFrontSideContent() {
    return this.frontSideContent;
  }

  public String getFrontSideLanguage() {
    return this.frontSideLanguage;
  }

  public String getBackSideContent() {
    return this.backSideContent;
  }

  public String getBackSideDetails() {
    return this.backSideDetails;
  }

  public String getBackSideLanguage() {
    return this.backSideLanguage;
  }

  public long getFlashCardId() {
    return flashCardId;
  }

  public void setFlashCardId(long id) {
    this.flashCardId = id;
  }

  public String toJson() {
    return FlashCard.toJson(frontSideContent, frontSideLanguage,
        backSideContent, backSideDetails, backSideLanguage, flashCardId);
  }

  public static String toJson(String frontSideContent, String frontSideLanguage,
      String backSideContent, String backSideDetails, String backSideLanguage, long id) {
    String json = "{"
        + " \"frontSideContent\" : \"" + frontSideContent + "\","
        + " \"frontSideLanguage\" : \"" + frontSideLanguage + "\","
        + " \"backSideContent\" : \"" + backSideContent + "\","
        + " \"backSideDetails\" : \"" + backSideDetails + "\","
        + " \"backSideLanguage\" : \"" + backSideLanguage + "\","
        + " \"flashCardId\" : \"" + id + "\""
        + "}";
    return json;
  }
}
