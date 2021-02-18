<script>
	import { MaterialApp } from 'svelte-materialify/src';
	import { isMobile, theme } from '../stores';

	$: bgTheme = $theme === 'light' ? '#ffffff' : '#212121';
</script>

<svelte:window on:resize={isMobile.check} />

<div style="height: 100%; background-color: {bgTheme}">
	<MaterialApp theme={$theme}>
		<!--  -->
	</MaterialApp>
</div>
