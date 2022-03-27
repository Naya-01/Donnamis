package be.vinci.pae.main;

import be.vinci.pae.utils.ApplicationBinder;
import be.vinci.pae.utils.Config;
import be.vinci.pae.utils.Log;
import be.vinci.pae.utils.WebExceptionMapper;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Main class.
 */
public class Main {

  /**
   * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
   *
   * @return Grizzly HTTP server.
   */
  public static HttpServer startServer() {
    // create a resource config that scans for JAX-RS resources and providers
    // in vinci.be package
    final ResourceConfig rc = new ResourceConfig().packages("be.vinci.pae.ihm")
        .register(JacksonFeature.class)
        .register(ApplicationBinder.class)
        .register(WebExceptionMapper.class)
        .register(MultiPartFeature.class);

    // create and start a new instance of grizzly http server
    // exposing the Jersey application at BASE_URI
    return GrizzlyHttpServerFactory.createHttpServer(URI.create(Config.getProperty("BaseUri")), rc);
  }

  /**
   * Main method.
   *
   * @param args : Array of arguments
   * @throws IOException : If properties file not find
   */
  public static void main(String[] args) throws IOException {

    Config.load("prod.properties");
    Log.config();

    //Création des dossiers possiblement manquants
    String[] paths = {"img//", "img//profils//", "img//objects//"};
    for (String p : paths) {
      String directoryName = Config.getProperty("ImagePath") + p;
      File directory = new File(directoryName);
      if (!directory.exists()) {
        directory.mkdir();
      }
    }

    final HttpServer server = startServer();
    System.out.println(String.format("Jersey app started with WADL available at "
        + "%sapplication.wadl\nHit enter to stop it...", Config.getProperty("BaseUri")));
    System.in.read();
    server.stop();
  }
}

