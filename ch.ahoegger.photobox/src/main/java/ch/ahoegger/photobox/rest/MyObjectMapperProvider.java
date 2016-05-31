package ch.ahoegger.photobox.rest;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

@Provider
public class MyObjectMapperProvider implements ContextResolver<ObjectMapper> {

  final ObjectMapper defaultObjectMapper;

  public MyObjectMapperProvider() {
    defaultObjectMapper = createDefaultMapper();
  }

  @Override
  public ObjectMapper getContext(Class<?> type) {
    return defaultObjectMapper;

  }

  private static ObjectMapper createDefaultMapper() {
    final ObjectMapper result = new ObjectMapper() {
      @Override
      public <W extends ObjectWriter> W writerFor(Class<?> rootType) {
        return super.writerFor(rootType);
      }

    };
//    SerializationConfig serializationConfig = result.getSerializationConfig();
//    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
//    serializationConfig.with(df);
//
//    result.setDateFormat(df);

    result.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    return result;
  }

}
