package org.ericbeach.flashcards.servlets;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ericbeach.flashcards.services.FlashCardSeriesGeneratorService;
import org.ericbeach.flashcards.services.UsersService;

@SuppressWarnings("serial")
public class FlashCardSeriesGeneratorJsonServlet extends HttpServlet {
  private final FlashCardSeriesGeneratorService flashCardSeriesGeneratorService =
      new FlashCardSeriesGeneratorService();
  private final UsersService usersService = new UsersService();

  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    doGet(req, resp);
  }

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");

    int includeCardsSeenInLastXDays = FlashCardSeriesGeneratorService.CRITERIA_NOT_SET;
    if (req.getParameter("include_seen_days") != null
        && req.getParameter("include_seen_days").length() > 0) {
      includeCardsSeenInLastXDays = Integer.parseInt(req.getParameter("include_seen_days"), 10);
    }

    int excludeCardsSeenInLastYDays = FlashCardSeriesGeneratorService.CRITERIA_NOT_SET;
    if (req.getParameter("exclude_seen_days") != null
        && req.getParameter("exclude_seen_days").length() > 0) {
      excludeCardsSeenInLastYDays = Integer.parseInt(req.getParameter("exclude_seen_days"), 10);
    }

    int maxNumCardsInSeries = FlashCardSeriesGeneratorService.CRITERIA_NOT_SET;
    if (req.getParameter("max_num_cards_in_series") != null
        && req.getParameter("max_num_cards_in_series").length() > 0) {
      maxNumCardsInSeries = Integer.parseInt(req.getParameter("max_num_cards_in_series"), 10);
    }

    int includeCardsAnsweredLessThanZPercentCorrectInSeries =
        FlashCardSeriesGeneratorService.CRITERIA_NOT_SET;
    if (req.getParameter("percent_correct_series") != null
        && req.getParameter("percent_correct_series").length() > 0) {
      includeCardsAnsweredLessThanZPercentCorrectInSeries =
          Integer.parseInt(req.getParameter("percent_correct_series"), 10);
    }

    Set<Long> labelIdsToInclude = new HashSet<Long>();
    if (req.getParameter("label_ids") != null
        && req.getParameter("label_ids").length() > 0) {
      labelIdsToInclude = getLabelIdsToInclude(req.getParameter("label_ids"));
    }

    String userEmailAddress = usersService.getCurrentUserEmailAddress();
    List<Long> flashCardIds = flashCardSeriesGeneratorService.getFlashCardIdsByCriteria(
        includeCardsSeenInLastXDays, excludeCardsSeenInLastYDays, maxNumCardsInSeries,
        includeCardsAnsweredLessThanZPercentCorrectInSeries, userEmailAddress,
        labelIdsToInclude);
    resp.getWriter().println(getFlashCardIdsAsJson(flashCardIds));
  }

  private Set<Long> getLabelIdsToInclude(String inputLabelIds) {
    Set<Long> labelIdsToInclude = new HashSet<Long>();
    String pattern = "^[0-9]+(,[0-9]+)*$";
    if (!inputLabelIds.matches(pattern)) {
      return labelIdsToInclude;
    }

    String[] flashCardIds = inputLabelIds.split(",");
    for (String flashCardId : flashCardIds) {
      labelIdsToInclude.add(Long.parseLong(flashCardId));
    }
    return labelIdsToInclude;
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
