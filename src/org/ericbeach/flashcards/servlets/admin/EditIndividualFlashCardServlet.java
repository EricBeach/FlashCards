package org.ericbeach.flashcards.servlets.admin;

import org.ericbeach.flashcards.datastore.FlashCardDatastoreHelper;
import org.ericbeach.flashcards.datastore.NoSuchEntityException;
import org.ericbeach.flashcards.models.FlashCard;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class EditIndividualFlashCardServlet extends HttpServlet {
  private final FlashCardDatastoreHelper flashCardDatastoreHelper = new FlashCardDatastoreHelper();

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String htmlContents = "";
    if (req.getParameter("flash_card_id") != null) {
      long flashCardId = Long.parseLong(req.getParameter("flash_card_id"));
      FlashCard flashCardToEdit;
      try {
        flashCardToEdit = flashCardDatastoreHelper.getFlashCardById(flashCardId);
        htmlContents += getHtmlBodyContents(flashCardToEdit);
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
    String frontSideContents = req.getParameter("front_side_contents");
    String frontSideLanguage = req.getParameter("front_side_language");
    String backSideContents = req.getParameter("back_side_contents");
    String backSideDetails = req.getParameter("back_side_details");
    String backSideLanguage = req.getParameter("back_side_language");

    FlashCard newFlashCard = new FlashCard(frontSideContents, frontSideLanguage,
        backSideContents, backSideDetails, backSideLanguage);
    if (req.getParameter("flash_card_id") != null &&
        req.getParameter("flash_card_id").length() > 0) {
      long flashCardId = Long.parseLong(req.getParameter("flash_card_id"));
      newFlashCard.setFlashCardId(flashCardId);
    }

    flashCardDatastoreHelper.addOrEditFlashCard(newFlashCard);

    String htmlContents = "Successfully added new flash card!";

    resp.getWriter().println(CommonAdminServletHelper.getHtmlForTopOfAdminPages()
        + htmlContents
        + CommonAdminServletHelper.getHtmlForBottomOfAdminPages());
  }

  private String getHtmlBodyContents(FlashCard flashCard) {
    return getHtmlBodyContents(flashCard.getFrontSideContent(), flashCard.getFrontSideLanguage(),
        flashCard.getBackSideContent(), flashCard.getBackSideDetails(),
        flashCard.getBackSideLanguage(), flashCard.getFlashCardId());
  }

  private String getHtmlBodyContents(String frontSideContents, String frontSideLanguage,
      String backSideContents, String backSideDetails, String backSideLanguage, long flashCardId) {
    String contents = "<form method=\"post\" action=\"/admin/edit_individual_flashcard\">"
        + "<p>Front Side Contents: <input type=\"text\" name=\"front_side_contents\" value=\""
        + frontSideContents + "\" size=\"40\" /></p>"
        + "<p>Front Side Language: <select name=\"front_side_language\">"
        + getLanguageOption(frontSideLanguage) + "</select></p>"
        + "<p>Back Side Contents: <input type=\"text\" name=\"back_side_contents\" value=\""
        + backSideContents + "\" size=\"40\" /></p>"
        + "<p>Back Side Details: <input type=\"text\" name=\"back_side_details\" value=\""
        + backSideDetails + "\" size=\"40\" /></p>"
        + "<p>Back Side Language: <select name=\"back_side_language\">"
        + getLanguageOption(backSideLanguage) + "</select></p>"
        + "<input type=\"hidden\" name=\"flash_card_id\" value=\""
        + flashCardId + "\">"
        + "<button type=\"submit\">Submit Flash Card</button>"
        + "</form>";
    return contents;
  }

  private String getLanguageOption(String selectedLanguage) {
    String enUsSelected = "";
    if (selectedLanguage.equals("en-US")) {
      enUsSelected = "selected";
    }
    String znCnSelected = "";
    if (selectedLanguage.equals("zh-CN")) {
      znCnSelected = "selected";
    }
    String contents = "<option " + enUsSelected + ">en-US</option><option "
        + znCnSelected + ">zh-CN</option>";
    return contents;
  }

  private String getHtmlBodyContents() {
    return getHtmlBodyContents("", "en-US", "", "", "zh-CN", 0);
  }
}
