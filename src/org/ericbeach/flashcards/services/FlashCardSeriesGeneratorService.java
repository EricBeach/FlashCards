package org.ericbeach.flashcards.services;

import java.util.ArrayList;
import java.util.List;

public class FlashCardSeriesGeneratorService {
  public static final int CRITERIA_NOT_SET = -1;

  public List<Long> getFlashCardIdsByCriteria(int includeSeenDays, int excludeSeenDays,
      int maxNumCardsInSeries, int percentCorrectInSeries) {
    List<Long> flashCardIds = new ArrayList<Long>();
    flashCardIds.add(10L);
    flashCardIds.add(100L);
    flashCardIds.add(90L);

    return flashCardIds;
  }
}
