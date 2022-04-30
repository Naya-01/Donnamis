package be.vinci.pae.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.util.List;

public class JsonViews<T> {

  private static final ObjectMapper jsonMapper = JsonMapper.builder()
      .findAndAddModules()
      .build();


  static {

    //Disable the unknown properties because they can make error if they stay enabled.
    jsonMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
  }


  /**
   * Filter a list of object.
   *
   * @param list a list that need to be filtered
   * @param type : class type to filter
   * @param <T>  the type of the list
   * @return a filtered list
   */
  public static <T> List<T> filterPublicJsonViewAsList(List<T> list, Class<T> type) {
    try {
      JavaType javaType = jsonMapper.getTypeFactory().constructCollectionType(List.class, type);
      // serialize using JSON Views : public view (all fields not required in the
      // views are not serialized)
      String publicItemListAsString = jsonMapper.writerWithView(Views.Public.class)
          .writeValueAsString(list);
      // deserialize using JSON Views : Public View (all fields that are not serialized
      // are set to their default values in the POJOs)
      return jsonMapper.readerWithView(Views.Public.class).forType(javaType)
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
   * @param type : class type to filter
   * @param <T>  the type of the object
   * @return a filtered object
   */
  public static <T> T filterPublicJsonView(T item, Class<T> type) {
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
