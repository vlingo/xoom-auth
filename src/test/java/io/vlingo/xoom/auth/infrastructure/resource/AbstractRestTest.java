package io.vlingo.xoom.auth.infrastructure.resource;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.parsing.Parser;
import io.restassured.specification.RequestSpecification;
import io.vlingo.xoom.turbo.ComponentRegistry;
import io.vlingo.xoom.turbo.exchange.ExchangeInitializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import io.vlingo.xoom.auth.infrastructure.*;

import io.vlingo.xoom.lattice.grid.Grid;
import io.vlingo.xoom.turbo.exchange.ExchangeInitializer;

public class AbstractRestTest {

  protected int port;
  private XoomInitializer xoom;

  @BeforeAll
  public static void init() {
    RestAssured.defaultParser = Parser.JSON;
  }

  @BeforeEach
  public void setUp() throws Exception {
    resolvePort();
    ComponentRegistry.clear();
    ComponentRegistry.register(ExchangeInitializer.class, new MockExchangeInitializer());
    XoomInitializer.main(new String[]{"-Dport=" + port});
    xoom = XoomInitializer.instance();
    Boolean startUpSuccess = xoom.server().startUp().await(100);
    System.out.println("==== Test Server running on " + port);
    assertThat(startUpSuccess, is(equalTo(true)));
  }

  @AfterEach
  public void cleanUp() throws InterruptedException {
    System.out.println("==== Test Server shutting down ");
    xoom.terminateWorld();
    waitServerClose();
  }

  public RequestSpecification given() {
    return io.restassured.RestAssured.given()
    .config(RestAssured.config.objectMapperConfig(new ObjectMapperConfig(ObjectMapperType.GSON).gsonObjectMapperFactory((type, s) ->
      new GsonBuilder()
        .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (source, typeOfTarget, context) -> new JsonPrimitive(source.toEpochDay()))
        .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfTarget, context) -> LocalDate.ofEpochDay(Long.parseLong(json.getAsJsonPrimitive().getAsString())))
        .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (source, typeOfTarget, context) -> new JsonPrimitive(source.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()))
        .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfTarget, context) -> LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(json.getAsJsonPrimitive().getAsString())), ZoneOffset.UTC))
        .create())))
    .filter(new RequestLoggingFilter())
    .filter(new ResponseLoggingFilter())
    .port(port)
    .accept(ContentType.JSON)
    .contentType(ContentType.JSON);
  }

  private void waitServerClose() throws InterruptedException {
    while(xoom != null && xoom.server() != null && !xoom.server().isStopped()) {
      Thread.sleep(100);
    }
  }

  private void resolvePort() {
    port = (int) (Math.random() * 55535) + 10000;
  }
}
class MockExchangeInitializer implements ExchangeInitializer {

  @Override
  public void init(Grid grid) {

  }

}