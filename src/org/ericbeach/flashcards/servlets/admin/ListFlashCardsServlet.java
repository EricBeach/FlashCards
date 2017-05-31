package org.ericbeach.flashcards.servlets.admin;

import org.ericbeach.flashcards.datastore.FlashCardDatastoreHelper;
import org.ericbeach.flashcards.datastore.FlashCardLabelDatastoreHelper;
import org.ericbeach.flashcards.models.FlashCard;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

@SuppressWarnings("serial")
public class ListFlashCardsServlet extends HttpServlet {
  private final FlashCardDatastoreHelper flashCardDatastoreHelper =
      new FlashCardDatastoreHelper();
  private final FlashCardLabelDatastoreHelper flashCardLabelDatastoreHelper =
      new FlashCardLabelDatastoreHelper();

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/html");
    resp.setCharacterEncoding("UTF-8");
    resp.getWriter().println(CommonAdminServletHelper.getHtmlForTopOfAdminPages()
        + getHtmlBodyContents()
        + CommonAdminServletHelper.getHtmlForBottomOfAdminPages());
  }

  private String getHtmlBodyContents() {
    List<FlashCard> flashCards = flashCardDatastoreHelper.getAllFlashCards();
    String contents = "<table>"
        + "<t>"
        + "  <th>#</th>"
        + "  <th>Edit</th>"
        + "  <th>Edit Labels</th>"
        + "  <th>Delete</th>"
        + "  <th>&nbsp;</th>"
        + "  <th>ID</th>"
        + "  <th>Front Side Contents</th>"
        + "  <th>Front Side Language</th>"
        + "  <th>Back Side Contents</th>"
        + "  <th>Back Side Details</th>"
        + "  <th>Back Side Language</th>"
        + "</t>";

    int i = 1;
    for (FlashCard flashCard : flashCards) {
      int numLabelsActive = flashCardLabelDatastoreHelper
          .getAllLabelIdsForFlashCardByFlashCardId(flashCard.getFlashCardId()).size();
      contents += "<tr>"
      + "  <td>" + i + "</td>"
      + "  <td><a href=\"/admin/edit_individual_flashcard?flash_card_id="
      + flashCard.getFlashCardId() + "\">x</a></td>"
      + "  <td><a href=\"/admin/edit_applied_labels?flash_card_id="
      + flashCard.getFlashCardId() + "\">x</a> " + numLabelsActive + "</td>"
      + "  <td>x</td>"
      + "  <td>&nbsp;</td>"
      + "  <td>" + flashCard.getFlashCardId() + "</td>"
      + "  <td>" + flashCard.getFrontSideContent() + "</td>"
      + "  <td>" + flashCard.getFrontSideLanguage() + "</td>"
      + "  <td>" + flashCard.getBackSideContent() + "</td>"
      + "  <td>" + flashCard.getBackSideDetails() + "</td>"
      + "  <td>" + flashCard.getBackSideLanguage() + "</td>"
      + "</tr>";
      i++;
    }

    contents += "</table>";
    return contents;
  }
}
