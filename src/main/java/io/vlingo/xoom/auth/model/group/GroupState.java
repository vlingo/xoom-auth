package io.vlingo.xoom.auth.model.group;


import io.vlingo.xoom.auth.model.user.UserId;
import io.vlingo.xoom.auth.model.value.EncodedMember;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class GroupState {

  public final GroupId id;
  public final String name;
  public final String description;
  public final Set<EncodedMember> members;

  public static GroupState identifiedBy(final GroupId groupId) {
    return new GroupState(groupId, null, null, Collections.emptySet());
  }

  public GroupState(final GroupId groupId, final String name, final String description) {
    this(groupId, name, description, Collections.emptySet());
  }

  private GroupState(final GroupId groupId, final String name, final String description, final Set<EncodedMember> members) {
    this.id = groupId;
    this.name = name;
    this.description = description;
    this.members = Collections.unmodifiableSet(members);
  }

  public GroupState provisionGroup(final String name, final String description) {
    return new GroupState(this.id, name, description, Collections.emptySet());
  }

  public GroupState changeDescription(final String description) {
    return new GroupState(this.id, this.name, description, members);
  }

  public GroupState assignGroup(final GroupId innerGroupId) {
    final Set<EncodedMember> updatedMembers = includeMember(EncodedMember.group(innerGroupId));
    return new GroupState(this.id, this.name, this.description, updatedMembers);
  }

  public GroupState unassignGroup(final GroupId innerGroupId) {
    final Set<EncodedMember> updatedMembers = removeMember(EncodedMember.group(innerGroupId));
    return new GroupState(this.id, this.name, this.description, updatedMembers);
  }

  public GroupState assignUser(final UserId userId) {
    final Set<EncodedMember> updatedMembers = includeMember(EncodedMember.user(userId));
    return new GroupState(this.id, this.name, this.description, updatedMembers);
  }

  public GroupState unassignUser(final UserId userId) {
    final Set<EncodedMember> updatedMembers = removeMember(EncodedMember.user(userId));
    return new GroupState(this.id, this.name, this.description, updatedMembers);
  }

  private Set<EncodedMember> includeMember(final EncodedMember member) {
    return Stream.concat(members.stream(), Stream.of(member)).collect(Collectors.toSet());
  }

  private Set<EncodedMember> removeMember(final EncodedMember member) {
    return members.stream().filter(m -> !m.equals(member)).collect(Collectors.toSet());
  }
}
