package org.ericbeach.flashcards.servlets.admin;

import org.ericbeach.flashcards.datastore.FlashCardInteractionDatastoreHelper;
import org.ericbeach.flashcards.datastore.SettingsDatastoreHelper;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class ClearUserDataServlet extends HttpServlet {
  private final SettingsDatastoreHelper settingsDatastoreHelper = new SettingsDatastoreHelper();
  private final FlashCardInteractionDatastoreHelper flashCardInteractionDatastoreHelper =
      new FlashCardInteractionDatastoreHelper();

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String htmlContents = getHtmlBodyContents();

    resp.setContentType("text/html");
    resp.setCharacterEncoding("UTF-8");
    resp.getWriter().println(CommonAdminServletHelper.getHtmlForTopOfAdminPages()
        + htmlContents
        + CommonAdminServletHelper.getHtmlForBottomOfAdminPages());
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String userEmailAdddress = req.getParameter("user_email");
    boolean isDeleteSettings = false;
    if (req.getParameter("user_settings") != null) {
      isDeleteSettings = true;
    }
    boolean isDeleteUserInteractions = false;
    if (req.getParameter("user_interactions") != null) {
      isDeleteUserInteractions = true;
    }

    String htmlContents = "<p>Executed operation for " + userEmailAdddress + "</p>";

    if (isDeleteSettings) {
      htmlContents += "<p>Deleted settings.</p>";
      settingsDatastoreHelper.deleteSettingsByUser(userEmailAdddress);
    }
    if (isDeleteUserInteractions) {
      htmlContents += "<p>Deleted user interactions.</p>";
      flashCardInteractionDatastoreHelper.deleteAllFlashCardInteractionsByUser(userEmailAdddress);
    }

    resp.getWriter().println(CommonAdminServletHelper.getHtmlForTopOfAdminPages()
        + htmlContents
        + CommonAdminServletHelper.getHtmlForBottomOfAdminPages());
  }

  private String getHtmlBodyContents() {
    String contents = "<form method=\"post\" action=\"/admin/clear_user_data\">"
        + "<p>Email Address: <input type=\"text\" name=\"user_email\" size=\"40\" /></p>"
        + "<p>Delete Settings: <input type=\"checkbox\" name=\"user_settings\" /></p>"
        + "<p>Delete User Interactions: <input type=\"checkbox\" name=\"user_interactions\" /></p>"
        + "<button type=\"submit\">Delete User Data</button>"
        + "</form>";
    return contents;
  }

}
