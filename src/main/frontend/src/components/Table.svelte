<script>
	import { Table } from 'svelte-materialify/src';

	export let headers = [];
</script>

<Table class="p-5 mt-5 s-card">
	<thead>
		<tr style="font-weight:bold">
			{#each headers as header}
				<td>{header}</td>
			{/each}
			<td class="text-center">Actions</td>
		</tr>
	</thead>
	<tbody>
		<slot />
	</tbody>
</Table>
