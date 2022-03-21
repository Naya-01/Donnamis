package be.vinci.pae.utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {

  private static Logger logger;

  /**
   * Config the Logger for the files & console log.
   */
  public static void config() {
    logger = Logger.getLogger("Log");
    logger.setLevel(Level.INFO);
    try {
      FileHandler fileHandler = new FileHandler("LogFile%g.log", 50000, 5, true);
      fileHandler.setLevel(Level.ALL);
      logger.addHandler(fileHandler);
      SimpleFormatter formatter = new SimpleFormatter();
      fileHandler.setFormatter(formatter);
      logger.info("Init Logger");
      System.out.println(Logger.getLogger("Log"));
      System.out.println(logger);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
