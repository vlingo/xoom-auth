<script>
	import { mdiCheckboxBlank, mdiCheckboxMarked, mdiDelete, mdiPencil } from '@mdi/js';
	import {
		Button,
		Checkbox,
		Col,
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

	let newTenant = { ...initialTenant };

	function _addTenant() {
		loading.create = true;
		addTenant(newTenant)
			.then(({ status }) => status === 200 && resetTenant())
			.finally(() => (loading.create = false));
	}

	function resetTenant() {
		newTenant = { ...initialTenant };
	}
</script>

<svelte:head>
	<title>Tenants Subscribtion - Vlingo Authentication</title>
</svelte:head>

<h6>Tenants Subscribtion</h6>

<div class="mt-5">
	<form on:submit|preventDefault={_addTenant} class="mt-5 mb-5 s-card d-flex flex-column">
		<Row>
			<Col>
				<TextField autofocus bind:value={newTenant.name} required>Name</TextField>
			</Col>
		</Row>
		<Row>
			<Col>
				<TextField bind:value={newTenant.description} required>Description</TextField>
			</Col>
		</Row>
		<Row>
			<Col>
				<Checkbox bind:checked={newTenant.active}>Active</Checkbox>
			</Col>
		</Row>
		<Divider />
		<div class="mt-3 mb-3 d-flex">
			<Button class="ml-auto primary-color" type="submit">
				{loading.create ? 'subscribing...' : 'subscribe'}
			</Button>
		</div>
	</form>

	<Table class="p-5 s-card">
		<thead>
			<tr style="font-weight:bold">
				<td>Name</td>
				<td>Description</td>
				<td class="text-center">Active</td>
				<td class="text-center">Action</td>
			</tr>
		</thead>
		<tbody>
			{#each $tenants as tenant}
				<tr>
					<td>{tenant.name}</td>
					<td>{tenant.description}</td>
					<td class="text-center">
						<Icon path={tenant.active ? mdiCheckboxMarked : mdiCheckboxBlank} />
					</td>
					<td class="text-center">
						<Button fab depressed rounded size="x-small">
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
</div>
