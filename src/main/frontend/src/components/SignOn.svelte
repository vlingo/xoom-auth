<script>
	import { Col, TextField } from 'svelte-materialify/src';
	import { login } from '../stores/index.js';

	/**  @type {HTMLFormElement} */
	let form;

	const loginData = {
		tenantId: '',
		username: '',
		credentialId: '',
		secret: '',
	};

	function _login() {
		if (form.checkValidity()) {
			login();
		} else {
			alert('Login failed');
		}
	}
</script>

<form bind:this={form} id="register-and-login-form" on:submit|preventDefault={_login}>
	<Col>
		<TextField autofocus bind:value={loginData.tenantId} required>Tenant ID</TextField>
	</Col>
	<Col>
		<TextField bind:value={loginData.username} required>Username</TextField>
	</Col>
	<Col>
		<TextField bind:value={loginData.secret} type="password" required>Secret</TextField>
	</Col>
</form>
