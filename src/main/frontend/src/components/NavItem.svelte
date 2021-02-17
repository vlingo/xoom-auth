<script>
	import { Icon, ListItem } from "svelte-materialify/src";
	import { stores } from '@sapper/app';

	const { page } = stores();

	export let item;
	export let segment;

	$: active = segment ? $page.path === item.href : item.href === "";
</script>

<a href={item.href} rel="prefetch">
	<ListItem active={active} dense>
		<span slot="prepend">
			{#if item.icon}
				<Icon path={active ? item.openIcon : item.icon} />
			{/if}
		</span>
		{item.text}
	</ListItem>
</a>

<!-- <style global>
	.nav-active-link {
	  color: rgb(25, 118, 210) !important;
	}
	.nav-active-link .s-icon {
	  color: inherit;
	}
</style> -->
