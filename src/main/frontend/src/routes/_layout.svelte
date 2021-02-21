<script>
	import {
		AppBar,
		Button,
		Container,
		Icon,
		ListItem,
		MaterialApp,
		Menu,
	} from 'svelte-materialify/src';
	import {
		mdiMenu,
		mdiDotsVertical,
		mdiCodeTags,
		mdiInformation,
		mdiWeatherNight,
		mdiWeatherSunny,
	} from '@mdi/js';
	import { theme, isLoggedIn } from '../stores';
	import SiteNavigation from '../components/SiteNavigation.svelte';

	const toggleTheme = () => ($theme = $theme === 'light' ? 'dark' : 'light');

	export let segment;
	let sidenav = process.browser && window.innerWidth > 768;
</script>

<MaterialApp theme={$theme}>
	{#if $isLoggedIn}
		<AppBar fixed style="width:100%">
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
			<Button aria-label="Toggle Theme" depressed fab on:click={toggleTheme} size="small">
				<Icon path={$theme === 'light' ? mdiWeatherNight : mdiWeatherSunny} />
			</Button>
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

		<SiteNavigation {segment} bind:sidenav />
	{/if}

	<Container class="main">
		<slot />
	</Container>
</MaterialApp>

<style lang="scss" src="../sass/main.scss" global></style>
