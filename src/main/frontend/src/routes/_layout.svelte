<script>
	import { Container, MaterialApp } from 'svelte-materialify/src';
	import { isMobile, theme } from '../stores';
</script>

<svelte:window on:resize={isMobile.check} />

<MaterialApp theme={$theme}>
	<Container>
		<slot />
	</Container>
</MaterialApp>
