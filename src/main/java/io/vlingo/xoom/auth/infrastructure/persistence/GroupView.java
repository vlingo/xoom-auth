package io.vlingo.xoom.auth.infrastructure.persistence;

import io.vlingo.xoom.auth.model.group.GroupId;
import io.vlingo.xoom.auth.model.group.GroupState;
import io.vlingo.xoom.auth.model.role.RoleId;
import io.vlingo.xoom.auth.model.user.UserId;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupView {
  public final String id;
  public final String tenantId;
  public final String name;
  public final String description;
  public final Set<String> groups;
  public final Set<String> users;
  public final Set<Relation<GroupId, RoleId>> roles;

  public static GroupView empty() {
    return new GroupView("", "", "", "", Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
  }

  public static GroupView from(GroupState groupState) {
    return from(groupState.id, groupState.name, groupState.description);
  }

  public static GroupView from(final GroupId groupId, final String name, String description) {
    return from(groupId, name, description, Collections.emptySet(), Collections.emptySet(), Collections.emptySet());
  }

  public static GroupView from(final GroupId groupId, final String name, final String description, final Set<GroupId> groupIds, final Set<UserId> userIds, final Set<Relation<GroupId, RoleId>> roles) {
    return new GroupView(
            groupId.idString(),
            groupId.tenantId.idString(),
            name,
            description,
            groupIds.stream().map(g -> g.idString()).collect(Collectors.toSet()),
            userIds.stream().map(u -> u.idString()).collect(Collectors.toSet()),
            roles
    );
  }

  private GroupView(final String id, final String tenantId, final String name, final String description, final Set<String> groups, final Set<String> users, final Set<Relation<GroupId, RoleId>> roles) {
    this.id = id;
    this.tenantId = tenantId;
    this.name = name;
    this.description = description;
    this.groups = groups;
    this.users = users;
    this.roles = roles;
  }

  public boolean isInRole(final RoleId roleId) {
    return roles.stream().filter(r -> r.right.equals(roleId)).findFirst().isPresent();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GroupView)) return false;
    GroupView groupView = (GroupView) o;
    return id.equals(groupView.id) && tenantId.equals(groupView.tenantId) && name.equals(groupView.name) && description.equals(groupView.description) && groups.equals(groupView.groups) && users.equals(groupView.users) && roles.equals(groupView.roles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, tenantId, name, description, groups, users, roles);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
            .append("id", id)
            .append("tenantId", tenantId)
            .append("name", name)
            .append("description", description)
            .append("groups", groups)
            .append("users", users)
            .append("roles", roles)
            .toString();
  }
}
