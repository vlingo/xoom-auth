package io.vlingo.xoom.auth.model.value;

import io.vlingo.xoom.auth.model.group.GroupId;
import io.vlingo.xoom.auth.model.user.UserId;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

public class EncodedMember {
  static private final char GroupType = 'G';
  static private final char PermissionType = 'P';
  static private final char RoleType = 'R';
  static private final char UserType = 'U';

  public final String id;
  public final char type;


  public static GroupMember group(GroupId groupId) {
    return new GroupMember(groupId);
  }

  public static UserMember user(UserId userId) {
    return new UserMember(userId);
  }

  private EncodedMember(final String id, final char type) {
    this.id = id;
    this.type = type;
  }

  public static final class GroupMember extends EncodedMember {
    private GroupMember(GroupId groupId) {
      super(groupId.idString(), GroupType);
    }
  }

  public static final class UserMember extends EncodedMember {
    private UserMember(UserId userId) {
      super(userId.idString(), UserType);
    }
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof EncodedMember)) {
      return false;
    }
    EncodedMember member = (EncodedMember) other;
    return type == member.type && id.equals(member.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, type);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
            .append("type", type)
            .append("id", id)
            .toString();
  }
}
