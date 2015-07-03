package org.ericbeach.flashcards.servlets.admin;

public class CommonAdminServletHelper {
  public static String getHtmlForTopOfAdminPages() {
    String htmlContents = "<!DOCTYPE html>"
        + "  <head>"
        + "    <meta name=\"viewport\" content=\"initial-scale=1.0, user-scalable=no\">"
        + "    <link rel=\"stylesheet\" type=\"text/css\" href=\"/static/admin/css/style.css\">"
        + "    <title>Flash Cards - Admin</title>"
        + "  </head>"
        + "  <body class=\"admin-page\">"
        + "  <p><a href=\"/admin/edit_individual_flashcard\">Create Flash Card</a> &middot; "
        + "    <a href=\"/admin/list_flashcards\">List Flash Cards</a> | "
        + "    <a href=\"/admin/edit_individual_label\">Create Label</a> &middot; "
        + "    <a href=\"/admin/list_labels\">List Labels</a> | "
        + "    <a href=\"/admin/clear_user_data\">Clear User Data</a>"
        + "  </p>";
    return htmlContents;
  }

  public static String getHtmlForBottomOfAdminPages() {
    String htmlContents = "  </body>"
      + "</html>";
    return htmlContents;
  }
}