package org.ericbeach.flashcards.datastore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.ericbeach.flashcards.models.Label;

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

public class LabelDatastoreHelper {
  private static final String LABEL_KIND_NAME = "label";
  private static final String LABEL_NAME_PROPERTY_NAME = "label_name";
  private static final String LABEL_PARENT_ID_PROPERTY_NAME = "label_parent_id";
  private static final String LABEL_CHILD_IDS_PROPERTY_NAME = "label_child_ids";

  private static final Logger log = Logger.getLogger(LabelDatastoreHelper.class.getName());

  private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

  public void updateLabel(Label updatedVersionOfLabel) throws NoSuchEntityException {
    Label oldVersionOfLabel = getLabelById(updatedVersionOfLabel.getLabelId());

    Entity updatedVerionOfLabelEntity = convertPopulatedLabelIntoEntity(updatedVersionOfLabel);
    log.info("About to update label " + updatedVersionOfLabel.toString() + " with entity "
        + updatedVerionOfLabelEntity.toString());
    datastoreService.put(updatedVerionOfLabelEntity);

    // Update old parent label's children reference
    if (oldVersionOfLabel.getParentLabelId() != Label.PARENT_LABEL_ID_FOR_NO_PARENT_LABEL) {
      Label oldParentLabel = getLabelById(oldVersionOfLabel.getParentLabelId());
      oldParentLabel.removeChildLabelId(oldVersionOfLabel.getLabelId());
      Entity oldParentLabelEntity = convertPopulatedLabelIntoEntity(oldParentLabel);
      datastoreService.put(oldParentLabelEntity);
    }

    // Update new parent label's children reference
    if (updatedVersionOfLabel.getParentLabelId() != Label.PARENT_LABEL_ID_FOR_NO_PARENT_LABEL) {
      Label newParentLabel = getLabelById(updatedVersionOfLabel.getParentLabelId());
      newParentLabel.addChildLabelId(updatedVersionOfLabel.getLabelId());
      Entity newParentLabelEntity = convertPopulatedLabelIntoEntity(newParentLabel);
      datastoreService.put(newParentLabelEntity);
    }
  }

  public void createLabel(Label labelToCreate) throws NoSuchEntityException {
    Entity labelEntity = new Entity(LABEL_KIND_NAME);
    labelEntity.setUnindexedProperty(LABEL_NAME_PROPERTY_NAME,
        labelToCreate.getLabelName());
    labelEntity.setUnindexedProperty(LABEL_CHILD_IDS_PROPERTY_NAME,
        "");
    labelEntity.setProperty(LABEL_PARENT_ID_PROPERTY_NAME,
        labelToCreate.getParentLabelId());

    Key labelKey = datastoreService.put(labelEntity);

    // Update the Child Label IDs property of the parent
    if (labelToCreate.getParentLabelId() != Label.PARENT_LABEL_ID_FOR_NO_PARENT_LABEL) {
      long labelId = labelKey.getId();
      Label parentLabel = getLabelById(labelToCreate.getParentLabelId());
      parentLabel.addChildLabelId(labelId);
      updateLabel(parentLabel);
    }
  }

  public Label getLabelById(long labelId) throws NoSuchEntityException {
    try {
      Key myKey = KeyFactory.createKey(LABEL_KIND_NAME, labelId);
      Entity labelEntity = datastoreService.get(myKey);
      return convertPopulatedEntityIntoLabel(labelEntity);
    } catch (EntityNotFoundException e) {
      throw new NoSuchEntityException();
    }
  }

  public List<Label> getAllLabelsAsTree() {
    List<Label> rootLabels = getLabelsByParentLabelId(Label.PARENT_LABEL_ID_FOR_NO_PARENT_LABEL);
    List<Label> allLabelsInTreeOrder = new ArrayList<Label>();
    for (Label rootLabel : rootLabels) {
      allLabelsInTreeOrder.addAll(getAllDescendantLabelsByLabelId(rootLabel.getLabelId()));
    }
    return allLabelsInTreeOrder;
  }

  public List<Label> getAllDescendantLabelsByLabelId(long labelId) {
    List<Label> resultLabels = new ArrayList<Label>();
    // Step 1: Query for label
    Label label;
    try {
      label = getLabelById(labelId);
      resultLabels.add(label);
    } catch (NoSuchEntityException e) {
      return resultLabels;
    }

    // Base Case: parent label is at the bottom of the tree
    Set<Long> childLabelIds = label.getChildrenLabelIds();
    if (childLabelIds.size() > 0) {
      for (long childLabelId : childLabelIds) {
        resultLabels.addAll(getAllDescendantLabelsByLabelId(childLabelId));
      }
    }
    log.info("Query for all descendant labels of label ID " + labelId + " returned "
        + resultLabels.size() + " results");
    return resultLabels;
  }

  private List<Label> getLabelsByParentLabelId(long parentLabelId) {
    List<Label> resultLabels = new ArrayList<Label>();
    Filter parentLabelIdFilter =
        new FilterPredicate(LABEL_PARENT_ID_PROPERTY_NAME,
                            FilterOperator.EQUAL,
                            parentLabelId);

    Query query = new Query(LABEL_KIND_NAME).setFilter(parentLabelIdFilter);
    List<Entity> listOfLabelEntities =
        datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());
    for (Entity labelEntity : listOfLabelEntities) {
      resultLabels.add(convertPopulatedEntityIntoLabel(labelEntity));
    }
    return resultLabels;
  }

  private Label convertPopulatedEntityIntoLabel(Entity labelEntity) {
    Label label = new Label(
        (String) labelEntity.getProperty(LABEL_NAME_PROPERTY_NAME),
        labelEntity.getKey().getId(),
        (long) labelEntity.getProperty(LABEL_PARENT_ID_PROPERTY_NAME));

    String rawChildrenLabelIds = (String) labelEntity.getProperty(LABEL_CHILD_IDS_PROPERTY_NAME);
    if (rawChildrenLabelIds.length() > 0) {
      Set<Long> parsedChildLabelIds = new HashSet<Long>();
      String[] childLabelIds = rawChildrenLabelIds.split(",");
      for (String childLabelId : childLabelIds) {
        parsedChildLabelIds.add(Long.parseLong(childLabelId));
      }
      label.setChildrenLabelIds(parsedChildLabelIds);
    }
    return label;
  }

  private Entity convertPopulatedLabelIntoEntity(Label label) {
    Entity labelEntity = new Entity(LABEL_KIND_NAME, label.getLabelId());
    labelEntity.setUnindexedProperty(LABEL_NAME_PROPERTY_NAME,
        label.getLabelName());
    labelEntity.setUnindexedProperty(LABEL_CHILD_IDS_PROPERTY_NAME,
        label.getChildrenLabelIdsAsString());
    labelEntity.setProperty(LABEL_PARENT_ID_PROPERTY_NAME,
        label.getParentLabelId());
    return labelEntity;
  }
}
