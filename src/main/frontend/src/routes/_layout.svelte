<script>
	import {
		AppBar,
		Button,
		Container,
		Icon,
		ListItem,
		MaterialApp,
		Menu,
		NavigationDrawer,
	} from 'svelte-materialify/src';
	import { mdiMenu, mdiDotsVertical, mdiCodeTags, mdiInformation } from '@mdi/js';
	import { isMobile, theme } from '../stores';
	import SiteNavigation from '../components/SiteNavigation.svelte';

	export let segment;

	let sidenav = false;
</script>

<svelte:window on:resize={isMobile.check} />

<MaterialApp theme={$theme}>
	<AppBar>
		<div slot="icon">
			<Button
				aria-label="Open Menu"
				depressed
				fab
				size="small"
				on:click={() => (sidenav = !sidenav)}>
				<Icon path={mdiMenu} />
			</Button>
		</div>
		<span slot="title">Title</span>
		<div style="flex-grow:1" />
		<Menu right>
			<div slot="activator">
				<Button depressed fab size="small">
					<Icon path={mdiDotsVertical} />
				</Button>
			</div>
			<ListItem>
				<a href="https://github.com/vlingo/vlingo-auth" target="_blank">
					<Icon class="m-0 mr-3" path={mdiCodeTags} />
					<span>Source Code</span>
				</a>
			</ListItem>
			<ListItem>
				<a href="https://docs.vlingo.io/vlingo-auth" target="_blank">
					<Icon class="m-0 mr-3" path={mdiInformation} />
					<span>Documentation</span>
				</a>
			</ListItem>
		</Menu>
	</AppBar>

	<SiteNavigation {segment} mobile={$isMobile} bind:sidenav />

	<Container>
		<slot />
	</Container>
</MaterialApp>
