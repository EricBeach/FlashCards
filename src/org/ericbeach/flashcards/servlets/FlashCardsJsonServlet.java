package org.ericbeach.flashcards.servlets;

import org.ericbeach.flashcards.datastore.FlashCardDatastoreHelper;
import org.ericbeach.flashcards.models.FlashCard;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class FlashCardsJsonServlet extends HttpServlet {
  private final FlashCardDatastoreHelper flashCardDatastoreHelper = new FlashCardDatastoreHelper();

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");

    List<FlashCard> requestedFlashCards = getRequestedFlashCards(req);

    String responseMethod = req.getParameter("responseMethod");
    if (responseMethod != null && responseMethod.equals("mapById")) {
      writeResponseAsMapOfFlashCardsById(requestedFlashCards, resp);
    } else {
      writeResponseAsArrayOfFlashCards(requestedFlashCards, resp);
    }
  }

  private void writeResponseAsMapOfFlashCardsById(List<FlashCard> requestedFlashCards,
      HttpServletResponse resp) throws IOException {
    String flashCardsJson = "{";
    for (FlashCard flashCard : requestedFlashCards) {
      flashCardsJson += "\"" + flashCard.getFlashCardId() + "\" : "
          + flashCard.toJson()
          + ",";
    }
    flashCardsJson = flashCardsJson.substring(0, flashCardsJson.length()-1);
    flashCardsJson += "}";
    resp.getWriter().println(flashCardsJson);
  }

  private void writeResponseAsArrayOfFlashCards(List<FlashCard> requestedFlashCards,
      HttpServletResponse resp) throws IOException {
    String flashCardsJson = "[";
    for (FlashCard flashCard : requestedFlashCards) {
      flashCardsJson += flashCard.toJson() + ",";
    }
    flashCardsJson = flashCardsJson.substring(0, flashCardsJson.length()-1);
    flashCardsJson += "]";
    resp.getWriter().println(flashCardsJson);
  }

  private List<FlashCard> getRequestedFlashCards(HttpServletRequest req) {
    if (req.getParameter("requestedFlashCardIds") !=null) {
      List<Long> flashCardIdsToGet = getListOfFlashCardIdsToQueryFromString(
          req.getParameter("requestedFlashCardIds"));
      return flashCardDatastoreHelper.getFlashCardsByIds(flashCardIdsToGet);
    } else if (req.getParameter("numLimit") != null) {
      int limit = Integer.parseInt(req.getParameter("numLimit"));
      return flashCardDatastoreHelper.getRansomFlashCards(limit);
    } else {
      return flashCardDatastoreHelper.getAllFlashCards();
    }
  }

  private List<Long> getListOfFlashCardIdsToQueryFromString(String httpParamValue) {
    List<Long> listOfFlashCardItsToFetch = new ArrayList<Long>();

    String pattern = "^[0-9]+(,[0-9]+)*$";
    if (!httpParamValue.matches(pattern)) {
      return listOfFlashCardItsToFetch;
    }

    String[] flashCardIds = httpParamValue.split(",");
    for (String flashCardId : flashCardIds) {
      listOfFlashCardItsToFetch.add(Long.parseLong(flashCardId));
    }
    return listOfFlashCardItsToFetch;
  }

}
