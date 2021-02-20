<script>
	import { mdiCheckboxBlank, mdiCheckboxMarked } from '@mdi/js';
	import { Icon, Table } from 'svelte-materialify/src';

	let data = [
		{
			name: 'John Doe',
			description: 'Description of the tenant',
			active: false,
		},
		{
			name: 'Kevin Wu',
			description: 'Description of the tenant',
			active: true,
		},
		{
			name: 'Lisa Smit',
			description: 'Description of the tenant',
			active: false,
		},
		{
			name: 'David Willish',
			description: 'Description of the tenant',
			active: true,
		},
		{
			name: 'Samantha Bryan',
			description: 'Description of the tenant',
			active: false,
		},
	];
</script>

<svelte:head>
	<title>Tenants Subscribtion - Vlingo Authentication</title>
</svelte:head>

<h6>Tenants Subscribtion</h6>

<div class="mt-5">
	<Table class="p-5 s-card">
		<thead>
			<tr style="font-weight:bold">
				<td>Name</td>
				<td>Description</td>
				<td class="text-center">Active</td>
			</tr>
		</thead>
		<tbody>
			{#each data as tenant}
				<tr>
					<td>{tenant.name}</td>
					<td>{tenant.description}</td>
					<td class="text-center">
						<Icon path={tenant.active ? mdiCheckboxMarked : mdiCheckboxBlank} />
					</td>
				</tr>
			{/each}
		</tbody>
	</Table>
</div>
