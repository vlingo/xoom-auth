<script>
	import { Checkbox, Col, TextField } from 'svelte-materialify/src';
	import { login } from '../stores/index.js';

	/**  @type {HTMLFormElement} */
	let form;

	const loginData = {
		tenantId: '',
		username: '',
		credentialId: '',
		secret: '',
	};

	let useAsCredentialID = false;

	function _login() {
		if (form.checkValidity()) {
			login();
		} else {
			alert('Login failed');
		}
	}

	$: if (useAsCredentialID) {
		loginData.credentialId = loginData.username;
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
		<Checkbox bind:checked={useAsCredentialID}>Use as Credential ID</Checkbox>
	</Col>
	<Col>
		<TextField bind:value={loginData.credentialId} required>Credential ID</TextField>
	</Col>
	<Col>
		<TextField bind:value={loginData.secret} type="password" required>Secret</TextField>
	</Col>
</form>
