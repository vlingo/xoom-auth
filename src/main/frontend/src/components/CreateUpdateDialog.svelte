<script>
	import { Button, Dialog, Divider } from 'svelte-materialify/src';
	import { createEventDispatcher } from 'svelte';

	const emit = createEventDispatcher();

	export let active = false;
	export let title = '';
	export let loading = false;
	export let submitButtonCaption = '';
	export let submitButtonCaptionOnLoading = '';
</script>

<Dialog class="pa-4" bind:active>
	<form on:submit|preventDefault={() => emit('form-submit')} class="d-flex flex-column">
		<h6 class="mb-2">{title}</h6>
		<Divider />
		<slot />
		<Divider />
		<div class="mt-3 d-flex">
			<Button depressed on:click={() => (active = false)}>cancel</Button>
			<Button class="ml-auto primary-color" name="button" disabled={loading} type="submit">
				{#if loading}
					{submitButtonCaptionOnLoading}
				{:else}
					{submitButtonCaption}
				{/if}
			</Button>
		</div>
	</form>
</Dialog>
