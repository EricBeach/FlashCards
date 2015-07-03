package org.ericbeach.flashcards.datastore;

import org.ericbeach.flashcards.models.FlashCardInteraction;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import java.util.ArrayList;
import java.util.List;

public class FlashCardInteractionDatastoreHelper {
  private static final String FLASH_CARD_INTERACTION_KIND_NAME = "flash_card_interaction";
  private static final String FLASH_CARD_INTERACTION_FLASH_CARD_ID_PROPERTY_NAME = "flash_card_id";
  private static final String FLASH_CARD_INTERACTION_USER_EMAIL_ADDRESS_PROPERTY_NAME =
      "user_email_address";
  private static final String FLASH_CARD_INTERACTION_WAS_ANSWER_CORRECT_PROPERTY_NAME =
      "was_answer_correct";
  private static final String FLASH_CARD_INTERACTION_TIMESTAMP_PROPERTY_NAME =
      "timestamp";

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

    Query query = new Query(FLASH_CARD_INTERACTION_KIND_NAME).setFilter(userEmailAddressFilter);
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
    flashCardInteractionEntity.setUnindexedProperty(FLASH_CARD_INTERACTION_USER_EMAIL_ADDRESS_PROPERTY_NAME,
        flashCardInteraction.getUserEmailAddress());
    flashCardInteractionEntity.setProperty(FLASH_CARD_INTERACTION_WAS_ANSWER_CORRECT_PROPERTY_NAME,
        flashCardInteraction.getWasAnswerCorrect());
    flashCardInteractionEntity.setProperty(FLASH_CARD_INTERACTION_TIMESTAMP_PROPERTY_NAME,
        flashCardInteraction.getTimestamp());
    datastoreService.put(flashCardInteractionEntity);
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
