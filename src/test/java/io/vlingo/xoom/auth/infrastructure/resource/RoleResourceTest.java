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

public class RoleResourceTest extends AbstractRestTest {

  @Test
  public void testEmptyResponse() {
    given()
      .when()
      .get("/tenants")
      .then()
      .statusCode(200)
      .body(is(equalTo("[]")));
  }

  private RoleData saveExampleData(RoleData data) {
    return given()
      .when()
      .body(data)
      .post("/tenants")
      .then()
      .statusCode(201)
      .extract()
      .body()
      .as(RoleData.class);
  }

  @Test
  public void provisionRole() {
    RoleData firstData = RoleData.from("1", "first-role-tenantId", "first-role-name", "first-role-description");
    RoleData result = given()
        .when()
        .body(firstData)
        .post("/tenants/{tenantId}/roles")
        .then()
        .statusCode(201)
        .extract()
        .body()
        .as(RoleData.class);

    assertNotNull(result.id);
    assertEquals(result.tenantId, "first-role-tenantId");
    assertEquals(result.name, "first-role-name");
    assertEquals(result.description, "first-role-description");
  }

  @Test
  public void changeDescription() {
    RoleData firstData = RoleData.from("1", "first-role-tenantId", "first-role-name", "first-role-description");
    firstData = saveExampleData(firstData);
    RoleData result = given()
        .when()
        .body(firstData)
        .patch("/tenants/{tenantId}/roles/{roleName}/description")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(RoleData.class);

    assertNotNull(result.id);
    assertEquals(result.tenantId, "first-role-tenantId");
    assertEquals(result.name, "first-role-name");
    assertEquals(result.description, "first-role-description");
  }

  @Test
  public void assignGroup() {
    RoleData firstData = RoleData.from("1", "first-role-tenantId", "first-role-name", "first-role-description");
    firstData = saveExampleData(firstData);
    RoleData result = given()
        .when()
        .body(firstData)
        .put("/tenants/{tenantId}/roles/{roleName}/groups")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(RoleData.class);

    assertNotNull(result.id);
    assertEquals(result.tenantId, "first-role-tenantId");
    assertEquals(result.name, "first-role-name");
    assertEquals(result.description, "first-role-description");
  }

  @Test
  public void unassignGroup() {
    RoleData firstData = RoleData.from("1", "first-role-tenantId", "first-role-name", "first-role-description");
    firstData = saveExampleData(firstData);
    given()
        .when()
        .delete("/tenants/{tenantId}/roles/{roleName}/groups/{groupName}")
        .then()
        .statusCode(200);

  }

  @Test
  public void assignUser() {
    RoleData firstData = RoleData.from("1", "first-role-tenantId", "first-role-name", "first-role-description");
    firstData = saveExampleData(firstData);
    RoleData result = given()
        .when()
        .body(firstData)
        .put("/tenants/{tenantId}/roles/{roleName}/users")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(RoleData.class);

    assertNotNull(result.id);
    assertEquals(result.tenantId, "first-role-tenantId");
    assertEquals(result.name, "first-role-name");
    assertEquals(result.description, "first-role-description");
  }

  @Test
  public void unassignUser() {
    RoleData firstData = RoleData.from("1", "first-role-tenantId", "first-role-name", "first-role-description");
    firstData = saveExampleData(firstData);
    given()
        .when()
        .delete("/tenants/{tenantId}/roles/{roleName}/users/{username}")
        .then()
        .statusCode(200);

  }

  @Test
  public void attach() {
    RoleData firstData = RoleData.from("1", "first-role-tenantId", "first-role-name", "first-role-description");
    firstData = saveExampleData(firstData);
    RoleData result = given()
        .when()
        .body(firstData)
        .put("/tenants/{tenantId}/roles/{roleName}/permissions")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(RoleData.class);

    assertNotNull(result.id);
    assertEquals(result.tenantId, "first-role-tenantId");
    assertEquals(result.name, "first-role-name");
    assertEquals(result.description, "first-role-description");
  }

  @Test
  public void detach() {
    RoleData firstData = RoleData.from("1", "first-role-tenantId", "first-role-name", "first-role-description");
    firstData = saveExampleData(firstData);
    given()
        .when()
        .delete("/tenants/{tenantId}/roles/{roleName}/permissions/{permissionName}")
        .then()
        .statusCode(200);

  }

  @Test
  public void roles() {
    RoleData firstData = RoleData.from("1", "first-role-tenantId", "first-role-name", "first-role-description");
    firstData = saveExampleData(firstData);
    RoleData[] result = given()
        .when()
        .get("/tenants")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(RoleData[].class);

    assertNotNull(result[0].id);
    assertEquals(result[0].tenantId, "first-role-tenantId");
    assertEquals(result[0].name, "first-role-name");
    assertEquals(result[0].description, "first-role-description");
  }

  @Test
  public void roleOf() {
    RoleData firstData = RoleData.from("1", "first-role-tenantId", "first-role-name", "first-role-description");
    firstData = saveExampleData(firstData);
    RoleData result = given()
        .when()
        .get("/tenants/"+firstData.id+"")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(RoleData.class);

    assertNotNull(result.id);
    assertEquals(result.tenantId, "first-role-tenantId");
    assertEquals(result.name, "first-role-name");
    assertEquals(result.description, "first-role-description");
  }
}
