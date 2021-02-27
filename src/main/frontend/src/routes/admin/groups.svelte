<script>
	import Title from '../../components/title.svelte';
	import { mdiAccountGroup, mdiDelete, mdiPencil } from '@mdi/js';
	import { Checkbox, Col, Row, Textarea, TextField } from 'svelte-materialify/src';
	import { groups, create, update, remove } from '../../stores/groups.js';
	import DeleteDialog from '../../components/DeleteDialog.svelte';
	import CommonDialog from '../../components/CommonDialog.svelte';
	import { dialogState, loading } from '../../shared/common.js';
	import { fetchUsers, users } from '../../stores/users.js';
	import uniqBy from 'lodash.uniqby';
	import Table from '../../components/Table.svelte';
	import SmallButton from '../../components/SmallButton.svelte';
	import FloatAddButton from '../../components/FloatAddButton.svelte';

	fetchUsers();

	dialogState.manageMembers = false;
	loading.manageMembers = false;

	let initialGroup = {
		name: '',
		description: '',
		members: [],
	};

	let indexToUpdateOrDelete = 0;
	let updateMode = false;

	let usersLists = [];
	let transformedUsers;
	let transformedGroupMembers;

	let group = { ...initialGroup };

	function _create() {
		loading.createOrUpdate = true;
		create(group)
			.then(({ status }) => status === 200 && reset())
			.finally(() => {
				loading.createOrUpdate = false;
				dialogState.createOrUpdate = false;
			});
	}

	function _update() {
		loading.createOrUpdate = true;
		update(indexToUpdateOrDelete, group)
			.then(({ status }) => status === 200 && reset())
			.finally(() => {
				loading.createOrUpdate = false;
				dialogState.createOrUpdate = false;
			});
	}

	function updateMembers() {
		loading.manageMembers = true;
		group.members = usersLists.filter((user) => user.selected).map((user) => user.username);
		update(indexToUpdateOrDelete, group).finally(() => {
			loading.manageMembers = false;
			dialogState.manageMembers = false;
		});
	}

	function _remove() {
		loading.remove = true;
		remove(indexToUpdateOrDelete)
			.then(({ status }) => {
				if (status === 200) {
					dialogState.remove = false;
				}
			})
			.finally(() => {
				loading.remove = false;
			});
	}

	function openCreateDialog() {
		updateMode = false;
		dialogState.createOrUpdate = true;
	}

	function openUpdateDialog(index) {
		updateMode = true;
		indexToUpdateOrDelete = index;
		group = $groups[index];
		dialogState.createOrUpdate = true;
	}

	function openManageMembersDialog(index) {
		fetchUsers();
		indexToUpdateOrDelete = index;
		group = $groups[index];

		transformedGroupMembers = group.members.map((member) => ({
			username: member,
			selected: true,
		}));

		transformedUsers = $users.map(({ username }) => ({
			username,
			selected: false,
		}));

		const mergedMembersAndUsers = transformedGroupMembers.concat(transformedUsers);
		usersLists = uniqBy(mergedMembersAndUsers, (v) => v.username).sort();
		dialogState.manageMembers = true;
	}

	function openDeleteDialog(index) {
		indexToUpdateOrDelete = index;
		group = { ...$groups[index] };
		dialogState.remove = true;
	}

	function handleFormPost() {
		if (updateMode) {
			_update();
		} else {
			_create();
		}
	}

	function reset() {
		group = { ...initialGroup };
	}

	$: if (
		dialogState.createOrUpdate == false &&
		!dialogState.remove &&
		!dialogState.manageMembers
	) {
		updateMode = false;
		reset();
	}
</script>

<Title title="Groups" />

<h6>Groups</h6>

<!-- DIALOG CREATE/UPDATE GROUP -->
<CommonDialog
	on:form-submit={handleFormPost}
	bind:active={dialogState.createOrUpdate}
	loading={loading.createOrUpdate}
	submitButtonCaption={updateMode ? 'Update' : 'Add Group'}
	submitButtonCaptionOnLoading={updateMode ? 'Updating...' : 'Adding Group...'}
	title="{updateMode ? 'Update' : 'Add'} Group">
	<Row class="mt-3">
		<Col>
			<TextField bind:value={group.name} disabled={updateMode} required>Name</TextField>
		</Col>
	</Row>
	<Row>
		<Col>
			<Textarea bind:value={group.description} required>Description</Textarea>
		</Col>
	</Row>
</CommonDialog>

<!-- DIALOG MANAGE GROUP MEMBERS -->
<CommonDialog
	on:form-submit={updateMembers}
	bind:active={dialogState.manageMembers}
	loading={loading.manageMembers}
	submitButtonCaption="Save"
	submitButtonCaptionOnLoading="Saving..."
	title="Manage Members of {group.name}">
	<div class="d-flex pl-2 pr-2 flex-column">
		{#each usersLists as user}
			<div class="mb-3">
				<Checkbox bind:checked={user.selected}>{user.username}</Checkbox>
			</div>
		{/each}
	</div>
</CommonDialog>

<!-- DIALOG REMOVE GROUP -->
<DeleteDialog
	bind:active={dialogState.remove}
	loading={loading.remove}
	title="Delete Group"
	on:remove-button-clicked={_remove}>
	Are you sure want to delete <b>{group.name}</b> from groups?
</DeleteDialog>

{#if $groups.length}
	<Table headers={['Name', 'Description']}>
		{#each $groups as group, index}
			<tr>
				<td>{group.name}</td>
				<td>{group.description}</td>
				<td class="text-center table-row-actions">
					<SmallButton
						on:click={() => openManageMembersDialog(index)}
						iconPath={mdiAccountGroup}
						title="Manage group members" />
					<SmallButton
						on:click={() => openUpdateDialog(index)}
						iconPath={mdiPencil}
						title="Update/edit group" />
					<SmallButton
						on:click={() => openDeleteDialog(index)}
						iconPath={mdiDelete}
						title="Remove/delete group" />
				</td>
			</tr>
		{/each}
	</Table>
{:else}
	<div style="padding-top: 2em">No data available</div>
{/if}

<FloatAddButton on:click={openCreateDialog} />
