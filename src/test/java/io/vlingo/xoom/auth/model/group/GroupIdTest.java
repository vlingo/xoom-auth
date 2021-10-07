package io.vlingo.xoom.auth.model.group;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GroupIdTest {
  @Test
  public void itIsComposedOfTenantIdAndGroupName() {
    final GroupId groupId = GroupId.from("7c467e63-2b63-43dd-8198-67d31a776f4d", "Admin");

    assertEquals("7c467e63-2b63-43dd-8198-67d31a776f4d", groupId.tenantId);
    assertEquals("Admin", groupId.groupName);
    assertEquals("7c467e63-2b63-43dd-8198-67d31a776f4d:Admin", groupId.idString());
  }

  @Test
  public void twoGroupIdsAreTheSameIfTenantIdAndGroupIdAreTheSame() {
    final GroupId groupId = GroupId.from("7c467e63-2b63-43dd-8198-67d31a776f4d", "Admin");

    assertEquals(groupId, GroupId.from("7c467e63-2b63-43dd-8198-67d31a776f4d", "Admin"));
    assertNotEquals(groupId, GroupId.from("97d57df1-1b38-4583-bd3c-6b4731e7a605", "Admin"));
    assertNotEquals(groupId, GroupId.from("7c467e63-2b63-43dd-8198-67d31a776f4d", "Staff"));
  }
}
