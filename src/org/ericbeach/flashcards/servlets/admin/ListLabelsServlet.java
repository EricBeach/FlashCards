package org.ericbeach.flashcards.servlets.admin;

import org.ericbeach.flashcards.datastore.LabelDatastoreHelper;
import org.ericbeach.flashcards.models.Label;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

@SuppressWarnings("serial")
public class ListLabelsServlet extends HttpServlet {
  private final LabelDatastoreHelper labelDatastoreHelper = new LabelDatastoreHelper();

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/html");
    resp.setCharacterEncoding("UTF-8");
    resp.getWriter().println(CommonAdminServletHelper.getHtmlForTopOfAdminPages()
        + getHtmlBodyContents()
        + CommonAdminServletHelper.getHtmlForBottomOfAdminPages());
  }

  private String getHtmlBodyContents() {
    String contents = "";
    List<Label> labels = labelDatastoreHelper.getAllLabelsAsTree();

    contents += "<h1>Hierarchy View</h1>";
    Label previousLabel = null;
    String spacing = "";
    final String addSpacingString = "&nbsp;&nbsp;";
    for (Label label : labels) {
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

      contents += "<p>" + spacing + "<a href=\"/admin/edit_individual_label?label_id=" + label.getLabelId() + "\">"
          + label.getLabelName() + " (" + label.getLabelId() + ")</a></p>";
      previousLabel = label;
    }

    contents += "<h1>Table View</h1>";
    contents += "<table>"
        + "<t>"
        + "  <th>#</th>"
        + "  <th>Edit</th>"
        + "  <th>Delete</th>"
        + "  <th>&nbsp;</th>"
        + "  <th>ID</th>"
        + "  <th>Label Name</th>"
        + "  <th>Parent Label</th>"
        + "  <th>Children Labels</th>"
        + "</t>";

    int i = 1;
    for (Label label : labels) {
      contents += "<tr>"
      + "  <td>" + i + "</td>"
      + "  <td><a href=\"/admin/edit_individual_label?label_id=" + label.getLabelId() + "\">x</a></td>"
      + "  <td>x</td>"
      + "  <td>&nbsp;</td>"
      + "  <td>" + label.getLabelId() + "</td>"
      + "  <td>" + label.getLabelName()+ "</td>"
      + "  <td>" + label.getParentLabelId() + "</td>"
      + "  <td>" + label.getChildrenLabelIds() + "</td>"
      + "</tr>";
      i++;
    }

    contents += "</table>";
    return contents;
  }
}
