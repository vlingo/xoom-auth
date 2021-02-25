<script context="module">
	export async function preload(/* page, session */) {
		const fetchPermissions = await this.fetch('/api/tenants/permissions');
		const permissions = await fetchPermissions.json();
		return { permissions };
	}
</script>

<script>
	import Title from '../../components/title.svelte';
	import { mdiDelete, mdiPencil, mdiPlus } from '@mdi/js';
	import { Button, Col, Icon, Row, Select, Table, TextField } from 'svelte-materialify/src';
	import {
		permissions as permissionsStore,
		names,
		fetchNames,
		create,
		update,
		remove,
	} from '../../stores/permissions.js';
	import DeleteDialog from '../../components/DeleteDialog.svelte';
	import CreateUpdateDialog from '../../components/CreateUpdateDialog.svelte';
	import { dialogState, loading } from '../../shared/common.js';

	export let permissions;
	$permissionsStore = permissions;

	let initialPermission = {
		name: '',
		description: '',
		constraints: [],
	};

	let indexToUpdateOrDelete = 0;
	let updateMode = false;

	let permission = { ...initialPermission };

	const mapNames = (name) => ({ name, value: name });

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

	function openCreateDialog() {
		updateMode = false;
		dialogState.createOrUpdate = true;
	}

	function openUpdateDialog(index) {
		updateMode = true;
		indexToUpdateOrDelete = index;
		permission = $permissionsStore[index];
		dialogState.createOrUpdate = true;
	}

	function openDeleteDialog(index) {
		indexToUpdateOrDelete = index;
		permission = { ...$permissionsStore[index] };
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

	function getAvailableNames() {
		let usedNames = [];
		$permissionsStore.forEach(
			(permission) => (usedNames = [...usedNames, ...permission.constraints])
		);
		return $names.filter((name) => !usedNames.includes(name));
	}

	$: if (dialogState.createOrUpdate == false && !dialogState.remove) {
		updateMode = false;
		reset();
	}

	// Auto fetch names when create dialog or update dialog is opened
	$: if (dialogState.createOrUpdate) {
		fetchNames();
	}

	$: availableNames = updateMode
		? getAvailableNames().concat(permission.constraints).map(mapNames)
		: $names.map(mapNames);
</script>

<Title title="Permissions" />

<h6>Permissions</h6>

<!-- DIALOG CREATE/UPDATE PERMISSION -->
<CreateUpdateDialog
	on:form-submit={handleFormPost}
	bind:active={dialogState.createOrUpdate}
	loading={loading.createOrUpdate}
	submitButtonCaption={updateMode ? 'Update' : 'Add Permission'}
	submitButtonCaptionOnLoading={updateMode ? 'Updating...' : 'Adding Permission...'}
	title="{updateMode ? 'Update' : 'Add'} Permission">
	<Row class="mt-3">
		<Col>
			<TextField bind:value={permission.name} required>Name</TextField>
		</Col>
	</Row>
	<Row>
		<Col>
			<TextField bind:value={permission.description} required>Description</TextField>
		</Col>
	</Row>
	<Row>
		<Col>
			<Select items={availableNames} bind:value={permission.constraints} multiple>
				Constraints
			</Select>
		</Col>
	</Row>
</CreateUpdateDialog>

<!-- DIALOG REMOVE PERMISSION -->
<DeleteDialog
	bind:active={dialogState.remove}
	loading={loading.remove}
	title="Delete Permission"
	on:remove-button-clicked={_remove}>
	Are you sure want to delete <b>{permission.name}</b> from permissions?
</DeleteDialog>

{#if $permissionsStore.length}
	<Table class="p-5 mt-5 s-card">
		<thead>
			<tr style="font-weight:bold">
				<td>Name</td>
				<td>Description</td>
				<td>Constraints</td>
				<td class="text-center">Actions</td>
			</tr>
		</thead>
		<tbody>
			{#each $permissionsStore as permission, index}
				<tr>
					<td>{permission.name}</td>
					<td>{permission.description}</td>
					<td>{permission.constraints.join(', ')}</td>
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
