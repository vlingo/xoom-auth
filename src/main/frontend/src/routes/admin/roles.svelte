<script>
	import Title from '../../components/title.svelte';
	import { mdiDelete, mdiPencil } from '@mdi/js';
	import { Col, Row, Textarea, TextField } from 'svelte-materialify/src';
	import { roles, create, update, remove } from '../../stores/roles.js';
	import DeleteDialog from '../../components/DeleteDialog.svelte';
	import CommonDialog from '../../components/CommonDialog.svelte';
	import { dialogState, loading } from '../../shared/common.js';
	import Table from '../../components/Table.svelte';
	import SmallButton from '../../components/SmallButton.svelte';
	import FloatAddButton from '../../components/FloatAddButton.svelte';

	let initialRole = {
		name: '',
		description: '',
	};

	let indexToUpdateOrDelete = 0;
	let updateMode = false;

	let role = { ...initialRole };

	function _create() {
		loading.createOrUpdate = true;
		create(role)
			.then(({ status }) => status === 200 && reset())
			.finally(() => {
				loading.createOrUpdate = false;
				dialogState.createOrUpdate = false;
			});
	}

	function _update() {
		loading.createOrUpdate = true;
		update(indexToUpdateOrDelete, role)
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
		role = $roles[index];
		dialogState.createOrUpdate = true;
	}

	function openDeleteDialog(index) {
		indexToUpdateOrDelete = index;
		role = { ...$roles[index] };
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
		role = { ...initialRole };
	}

	$: if (dialogState.createOrUpdate == false && !dialogState.remove) {
		updateMode = false;
		reset();
	}
</script>

<Title title="Roles" />

<h6>Roles</h6>

<!-- DIALOG CREATE/UPDATE ROLE -->
<CommonDialog
	on:form-submit={handleFormPost}
	bind:active={dialogState.createOrUpdate}
	loading={loading.createOrUpdate}
	submitButtonCaption={updateMode ? 'Update' : 'Add Role'}
	submitButtonCaptionOnLoading={updateMode ? 'Updating...' : 'Adding Role...'}
	title="{updateMode ? 'Update' : 'Add'} Role">
	<Row class="mt-3">
		<Col>
			<TextField bind:value={role.name} disabled={updateMode} required>Name</TextField>
		</Col>
	</Row>
	<Row>
		<Col>
			<Textarea bind:value={role.description} required>Description</Textarea>
		</Col>
	</Row>
</CommonDialog>

<!-- DIALOG REMOVE ROLE -->
<DeleteDialog
	bind:active={dialogState.remove}
	loading={loading.remove}
	title="Delete Role"
	on:remove-button-clicked={_remove}>
	Are you sure want to delete <b>{role.name}</b> from roles?
</DeleteDialog>

{#if $roles.length}
	<Table headers={['Name', 'Description']}>
		{#each $roles as role, index}
			<tr>
				<td>{role.name}</td>
				<td>{role.description}</td>
				<td class="text-center table-row-actions">
					<SmallButton
						on:click={() => openUpdateDialog(index)}
						iconPath={mdiPencil}
						title="Update/edit role" />
					<SmallButton
						on:click={() => openDeleteDialog(index)}
						iconPath={mdiDelete}
						title="Remove/delete role" />
				</td>
			</tr>
		{/each}
	</Table>
{:else}
	<div style="padding-top: 2em">No data available</div>
{/if}

<FloatAddButton on:click={openCreateDialog} />
