<script>
	export let sidenav;
	import { NavigationDrawer, List, Overlay } from 'svelte-materialify/src/';
	import NavItem from './NavItem.svelte';
	import routes from '../util/routes';

	export let segment;
</script>

<NavigationDrawer active={sidenav} borderless class="h-screen" clipped fixed>
	<List dense nav>
		{#each routes as item}
			<NavItem {item} {segment} />
		{/each}
	</List>
</NavigationDrawer>
<Overlay
	class="md:d-none"
	index="3"
	active={sidenav}
	on:click={() => (sidenav = false)}
	fadeOptions={{ duration: 250 }} />
