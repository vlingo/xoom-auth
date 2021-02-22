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
	import { tenants, addTenant } from '../../stores/index.js';

	let initialTenant = {
		name: '',
		description: '',
		active: false,
	};

	let loading = {
		create: false,
	};

	let dialogActive = false;
	let updateMode = false;

	let tenant = { ...initialTenant };

	function _addTenant() {
		loading.create = true;
		addTenant(tenant)
			.then(({ status }) => status === 200 && resetTenant())
			.finally(() => {
				loading.create = false;
				dialogActive = false;
			});
	}

	function openUpdateDialog(index) {
		updateMode = true;
		tenant = $tenants[index];
		dialogActive = true;
	}

	function resetTenant() {
		tenant = { ...initialTenant };
	}

	$: if (dialogActive == false) updateMode = false;
</script>

<svelte:head>
	<title>Tenants Subscribtion - Vlingo Authentication</title>
</svelte:head>

<h6>Tenants Subscribtion</h6>

<!-- DIALOG CREATE TENANT SUBSCRIBTION -->
<Dialog class="pa-4" bind:active={dialogActive}>
	<form on:submit|preventDefault={_addTenant} class="s-card d-flex flex-column">
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
			<Button class="ml-auto primary-color" type="submit">
				{loading.create ? 'subscribing...' : 'subscribe'}
			</Button>
		</div>
	</form>
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
					<Button fab depressed rounded size="x-small">
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
	on:click={() => (dialogActive = true)}
	style="position: fixed; margin: 1em; right: 0; bottom: 0;">
	<Icon path={mdiPlus} float />
</Button>
