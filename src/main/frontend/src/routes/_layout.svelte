<script context="module">
	export async function preload(page /* session */) {
		if (page.path != '/') {
			const fetchGroups = await this.fetch('/api/tenants/groups');
			const _groups = await fetchGroups.json();

			const fetchPermissions = await this.fetch('/api/tenants/permissions');
			const _permissions = await fetchPermissions.json();

			const fetchRoles = await this.fetch('/api/tenants/roles');
			const _roles = await fetchRoles.json();

			const fetchTenants = await this.fetch('/api/tenants');
			const _tenants = await fetchTenants.json();

			const fetchUsers = await this.fetch('/api/tenants/users');
			const _users = await fetchUsers.json();

			return { _groups, _permissions, _roles, _tenants, _users };
		}
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
	import { theme, isLoggedIn, title } from '../stores/index.js';
	import { stores } from '@sapper/app';
	import { permissions } from '../stores/permissions.js';
	import { groups } from '../stores/groups.js';
	import { roles } from '../stores/roles.js';
	import { tenants } from '../stores/tenantsSubscription.js';
	import { users } from '../stores/users.js';

	import SiteNavigation from '../components/SiteNavigation.svelte';

	const toggleTheme = () => ($theme = $theme === 'light' ? 'dark' : 'light');

	export let segment;

	export let _permissions;
	export let _groups;
	export let _roles;
	export let _tenants;
	export let _users;

	const { page } = stores();

	$: isLoginPage = $page.path === '/';

	if (!isLoginPage && isLoggedIn) {
		$permissions = _permissions;
		$groups = _groups;
		$roles = _roles;
		$tenants = _tenants;
		$users = _users;
	}

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
