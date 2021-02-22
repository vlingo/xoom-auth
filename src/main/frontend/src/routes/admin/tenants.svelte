<script>
	import { mdiCheckboxBlank, mdiCheckboxMarked, mdiDelete, mdiPencil, mdiPlus } from '@mdi/js';
	import {
		Button,
		Checkbox,
		Col,
		Dialog,
		Divider,
		Icon,
		Row,
		Table,
		TextField,
	} from 'svelte-materialify/src';
	import { tenants, addTenant, updateTenant, removeTenant } from '../../stores/index.js';

	let initialTenant = {
		name: '',
		description: '',
		active: false,
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

	let tenant = { ...initialTenant };

	function _addTenant() {
		loading.createOrUpdate = true;
		addTenant(tenant)
			.then(({ status }) => status === 200 && resetTenant())
			.finally(() => {
				loading.createOrUpdate = false;
				dialogState.createOrUpdate = false;
			});
	}

	function _updateTenant() {
		loading.createOrUpdate = true;
		updateTenant(indexToUpdateOrDelete, tenant)
			.then(({ status }) => status === 200 && resetTenant())
			.finally(() => {
				loading.createOrUpdate = false;
				dialogState.createOrUpdate = false;
			});
	}

	function _removeTenant() {
		loading.remove = true;
		removeTenant(indexToUpdateOrDelete)
			.then(({ status }) => {
				if (status === 200) {
					dialogState.remove = false;
				}
			})
			.finally(() => {
				loading.remove = false;
			});
	}

	function openUpdateDialog(index) {
		updateMode = true;
		tenant = $tenants[index];
		dialogState.createOrUpdate = true;
	}

	function openDeleteDialog(index) {
		indexToUpdateOrDelete = index;
		tenant = { ...$tenants[index] };
		dialogState.remove = true;
	}

	function handleFormPost() {
		if (updateMode) {
			_updateTenant();
		} else {
			_addTenant();
		}
	}

	function resetTenant() {
		tenant = { ...initialTenant };
	}

	$: if (dialogState.createOrUpdate == false && !dialogState.remove) {
		updateMode = false;
		resetTenant();
	}
</script>

<svelte:head>
	<title>Tenants Subscribtion - Vlingo Authentication</title>
</svelte:head>

<h6>Tenants Subscribtion</h6>

<!-- DIALOG CREATE/UPDATE TENANT SUBSCRIBTION -->
<Dialog class="pa-4" bind:active={dialogState.createOrUpdate}>
	<form on:submit|preventDefault={handleFormPost} class="s-card d-flex flex-column">
		<h6 class="mb-2">{updateMode ? 'Update' : ''} Tenant Subscribtion</h6>
		<Divider />
		<Row class="mt-4">
			<Col>
				<TextField autofocus bind:value={tenant.name} required>Name</TextField>
			</Col>
		</Row>
		<Row>
			<Col>
				<TextField bind:value={tenant.description} required>Description</TextField>
			</Col>
		</Row>
		<Row class="mb-2">
			<Col>
				<Checkbox bind:checked={tenant.active}>Active</Checkbox>
			</Col>
		</Row>
		<Divider />
		<div class="mt-3 d-flex">
			<Button class="ml-auto primary-color" disabled={loading.createOrUpdate} type="submit">
				{#if updateMode}
					{loading.createOrUpdate ? 'updating...' : 'update'}
				{:else}
					{loading.createOrUpdate ? 'subscribing...' : 'subscribe'}
				{/if}
			</Button>
		</div>
	</form>
</Dialog>

<!-- DIALOG REMOVE TENANT SUBSCRIBTION -->
<Dialog class="pa-4" bind:active={dialogState.remove}>
	<h6 class="mb-2">Delete Tenant Subscribtion</h6>
	<Divider />
	<div class="mt-4 mb-4">
		Are you sure want to delete <b>{tenant.name}</b> from tenants?
	</div>
	<Divider />
	<div class="mt-3 d-flex">
		<Button
			class="ml-auto red white-text"
			depressed
			disabled={loading.remove}
			on:click={_removeTenant}>
			{loading.remove ? 'deleting...' : 'delete'}
		</Button>
	</div>
</Dialog>

<Table class="p-5 mt-5 s-card">
	<thead>
		<tr style="font-weight:bold">
			<td>Name</td>
			<td>Description</td>
			<td class="text-center">Active</td>
			<td class="text-center">Action</td>
		</tr>
	</thead>
	<tbody>
		{#each $tenants as tenant, index}
			<tr>
				<td>{tenant.name}</td>
				<td>{tenant.description}</td>
				<td class="text-center">
					<Icon path={tenant.active ? mdiCheckboxMarked : mdiCheckboxBlank} />
				</td>
				<td class="text-center">
					<Button
						fab
						depressed
						on:click={() => openUpdateDialog(index)}
						rounded
						size="x-small">
						<Icon path={mdiPencil} />
					</Button>
					<Button
						fab
						depressed
						on:click={() => openDeleteDialog(index)}
						rounded
						size="x-small">
						<Icon path={mdiDelete} />
					</Button>
				</td>
			</tr>
		{/each}
	</tbody>
</Table>

<Button
	class="primary-color"
	fab
	on:click={() => (dialogState.createOrUpdate = true)}
	style="position: fixed; margin: 1em; right: 0; bottom: 0;">
	<Icon path={mdiPlus} float />
</Button>
