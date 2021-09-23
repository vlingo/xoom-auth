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

public class GroupResourceTest extends AbstractRestTest {

  @Test
  public void testEmptyResponse() {
    given()
      .when()
      .get("/tenants")
      .then()
      .statusCode(200)
      .body(is(equalTo("[]")));
  }

  private GroupData saveExampleData(GroupData data) {
    return given()
      .when()
      .body(data)
      .post("/tenants")
      .then()
      .statusCode(201)
      .extract()
      .body()
      .as(GroupData.class);
  }

  @Test
  public void provisionGroup() {
    GroupData firstData = GroupData.from("1", "first-group-name", "first-group-description", "first-group-tenantId");
    GroupData result = given()
        .when()
        .body(firstData)
        .post("/tenants/{tenantId}/groups")
        .then()
        .statusCode(201)
        .extract()
        .body()
        .as(GroupData.class);

    assertNotNull(result.id);
    assertEquals(result.name, "first-group-name");
    assertEquals(result.description, "first-group-description");
    assertEquals(result.tenantId, "first-group-tenantId");
  }

  @Test
  public void changeDescription() {
    GroupData firstData = GroupData.from("1", "first-group-name", "first-group-description", "first-group-tenantId");
    firstData = saveExampleData(firstData);
    GroupData result = given()
        .when()
        .body(firstData)
        .patch("/tenants/{tenantId}/groups/{groupName}/description")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(GroupData.class);

    assertNotNull(result.id);
    assertEquals(result.name, "first-group-name");
    assertEquals(result.description, "first-group-description");
    assertEquals(result.tenantId, "first-group-tenantId");
  }

  @Test
  public void assignGroup() {
    GroupData firstData = GroupData.from("1", "first-group-name", "first-group-description", "first-group-tenantId");
    firstData = saveExampleData(firstData);
    GroupData result = given()
        .when()
        .body(firstData)
        .put("/tenants/{tenantId}/groups/{groupName}/groups/{innerGroupName}")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(GroupData.class);

    assertNotNull(result.id);
    assertEquals(result.name, "first-group-name");
    assertEquals(result.description, "first-group-description");
    assertEquals(result.tenantId, "first-group-tenantId");
  }

  @Test
  public void unassignGroup() {
    GroupData firstData = GroupData.from("1", "first-group-name", "first-group-description", "first-group-tenantId");
    firstData = saveExampleData(firstData);
    given()
        .when()
        .delete("/tenants/{tenantId}/groups/{groupName}/groups/{innerGroupName}")
        .then()
        .statusCode(200);

  }

  @Test
  public void assignUser() {
    GroupData firstData = GroupData.from("1", "first-group-name", "first-group-description", "first-group-tenantId");
    firstData = saveExampleData(firstData);
    GroupData result = given()
        .when()
        .body(firstData)
        .put("/tenants/{tenantId}/groups/{groupName}/users/{username}")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(GroupData.class);

    assertNotNull(result.id);
    assertEquals(result.name, "first-group-name");
    assertEquals(result.description, "first-group-description");
    assertEquals(result.tenantId, "first-group-tenantId");
  }

  @Test
  public void unassignUser() {
    GroupData firstData = GroupData.from("1", "first-group-name", "first-group-description", "first-group-tenantId");
    firstData = saveExampleData(firstData);
    given()
        .when()
        .delete("/tenants/{tenantId}/groups/{groupName}/users/{username}")
        .then()
        .statusCode(200);

  }

  @Test
  public void groups() {
    GroupData firstData = GroupData.from("1", "first-group-name", "first-group-description", "first-group-tenantId");
    firstData = saveExampleData(firstData);
    GroupData[] result = given()
        .when()
        .get("/tenants")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(GroupData[].class);

    assertNotNull(result[0].id);
    assertEquals(result[0].name, "first-group-name");
    assertEquals(result[0].description, "first-group-description");
    assertEquals(result[0].tenantId, "first-group-tenantId");
  }

  @Test
  public void groupOf() {
    GroupData firstData = GroupData.from("1", "first-group-name", "first-group-description", "first-group-tenantId");
    firstData = saveExampleData(firstData);
    GroupData result = given()
        .when()
        .get("/tenants/"+firstData.id+"")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(GroupData.class);

    assertNotNull(result.id);
    assertEquals(result.name, "first-group-name");
    assertEquals(result.description, "first-group-description");
    assertEquals(result.tenantId, "first-group-tenantId");
  }
}
