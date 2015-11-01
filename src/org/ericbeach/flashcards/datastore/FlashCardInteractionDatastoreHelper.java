package org.ericbeach.flashcards.datastore;

import org.ericbeach.flashcards.models.FlashCardInteraction;
import org.ericbeach.flashcards.models.FlashCardInteractionSummary;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class FlashCardInteractionDatastoreHelper {
  private static final String FLASH_CARD_INTERACTION_KIND_NAME =
      "flash_card_interaction";
  private static final String FLASH_CARD_INTERACTION_FLASH_CARD_ID_PROPERTY_NAME =
      "flash_card_id";
  private static final String FLASH_CARD_INTERACTION_USER_EMAIL_ADDRESS_PROPERTY_NAME =
      "user_email_address";
  private static final String FLASH_CARD_INTERACTION_WAS_ANSWER_CORRECT_PROPERTY_NAME =
      "was_answer_correct";
  private static final String FLASH_CARD_INTERACTION_TIMESTAMP_PROPERTY_NAME =
      "timestamp";

  private static final Logger log =
      Logger.getLogger(FlashCardInteractionDatastoreHelper.class.getName());

  private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

  public void deleteAllFlashCardInteractionsByUser(String userEmailAddress) {
    List<Entity> listOfFlashCardEntities = getAllFlashCardEntitiesByUser(userEmailAddress);
    for (Entity flashCardEntity : listOfFlashCardEntities) {
      datastoreService.delete(flashCardEntity.getKey());
    }
  }

  public List<Entity> getAllFlashCardEntitiesByUser(String userEmailAddress) {
    Filter userEmailAddressFilter =
        new FilterPredicate(FLASH_CARD_INTERACTION_USER_EMAIL_ADDRESS_PROPERTY_NAME,
                            FilterOperator.EQUAL,
                            userEmailAddress);

    Query query = new Query(FLASH_CARD_INTERACTION_KIND_NAME)
        .setFilter(userEmailAddressFilter)
        .addSort(FLASH_CARD_INTERACTION_TIMESTAMP_PROPERTY_NAME, SortDirection.DESCENDING);;
    List<Entity> listOfFlashCardEntities =
        datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
    return listOfFlashCardEntities;
  }

  public List<FlashCardInteraction> getAllFlashCardInteractionsByUser(String userEmailAddress) {
    List<Entity> listOfFlashCardEntities = getAllFlashCardEntitiesByUser(userEmailAddress);

    List<FlashCardInteraction> listOfFlashCardInteractions = new ArrayList<FlashCardInteraction>();
    for (Entity flashCardInteractionEntity : listOfFlashCardEntities) {
      FlashCardInteraction flashCardInteraction =
          convertPopulatedEntityIntoFlashCard(flashCardInteractionEntity);
      listOfFlashCardInteractions.add(flashCardInteraction);
    }

    return listOfFlashCardInteractions;
  }

  public void putFlashCardInteraction(FlashCardInteraction flashCardInteraction) {
    Entity flashCardInteractionEntity = new Entity(FLASH_CARD_INTERACTION_KIND_NAME);
    flashCardInteractionEntity.setProperty(FLASH_CARD_INTERACTION_FLASH_CARD_ID_PROPERTY_NAME,
        flashCardInteraction.getFlashCardId());
    flashCardInteractionEntity.setProperty(FLASH_CARD_INTERACTION_USER_EMAIL_ADDRESS_PROPERTY_NAME,
        flashCardInteraction.getUserEmailAddress());
    flashCardInteractionEntity.setProperty(FLASH_CARD_INTERACTION_WAS_ANSWER_CORRECT_PROPERTY_NAME,
        flashCardInteraction.getWasAnswerCorrect());
    flashCardInteractionEntity.setProperty(FLASH_CARD_INTERACTION_TIMESTAMP_PROPERTY_NAME,
        flashCardInteraction.getTimestamp());
    datastoreService.put(flashCardInteractionEntity);
  }

  public Set<Long> getFlashCardIdsByTimestampCriteria(long includeLastSeenAfterTimestamp,
      long excludeCardsSeenSeenAfterTimestamp) {

    Filter includeCardsSeenInLastXDaysFilter =
        new FilterPredicate(FLASH_CARD_INTERACTION_TIMESTAMP_PROPERTY_NAME,
                            FilterOperator.GREATER_THAN_OR_EQUAL,
                            includeLastSeenAfterTimestamp);
    Filter excludeCardsSeenInLastYDaysFilter =
        new FilterPredicate(FLASH_CARD_INTERACTION_TIMESTAMP_PROPERTY_NAME,
                            FilterOperator.LESS_THAN_OR_EQUAL,
                            excludeCardsSeenSeenAfterTimestamp);

    Filter flashCardFilter = CompositeFilterOperator.and(includeCardsSeenInLastXDaysFilter,
        excludeCardsSeenInLastYDaysFilter);

    Query query = new Query(FLASH_CARD_INTERACTION_KIND_NAME).setFilter(flashCardFilter);
    List<Entity> listOfFlashCardEntities =
        datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
    log.info("Query for FlashCardInteraction values with timestamps >= "
        + includeLastSeenAfterTimestamp + " and <= " + excludeCardsSeenSeenAfterTimestamp
        + " returned " + listOfFlashCardEntities.size() + " results.");

    Set<Long> flashCardIds = new HashSet<Long>();
    for (Entity flashCardEntity : listOfFlashCardEntities) {
      flashCardIds.add((long) flashCardEntity.getProperty(
          FLASH_CARD_INTERACTION_FLASH_CARD_ID_PROPERTY_NAME));
    }

    return flashCardIds;
  }

  public Map<Long, FlashCardInteractionSummary> getFlashCardInteractionSummaryByFlashCardId(
      String userEmailAddress, List<Long> flashCardIds) {
    log.info("Querying for flash card interaction summary for user " + userEmailAddress
        + " and total of " + flashCardIds.size() + " flashCardIds");

    Map<Long, FlashCardInteractionSummary> flashCardInterationSummary =
        new HashMap<Long, FlashCardInteractionSummary>();

    Filter userEmailAddressFilter =
        new FilterPredicate(FLASH_CARD_INTERACTION_USER_EMAIL_ADDRESS_PROPERTY_NAME,
                            FilterOperator.EQUAL,
                            userEmailAddress);
    Filter flashCardIdFilter =
        new FilterPredicate(FLASH_CARD_INTERACTION_FLASH_CARD_ID_PROPERTY_NAME,
                            FilterOperator.IN,
                            flashCardIds);
    Filter flashCardInteractionFilter = CompositeFilterOperator.and(userEmailAddressFilter,
        flashCardIdFilter);

    Query query = new Query(FLASH_CARD_INTERACTION_KIND_NAME).setFilter(flashCardInteractionFilter);
    List<Entity> listOfFlashCardInteractionEntities =
        datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
    log.info("Query for flash card interactions by user " + userEmailAddress
        + " restricted to list of " + flashCardIds.size() + " flashCardIds returned "
        + listOfFlashCardInteractionEntities.size() + " entries");

    for (Entity flashCardEntity : listOfFlashCardInteractionEntities) {
      long flashCardId = (long) flashCardEntity.getProperty(
          FLASH_CARD_INTERACTION_FLASH_CARD_ID_PROPERTY_NAME);
      boolean wasAnsweredCorrectly = (boolean) flashCardEntity.getProperty(
          FLASH_CARD_INTERACTION_WAS_ANSWER_CORRECT_PROPERTY_NAME);
      if (!flashCardInterationSummary.containsKey(flashCardId)) {
        FlashCardInteractionSummary newSummary = new FlashCardInteractionSummary(flashCardId);
        flashCardInterationSummary.put(flashCardId, newSummary);
      }

      if (wasAnsweredCorrectly) {
        flashCardInterationSummary.get(flashCardId).incrementAttemptedAndCorrectCount();
      } else  {
        flashCardInterationSummary.get(flashCardId).incrementAttemptedCount();
      }
    }

    return flashCardInterationSummary;
  }

  public Set<Long> getFlashCardIdsByPercentCorrect(String userEmailAddress,
      int includeCardsAnsweredLessThanZPercentCorrectInSeries) {
    Filter userEmailAddressFilter =
        new FilterPredicate(FLASH_CARD_INTERACTION_USER_EMAIL_ADDRESS_PROPERTY_NAME,
                            FilterOperator.EQUAL,
                            userEmailAddress);

    Query query = new Query(FLASH_CARD_INTERACTION_KIND_NAME).setFilter(userEmailAddressFilter);
    List<Entity> listOfFlashCardInteractionEntities =
        datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
    log.info("Query for all flash card interactions by user " + userEmailAddress
        + " returned " + listOfFlashCardInteractionEntities.size() + " entries");

    Map<Long, Integer> mapOfFlashCardIdsToTimesCardAttempted = new HashMap<Long, Integer>();
    Map<Long, Integer> mapOfFlashCardIdsToTimesCardAnsweredCorretly = new HashMap<Long, Integer>();
    for (Entity flashCardEntity : listOfFlashCardInteractionEntities) {
      long flashCardId = (long) flashCardEntity.getProperty(
          FLASH_CARD_INTERACTION_FLASH_CARD_ID_PROPERTY_NAME);
      int numTimesAttempted = 0;
      try {
        numTimesAttempted = mapOfFlashCardIdsToTimesCardAttempted.get(flashCardId);
      } catch (NullPointerException e) {
        // Do nothing. Case will be when an ID doesn't exist, meaning its defacto value is 0.
      }
      numTimesAttempted += 1;
      mapOfFlashCardIdsToTimesCardAttempted.put(flashCardId, numTimesAttempted);

      boolean wasAnsweredCorrectly = (boolean) flashCardEntity.getProperty(
          FLASH_CARD_INTERACTION_WAS_ANSWER_CORRECT_PROPERTY_NAME);
      if (wasAnsweredCorrectly) {
        int numTimesCorrect = 0;
        try {
          numTimesCorrect = mapOfFlashCardIdsToTimesCardAnsweredCorretly.get(flashCardId);
        } catch (NullPointerException e) {
          // Do nothing. Case will be when an ID doesn't exist, meaning its defacto value is 0.
        }
        numTimesCorrect += 1;
        mapOfFlashCardIdsToTimesCardAnsweredCorretly.put(flashCardId, numTimesCorrect);
      }
    }

    Set<Long> matchingFlashCardIds = new HashSet<Long>();
    for (Map.Entry<Long, Integer> entry : mapOfFlashCardIdsToTimesCardAttempted.entrySet()) {
      long flashCardId = entry.getKey();
      int timesAnsweredCorrectly = 0;
      try {
        timesAnsweredCorrectly = mapOfFlashCardIdsToTimesCardAnsweredCorretly.get(flashCardId);
      } catch (NullPointerException e) {
        // Do nothing. Case will be when an ID doesn't exist, meaning its defacto value is 0.
      }
      int timesAttempted = entry.getValue();
      if ((timesAnsweredCorrectly / timesAttempted) * 100
          < includeCardsAnsweredLessThanZPercentCorrectInSeries) {
        matchingFlashCardIds.add(flashCardId);
      }
    }
    log.info("Query for questions answered less than "
        + includeCardsAnsweredLessThanZPercentCorrectInSeries + "% correct returned "
        + matchingFlashCardIds.size() + " results");
    return matchingFlashCardIds;
  }

  private FlashCardInteraction convertPopulatedEntityIntoFlashCard(
      Entity flashCardInteractionEntity) {
    FlashCardInteraction flashCardInteraction = new FlashCardInteraction(
        (long) flashCardInteractionEntity.getProperty(
            FLASH_CARD_INTERACTION_FLASH_CARD_ID_PROPERTY_NAME),
        (String) flashCardInteractionEntity.getProperty(
            FLASH_CARD_INTERACTION_USER_EMAIL_ADDRESS_PROPERTY_NAME),
        (boolean) flashCardInteractionEntity.getProperty(
            FLASH_CARD_INTERACTION_WAS_ANSWER_CORRECT_PROPERTY_NAME),
        (long) flashCardInteractionEntity.getProperty(
            FLASH_CARD_INTERACTION_TIMESTAMP_PROPERTY_NAME));
    return flashCardInteraction;
  }
}
