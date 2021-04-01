<script>
	import { ListItemGroup, ListItem, Checkbox } from 'svelte-materialify/src';
	import { users } from '../stores/users.js';
	import { groups } from '../stores/groups.js';

	export let currentGroupName = '';

	export let members = {
		users: [],
		groups: [],
	};
</script>

<div class="d-flex flex-column">
	<h6>Groups</h6>
	<div class="list-item-group-wrapper">
		<ListItemGroup multiple bind:value={members.groups}>
			{#each $groups.filter((group) => group.name !== currentGroupName) as group}
				<ListItem value={group.name}>
					{group.name}
					<span slot="append">
						<Checkbox
							checked={members.groups.includes(group.name)}
							bind:group={members.groups} />
					</span>
				</ListItem>
			{/each}
		</ListItemGroup>
	</div>

	<h6 class="mt-3">Users</h6>
	<div class="list-item-group-wrapper">
		<ListItemGroup multiple bind:value={members.users}>
			{#each $users as user}
				<ListItem value={user.username}>
					{user.username}
					<span slot="append">
						<Checkbox
							checked={members.users.includes(user.username)}
							bind:group={members.users} />
					</span>
				</ListItem>
			{/each}
		</ListItemGroup>
	</div>
</div>
