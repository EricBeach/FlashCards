package org.ericbeach.flashcards.servlets;

import org.ericbeach.flashcards.datastore.NoSuchEntityException;
import org.ericbeach.flashcards.datastore.SettingsDatastoreHelper;
import org.ericbeach.flashcards.models.Settings;
import org.ericbeach.flashcards.services.UsersService;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class SettingsJsonServlet extends HttpServlet {
  protected static final Logger log = Logger.getLogger(SettingsJsonServlet.class.getName());
  private UsersService usersService = new UsersService();
  private SettingsDatastoreHelper settingsDatastoreHelper = new SettingsDatastoreHelper();

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    pringSettingsJsonResponse(resp);
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Iterator<String> iterator = Settings.KEYS_OF_USER_ADJUSTABLE_SETTINGS.iterator();
    while(iterator.hasNext()) {
      String settingKey = iterator.next();
      String potentialSettingKeyFormElemId = "setting__" + settingKey;
      if (req.getParameter(potentialSettingKeyFormElemId) != null
          && req.getParameter(potentialSettingKeyFormElemId).length() > 0) {
        settingsDatastoreHelper.setUserSetting(usersService.getCurrentUserEmailAddress(),
            settingKey, req.getParameter(potentialSettingKeyFormElemId));
        log.info("Changing setting " + settingKey + " to "
            + req.getParameter(potentialSettingKeyFormElemId) + " for "
            + usersService.getCurrentUserEmailAddress());
      }
    }

    pringSettingsJsonResponse(resp);
  }

  public void pringSettingsJsonResponse(HttpServletResponse resp) throws IOException {
    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    try {
      Settings settings =
          settingsDatastoreHelper.getSettings(usersService.getCurrentUserEmailAddress());
      resp.getWriter().println(settings.toJson());
    } catch (NoSuchEntityException e) {
      resp.getWriter().println(Settings.toJsonForUserWithNoSettings());
    }
  }
}
