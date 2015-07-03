package org.ericbeach.flashcards.datastore;

import org.ericbeach.flashcards.models.Settings;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class SettingsDatastoreHelper {
  private static final String SETTINGS_KIND_NAME = "settings";

  private final DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

  private Entity getSettingsByUserEmail(String userEmailAddress) throws NoSuchEntityException {
    try {
      Key userEmailAddressKey = KeyFactory.createKey(SETTINGS_KIND_NAME, userEmailAddress);
      Entity settingsEntity = datastoreService.get(userEmailAddressKey);
      return settingsEntity;
    } catch (EntityNotFoundException e) {
      throw new NoSuchEntityException();
    }
  }

  public void deleteSettingsByUser(String userEmailAddress) {
    Key userEmailAddressKey = KeyFactory.createKey(SETTINGS_KIND_NAME, userEmailAddress);
    datastoreService.delete(userEmailAddressKey);
  }

  public Settings getSettings(String userEamilAddress) throws NoSuchEntityException {
    Entity result = getSettingsByUserEmail(userEamilAddress);
    Settings Settings = convertPopulatedEntityIntoSettings(result);
    return Settings;
  }

  private Settings convertPopulatedEntityIntoSettings(Entity entity) {
    Settings settings = new Settings();
    for (String settingKey : Settings.KEYS_OF_USER_ADJUSTABLE_SETTINGS) {
      if (entity.getProperty(settingKey) != null) {
        settings.setSetting(settingKey, (String) entity.getProperty(settingKey));
      }
    }
    return settings;
  }

  public void setUserSetting(String userEmailAddress, String settingKey, String settingValue) {
    if (Settings.KEYS_OF_USER_ADJUSTABLE_SETTINGS.contains((settingKey))) {
      try {
        Entity settingsEntity = getSettingsByUserEmail(userEmailAddress);
        settingsEntity.setUnindexedProperty(settingKey, settingValue);
        datastoreService.put(settingsEntity);
      } catch (NoSuchEntityException e) {
        Entity settingsEntity = new Entity(SETTINGS_KIND_NAME, userEmailAddress);
        settingsEntity.setUnindexedProperty(settingKey, settingValue);
        datastoreService.put(settingsEntity);
      }
    }
  }
}
