package be.vinci.pae.utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {

  private static Logger logger;

  /**
   * Config the Logger for the files & console log.
   */
  public static void config() {
    logger = Logger.getLogger("Log");
    try {
      FileHandler fileHandler = new FileHandler("LogFile%g.log", 75000, 5, true);
      logger.addHandler(fileHandler);
      SimpleFormatter formatter = new SimpleFormatter();
      fileHandler.setFormatter(formatter);
      logger.info("Init Logger");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
