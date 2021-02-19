<script>
	import { Button, Card, CardActions, Divider, Row } from 'svelte-materialify/src';
	import RegisterUser from '../components/RegisterUser.svelte';
	import SignOn from '../components/SignOn.svelte';

	$: caption = register ? 'Register' : 'Sign On';

	let register = false;
</script>

<svelte:head>
	<title>{caption} - Vlingo Auth</title>
</svelte:head>

<Row class="justify-center flex-column align-center">
	<Card class="login-register-card" outlined style="width: 100%; max-width: 500px">
		<div class="pa-5">
			<Row class="flex-column">
				<img
					class="align-self-center"
					src="/logo.e642b219.png"
					alt="Vlingo Logo"
					style="width: 6em" />

				<h6 class="text-center">{!register ? 'Sign On' : 'Register'}</h6>

				{#if register}
					<RegisterUser />
				{:else}
					<SignOn />
				{/if}
			</Row>

			<Divider class="mb-2" />

			<CardActions>
				{#if register}
					<Button depressed on:click={() => (register = false)}>Cancel</Button>
				{:else}
					<!-- svelte-ignore a11y-invalid-attribute -->
					<a class="d-block" href="#" on:click={() => (register = true)}>
						Don't have an account yet? Register here
					</a>
				{/if}
				<Button
					class="ml-auto primary-color"
					depressed
					form="register-and-login-form"
					type="submit">
					{caption}
				</Button>
			</CardActions>
		</div>
	</Card>
</Row>
