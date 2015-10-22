package org.ericbeach.flashcards.models;

public class FlashCardInteractionSummary {
  private final long flashCardId;
  private int numberOfTimesAttempted;
  private int numberOfTimesCorrect;

  public FlashCardInteractionSummary(long flashCardId) {
    this.flashCardId = flashCardId;
  }

  public int getNumberOfTimesAttempted() {
    return numberOfTimesAttempted;
  }

  public int getNumberOfTimesCorrect() {
    return numberOfTimesCorrect;
  }

  public long getFlashCardId() {
    return flashCardId;
  }

  public void incrementAttemptedCount() {
    numberOfTimesAttempted++;
  }

  public void incrementAttemptedAndCorrectCount() {
    incrementAttemptedCount();
    numberOfTimesCorrect++;
  }

  public String toJson() {
    String json = "{"
        + "  \"numberOfTimesAttempted\" : " + numberOfTimesAttempted + ","
        + "  \"numberOfTimesCorrect\" : " + numberOfTimesCorrect + ","
        + "  \"flashCardId\" : " + flashCardId
        + "}";
    return json;
  }
}
