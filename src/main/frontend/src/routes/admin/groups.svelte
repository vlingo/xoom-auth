<script>
	import Title from '../../components/title.svelte';
	import { mdiAccountGroup, mdiDelete, mdiPencil, mdiPlus } from '@mdi/js';
	import { Button, Checkbox, Col, Icon, Row, Table, TextField } from 'svelte-materialify/src';
	import { groups, create, update, remove } from '../../stores/groups.js';
	import DeleteDialog from '../../components/DeleteDialog.svelte';
	import CreateUpdateDialog from '../../components/CreateUpdateDialog.svelte';
	import { dialogState, loading } from '../../shared/common.js';
	import { fetchUsers, users } from '../../stores/users.js';
	import uniqBy from 'lodash.uniqby';

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
<CreateUpdateDialog
	on:form-submit={handleFormPost}
	bind:active={dialogState.createOrUpdate}
	loading={loading.createOrUpdate}
	submitButtonCaption={updateMode ? 'Update' : 'Add Group'}
	submitButtonCaptionOnLoading={updateMode ? 'Updating...' : 'Adding Group...'}
	title="{updateMode ? 'Update' : 'Add'} Group">
	<Row class="mt-3">
		<Col>
			<TextField bind:value={group.name} required>Name</TextField>
		</Col>
	</Row>
	<Row>
		<Col>
			<TextField bind:value={group.description} required>Description</TextField>
		</Col>
	</Row>
</CreateUpdateDialog>

<!-- DIALOG MANAGE GROUP MEMBERS -->
<CreateUpdateDialog
	on:form-submit={updateMembers}
	bind:active={dialogState.manageMembers}
	loading={loading.manageMembers}
	submitButtonCaption="Save"
	submitButtonCaptionOnLoading="Saving..."
	title="Manage Members of {group.name}">
	<div class="d-flex flex-column">
		{#each usersLists as user}
			<Checkbox bind:checked={user.selected}>{user.username}</Checkbox>
		{/each}
	</div>
</CreateUpdateDialog>

<!-- DIALOG REMOVE GROUP -->
<DeleteDialog
	bind:active={dialogState.remove}
	loading={loading.remove}
	title="Delete Group"
	on:remove-button-clicked={_remove}>
	Are you sure want to delete <b>{group.name}</b> from groups?
</DeleteDialog>

{#if $groups.length}
	<Table class="p-5 mt-5 s-card">
		<thead>
			<tr style="font-weight:bold">
				<td>Name</td>
				<td>Description</td>
				<td class="text-center">Actions</td>
			</tr>
		</thead>
		<tbody>
			{#each $groups as group, index}
				<tr>
					<td>{group.name}</td>
					<td>{group.description}</td>
					<td class="text-center table-row-actions">
						<Button
							fab
							depressed
							on:click={() => openManageMembersDialog(index)}
							rounded
							size="x-small"
							text
							title="Manage group members">
							<Icon path={mdiAccountGroup} />
						</Button>
						<Button
							fab
							depressed
							on:click={() => openUpdateDialog(index)}
							rounded
							size="x-small"
							text
							title="Update/edit group">
							<Icon path={mdiPencil} />
						</Button>
						<Button
							fab
							depressed
							on:click={() => openDeleteDialog(index)}
							rounded
							size="x-small"
							text
							title="Remove group">
							<Icon path={mdiDelete} />
						</Button>
					</td>
				</tr>
			{/each}
		</tbody>
	</Table>
{:else}
	<div style="padding-top: 2em">No data available</div>
{/if}

<Button
	class="primary-color"
	fab
	on:click={openCreateDialog}
	style="position: fixed; margin: 1em; right: 0; bottom: 0;"
	float>
	<Icon path={mdiPlus} />
</Button>
