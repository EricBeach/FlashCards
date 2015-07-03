package org.ericbeach.flashcards.servlets.admin;

import org.ericbeach.flashcards.datastore.LabelDatastoreHelper;
import org.ericbeach.flashcards.datastore.NoSuchEntityException;
import org.ericbeach.flashcards.models.Label;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class EditIndividualLabelServlet extends HttpServlet {
  private static final Logger log = Logger.getLogger(EditIndividualLabelServlet.class.getName());
  private final LabelDatastoreHelper labelDatastoreHelper = new LabelDatastoreHelper();

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String htmlContents = "";
    if (req.getParameter("label_id") != null) {
      long labelId = Long.parseLong(req.getParameter("label_id"));
      Label labelToEdit;
      try {
        labelToEdit = labelDatastoreHelper.getLabelById(labelId);
        htmlContents += getHtmlBodyContents(labelToEdit);
      } catch (NoSuchEntityException e) {
        htmlContents += getHtmlBodyContents();
      }
    } else {
      htmlContents += getHtmlBodyContents();
    }

    resp.setContentType("text/html");
    resp.setCharacterEncoding("UTF-8");
    resp.getWriter().println(CommonAdminServletHelper.getHtmlForTopOfAdminPages()
        + htmlContents
        + CommonAdminServletHelper.getHtmlForBottomOfAdminPages());
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String labelName = req.getParameter("label_name");
    long parentLabelId = Label.PARENT_LABEL_ID_FOR_NO_PARENT_LABEL;
    if (req.getParameter("parent_label_id") != null &&
        req.getParameter("parent_label_id").length() > 0) {
      parentLabelId = Long.parseLong(req.getParameter("parent_label_id"));
    }

    String htmlContents = "";
    Label newLabel;
    if (req.getParameter("label_id") != null &&
        req.getParameter("label_id").length() > 0) {
      long labelId = Long.parseLong(req.getParameter("label_id"));
      newLabel = new Label(labelName, labelId);
      newLabel.setParentLabelId(parentLabelId);
      try {
        log.info("Attempting to update label " + newLabel.toString());
        labelDatastoreHelper.updateLabel(newLabel);
      } catch (NoSuchEntityException e) {
        htmlContents += "Something really bad happened with finding parent ID.";
      }
    } else {
      newLabel = new Label(labelName);
      newLabel.setParentLabelId(parentLabelId);
      try {
        log.info("Attempting to create label " + newLabel.toString());
        labelDatastoreHelper.createLabel(newLabel);
      } catch (NoSuchEntityException e) {
        htmlContents += "Something really bad happened with finding parent ID.";
      }
    }

    htmlContents += "Successfully added/updated label";

    resp.getWriter().println(CommonAdminServletHelper.getHtmlForTopOfAdminPages()
        + htmlContents
        + CommonAdminServletHelper.getHtmlForBottomOfAdminPages());
  }

  private String getHtmlBodyContents(Label label) {
    return getHtmlBodyContents(label.getLabelName(), label.getLabelId(), label.getParentLabelId());
  }

  private String getHtmlBodyContents(String labelName, long labelId, long parentLabelId) {
    String contents = "<form method=\"post\" action=\"/admin/edit_individual_label\">"
        + "<p>Label Name: <input type=\"text\" name=\"label_name\" value=\""
        + labelName + "\" size=\"40\" /></p>";

    if (labelId != Label.NO_LABEL_ID) {
      contents += "<input type=\"hidden\" name=\"label_id\" value=\"" + labelId + "\" />";
    }

    contents += getParentLabelIdSelectorHtml(parentLabelId)
        + "<button type=\"submit\">Submit Label</button>"
        + "</form>";
    return contents;
  }

  private String getParentLabelIdSelectorHtml(long parentLabelId) {
    String contents = "<p>Parent Label: <select name=\"parent_label_id\">";
    List<Label> allLabels = labelDatastoreHelper.getAllLabelsAsTree();

    contents += "<option value=\"" + Label.PARENT_LABEL_ID_FOR_NO_PARENT_LABEL + "\"></option>";

    Label previousLabel = null;
    String spacing = "";
    final String addSpacingString = "&nbsp;&nbsp;";
    for (Label label : allLabels) {
      if (previousLabel == null) {
        spacing = "";
      } else if (label.getParentLabelId() == Label.PARENT_LABEL_ID_FOR_NO_PARENT_LABEL) {
        spacing = "";
      } else if (label.getParentLabelId() == previousLabel.getParentLabelId()) {
      } else if (label.getParentLabelId() == previousLabel.getLabelId()) {
        spacing += addSpacingString;
      } else {
        spacing = spacing.substring(0, spacing.length() - addSpacingString.length());
      }

      String selected = "";
      if (parentLabelId == label.getLabelId()) {
        selected = "selected=\"selected\"";
      }
      contents += "<option " + selected + " value=\"" + label.getLabelId() + "\">"
          + spacing + label.getLabelName() + "</option>";
      previousLabel = label;
    }
    contents += "</select></p>";
    return contents;
  }

  private String getHtmlBodyContents() {
    return getHtmlBodyContents("", Label.NO_LABEL_ID, Label.PARENT_LABEL_ID_FOR_NO_PARENT_LABEL);
  }
}
