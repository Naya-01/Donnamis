package be.vinci.pae.business.utils;

import java.util.HashMap;

public class Status {

  private static HashMap<String, String> statusMap = new HashMap<>();

  /**
   * Load the map with the different Key/value.
   */
  public static void loadMap() {
    statusMap.put("available", "published");
    statusMap.put("interested", "published");
    statusMap.put("assigned", "assigned");
    statusMap.put("given", "received");
    statusMap.put("cancelled", "cancelled");
  }

  /**
   * Get the interest status by the object status.
   *
   * @param status object status
   * @return status if exist else null
   */
  public static String getStatus(String status) {
    return statusMap.get(status);
  }
}
