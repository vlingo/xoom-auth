<script>
	import Title from '../../components/title.svelte';
	import { mdiDelete, mdiPencil, mdiPlus } from '@mdi/js';
	import { Button, Checkbox, Dialog, Divider, Icon, Table } from 'svelte-materialify/src';
	import UserForm from '../../components/UserForm.svelte';
	import { users, addUser, updateUser, removeUser } from '../../stores/index.js';

	let initialUser = {
		username: '',
		email: '',
		givenName: '',
		secondName: '',
		familyName: '',
		phone: '',
		active: false,
		credential: {
			authority: 'vlingo',
			id: '',
			secret: '',
		},
	};

	let loading = {
		createOrUpdate: false,
		remove: false,
	};

	let dialogState = {
		createOrUpdate: false,
		remove: false,
	};

	let indexToUpdateOrDelete = 0;
	let updateMode = false;

	let user = { ...initialUser };

	function _addUser() {
		loading.createOrUpdate = true;
		addUser(user)
			.then(({ status }) => status === 200 && reset())
			.finally(() => {
				loading.createOrUpdate = false;
				dialogState.createOrUpdate = false;
			});
	}

	function _updateUser() {
		loading.createOrUpdate = true;
		updateUser(indexToUpdateOrDelete, user)
			.then(({ status }) => status === 200 && reset())
			.finally(() => {
				loading.createOrUpdate = false;
				dialogState.createOrUpdate = false;
			});
	}

	function _removeUser() {
		loading.remove = true;
		removeUser(indexToUpdateOrDelete)
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
		user = $users[index];
		dialogState.createOrUpdate = true;
	}

	function openDeleteDialog(index) {
		indexToUpdateOrDelete = index;
		user = { ...$users[index] };
		dialogState.remove = true;
	}

	function handleFormPost() {
		if (updateMode) {
			_updateUser();
		} else {
			_addUser();
		}
	}

	function reset() {
		user = { ...initialUser };
	}

	$: if (dialogState.createOrUpdate == false && !dialogState.remove) {
		updateMode = false;
		reset();
	}
</script>

<Title title="Users" />

<h6>Users</h6>

<!-- DIALOG CREATE/UPDATE USER -->
<Dialog class="pa-4" bind:active={dialogState.createOrUpdate}>
	<form on:submit|preventDefault={handleFormPost} class="d-flex flex-column">
		<h6 class="mb-2">{updateMode ? 'Update' : 'Register'} User</h6>
		<Divider class="mb-4" />
		<UserForm bind:user />
		<Divider />
		<div class="mt-3 d-flex">
			<Button class="ml-auto primary-color" disabled={loading.createOrUpdate} type="submit">
				{#if updateMode}
					{loading.createOrUpdate ? 'updating...' : 'update'}
				{:else}
					{loading.createOrUpdate ? 'registering user...' : 'register'}
				{/if}
			</Button>
		</div>
	</form>
</Dialog>

<!-- DIALOG REMOVE USER -->
<Dialog class="pa-4" bind:active={dialogState.remove}>
	<h6 class="mb-2">Delete User</h6>
	<Divider />
	<div class="mt-4 mb-4">
		Are you sure want to delete <b>{user.username}</b> from users?
	</div>
	<Divider />
	<div class="mt-3 d-flex">
		<Button
			class="ml-auto red white-text"
			depressed
			disabled={loading.remove}
			on:click={_removeUser}>
			{loading.remove ? 'deleting...' : 'delete'}
		</Button>
	</div>
</Dialog>

{#if $users.length}
	<Table class="p-5 mt-5 s-card">
		<thead>
			<tr style="font-weight:bold">
				<td>Username</td>
				<td>Name</td>
				<td>Email</td>
				<!-- <td>Phone</td> -->
				<td class="text-center">Active</td>
				<td class="text-center">Actions</td>
			</tr>
		</thead>
		<tbody>
			{#each $users as user, index}
				<tr>
					<td>{user.username}</td>
					<td>{user.givenName} {user.secondName} {user.familyName}</td>
					<td>{user.email}</td>
					<!-- <td>{user.phone}</td> -->
					<td class="justify-center d-flex">
						<Checkbox class="m-0" bind:checked={user.active} />
					</td>
					<td class="text-center table-row-actions">
						<Button
							fab
							depressed
							on:click={() => openUpdateDialog(index)}
							rounded
							size="x-small"
							text>
							<Icon path={mdiPencil} />
						</Button>
						<Button
							fab
							depressed
							on:click={() => openDeleteDialog(index)}
							rounded
							size="x-small"
							text>
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
