<script>
	export let sidenav;
	import { NavigationDrawer, List, Overlay, ListItem, Icon } from 'svelte-materialify/src/';
	import NavItem from './NavItem.svelte';
	import routes from '../util/routes';
	import { mdiExitToApp } from '@mdi/js';
	import { logoout } from '../stores/index.js';

	export let segment;
</script>

<NavigationDrawer active={sidenav} borderless class="h-screen" clipped fixed>
	<List dense nav>
		{#each routes as item}
			<NavItem {item} {segment} />
		{/each}
		<ListItem class="mt-auto" dense on:click={logoout}>
			<span slot="prepend">
				<Icon path={mdiExitToApp} />
			</span>
			Logout
		</ListItem>
	</List>
</NavigationDrawer>
<Overlay class="md:d-none" index="3" active={sidenav} on:click={() => (sidenav = false)} />
