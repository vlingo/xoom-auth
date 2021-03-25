<script>
	import SmallButton from '../components/SmallButton.svelte';
	import { users } from '../stores/users.js';
	import { mdiMinus, mdiPlus } from '@mdi/js';

	export let members = [];

	/** @type {HTMLSelectElement} */
	let selectUserElement;
	/** @type {HTMLSelectElement} */
	let selectMemberElement;

	let transformedUsers = [];
	let transformedMembers = [];

	function addUserToMembers() {
		const index = selectUserElement.selectedIndex;
		if (index > -1) {
			const userToAdd = transformedUsers[index];
			if (!userToAdd.active) {
				return;
			}
			members = [...members, userToAdd.username];
		}
	}

	function removeMember() {
		const index = selectMemberElement.selectedIndex;
		if (index > -1) {
			const memberToRemove = transformedMembers[index];
			const indexOnMembers = members.findIndex(
				(member) => member === memberToRemove.username
			);
			const _members = [...members];
			_members.splice(indexOnMembers, 1);
			members = _members;
		}
	}

	$: if (members) {
		transformedMembers = members.map((member) => {
			const user = $users.find((_user) => _user.username === member);
			return {
				username: member,
				active: user.active,
			};
		});

		transformedUsers = $users
			.filter(({ username }) => !members.includes(username))
			.map(({ username, active }) => ({
				username,
				active,
			}));
	}

	$: inactiveMembers = transformedMembers.filter((member) => !member.active);
</script>

<div class="d-flex flex-column">
	<h6>Users</h6>
	<select bind:this={selectUserElement} size="5">
		{#each transformedUsers as user}
			<option class="pa-1" class:line-through={!user.active} disabled={!user.active}>
				{user.username}
			</option>
		{/each}
	</select>
	<div class="justify-center pt-2 pb-2 d-flex">
		<SmallButton iconPath={mdiPlus} on:click={addUserToMembers} />
		<SmallButton iconPath={mdiMinus} on:click={removeMember} />
	</div>
	<h6>Members</h6>
	<select bind:this={selectMemberElement} size="5">
		{#each transformedMembers as member}
			<option class="pa-1" class:line-through={!member.active}>{member.username}</option>
		{/each}
	</select>
	{#if !!inactiveMembers.length}
		<div class="mt-1 red-text">
			Warning: You have {inactiveMembers.length} inactive users on your group members
		</div>
	{/if}
</div>
