package org.ericbeach.flashcards.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.ericbeach.flashcards.datastore.FlashCardDatastoreHelper;
import org.ericbeach.flashcards.datastore.FlashCardInteractionDatastoreHelper;
import org.ericbeach.flashcards.datastore.FlashCardLabelDatastoreHelper;

public class FlashCardSeriesGeneratorService {
  public static final int CRITERIA_NOT_SET = -1;
  private static final Logger log =
      Logger.getLogger(FlashCardInteractionDatastoreHelper.class.getName());
  private final FlashCardInteractionDatastoreHelper flashCardInteractionDatastoreHelper =
      new FlashCardInteractionDatastoreHelper();
  private final FlashCardDatastoreHelper flashCardDatastoreHelper =
      new FlashCardDatastoreHelper();
  private final FlashCardLabelDatastoreHelper flashCardLabelDatastoreHelper =
      new FlashCardLabelDatastoreHelper();

  public List<Long> getFlashCardIdsByCriteria(int includeCardsSeenInLastXDays,
      int excludeCardsSeenInLastYDays, int maxNumCardsInSeries,
      int includeCardsAnsweredLessThanZPercentCorrectInSeries, String userEmailAddress,
      Set<Long> labelIdsToInclude) {
    log.info("includeCardsSeenInLastXDays: " + includeCardsSeenInLastXDays);
    log.info("excludeCardsSeenInLastYDays: " + excludeCardsSeenInLastYDays);
    log.info("maxNumCardsInSeries: " + maxNumCardsInSeries);
    log.info("includeCardsAnsweredLessThanZPercentCorrectInSeries: "
        + includeCardsAnsweredLessThanZPercentCorrectInSeries);
    log.info("userEmailAddress: " + userEmailAddress);
    log.info("num labelIdsToInclude: " + labelIdsToInclude.size());

    // Start by getting all flash cards. This is necessary or the end-user
    // will be in a catch-22 where he never sees more than the 20 cards on the homepage
    // and therefore has no interactions with the other cards and therefore never sees
    // the other cards.
    Set<Long> allPotentialFlashCardIds = flashCardDatastoreHelper.getAllFlashCardIds();

    // FULFILL includeCardsSeenInLastXDays
    // FULFILL excludeCardsSeenInLastYDays
    Date date= new Date();
    long currentTmestamp = date.getTime() / 1000;
    long includeLastSeenAfterTimestamp = 0L;
    if (includeCardsSeenInLastXDays != CRITERIA_NOT_SET) {
      includeLastSeenAfterTimestamp =
          currentTmestamp - (includeCardsSeenInLastXDays * 1000 * 60 * 60 * 24); 
    }
    long excludeCardsSeenSeenAfterTimestamp = currentTmestamp;
    if (excludeCardsSeenInLastYDays != CRITERIA_NOT_SET) {
      excludeCardsSeenSeenAfterTimestamp =
          currentTmestamp - (excludeCardsSeenInLastYDays * 1000 * 60 * 60 * 24);
    }
    Set<Long> timeConstrainedFlashCardIds =
        flashCardInteractionDatastoreHelper.getFlashCardIdsByTimestampCriteria(
            includeLastSeenAfterTimestamp, excludeCardsSeenSeenAfterTimestamp);

    // FULFILL includeCardsAnsweredXPercentCorrectInSeries
    Set<Long> percentCorrectFlashCardIds = new HashSet<Long>();
    if (includeCardsAnsweredLessThanZPercentCorrectInSeries != CRITERIA_NOT_SET) {
      percentCorrectFlashCardIds =
          flashCardInteractionDatastoreHelper.getFlashCardIdsByPercentCorrect(
              userEmailAddress, includeCardsAnsweredLessThanZPercentCorrectInSeries);
    }

    // FULFILL labelIdsToInclude
    Set<Long> labelMatchingFlashCardIds = new HashSet<Long>();
    if (!labelIdsToInclude.isEmpty()) {
      for (long labelId : labelIdsToInclude) {
        labelMatchingFlashCardIds.addAll(
            flashCardLabelDatastoreHelper.getAllFlashCardIdsByLabelId(labelId, true));
      }
    }

    // CREATE end result.
    // Find union of the above criteria.
    Set<Long> allFilteredFlashCardIds = new HashSet<Long>();
    allFilteredFlashCardIds.addAll(allPotentialFlashCardIds);
    for (long flashCardId : allPotentialFlashCardIds) {
      if (includeCardsAnsweredLessThanZPercentCorrectInSeries != CRITERIA_NOT_SET
          && !percentCorrectFlashCardIds.contains(flashCardId)) {
        allFilteredFlashCardIds.remove(flashCardId);
      }
      if ((excludeCardsSeenInLastYDays != CRITERIA_NOT_SET
          || includeCardsSeenInLastXDays != CRITERIA_NOT_SET)
          && !timeConstrainedFlashCardIds.contains(flashCardId)) {
        allFilteredFlashCardIds.remove(flashCardId);
      }
      if (!labelIdsToInclude.isEmpty() && !labelMatchingFlashCardIds.contains(flashCardId)) {
        allFilteredFlashCardIds.remove(flashCardId);
      }
    }

    // FULFILL maxNumCardsInSeries
    List<Long> flashCardIds = new ArrayList<Long>();
    flashCardIds.addAll(allFilteredFlashCardIds);
    if (maxNumCardsInSeries != CRITERIA_NOT_SET
        && maxNumCardsInSeries < flashCardIds.size()) {
      flashCardIds = flashCardIds.subList(0, maxNumCardsInSeries);
    }

    log.info("Query for flash cards by criteria returned " + flashCardIds.size() + " results");
    return flashCardIds;
  }
}
