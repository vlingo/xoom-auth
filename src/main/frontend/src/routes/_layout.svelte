<script>
	import { Container, MaterialApp } from 'svelte-materialify/src';
	import { isMobile, theme } from '../stores';

	$: bgTheme = $theme === 'light' ? '#ffffff' : '#212121';
</script>

<svelte:window on:resize={isMobile.check} />

<div style="background-color: {bgTheme}">
	<MaterialApp theme={$theme}>
		<Container class="ma-0">
			<slot />
		</Container>
	</MaterialApp>
</div>
