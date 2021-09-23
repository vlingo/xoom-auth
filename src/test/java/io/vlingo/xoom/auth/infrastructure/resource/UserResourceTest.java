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

public class UserResourceTest extends AbstractRestTest {

  @Test
  public void testEmptyResponse() {
    given()
      .when()
      .get("/tenants")
      .then()
      .statusCode(200)
      .body(is(equalTo("[]")));
  }

  private UserData saveExampleData(UserData data) {
    return given()
      .when()
      .body(data)
      .post("/tenants")
      .then()
      .statusCode(201)
      .extract()
      .body()
      .as(UserData.class);
  }

  @Test
  public void registerUser() {
    UserData firstData = UserData.from("1", "first-user-tenantId", "first-user-username", true, new HashSet<>(), ProfileData.from("first-user-profile-emailAddress", PersonNameData.from("first-user-profile-name-given", "first-user-profile-name-family", "first-user-profile-name-second"), "first-user-profile-phone"));
    UserData result = given()
        .when()
        .body(firstData)
        .post("/tenants/{tenantId}/users")
        .then()
        .statusCode(201)
        .extract()
        .body()
        .as(UserData.class);

    assertNotNull(result.id);
    assertEquals(result.tenantId, "first-user-tenantId");
    assertEquals(result.username, "first-user-username");
    assertEquals(result.active, true);
    assertNotNull(result.credentials);
    assertEquals(result.profile.emailAddress, "first-user-profile-emailAddress");
    assertEquals(result.profile.name.given, "first-user-profile-name-given");
    assertEquals(result.profile.name.family, "first-user-profile-name-family");
    assertEquals(result.profile.name.second, "first-user-profile-name-second");
    assertEquals(result.profile.phone, "first-user-profile-phone");
  }

  @Test
  public void activate() {
    UserData firstData = UserData.from("1", "first-user-tenantId", "first-user-username", true, new HashSet<>(), ProfileData.from("first-user-profile-emailAddress", PersonNameData.from("first-user-profile-name-given", "first-user-profile-name-family", "first-user-profile-name-second"), "first-user-profile-phone"));
    firstData = saveExampleData(firstData);
    UserData result = given()
        .when()
        .body(firstData)
        .patch("/tenants/{tenantId}/users/{username}/activate")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(UserData.class);

    assertNotNull(result.id);
    assertEquals(result.tenantId, "first-user-tenantId");
    assertEquals(result.username, "first-user-username");
    assertEquals(result.active, true);
    assertNotNull(result.credentials);
    assertEquals(result.profile.emailAddress, "first-user-profile-emailAddress");
    assertEquals(result.profile.name.given, "first-user-profile-name-given");
    assertEquals(result.profile.name.family, "first-user-profile-name-family");
    assertEquals(result.profile.name.second, "first-user-profile-name-second");
    assertEquals(result.profile.phone, "first-user-profile-phone");
  }

  @Test
  public void deactivate() {
    UserData firstData = UserData.from("1", "first-user-tenantId", "first-user-username", true, new HashSet<>(), ProfileData.from("first-user-profile-emailAddress", PersonNameData.from("first-user-profile-name-given", "first-user-profile-name-family", "first-user-profile-name-second"), "first-user-profile-phone"));
    firstData = saveExampleData(firstData);
    UserData result = given()
        .when()
        .body(firstData)
        .patch("/tenants/{tenantId}/users/{username}/deactivate")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(UserData.class);

    assertNotNull(result.id);
    assertEquals(result.tenantId, "first-user-tenantId");
    assertEquals(result.username, "first-user-username");
    assertEquals(result.active, true);
    assertNotNull(result.credentials);
    assertEquals(result.profile.emailAddress, "first-user-profile-emailAddress");
    assertEquals(result.profile.name.given, "first-user-profile-name-given");
    assertEquals(result.profile.name.family, "first-user-profile-name-family");
    assertEquals(result.profile.name.second, "first-user-profile-name-second");
    assertEquals(result.profile.phone, "first-user-profile-phone");
  }

  @Test
  public void addCredential() {
    UserData firstData = UserData.from("1", "first-user-tenantId", "first-user-username", true, new HashSet<>(), ProfileData.from("first-user-profile-emailAddress", PersonNameData.from("first-user-profile-name-given", "first-user-profile-name-family", "first-user-profile-name-second"), "first-user-profile-phone"));
    firstData = saveExampleData(firstData);
    UserData result = given()
        .when()
        .body(firstData)
        .put("/tenants/{tenantId}/users/{username}/credentials")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(UserData.class);

    assertNotNull(result.id);
    assertEquals(result.tenantId, "first-user-tenantId");
    assertEquals(result.username, "first-user-username");
    assertEquals(result.active, true);
    assertNotNull(result.credentials);
    assertEquals(result.profile.emailAddress, "first-user-profile-emailAddress");
    assertEquals(result.profile.name.given, "first-user-profile-name-given");
    assertEquals(result.profile.name.family, "first-user-profile-name-family");
    assertEquals(result.profile.name.second, "first-user-profile-name-second");
    assertEquals(result.profile.phone, "first-user-profile-phone");
  }

  @Test
  public void removeCredential() {
    UserData firstData = UserData.from("1", "first-user-tenantId", "first-user-username", true, new HashSet<>(), ProfileData.from("first-user-profile-emailAddress", PersonNameData.from("first-user-profile-name-given", "first-user-profile-name-family", "first-user-profile-name-second"), "first-user-profile-phone"));
    firstData = saveExampleData(firstData);
    given()
        .when()
        .delete("/tenants/{tenantId}/users/{username}/credentials/{authority}")
        .then()
        .statusCode(200);

  }

  @Test
  public void replaceCredential() {
    UserData firstData = UserData.from("1", "first-user-tenantId", "first-user-username", true, new HashSet<>(), ProfileData.from("first-user-profile-emailAddress", PersonNameData.from("first-user-profile-name-given", "first-user-profile-name-family", "first-user-profile-name-second"), "first-user-profile-phone"));
    firstData = saveExampleData(firstData);
    UserData result = given()
        .when()
        .body(firstData)
        .patch("/tenants/{tenantId}/users/{username}/credentials/{authority}")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(UserData.class);

    assertNotNull(result.id);
    assertEquals(result.tenantId, "first-user-tenantId");
    assertEquals(result.username, "first-user-username");
    assertEquals(result.active, true);
    assertNotNull(result.credentials);
    assertEquals(result.profile.emailAddress, "first-user-profile-emailAddress");
    assertEquals(result.profile.name.given, "first-user-profile-name-given");
    assertEquals(result.profile.name.family, "first-user-profile-name-family");
    assertEquals(result.profile.name.second, "first-user-profile-name-second");
    assertEquals(result.profile.phone, "first-user-profile-phone");
  }

  @Test
  public void replaceProfile() {
    UserData firstData = UserData.from("1", "first-user-tenantId", "first-user-username", true, new HashSet<>(), ProfileData.from("first-user-profile-emailAddress", PersonNameData.from("first-user-profile-name-given", "first-user-profile-name-family", "first-user-profile-name-second"), "first-user-profile-phone"));
    firstData = saveExampleData(firstData);
    UserData result = given()
        .when()
        .body(firstData)
        .patch("/tenants/{tenantId}/users/{username}/profile")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(UserData.class);

    assertNotNull(result.id);
    assertEquals(result.tenantId, "first-user-tenantId");
    assertEquals(result.username, "first-user-username");
    assertEquals(result.active, true);
    assertNotNull(result.credentials);
    assertEquals(result.profile.emailAddress, "first-user-profile-emailAddress");
    assertEquals(result.profile.name.given, "first-user-profile-name-given");
    assertEquals(result.profile.name.family, "first-user-profile-name-family");
    assertEquals(result.profile.name.second, "first-user-profile-name-second");
    assertEquals(result.profile.phone, "first-user-profile-phone");
  }

  @Test
  public void users() {
    UserData firstData = UserData.from("1", "first-user-tenantId", "first-user-username", true, new HashSet<>(), ProfileData.from("first-user-profile-emailAddress", PersonNameData.from("first-user-profile-name-given", "first-user-profile-name-family", "first-user-profile-name-second"), "first-user-profile-phone"));
    firstData = saveExampleData(firstData);
    UserData[] result = given()
        .when()
        .get("/tenants")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(UserData[].class);

    assertNotNull(result[0].id);
    assertEquals(result[0].tenantId, "first-user-tenantId");
    assertEquals(result[0].username, "first-user-username");
    assertEquals(result[0].active, true);
    assertNotNull(result[0].credentials);
    assertEquals(result[0].profile.emailAddress, "first-user-profile-emailAddress");
    assertEquals(result[0].profile.name.given, "first-user-profile-name-given");
    assertEquals(result[0].profile.name.family, "first-user-profile-name-family");
    assertEquals(result[0].profile.name.second, "first-user-profile-name-second");
    assertEquals(result[0].profile.phone, "first-user-profile-phone");
  }

  @Test
  public void userOf() {
    UserData firstData = UserData.from("1", "first-user-tenantId", "first-user-username", true, new HashSet<>(), ProfileData.from("first-user-profile-emailAddress", PersonNameData.from("first-user-profile-name-given", "first-user-profile-name-family", "first-user-profile-name-second"), "first-user-profile-phone"));
    firstData = saveExampleData(firstData);
    UserData result = given()
        .when()
        .get("/tenants/"+firstData.id+"")
        .then()
        .statusCode(200)
        .extract()
        .body()
        .as(UserData.class);

    assertNotNull(result.id);
    assertEquals(result.tenantId, "first-user-tenantId");
    assertEquals(result.username, "first-user-username");
    assertEquals(result.active, true);
    assertNotNull(result.credentials);
    assertEquals(result.profile.emailAddress, "first-user-profile-emailAddress");
    assertEquals(result.profile.name.given, "first-user-profile-name-given");
    assertEquals(result.profile.name.family, "first-user-profile-name-family");
    assertEquals(result.profile.name.second, "first-user-profile-name-second");
    assertEquals(result.profile.phone, "first-user-profile-phone");
  }
}
