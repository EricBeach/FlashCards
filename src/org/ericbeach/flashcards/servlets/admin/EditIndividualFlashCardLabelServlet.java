package org.ericbeach.flashcards.servlets.admin;

import org.ericbeach.flashcards.datastore.FlashCardDatastoreHelper;
import org.ericbeach.flashcards.datastore.FlashCardLabelDatastoreHelper;
import org.ericbeach.flashcards.datastore.LabelDatastoreHelper;
import org.ericbeach.flashcards.datastore.NoSuchEntityException;
import org.ericbeach.flashcards.models.FlashCard;
import org.ericbeach.flashcards.models.Label;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class EditIndividualFlashCardLabelServlet extends HttpServlet {
  private final FlashCardDatastoreHelper flashCardDatastoreHelper = new FlashCardDatastoreHelper();
  private final FlashCardLabelDatastoreHelper flashCardLabelDatastoreHelper = new FlashCardLabelDatastoreHelper();
  private final LabelDatastoreHelper labelDatastoreHelper = new LabelDatastoreHelper();

  private static final Logger log = Logger.getLogger(EditIndividualFlashCardLabelServlet.class.getName());

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String htmlContents = "";
    if (req.getParameter("flash_card_id") != null) {
      long flashCardId = Long.parseLong(req.getParameter("flash_card_id"));
      FlashCard flashCard;
      try {
        flashCard = flashCardDatastoreHelper.getFlashCardById(flashCardId);
        Set<Long> flashCardLabelIds = flashCardLabelDatastoreHelper.
            getAllLabelIdsForFlashCardByFlashCardId(flashCard.getFlashCardId());
        htmlContents += getHtmlBodyContents(flashCard, flashCardLabelIds);
      } catch (NoSuchEntityException e) {
        htmlContents += "Error occurred.";
      }
    } else {
      htmlContents += "You need to specify a flash card to edit labels for.";
    }

    resp.setContentType("text/html");
    resp.setCharacterEncoding("UTF-8");
    resp.getWriter().println(CommonAdminServletHelper.getHtmlForTopOfAdminPages()
        + htmlContents
        + CommonAdminServletHelper.getHtmlForBottomOfAdminPages());
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    if (req.getParameter("flash_card_id") != null &&
        req.getParameter("flash_card_id").length() > 0 &&
        req.getParameter("label_id") != null &&
        req.getParameter("label_id").length() > 0) {
      long flashCardId = Long.parseLong(req.getParameter("flash_card_id"));
      flashCardLabelDatastoreHelper.deleteAllLabelsFormClashCard(flashCardId);

      String[] rawLabelIds = req.getParameterValues("label_id");
      for (String rawLabelId : rawLabelIds) {
        long labelId = Long.parseLong(rawLabelId);
        flashCardLabelDatastoreHelper.addLabelToFlashCard(flashCardId, labelId);
      }
    }

    String htmlContents = "Successfully adjusted labels for flash card.";

    resp.getWriter().println(CommonAdminServletHelper.getHtmlForTopOfAdminPages()
        + htmlContents
        + CommonAdminServletHelper.getHtmlForBottomOfAdminPages());
  }

  private String getHtmlBodyContents(FlashCard flashCard, Set<Long> currentLabelIds) {
    log.info("Printing flash card label editor for card " + flashCard.getFlashCardId()
        + " which has a set of " + currentLabelIds.size() + " labels.");
    List<Label> allLabels = labelDatastoreHelper.getAllLabelsAsTree();

    String contents = "<form method=\"post\" action=\"/admin/edit_applied_labels\">"
        + "<fieldset>"
        + "<legend>Flash Card</legend>"
        + "<p>Front Side Contents: " + flashCard.getFrontSideContent() + "</p>"
        + "<p>Back Side Contents: " + flashCard.getBackSideContent() + "</p>"
        + "</fieldset>";

    contents += "<fieldset>"
        + "<legend>Labels</legend>";
    for (Label label : allLabels) {
      if (label.hasChildrenLabels()) {
        contents += "<p>" + label.getLabelName() + "</p>";
      } else {
        String checked = "";
        if (currentLabelIds.contains(label.getLabelId())) {
          checked = " checked=\"checked\"";
        }
        contents += "<p><input type=\"checkbox\" name=\"label_id\" " + checked
            + " value=\"" + label.getLabelId() + "\">" + label.getLabelName() + "</p>";
      }
    }
    contents += "</fieldset>";

    contents += "<input type=\"hidden\" name=\"flash_card_id\" value=\"" + flashCard.getFlashCardId() + "\">"
        + "<button type=\"submit\">Submit Flash Card Labels</button>"
        + "</form>";
    return contents;
  }
}
