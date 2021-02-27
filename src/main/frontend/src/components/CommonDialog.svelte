<script context="module">
	let id = 1;
</script>

<script>
	import { Button, Dialog } from 'svelte-materialify/src';
	import { createEventDispatcher } from 'svelte';

	id++;

	const emit = createEventDispatcher();

	export let active = false;
	export let title = '';
	export let loading = false;
	export let submitButtonCaption = '';
	export let submitButtonCaptionOnLoading = '';
</script>

<Dialog class="d-flex flex-column" bind:active>
	<h6 class="pa-2">{title}</h6>
	<!-- style="height:100%;max-height:100%;overflow-y:auto" -->
	<form
		on:submit|preventDefault={() => emit('form-submit')}
		id="dialog-form-{id}"
		class="d-flex flex-column pa-2 w-full h-full max-h-full overflow-y-auto">
		<slot />
	</form>
	<div class="mt-3 pa-2 d-flex">
		<Button depressed on:click={() => (active = false)}>cancel</Button>
		<Button
			class="ml-auto primary-color"
			form="dialog-form-{id}"
			name="button"
			disabled={loading}
			type="submit">
			{#if loading}
				{submitButtonCaptionOnLoading}
			{:else}
				{submitButtonCaption}
			{/if}
		</Button>
	</div>
</Dialog>
