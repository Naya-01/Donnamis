package be.vinci.pae.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class Filters<T> {

  private final ObjectMapper jsonMapper = new ObjectMapper();
  private Class<T> type;

  public Filters(Class<T> type) {
    this.type = type;
  }

  /**
   * Filter a list of object.
   *
   * @param list a list that need to be filtered
   * @param <T>  the type of the list
   * @return a filtered list
   */
  public <T> List<T> filterPublicJsonViewAsList(List<T> list) {
    try {
      JavaType type = jsonMapper.getTypeFactory().constructCollectionType(List.class, this.type);
      // serialize using JSON Views : public view (all fields not required in the
      // views are not serialized)
      String publicItemListAsString = jsonMapper.writerWithView(Views.Public.class)
          .writeValueAsString(list);
      // deserialize using JSON Views : Public View (all fields that are not serialized
      // are set to their default values in the POJOs)
      return jsonMapper.readerWithView(Views.Public.class).forType(type)
          .readValue(publicItemListAsString);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }

  }

  /**
   * Filter an object.
   *
   * @param item an object that need to be filtered
   * @param <T>  the type of the object
   * @return a filtered object
   */
  public <T> T filterPublicJsonView(T item) {
    try {
      // serialize using JSON Views : public view (all fields not required in the
      // views are not serialized)
      String publicItemAsString = jsonMapper.writerWithView(Views.Public.class)
          .writeValueAsString(item);
      // deserialize using JSON Views : Public View (all fields that are not serialized
      // are set to their default values in the POJO)
      return jsonMapper.readerWithView(Views.Public.class).forType(type)
          .readValue(publicItemAsString);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return null;
    }

  }
}