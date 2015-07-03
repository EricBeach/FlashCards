package org.ericbeach.flashcards.servlets;

import org.ericbeach.flashcards.datastore.FlashCardInteractionDatastoreHelper;
import org.ericbeach.flashcards.models.FlashCardInteraction;
import org.ericbeach.flashcards.services.UsersService;

import java.util.Date;
import java.util.List;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class FlashCardInteractionsJsonServlet extends HttpServlet {
  private final FlashCardInteractionDatastoreHelper flashCardInteractionDatastoreHelper =
      new FlashCardInteractionDatastoreHelper();
  private final UsersService usersService = new UsersService();

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    List<FlashCardInteraction> flashCardInteractions = flashCardInteractionDatastoreHelper
        .getAllFlashCardInteractionsByUser(usersService.getCurrentUserEmailAddress());

    String flashCardInteractionsJson = "";
    if (flashCardInteractions.isEmpty()) {
      flashCardInteractionsJson = "[]";
    } else {
      flashCardInteractionsJson = "[";
      for (FlashCardInteraction flashCardInteraction : flashCardInteractions) {
        flashCardInteractionsJson += flashCardInteraction.toJson() + ",";
      }
      flashCardInteractionsJson = flashCardInteractionsJson.substring(
          0, flashCardInteractionsJson.length()-1);
      flashCardInteractionsJson += "]";      
    }

    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    resp.getWriter().println(flashCardInteractionsJson);
  }

  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Date currentDate = new Date();
    long currentTimestamp = currentDate.getTime() / 1000L;

    long flashCardId = Long.parseLong(req.getParameter("flashCardId"), 10);
    boolean isCorrect = Boolean.parseBoolean(req.getParameter("isCorrect"));
    long timestamp = currentTimestamp;
    String userEmailAddress = usersService.getCurrentUserEmailAddress();

    FlashCardInteraction currentFlashCardInteraction = new FlashCardInteraction(flashCardId,
        userEmailAddress, isCorrect, timestamp);

    flashCardInteractionDatastoreHelper.putFlashCardInteraction(currentFlashCardInteraction);

    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    resp.flushBuffer();
  }
}
