package io.vlingo.xoom.auth.infrastructure.resource;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import io.vlingo.xoom.auth.infrastructure.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

public class TenantResourceTest extends AbstractRestTest {

  @Test
  public void testEmptyResponse() {
    given()
      .when()
      .get("/tenants")
      .then()
      .statusCode(200)
      .body(is(equalTo("[]")));
  }

  private TenantData saveExampleData(TenantData data) {
    return given()
      .when()
      .body(data)
      .post("/tenants")
      .then()
      .statusCode(201)
      .extract()
      .body()
      .as(TenantData.class);
  }

  @Test
  public void subscribeFor() {
    TenantData firstData = TenantData.from("1", "first-tenant-name", "first-tenant-description", true);
    TenantData result = given()
        .when()
        .body(firstData)
        .post("/tenants")
        .then()
        .statusCode(201)
        .extract()
        .body()
        .as(TenantData.class);

    assertNotNull(result.id);
    assertEquals(result.name, "first-tenant-name");
    assertEquals(result.description, "first-tenant-description");
    assertEquals(result.active, true);
  }

  @Test
  public void activate() {
    TenantData firstData = TenantData.from("1", "first-tenant-name", "first-tenant-description", true);
    firstData = saveExampleData(firstData);
    TenantData result = given()
        .when()
        .body(firstData)
        .patch("/tenants/"+firstData.id+"/activate")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(TenantData.class);

    assertNotNull(result.id);
    assertEquals(result.name, "first-tenant-name");
    assertEquals(result.description, "first-tenant-description");
    assertEquals(result.active, true);
  }

  @Test
  public void deactivate() {
    TenantData firstData = TenantData.from("1", "first-tenant-name", "first-tenant-description", true);
    firstData = saveExampleData(firstData);
    TenantData result = given()
        .when()
        .body(firstData)
        .patch("/tenants/"+firstData.id+"/deactivate")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(TenantData.class);

    assertNotNull(result.id);
    assertEquals(result.name, "first-tenant-name");
    assertEquals(result.description, "first-tenant-description");
    assertEquals(result.active, true);
  }

  @Test
  public void changeDescription() {
    TenantData firstData = TenantData.from("1", "first-tenant-name", "first-tenant-description", true);
    firstData = saveExampleData(firstData);
    TenantData result = given()
        .when()
        .body(firstData)
        .patch("/tenants/"+firstData.id+"/description")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(TenantData.class);

    assertNotNull(result.id);
    assertEquals(result.name, "first-tenant-name");
    assertEquals(result.description, "first-tenant-description");
    assertEquals(result.active, true);
  }

  @Test
  public void changeName() {
    TenantData firstData = TenantData.from("1", "first-tenant-name", "first-tenant-description", true);
    firstData = saveExampleData(firstData);
    TenantData result = given()
        .when()
        .body(firstData)
        .patch("/tenants/"+firstData.id+"/name")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(TenantData.class);

    assertNotNull(result.id);
    assertEquals(result.name, "first-tenant-name");
    assertEquals(result.description, "first-tenant-description");
    assertEquals(result.active, true);
  }

  @Test
  public void tenants() {
    TenantData firstData = TenantData.from("1", "first-tenant-name", "first-tenant-description", true);
    firstData = saveExampleData(firstData);
    TenantData[] result = given()
        .when()
        .get("/tenants")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(TenantData[].class);

    assertNotNull(result[0].id);
    assertEquals(result[0].name, "first-tenant-name");
    assertEquals(result[0].description, "first-tenant-description");
    assertEquals(result[0].active, true);
  }

  @Test
  public void tenantOf() {
    TenantData firstData = TenantData.from("1", "first-tenant-name", "first-tenant-description", true);
    firstData = saveExampleData(firstData);
    TenantData result = given()
        .when()
        .get("/tenants/"+firstData.id+"")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(TenantData.class);

    assertNotNull(result.id);
    assertEquals(result.name, "first-tenant-name");
    assertEquals(result.description, "first-tenant-description");
    assertEquals(result.active, true);
  }
}
