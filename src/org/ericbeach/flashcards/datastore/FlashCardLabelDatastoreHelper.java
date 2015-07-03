package org.ericbeach.flashcards.datastore;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class FlashCardLabelDatastoreHelper {
  private static final String FLASH_CARD_LABEL_KIND = "flash_card_label";
  private static final String FLASH_CARD_LABEL_FLASH_CARD_ID_PROPERTY_NAME = "flash_card_id";
  private static final String FLASH_CARD_LABEL_LABEL_ID_PROPERTY_NAME = "label_id";

  private static final Logger log = Logger.getLogger(FlashCardLabelDatastoreHelper.class.getName());

  private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

  public Set<Long> getAllLabelIdsForFlashCardByFlashCardId(long flashCardId) {
    Set<Long> labelIds = new HashSet<Long>();
    List<Entity> listOfFlashCardLabels = getFlashCardLabelEntitiesByFlashCardId(flashCardId);
    log.info("Query for flash card id " + flashCardId + " returned "
        + listOfFlashCardLabels.size() + " labels");
    for (Entity flashCardLabel : listOfFlashCardLabels) {
      labelIds.add((long) flashCardLabel.getProperty(
          FLASH_CARD_LABEL_LABEL_ID_PROPERTY_NAME));
    }
    return labelIds;
  }

  public Set<Long> getAllFlashCardIdsByLabelId(long labelId, boolean includeChildrenLabels) {
    Set<Long> flashCardIds = new HashSet<Long>();
    Filter labelIdFilter =
        new FilterPredicate(FLASH_CARD_LABEL_LABEL_ID_PROPERTY_NAME,
                            FilterOperator.EQUAL,
                            labelId);

    Query query = new Query(FLASH_CARD_LABEL_KIND).setFilter(labelIdFilter);
    List<Entity> listOfFlashCardLabels =
        datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
    for (Entity flashCardLabel : listOfFlashCardLabels) {
      flashCardIds.add((long) flashCardLabel.getProperty(
          FLASH_CARD_LABEL_FLASH_CARD_ID_PROPERTY_NAME));
    }
    return flashCardIds;
  }

  public void addLabelToFlashCard(long flashCardId, long labelId) {
    removeLabelFormFlashCard(flashCardId, labelId);

    Entity flashCardLabelEntity = new Entity(FLASH_CARD_LABEL_KIND);
    flashCardLabelEntity.setProperty(FLASH_CARD_LABEL_LABEL_ID_PROPERTY_NAME,
        labelId);
    flashCardLabelEntity.setProperty(FLASH_CARD_LABEL_FLASH_CARD_ID_PROPERTY_NAME,
        flashCardId);

    datastoreService.put(flashCardLabelEntity);
  }

  public void removeLabelFormFlashCard(long flashCardId, long labelId) {
    Filter flashCardIdFilter =
        new FilterPredicate(FLASH_CARD_LABEL_KIND,
                            FilterOperator.EQUAL,
                            flashCardId);
    Filter labelIdFilter =
        new FilterPredicate(FLASH_CARD_LABEL_KIND,
                            FilterOperator.EQUAL,
                            labelId);

    Query query = new Query(FLASH_CARD_LABEL_KIND).setFilter(
        flashCardIdFilter).setFilter(labelIdFilter);
    List<Entity> listOfFlashCardLabels =
        datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
    for (Entity flashCardLabelId : listOfFlashCardLabels) {
      datastoreService.delete(flashCardLabelId.getKey());
    }
  }

  public void deleteAllLabelsFormClashCard(long flashCardId) {
    List<Entity> flashCardLabelEntities = getFlashCardLabelEntitiesByFlashCardId(flashCardId);
    for (Entity flashCardLabelEntity : flashCardLabelEntities) {
      datastoreService.delete(flashCardLabelEntity.getKey());
    }
  }

  private List<Entity> getFlashCardLabelEntitiesByFlashCardId(long flashCardId) {
    Filter flashCardIdFilter =
        new FilterPredicate(FLASH_CARD_LABEL_FLASH_CARD_ID_PROPERTY_NAME,
                            FilterOperator.EQUAL,
                            flashCardId);

    Query query = new Query(FLASH_CARD_LABEL_KIND).setFilter(flashCardIdFilter);
    return datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
  }
}
