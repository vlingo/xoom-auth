<script>
	import Title from '../../components/title.svelte';
	import { mdiDelete, mdiPencil, mdiPlus } from '@mdi/js';
	import { Button, Checkbox, Icon, Table } from 'svelte-materialify/src';
	import UserForm from '../../components/UserForm.svelte';
	import { users, create, update, remove } from '../../stores/users.js';
	import DeleteDialog from '../../components/DeleteDialog.svelte';
	import CommonDialog from '../../components/CommonDialog.svelte';
	import { dialogState, loading } from '../../shared/common.js';
	import SmallButton from '../../components/SmallButton.svelte';

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

	let indexToUpdateOrDelete = 0;
	let updateMode = false;

	let user = { ...initialUser };

	function _create() {
		loading.createOrUpdate = true;
		create(user)
			.then(({ status }) => status === 200 && reset())
			.finally(() => {
				loading.createOrUpdate = false;
				dialogState.createOrUpdate = false;
			});
	}

	function _update() {
		loading.createOrUpdate = true;
		update(indexToUpdateOrDelete, user)
			.then(({ status }) => status === 200 && reset())
			.finally(() => {
				loading.createOrUpdate = false;
				dialogState.createOrUpdate = false;
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
			_update();
		} else {
			_create();
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
<CommonDialog
	on:form-submit={handleFormPost}
	bind:active={dialogState.createOrUpdate}
	loading={loading.createOrUpdate}
	submitButtonCaption={updateMode ? 'Update' : 'Register'}
	submitButtonCaptionOnLoading={updateMode ? 'Updating...' : 'Registering...'}
	title="{updateMode ? 'Update' : 'Register'} User">
	<UserForm bind:user />
</CommonDialog>

<!-- DIALOG REMOVE USER -->
<DeleteDialog
	bind:active={dialogState.remove}
	loading={loading.remove}
	title="Delete User"
	on:remove-button-clicked={_remove}>
	Are you sure want to delete <b>{user.username}</b> from users?
</DeleteDialog>

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
						<SmallButton
							on:click={() => openUpdateDialog(index)}
							iconPath={mdiPencil}
							title="Update/edit user" />
						<SmallButton
							on:click={() => openDeleteDialog(index)}
							iconPath={mdiDelete}
							title="Remove/delete user" />
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
