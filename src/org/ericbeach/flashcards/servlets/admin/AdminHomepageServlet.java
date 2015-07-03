package org.ericbeach.flashcards.servlets.admin;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class AdminHomepageServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.getWriter().println(CommonAdminServletHelper.getHtmlForTopOfAdminPages()
        + CommonAdminServletHelper.getHtmlForBottomOfAdminPages());
  }
}
