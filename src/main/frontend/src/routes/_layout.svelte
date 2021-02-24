<script context="module">
	export async function preload(/* page, session */) {
		const fetchTenants = await this.fetch(`/api/tenants`);
		const tenants = await fetchTenants.json();

		const fetchUsers = await this.fetch('/api/tenants/users');
		const users = await fetchUsers.json();

		const fetchGroups = await this.fetch('/api/tenants/groups');
		const groups = await fetchGroups.json();

		const fetchRoles = await this.fetch('/api/tenants/roles');
		const roles = await fetchRoles.json();

		return { tenants, users, groups, roles };
	}
</script>

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
	import {
		theme,
		isLoggedIn,
		tenants as tenantsStore,
		users as usersStore,
		groups as groupsStore,
		roles as rolesStore,
		title,
	} from '../stores/index.js';
	import { stores } from '@sapper/app';

	import SiteNavigation from '../components/SiteNavigation.svelte';

	const toggleTheme = () => ($theme = $theme === 'light' ? 'dark' : 'light');

	export let segment;
	const { page } = stores();

	// Get data from preload
	export let tenants;
	export let users;
	export let groups;
	export let roles;

	// Set data to their store
	tenantsStore.set(tenants);
	usersStore.set(users);
	groupsStore.set(groups);
	rolesStore.set(roles);

	$: isLoginPage = $page.path === '/';

	let sidenav = process.browser && window.innerWidth > 768 && !isLoginPage;
</script>

<MaterialApp theme={$theme}>
	{#if $isLoggedIn && !isLoginPage}
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
			<span slot="title">{$title}</span>
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
