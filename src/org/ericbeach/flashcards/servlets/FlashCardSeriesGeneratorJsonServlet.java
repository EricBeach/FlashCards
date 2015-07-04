package org.ericbeach.flashcards.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ericbeach.flashcards.services.FlashCardSeriesGeneratorService;

@SuppressWarnings("serial")
public class FlashCardSeriesGeneratorJsonServlet extends HttpServlet {
  private final FlashCardSeriesGeneratorService flashCardSeriesGeneratorService =
      new FlashCardSeriesGeneratorService();

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");

    int includeSeenDays = FlashCardSeriesGeneratorService.CRITERIA_NOT_SET;
    if (req.getParameter("include_seen_days") != null
        && req.getParameter("include_seen_days").length() > 0) {
      includeSeenDays = Integer.parseInt(req.getParameter("include_seen_days"), 10);
    }

    int excludeSeenDays = FlashCardSeriesGeneratorService.CRITERIA_NOT_SET;
    if (req.getParameter("exclude_seen_days") != null
        && req.getParameter("exclude_seen_days").length() > 0) {
      excludeSeenDays = Integer.parseInt(req.getParameter("exclude_seen_days"), 10);
    }

    int maxNumCardsInSeries = FlashCardSeriesGeneratorService.CRITERIA_NOT_SET;
    if (req.getParameter("max_num_cards_in_series") != null
        && req.getParameter("max_num_cards_in_series").length() > 0) {
      maxNumCardsInSeries = Integer.parseInt(req.getParameter("max_num_cards_in_series"), 10);
    }

    int percentCorrectInSeries = FlashCardSeriesGeneratorService.CRITERIA_NOT_SET;
    if (req.getParameter("percent_correct_series") != null
        && req.getParameter("percent_correct_series").length() > 0) {
      percentCorrectInSeries = Integer.parseInt(req.getParameter("percent_correct_series"), 10);
    }

    List<Long> flashCardIds = flashCardSeriesGeneratorService.getFlashCardIdsByCriteria(
        includeSeenDays, excludeSeenDays, maxNumCardsInSeries, percentCorrectInSeries);
    resp.getWriter().println(getFlashCardIdsAsJson(flashCardIds));
  }

  private String getFlashCardIdsAsJson(List<Long> flashCardIds) {
    if (flashCardIds.isEmpty()) {
      return "[]";
    } else {
      String json = "[";
      for (long flashCardId : flashCardIds) {
        json += flashCardId + ",";
      }
      json = json.substring(0, json.length() - 1);
      json += "]";
      return json;
    }
  }
}
