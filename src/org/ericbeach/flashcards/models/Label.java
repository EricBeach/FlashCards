package org.ericbeach.flashcards.models;

import java.util.HashSet;
import java.util.Set;

public class Label {
  public static final Long PARENT_LABEL_ID_FOR_NO_PARENT_LABEL = -1L;
  public static final Long NO_LABEL_ID = 0L;

  private final long labelId;
  private final String labelName;
  private long parentLabelId = PARENT_LABEL_ID_FOR_NO_PARENT_LABEL;
  private Set<Long> childrenLabelIds = new HashSet<Long>();

  public Label(String labelName, long labelId, long parentLabelId) {
    this.labelName = labelName;
    this.labelId = labelId;
    this.parentLabelId = parentLabelId;
  }

  public Label(String labelName, long labelId) {
    this.labelName = labelName;
    this.labelId = labelId;
  }

  public Label(String labelName) {
    this.labelName = labelName;
    this.labelId = NO_LABEL_ID;
  }

  public void setParentLabelId(long parentLabelId) {
    this.parentLabelId = parentLabelId;
  }

  public long getParentLabelId() {
    return parentLabelId;
  }

  public Set<Long> getChildrenLabelIds() {
    return childrenLabelIds;
  }

  public String getChildrenLabelIdsAsString() {
    String result = "";
    for (long childId : childrenLabelIds) {
      result += childId + ",";
    }
    if (result.length() > 0 && result.charAt(result.length() - 1) == ',') {
      result = result.substring(0, result.length() - 1);
    }
    return result;
  }

  public void setChildrenLabelIds(Set<Long> childrenLabelIds) {
    this.childrenLabelIds = childrenLabelIds;
  }

  public boolean hasChildrenLabels() {
    return (childrenLabelIds.size() > 0);
  }

  public String getLabelName() {
    return labelName;
  }

  public long getLabelId() {
    return labelId;
  }

  public void addChildLabelId(long newChildLabelId) {
    childrenLabelIds.add(newChildLabelId);
  }

  public void removeChildLabelId(long oldChildLabelId) {
    childrenLabelIds.remove(oldChildLabelId);
  }
}
