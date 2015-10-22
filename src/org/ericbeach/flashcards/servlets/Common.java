package org.ericbeach.flashcards.servlets;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Common {
  private static final Logger log = Logger.getLogger(Common.class.getName());

  public static List<Long> getListOfFlashCardIdsToQueryFromString(String httpParamValue) {
    List<Long> listOfFlashCardItsToFetch = new ArrayList<Long>();

    String pattern = "^[0-9]+(,[0-9]+)*$";
    if (!httpParamValue.matches(pattern)) {
      return listOfFlashCardItsToFetch;
    }

    String[] flashCardIds = httpParamValue.split(",");
    for (String flashCardId : flashCardIds) {
      listOfFlashCardItsToFetch.add(Long.parseLong(flashCardId));
    }

    log.info("Parsed " + httpParamValue.length() + " length string and returned "
        + flashCardIds.length + " flashCardIds");
    return listOfFlashCardItsToFetch;
  }
}
