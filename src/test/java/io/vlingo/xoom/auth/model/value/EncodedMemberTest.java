package io.vlingo.xoom.auth.model.value;

import io.vlingo.xoom.auth.model.group.GroupId;
import io.vlingo.xoom.auth.model.tenant.TenantId;
import io.vlingo.xoom.auth.model.user.UserId;
import io.vlingo.xoom.auth.model.value.EncodedMember.GroupMember;
import io.vlingo.xoom.auth.model.value.EncodedMember.UserMember;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class EncodedMemberTest {

  @Test
  public void itCreatesGroupMembers() {
    final GroupId groupId = GroupId.from(TenantId.unique(), "group-a");
    final GroupMember member = EncodedMember.group(groupId);

    assertEquals(groupId.idString(), member.id);
  }

  @Test
  public void twoMembersAreTheSameIfGivenTheSameId() {
    final GroupId groupId = GroupId.from(TenantId.unique(), "group-a");
    final GroupMember member = EncodedMember.group(groupId);

    assertEquals(member, EncodedMember.group(groupId));
    assertNotEquals(member, EncodedMember.group(GroupId.from(TenantId.unique(), "group-b")));
  }

  @Test
  public void itCreatesUserMembers() {
    final UserId userId = UserId.from(TenantId.unique(), "bobby");
    final UserMember member = EncodedMember.user(userId);

    assertEquals(userId.idString(), member.id);
  }
}
