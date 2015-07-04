package org.ericbeach.flashcards.datastore;

import org.ericbeach.flashcards.models.FlashCard;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import java.util.ArrayList;
import java.util.List;

public class FlashCardDatastoreHelper {
  private static final String FLASH_CARD_KIND_NAME = "flash_card";
  private static final String FLASH_CARD_FRONT_SIDE_CONTENTS_PROPERTY_NAME = "front_side_contents";
  private static final String FLASH_CARD_FRONT_SIDE_LANGUAGE_PROPERTY_NAME = "front_side_language";
  private static final String FLASH_CARD_BACK_SIDE_CONTENTS_PROPERTY_NAME = "back_side_contents";
  private static final String FLASH_CARD_BACK_SIDE_DETAILS_PROPERTY_NAME = "back_side_details";
  private static final String FLASH_CARD_BACK_SIDE_LANGUAGE_PROPERTY_NAME = "back_side_language";
  private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

  public List<FlashCard> getFlashCardsByIds(List<Long> flashCardIdsToFetch) {
    List<Key> listOfFlashCardKeys = new ArrayList<Key>();
    for (Long flashCardId: flashCardIdsToFetch) {
      listOfFlashCardKeys.add(KeyFactory.createKey(FLASH_CARD_KIND_NAME, flashCardId));
    }

    Filter propertyFilter =
        new FilterPredicate(Entity.KEY_RESERVED_PROPERTY,
                            FilterOperator.IN,
                            listOfFlashCardKeys);
    Query query = new Query(FLASH_CARD_KIND_NAME).setFilter(propertyFilter);
    FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();
    return executeQueryForFlashCardsAndPopulateResults(query, fetchOptions);
  }

  public List<FlashCard> getAllFlashCards() {
    Query query = new Query(FLASH_CARD_KIND_NAME);
    FetchOptions fetchOptions = FetchOptions.Builder.withDefaults();
    return executeQueryForFlashCardsAndPopulateResults(query, fetchOptions);
  }

  public List<FlashCard> getRansomFlashCards(int limit) {
    Query query = new Query(FLASH_CARD_KIND_NAME);
    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(limit);
    return executeQueryForFlashCardsAndPopulateResults(query, fetchOptions);
  }

  private List<FlashCard> executeQueryForFlashCardsAndPopulateResults(Query query,
      FetchOptions fetchOptions) {
    List<FlashCard> cards = new ArrayList<FlashCard>();

    List<Entity> listOfFlashCardEntities =
        datastoreService.prepare(query).asList(fetchOptions);

    List<FlashCard> listOfFlashCardObjects = new ArrayList<FlashCard>();
    for (Entity flashCardEntity : listOfFlashCardEntities) {
      FlashCard flashCard = convertPopulatedEntityIntoFlashCard(flashCardEntity);
      listOfFlashCardObjects.add(flashCard);
    }

    listOfFlashCardObjects.addAll(cards);
    return listOfFlashCardObjects;
  }

  private Entity getFlashCardEntityById(long flashCardId) throws NoSuchEntityException {
    try {
      Key myKey = KeyFactory.createKey(FLASH_CARD_KIND_NAME, flashCardId);
      Entity flashCardEntity = datastoreService.get(myKey);
      return flashCardEntity;
    } catch (EntityNotFoundException e) {
      throw new NoSuchEntityException();
    }
  }

  public FlashCard getFlashCardById(long flashCardId) throws NoSuchEntityException {
    Entity result = getFlashCardEntityById(flashCardId);
    FlashCard flashCard = convertPopulatedEntityIntoFlashCard(result);
    return flashCard;
  }

  public Key addOrEditFlashCard(FlashCard newFlashCard) {
    Entity flashCardEntity;
    if (newFlashCard.getFlashCardId() > 0) {
      flashCardEntity = new Entity(FLASH_CARD_KIND_NAME, newFlashCard.getFlashCardId());
    } else {
      flashCardEntity = new Entity(FLASH_CARD_KIND_NAME);
    }

    flashCardEntity.setProperty(FLASH_CARD_FRONT_SIDE_CONTENTS_PROPERTY_NAME,
        newFlashCard.getFrontSideContent());
    flashCardEntity.setProperty(FLASH_CARD_FRONT_SIDE_LANGUAGE_PROPERTY_NAME,
        newFlashCard.getFrontSideLanguage());
    flashCardEntity.setProperty(FLASH_CARD_BACK_SIDE_CONTENTS_PROPERTY_NAME,
        newFlashCard.getBackSideContent());
    flashCardEntity.setProperty(FLASH_CARD_BACK_SIDE_DETAILS_PROPERTY_NAME,
        newFlashCard.getBackSideDetails());
    flashCardEntity.setProperty(FLASH_CARD_BACK_SIDE_LANGUAGE_PROPERTY_NAME,
        newFlashCard.getBackSideLanguage());

    Key newlyAddedKey = datastoreService.put(flashCardEntity);
    return newlyAddedKey;
  }

  private FlashCard convertPopulatedEntityIntoFlashCard(Entity flashCardEntity) {
    FlashCard flashCard = new FlashCard(
        (String) flashCardEntity.getProperty(FLASH_CARD_FRONT_SIDE_CONTENTS_PROPERTY_NAME),
        (String) flashCardEntity.getProperty(FLASH_CARD_FRONT_SIDE_LANGUAGE_PROPERTY_NAME),
        (String) flashCardEntity.getProperty(FLASH_CARD_BACK_SIDE_CONTENTS_PROPERTY_NAME),
        (String) flashCardEntity.getProperty(FLASH_CARD_BACK_SIDE_DETAILS_PROPERTY_NAME),
        (String) flashCardEntity.getProperty(FLASH_CARD_BACK_SIDE_LANGUAGE_PROPERTY_NAME),
        flashCardEntity.getKey().getId());
    return flashCard;
  }
}
