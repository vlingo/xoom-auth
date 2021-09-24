// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.auth.infrastructure.resource;

import static io.vlingo.xoom.common.serialization.JsonSerialization.deserialized;
import static io.vlingo.xoom.common.serialization.JsonSerialization.serialized;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import io.vlingo.xoom.auth.infrastructure.*;
import org.junit.Test;

import io.vlingo.xoom.auth.model.Credential;
import io.vlingo.xoom.http.Response;

public class UserResourceTest extends ResourceTest {
  private PermissionData permissionData;
  private RoleData roleData;
  private TenantData tenantData;
  private UserRegistrationData userData;

  @Test
  public void testThatUserDeactivatesAndActivates() {
    user();

    final Response patchUserDeactive = patchUserDeactivateRequestResponse(userData);
    assertEquals(Response.Status.Ok, patchUserDeactive.status);
    final UserRegistrationData deactivatedUserData = deserialized(patchUserDeactive.entity.content(), UserRegistrationData.class);
    assertFalse(deactivatedUserData.active);

    final Response getUserResponse1 = getUserRequestResponse(userData);
    assertEquals(Response.Status.Ok, getUserResponse1.status);
    final UserRegistrationData getDeactivatedUserData = deserialized(getUserResponse1.entity.content(), UserRegistrationData.class);
    assertFalse(getDeactivatedUserData.active);

    final Response patchUserActivate = this.patchUserActivateRequestResponse(userData);
    assertEquals(Response.Status.Ok, patchUserActivate.status);
    final UserRegistrationData activatedUserData = deserialized(patchUserActivate.entity.content(), UserRegistrationData.class);
    assertTrue(activatedUserData.active);

    final Response getUserResponse2 = getUserRequestResponse(userData);
    assertEquals(Response.Status.Ok, getUserResponse2.status);
    final UserRegistrationData getActivatedUserData = deserialized(getUserResponse2.entity.content(), UserRegistrationData.class);
    assertTrue(getActivatedUserData.active);
  }

  @Test
  public void testThatUserManagesCredentials() {
    user();

    // add

    final CredentialData newCredentialData = CredentialData.from("abc", "username1", "secret1", Credential.Type.OAUTH.name());
    final Response patchUserAddCredential = putUserAddCredentialRequestResponse(userData, newCredentialData);
    assertEquals(Response.Status.Ok, patchUserAddCredential.status);
    final UserRegistrationData newCredentialUserData = deserialized(patchUserAddCredential.entity.content(), UserRegistrationData.class);
    assertEquals(newCredentialData.authority, newCredentialUserData.credentialOf("abc").authority);
    assertEquals(newCredentialData.id, newCredentialUserData.credentialOf("abc").id);
    assertEquals(newCredentialData.type, newCredentialUserData.credentialOf("abc").type);

    final Response getUserResponse1 = getUserRequestResponse(userData);
    assertEquals(Response.Status.Ok, getUserResponse1.status);
    final UserRegistrationData getUserData1 = deserialized(getUserResponse1.entity.content(), UserRegistrationData.class);
    assertEquals(newCredentialData.authority, getUserData1.credentialOf("abc").authority);
    assertEquals(newCredentialData.id, getUserData1.credentialOf("abc").id);
    assertEquals(newCredentialData.type, getUserData1.credentialOf("abc").type);

    // remove

    final Response deleteUserAddCredential = deleteUserCredentialRequestResponse(userData, "abc");
    assertEquals(Response.Status.Ok, deleteUserAddCredential.status);
    final UserRegistrationData deletedCredentialUserData = deserialized(deleteUserAddCredential.entity.content(), UserRegistrationData.class);
    assertNull(deletedCredentialUserData.credentialOf("abc"));

    final Response getUserResponse2 = getUserRequestResponse(userData);
    assertEquals(Response.Status.Ok, getUserResponse2.status);
    final UserRegistrationData getUserData2 = deserialized(getUserResponse2.entity.content(), UserRegistrationData.class);
    assertNull(getUserData2.credentialOf("abc"));

    // replace
    final CredentialData newCredentialDataAgain = CredentialData.from("abc", "username1", "secret1", Credential.Type.OAUTH.name());
    final Response newCredentialUserDataAgain = putUserAddCredentialRequestResponse(userData, newCredentialDataAgain);
    assertEquals(Response.Status.Ok, newCredentialUserDataAgain.status);

    final CredentialData credentialDataUsedToReplace = CredentialData.from("cba", "username2", "secret2", Credential.Type.OAUTH.name());
    final Response patchUserReplaceCredential = patchUserReplaceCredentialRequestResponse(userData, "abc", credentialDataUsedToReplace);
    assertEquals(Response.Status.Ok, patchUserReplaceCredential.status);

    final UserRegistrationData patchUserReplaceCredentialUserData = deserialized(patchUserReplaceCredential.entity.content(), UserRegistrationData.class);
    assertEquals(credentialDataUsedToReplace.authority, patchUserReplaceCredentialUserData.credentialOf("cba").authority);
    assertEquals(credentialDataUsedToReplace.id, patchUserReplaceCredentialUserData.credentialOf("cba").id);
    assertEquals(credentialDataUsedToReplace.type, patchUserReplaceCredentialUserData.credentialOf("cba").type);

    final Response getUserResponse3 = getUserRequestResponse(userData);
    assertEquals(Response.Status.Ok, getUserResponse3.status);
    final UserRegistrationData getUserData3 = deserialized(getUserResponse3.entity.content(), UserRegistrationData.class);
    assertEquals(credentialDataUsedToReplace.authority, getUserData3.credentialOf("cba").authority);
    assertEquals(credentialDataUsedToReplace.id, getUserData3.credentialOf("cba").id);
    assertEquals(credentialDataUsedToReplace.type, getUserData3.credentialOf("cba").type);
  }

  @Test
  public void testThatUserReplacesProfile() {
    user();

    final ProfileData newProfileData = ProfileData.from(PersonNameData.of("A", "B", "C"), "a@c.com", "888-888-8888");
    final Response patchUserReplaceProfile = patchUserReplaceProfileRequestResponse(userData, newProfileData);
    assertEquals(Response.Status.Ok, patchUserReplaceProfile.status);
    final UserRegistrationData newProfileUserData = deserialized(patchUserReplaceProfile.entity.content(), UserRegistrationData.class);
    assertEquals(newProfileData.name.given, newProfileUserData.profile.name.given);
    assertEquals(newProfileData.name.second, newProfileUserData.profile.name.second);
    assertEquals(newProfileData.name.family, newProfileUserData.profile.name.family);
    assertEquals(newProfileData.emailAddress, newProfileUserData.profile.emailAddress);
    assertEquals(newProfileData.phone, newProfileUserData.profile.phone);

    final Response getUserResponse1 = getUserRequestResponse(userData);
    assertEquals(Response.Status.Ok, getUserResponse1.status);
    final UserRegistrationData getUserData1 = deserialized(getUserResponse1.entity.content(), UserRegistrationData.class);
    assertEquals(newProfileData.name.given, getUserData1.profile.name.given);
    assertEquals(newProfileData.name.second, getUserData1.profile.name.second);
    assertEquals(newProfileData.name.family, getUserData1.profile.name.family);
    assertEquals(newProfileData.emailAddress, getUserData1.profile.emailAddress);
    assertEquals(newProfileData.phone, getUserData1.profile.phone);
  }

  @Test
  public void testThatUserQueries() {
    user();

    final Response getUserResponse1 = getUserRequestResponse(userData);
    assertEquals(Response.Status.Ok, getUserResponse1.status);
    final UserRegistrationData userDataAsQueried = deserialized(getUserResponse1.entity.content(), UserRegistrationData.class);
    assertEquals(userData.tenantId, userDataAsQueried.tenantId);
    assertEquals(userData.username, userDataAsQueried.username);
    assertEquals(userData.profile.name.given, userDataAsQueried.profile.name.given);
    assertEquals(userData.profile.name.second, userDataAsQueried.profile.name.second);
    assertEquals(userData.profile.name.family, userDataAsQueried.profile.name.family);
    assertEquals(userData.profile.emailAddress, userDataAsQueried.profile.emailAddress);
    assertEquals(userData.profile.phone, userDataAsQueried.profile.phone);
    assertEquals(userData.credentials.stream().findFirst().get().authority, userDataAsQueried.credentials.stream().findFirst().get().authority);
    assertEquals(userData.credentials.stream().findFirst().get().id, userDataAsQueried.credentials.stream().findFirst().get().id);
    assertEquals(userData.credentials.stream().findFirst().get().secret, userDataAsQueried.credentials.stream().findFirst().get().secret);
    assertEquals("VLINGO", userDataAsQueried.credentials.stream().findFirst().get().type);
    assertTrue(userDataAsQueried.active);
  }

  @Test
  public void testThatUserHasPermission() {
    userWithRolePermission();

    final Response getUserHasPermission = getUserHasPermissionRequestResponse(userData, permissionData.name);
    assertEquals(Response.Status.Ok, getUserHasPermission.status);
    final PermissionData userHasPermissionData = deserialized(getUserHasPermission.entity.content(), PermissionData.class);
    assertEquals(permissionData.tenantId, userHasPermissionData.tenantId);
    assertEquals(permissionData.name, userHasPermissionData.name);
    assertEquals(permissionData.description, userHasPermissionData.description);
  }

  @Test
  public void testThatUserIsInRole() {
    userWithRolePermission();

    final Response getUserInRoleResponse = getUserInRoleRequestResponse(userData, roleData.name);
    assertEquals(Response.Status.Ok, getUserInRoleResponse.status);
    final RoleData userInRoleData = deserialized(getUserInRoleResponse.entity.content(), RoleData.class);
    assertEquals(roleData.tenantId, userInRoleData.tenantId);
    assertEquals(roleData.name, userInRoleData.name);
    assertEquals(roleData.description, userInRoleData.description);
  }

  private Response deleteUserCredentialRequestResponse(final UserRegistrationData userData, String authority) {
    final String request = "DELETE /tenants/" + userData.tenantId + "/users/" + userData.username + "/credentials/" + authority + " HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  private Response getUserRequestResponse(final UserRegistrationData userData) {
    final String request = "GET /tenants/" + userData.tenantId + "/users/" + userData.username + " HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  private Response getUserHasPermissionRequestResponse(final UserRegistrationData userData, String permissionName) {
    final String request = "GET /tenants/" + userData.tenantId + "/users/" + userData.username + "/permissions/" + permissionName + " HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  private Response getUserInRoleRequestResponse(final UserRegistrationData userData, final String roleName) {
    final String request = "GET /tenants/" + userData.tenantId + "/users/" + userData.username + "/roles/" + roleName + " HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  private Response patchUserActivateRequestResponse(final UserRegistrationData userData) {
    final String request = "PATCH /tenants/" + userData.tenantId + "/users/" + userData.username + "/activate HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  private Response patchUserDeactivateRequestResponse(final UserRegistrationData userData) {
    final String request = "PATCH /tenants/" + userData.tenantId + "/users/" + userData.username + "/deactivate HTTP/1.1\nHost: vlingo.io\n\n";
    return requestResponse(request);
  }

  private Response patchUserReplaceCredentialRequestResponse(UserRegistrationData userData, String authority, CredentialData credentialData) {
    final String body = serialized(credentialData);
    final String request = "PATCH /tenants/" + userData.tenantId + "/users/" + userData.username + "/credentials/" + authority + " HTTP/1.1\nHost: vlingo.io\nContent-Length: " + body.length() + "\n\n" + body;
    return requestResponse(request);
  }

  private Response putUserAddCredentialRequestResponse(final UserRegistrationData userData, final CredentialData credentialData) {
    final String body = serialized(credentialData);
    final String request = "PUT /tenants/" + userData.tenantId + "/users/" + userData.username + "/credentials HTTP/1.1\nHost: vlingo.io\nContent-Length: " + body.length() + "\n\n" + body;
    return requestResponse(request);
  }

  private Response patchUserReplaceProfileRequestResponse(final UserRegistrationData userData, final ProfileData profileData) {
    final String body = serialized(profileData);
    final String request = "PATCH /tenants/" + userData.tenantId + "/users/" + userData.username + "/profile" + " HTTP/1.1\nHost: vlingo.io\nContent-Length: " + body.length() + "\n\n" + body;
    return requestResponse(request);
  }

  private Response putRoleUserRequestResponse(final RoleData roleData, final String username) {
    final String request = "PUT /tenants/" + roleData.tenantId + "/roles/" + roleData.name + "/users HTTP/1.1\nHost: vlingo.io\nContent-Length: " + username.length() + "\n\n" + username;
    return requestResponse(request);
  }

  private Response putRolePermissionRequestResponse(final RoleData roleData, final String permissionName) {
    final String request = "PUT /tenants/" + roleData.tenantId + "/roles/" + roleData.name + "/permissions HTTP/1.1\nHost: vlingo.io\nContent-Length: " + permissionName.length() + "\n\n" + permissionName;
    return requestResponse(request);
  }

  private void userWithRolePermission() {
    user();

    final Response roleResponse = postProvisionRole(tenantData.tenantId, "Role", "A role description.");
    roleData = deserialized(roleResponse.entity.content(), RoleData.class);

    final Response permissionProvisionedResponse = postProvisionPermission(tenantData.tenantId, "Permission", "A permission description.");
    permissionData = deserialized(permissionProvisionedResponse.entity.content(), PermissionData.class);

    final Response rolePermissionResponse = putRolePermissionRequestResponse(roleData, permissionData.name);
    assertEquals(Response.Status.Ok, rolePermissionResponse.status);

    final Response roleUserResponse = putRoleUserRequestResponse(roleData, userData.username);
    assertEquals(Response.Status.Ok, roleUserResponse.status);
  }

  private void user() {
    final Response createdResponse = postTenantSubscribesRequestResponse(tenantData());
    tenantData = deserialized(createdResponse.entity.content(), TenantData.class);
    final Response userRegistrationResponse = postRegisterUser(tenantData.tenantId, userRegistrationData(tenantData.tenantId));
    userData = deserialized(userRegistrationResponse.entity.content(), UserRegistrationData.class);
  }
}
