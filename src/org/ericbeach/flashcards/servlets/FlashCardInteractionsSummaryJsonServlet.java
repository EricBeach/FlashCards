package org.ericbeach.flashcards.servlets;

import org.ericbeach.flashcards.datastore.FlashCardInteractionDatastoreHelper;
import org.ericbeach.flashcards.models.FlashCardInteractionSummary;
import org.ericbeach.flashcards.services.UsersService;

import java.util.List;
import java.util.Map;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class FlashCardInteractionsSummaryJsonServlet extends HttpServlet {
  private final FlashCardInteractionDatastoreHelper flashCardInteractionDatastoreHelper =
      new FlashCardInteractionDatastoreHelper();
  private final UsersService usersService = new UsersService();

  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    if (req.getParameter("requestedFlashCardIds") == null) {
      resp.getWriter().println("{}");
    }

    List<Long> requestedFlashCardIds = Common.getListOfFlashCardIdsToQueryFromString(
        req.getParameter("requestedFlashCardIds"));

    Map<Long, FlashCardInteractionSummary> flashCardInteractionsSummary =
        flashCardInteractionDatastoreHelper.getFlashCardInteractionSummaryByFlashCardId(
            usersService.getCurrentUserEmailAddress(), requestedFlashCardIds);

    String flashCardInteractionsSummaryJson = "";
    if (flashCardInteractionsSummary.isEmpty()) {
      flashCardInteractionsSummaryJson = "{}";
    } else {
      flashCardInteractionsSummaryJson = "{";
      for (FlashCardInteractionSummary flashCardInteractionSummary :
           flashCardInteractionsSummary.values()) {
        flashCardInteractionsSummaryJson += "\"" + flashCardInteractionSummary.getFlashCardId() + "\" : "
           + flashCardInteractionSummary.toJson() + ",";
      }
      if (flashCardInteractionsSummary.size() > 0) {
        flashCardInteractionsSummaryJson = flashCardInteractionsSummaryJson.substring(
            0, flashCardInteractionsSummaryJson.length() - 1);
      }
      flashCardInteractionsSummaryJson += "}";      
    }
    resp.getWriter().println(flashCardInteractionsSummaryJson);
  }
}
