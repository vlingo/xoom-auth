<script>
	import Title from '../../components/title.svelte';
	import { mdiDelete, mdiLock, mdiPencil, mdiPlus } from '@mdi/js';
	import { Button, Col, Icon, Row, Textarea, TextField } from 'svelte-materialify/src';
	import { permissions, create, update, remove } from '../../stores/permissions.js';
	import DeleteDialog from '../../components/DeleteDialog.svelte';
	import CommonDialog from '../../components/CommonDialog.svelte';
	import { dialogState, loading } from '../../shared/common.js';
	import Table from '../../components/Table.svelte';
	import SmallButton from '../../components/SmallButton.svelte';
	import ConstraintsEditor from '../../components/ConstraintsEditor.svelte';

	dialogState.manageConstrains = false;
	loading.savingConstraints = false;

	/** @type {ConstraintsEditor} */
	let constrainEditor;

	let initialPermission = {
		name: '',
		description: '',
		constraints: [],
	};

	let indexToUpdateOrDelete = 0;
	let updateMode = false;

	let permission = { ...initialPermission };

	function _create() {
		loading.createOrUpdate = true;
		create(permission)
			.then(({ status }) => status === 200 && reset())
			.finally(() => {
				loading.createOrUpdate = false;
				dialogState.createOrUpdate = false;
			});
	}

	function _update() {
		loading.createOrUpdate = true;
		update(indexToUpdateOrDelete, permission)
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

	function updateConstraints() {
		loading.savingConstraints = false;
		update(indexToUpdateOrDelete, permission)
			.then(({ status }) => {
				if (status == 200) {
					dialogState.manageConstrains = false;
				}
			})
			.finally(() => {
				loading.savingConstraints = false;
			});
	}

	function openCreateDialog() {
		updateMode = false;
		dialogState.createOrUpdate = true;
	}

	function openUpdateDialog(index) {
		updateMode = true;
		indexToUpdateOrDelete = index;
		permission = $permissions[index];
		dialogState.createOrUpdate = true;
	}

	function openManageConstraintsDialog(index) {
		indexToUpdateOrDelete = index;
		reset();
		permission = { ...$permissions[index] };
		dialogState.manageConstrains = true;
	}

	function openDeleteDialog(index) {
		indexToUpdateOrDelete = index;
		permission = { ...$permissions[index] };
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
		permission = { ...initialPermission };
	}

	$: if (!dialogState.createOrUpdate && !dialogState.remove && !dialogState.manageConstrains) {
		updateMode = false;
		reset();
	}
</script>

<Title title="Permissions" />

<h6>Permissions</h6>

<!-- DIALOG CREATE/UPDATE PERMISSION -->
<CommonDialog
	on:form-submit={handleFormPost}
	bind:active={dialogState.createOrUpdate}
	loading={loading.createOrUpdate}
	submitButtonCaption={updateMode ? 'Update' : 'Add Permission'}
	submitButtonCaptionOnLoading={updateMode ? 'Updating...' : 'Adding Permission...'}
	title="{updateMode ? 'Update' : 'Add'} Permission">
	<Row class="mt-3">
		<Col>
			<TextField bind:value={permission.name} disabled={updateMode} required>Name</TextField>
		</Col>
	</Row>
	<Row>
		<Col>
			<Textarea bind:value={permission.description} required>Description</Textarea>
		</Col>
	</Row>
</CommonDialog>

<CommonDialog
	on:form-submit={updateConstraints}
	bind:active={dialogState.manageConstrains}
	loading={loading.savingConstraints}
	submitButtonCaption="Save"
	submitButtonCaptionOnLoading="Saving..."
	title="Manage Permission Constraints">
	<ConstraintsEditor bind:this={constrainEditor} bind:constraints={permission.constraints} />
</CommonDialog>

<!-- DIALOG REMOVE PERMISSION -->
<DeleteDialog
	bind:active={dialogState.remove}
	loading={loading.remove}
	title="Delete Permission"
	on:remove-button-clicked={_remove}>
	Are you sure want to delete <b>{permission.name}</b> from permissions?
</DeleteDialog>

{#if $permissions.length}
	<Table headers={['Name', 'Description', 'Constraints']}>
		{#each $permissions as permission, index}
			<tr>
				<td>{permission.name}</td>
				<td>{permission.description}</td>
				<td>{permission.constraints.map((c) => c.name).join(', ')}</td>
				<td class="text-center table-row-actions">
					<SmallButton
						on:click={() => openManageConstraintsDialog(index)}
						iconPath={mdiLock}
						title="Manage permission constraint" />
					<SmallButton
						on:click={() => openUpdateDialog(index)}
						iconPath={mdiPencil}
						title="Update/edit permission" />
					<SmallButton
						on:click={() => openDeleteDialog(index)}
						iconPath={mdiDelete}
						title="Remove/delete permission" />
				</td>
			</tr>
		{/each}
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
