<script>
	const tenants = [
		{
			name: 'John Doe',
			description: 'Description of tenant',
			active: true,
		},
		{
			name: 'Lisa Smith',
			description: 'Description of tenant',
			active: false,
		},
		{
			name: 'Andy Chan',
			description: 'Description of tenant',
			active: true,
		},
		{
			name: 'Kyle Press',
			description: 'Description of tenant',
			active: false,
		},
	]
</script>

<svelte:head>
	<title>Tenant Subsribtion</title>
</svelte:head>

<table>
	<thead>
		<tr>
			<td>Name</td>
			<td>Description</td>
			<td align="center">Active</td>
		</tr>
	</thead>
	<tbody>
		{#each tenants as tenant}
			<tr>
				<td>{tenant.name}</td>
				<td>{tenant.description}</td>
				<td align="center">
					<input class="checkbox" type="checkbox" bind:checked={tenant.active} disabled>
				</td>
			</tr>
		{/each}
	</tbody>
</table>

<style lang="css">
	table {
		width: 100%;
	}

	table, tr, td {
		border: 1px solid #cecece;
		border-collapse: collapse;
	}

	td {
		padding: 10px;
	}

	thead td {
		font-weight: bold;
	}

	.checkbox {
		display: block;
		width: 20px;
		height: 20px;
	}
</style>
