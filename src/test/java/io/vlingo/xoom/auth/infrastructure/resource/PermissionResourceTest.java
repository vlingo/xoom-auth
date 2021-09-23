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

public class PermissionResourceTest extends AbstractRestTest {

  @Test
  public void testEmptyResponse() {
    given()
      .when()
      .get("/tenants")
      .then()
      .statusCode(200)
      .body(is(equalTo("[]")));
  }

  private PermissionData saveExampleData(PermissionData data) {
    return given()
      .when()
      .body(data)
      .post("/tenants")
      .then()
      .statusCode(201)
      .extract()
      .body()
      .as(PermissionData.class);
  }

  @Test
  public void provisionPermission() {
    PermissionData firstData = PermissionData.from("1", new HashSet<>(), "first-permission-description", "first-permission-name", "first-permission-tenantId");
    PermissionData result = given()
        .when()
        .body(firstData)
        .post("/tenants/{tenantId}/permissions")
        .then()
        .statusCode(201)
        .extract()
        .body()
        .as(PermissionData.class);

    assertNotNull(result.id);
    assertNotNull(result.constraints);
    assertEquals(result.description, "first-permission-description");
    assertEquals(result.name, "first-permission-name");
    assertEquals(result.tenantId, "first-permission-tenantId");
  }

  @Test
  public void enforce() {
    PermissionData firstData = PermissionData.from("1", new HashSet<>(), "first-permission-description", "first-permission-name", "first-permission-tenantId");
    firstData = saveExampleData(firstData);
    PermissionData result = given()
        .when()
        .body(firstData)
        .patch("/tenants/{tenantId}/permissions/{permissionName}/constraints")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(PermissionData.class);

    assertNotNull(result.id);
    assertNotNull(result.constraints);
    assertEquals(result.description, "first-permission-description");
    assertEquals(result.name, "first-permission-name");
    assertEquals(result.tenantId, "first-permission-tenantId");
  }

  @Test
  public void enforceReplacement() {
    PermissionData firstData = PermissionData.from("1", new HashSet<>(), "first-permission-description", "first-permission-name", "first-permission-tenantId");
    firstData = saveExampleData(firstData);
    PermissionData result = given()
        .when()
        .body(firstData)
        .patch("/tenants/{tenantId}/permissions/{permissionName}/constraints/{constraintName}")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(PermissionData.class);

    assertNotNull(result.id);
    assertNotNull(result.constraints);
    assertEquals(result.description, "first-permission-description");
    assertEquals(result.name, "first-permission-name");
    assertEquals(result.tenantId, "first-permission-tenantId");
  }

  @Test
  public void forget() {
    PermissionData firstData = PermissionData.from("1", new HashSet<>(), "first-permission-description", "first-permission-name", "first-permission-tenantId");
    firstData = saveExampleData(firstData);
    given()
        .when()
        .delete("/tenants/{tenantId}/permissions/{permissionName}/constraints/{constraintName}")
        .then()
        .statusCode(200);

  }

  @Test
  public void changeDescription() {
    PermissionData firstData = PermissionData.from("1", new HashSet<>(), "first-permission-description", "first-permission-name", "first-permission-tenantId");
    firstData = saveExampleData(firstData);
    PermissionData result = given()
        .when()
        .body(firstData)
        .patch("/tenants/{tenantId}/permissions/{permissionName}/description")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(PermissionData.class);

    assertNotNull(result.id);
    assertNotNull(result.constraints);
    assertEquals(result.description, "first-permission-description");
    assertEquals(result.name, "first-permission-name");
    assertEquals(result.tenantId, "first-permission-tenantId");
  }

  @Test
  public void permissions() {
    PermissionData firstData = PermissionData.from("1", new HashSet<>(), "first-permission-description", "first-permission-name", "first-permission-tenantId");
    firstData = saveExampleData(firstData);
    PermissionData[] result = given()
        .when()
        .get("/tenants")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(PermissionData[].class);

    assertNotNull(result[0].id);
    assertNotNull(result[0].constraints);
    assertEquals(result[0].description, "first-permission-description");
    assertEquals(result[0].name, "first-permission-name");
    assertEquals(result[0].tenantId, "first-permission-tenantId");
  }

  @Test
  public void permissionOf() {
    PermissionData firstData = PermissionData.from("1", new HashSet<>(), "first-permission-description", "first-permission-name", "first-permission-tenantId");
    firstData = saveExampleData(firstData);
    PermissionData result = given()
        .when()
        .get("/tenants/"+firstData.id+"")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(PermissionData.class);

    assertNotNull(result.id);
    assertNotNull(result.constraints);
    assertEquals(result.description, "first-permission-description");
    assertEquals(result.name, "first-permission-name");
    assertEquals(result.tenantId, "first-permission-tenantId");
  }
}
