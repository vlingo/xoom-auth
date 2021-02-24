<script>
	export let title;
	import { title as titleStore } from '../stores/index.js';

	titleStore.set(title);
</script>

<svelte:head>
	<title>{title} - Vlingo Authentication</title>
</svelte:head>
