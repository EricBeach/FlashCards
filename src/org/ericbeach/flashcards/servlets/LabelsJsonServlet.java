package org.ericbeach.flashcards.servlets;

import org.ericbeach.flashcards.datastore.LabelDatastoreHelper;
import org.ericbeach.flashcards.models.Label;

import java.util.List;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class LabelsJsonServlet extends HttpServlet {
  private final LabelDatastoreHelper labelDatastoreHelper = new LabelDatastoreHelper();

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");

    List<Label> requestedLabels = getRequestedLabels(req);
    writeResponseAsArrayOfLabels(requestedLabels, resp);
  }

  private void writeResponseAsArrayOfLabels(List<Label> requestedLabels,
      HttpServletResponse resp) throws IOException {
    String labelsJson = "[";
    for (Label label : requestedLabels) {
      labelsJson += label.toJson() + ",";
    }
    if (!requestedLabels.isEmpty()) {
      labelsJson = labelsJson.substring(0, labelsJson.length() - 1);
    }
    labelsJson += "]";
    resp.getWriter().println(labelsJson);
  }

  private List<Label> getRequestedLabels(HttpServletRequest req) {
    return labelDatastoreHelper.getAllLabelsAsTree();
  }
}
