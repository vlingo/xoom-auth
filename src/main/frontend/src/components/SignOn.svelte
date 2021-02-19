<script>
	import { Col, Row, TextField } from 'svelte-materialify/src';

	const loginData = {
		tenantId: '',
		username: '',
		credentialId: '',
		secret: '',
	};

	function login() {
		console.log(loginData);
	}
</script>

<form class="" id="register-and-login-form" on:submit|preventDefault={login}>
	<Col>
		<TextField autofocus bind:value={loginData.tenantId} required>Tenant ID</TextField>
	</Col>
	<Col>
		<TextField bind:value={loginData.username} required>Username</TextField>
	</Col>
	<Col>
		<TextField bind:value={loginData.secret} required>Secret</TextField>
	</Col>
</form>
