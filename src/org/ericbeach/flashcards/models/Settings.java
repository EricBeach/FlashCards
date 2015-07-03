package org.ericbeach.flashcards.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ericbeach.flashcards.services.UsersService;

public class Settings {
  public static final Set<String> KEYS_OF_USER_ADJUSTABLE_SETTINGS = new HashSet<String>();
  static {
    KEYS_OF_USER_ADJUSTABLE_SETTINGS.add("start_on_back_side_of_card");
    KEYS_OF_USER_ADJUSTABLE_SETTINGS.add("hide_text_on_card");
  }

  private final Map<String, String> settingsMap = new HashMap<String, String>();
  private final UsersService usersService = new UsersService();

  public Settings() {
    settingsMap.put("userLogoutUrl", usersService.createLogoutURL("/"));
    settingsMap.put("userEmailAddress", usersService.getCurrentUserEmailAddress());
  }

  public String getSetting(String key) {
    return settingsMap.get(key);
  }

  public void setSetting(String key, String value) {
    if (KEYS_OF_USER_ADJUSTABLE_SETTINGS.contains(key)) {
      settingsMap.put(key, value);
    }
  }

  public String toJson() {
    String json = "{";
    for (Map.Entry<String, String> setting : settingsMap.entrySet()) {
      json += "\"" + setting.getKey() + "\" : "
          + "\"" + setting.getValue() + "\",";
    }

    json += "\"validSettingKeys\" : [";
    for (String settingKey : KEYS_OF_USER_ADJUSTABLE_SETTINGS) {
      json += "\"" + settingKey + "\",";
    }
    // Eliminate the final "," which isn't valid JSON. Only do this is there are elements.
    if (json.charAt(json.length() - 1) == ',') {
      json = json.substring(0, json.length() - 1);
    }
    json += "]";

    json += "}";
    return json;
  }

  public static String toJsonForUserWithNoSettings() {
    UsersService usersService = new UsersService();
    String json = "{";
    json += "\"userEmailAddress\" : \"" + usersService.getCurrentUserEmailAddress() + "\","
        + "\"userLogoutUrl\" : \"" + usersService.createLogoutURL("/") + "\",";

    json += "\"validSettingKeys\" : [";
    for (String settingKey : KEYS_OF_USER_ADJUSTABLE_SETTINGS) {
      json += "\"" + settingKey + "\",";
    }
    // Eliminate the final "," which isn't valid JSON. Only do this is there are elements.
    if (json.charAt(json.length() - 1) == ',') {
      json = json.substring(0, json.length() - 1);
    }
    json += "]";

    json += "}";
    return json;
  }
}
