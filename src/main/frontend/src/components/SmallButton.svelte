<script>
	import { Button, Icon } from 'svelte-materialify/src';
	export let iconPath;
	export let title;
</script>

<Button fab depressed on:click {title} rounded size="x-small" text>
	<Icon path={iconPath} />
</Button>
