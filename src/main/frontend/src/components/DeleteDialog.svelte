<script>
	import { Button, Dialog, Divider } from 'svelte-materialify/src';
	import { createEventDispatcher } from 'svelte';

	const emit = createEventDispatcher();

	export let active = false;
	export let loading = false;
	export let title = '';
</script>

<Dialog class="pa-4" bind:active>
	<h6 class="mb-2">{title}</h6>
	<Divider />
	<div class="mt-4 mb-4">
		<slot />
	</div>
	<Divider />
	<div class="mt-3 d-flex">
		<Button depressed on:click={() => (active = false)}>cancel</Button>
		<Button
			class="ml-auto red white-text"
			depressed
			disabled={loading}
			on:click={() => emit('remove-button-clicked')}>
			{loading ? 'deleting...' : 'delete'}
		</Button>
	</div>
</Dialog>
