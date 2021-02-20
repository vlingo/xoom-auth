<script>
	let data = [
		{
			name: 'John Doe',
			description: 'Description of the tenant',
			active: true,
		},
		{
			name: 'Kevin Wu',
			description: 'Description of the tenant',
			active: true,
		},
		{
			name: 'Lisa Smit',
			description: 'Description of the tenant',
			active: true,
		},
		{
			name: 'David Willish',
			description: 'Description of the tenant',
			active: true,
		},
		{
			name: 'Samantha Bryan',
			description: 'Description of the tenant',
			active: true,
		},
	];
</script>

<svelte:head>
	<title>Tenants Subscribtion - Vlingo Authentication</title>
</svelte:head>

<h6>Tenants Subscribtion</h6>

<div class="mt-5">
	<table style="width:100%">
		<thead>
			<tr>
				<td>Name</td>
				<td>Description</td>
				<td>Active</td>
			</tr>
		</thead>

		<tbody>
			{#each data as tenant}
				<tr>
					<td>{tenant.name}</td>
					<td>{tenant.description}</td>
					<td>{tenant.active}</td>
				</tr>
			{/each}
		</tbody>
	</table>
</div>
