<script>
	import { Icon, ListItem } from 'svelte-materialify/src';
	import { stores } from '@sapper/app';

	const { page } = stores();

	export let item;
	export let segment;

	$: active = segment ? $page.path === item.href : item.href === '';
</script>

<a href={item.href} rel="prefetch">
	<ListItem {active} dense>
		<span slot="prepend">
			{#if item.icon}
				<Icon path={active ? item.openIcon : item.icon} />
			{/if}
		</span>
		{item.text}
	</ListItem>
</a>
