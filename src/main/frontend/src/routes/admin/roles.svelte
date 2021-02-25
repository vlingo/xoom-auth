<script context="module">
	export async function preload(/* page, session */) {
		const fetchRoles = await this.fetch('/api/tenants/roles');
		const roles = await fetchRoles.json();
		return { roles };
	}
</script>

<script>
	import Title from '../../components/title.svelte';
	import { mdiDelete, mdiPencil, mdiPlus } from '@mdi/js';
	import { Button, Col, Icon, Row, Table, TextField } from 'svelte-materialify/src';
	import { roles as rolesStore, create, update, remove } from '../../stores/roles.js';
	import DeleteDialog from '../../components/DeleteDialog.svelte';
	import CreateUpdateDialog from '../../components/CreateUpdateDialog.svelte';

	export let roles;
	$rolesStore = roles;

	let initialRole = {
		name: '',
		description: '',
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
		role = $rolesStore[index];
		dialogState.createOrUpdate = true;
	}

	function openDeleteDialog(index) {
		indexToUpdateOrDelete = index;
		role = { ...$rolesStore[index] };
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
<CreateUpdateDialog
	on:form-submit={handleFormPost}
	bind:active={dialogState.createOrUpdate}
	loading={loading.createOrUpdate}
	submitButtonCaption={updateMode ? 'Update' : 'Add Role'}
	submitButtonCaptionOnLoading={updateMode ? 'Updating...' : 'Adding Role...'}
	title="{updateMode ? 'Update' : 'Add'} Role">
	<Row class="mt-3">
		<Col>
			<TextField bind:value={role.name} required>Name</TextField>
		</Col>
	</Row>
	<Row>
		<Col>
			<TextField bind:value={role.description} required>Description</TextField>
		</Col>
	</Row>
</CreateUpdateDialog>

<!-- DIALOG REMOVE ROLE -->
<DeleteDialog
	bind:active={dialogState.remove}
	loading={loading.remove}
	title="Delete Role"
	on:remove-button-clicked={_remove}>
	Are you sure want to delete <b>{role.name}</b> from roles?
</DeleteDialog>

{#if $rolesStore.length}
	<Table class="p-5 mt-5 s-card">
		<thead>
			<tr style="font-weight:bold">
				<td>Name</td>
				<td>Description</td>
				<td class="text-center">Actions</td>
			</tr>
		</thead>
		<tbody>
			{#each $rolesStore as role, index}
				<tr>
					<td>{role.name}</td>
					<td>{role.description}</td>
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
