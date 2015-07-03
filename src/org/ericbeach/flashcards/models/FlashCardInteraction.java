package org.ericbeach.flashcards.models;

public class FlashCardInteraction {
  private final long flashCardId;
  private final String userEmailAddress;
  private final boolean wasAnswerCorrect;
  private final long timestamp;

  public FlashCardInteraction(long flashCardId, String userEmailAddress, boolean wasAnswerCorrect,
      long timestamp) {
    this.flashCardId = flashCardId;
    this.userEmailAddress = userEmailAddress;
    this.wasAnswerCorrect = wasAnswerCorrect;
    this.timestamp = timestamp;
  }

  public long getFlashCardId() {
    return flashCardId;
  }

  public String getUserEmailAddress() {
    return userEmailAddress;
  }

  public boolean getWasAnswerCorrect() {
    return wasAnswerCorrect;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public String toJson() {
    return FlashCardInteraction.toJson(flashCardId, userEmailAddress, wasAnswerCorrect, timestamp);
  }

  public static String toJson(long flashCardId, String userEmailAddress,
      boolean wasAnswerCorrect, long timestamp) {
    String json = "{"
        + " \"flashCardId\" : \"" + flashCardId + "\","
        + " \"userEmailAddress\" : \"" + userEmailAddress + "\","
        + " \"wasAnswerCorrect\" : " + wasAnswerCorrect + ","
        + " \"timestamp\" : \"" + timestamp + "\""
        + "}";
    return json;
  }
}
