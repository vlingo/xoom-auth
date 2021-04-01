<script>
	import { Button, Icon } from 'svelte-materialify/src';
	import { mdiPlus } from '@mdi/js';
</script>

<Button
	class="primary-color"
	fab
	on:click
	style="position: fixed; margin: 1em; right: 0; bottom: 0"
	float>
	<Icon path={mdiPlus} />
</Button>
