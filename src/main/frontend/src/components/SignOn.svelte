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

	function handleUseAsCredentialIDCheckbox() {
		if (useAsCredentialID && !!loginData.username) {
			loginData.credentialId = loginData.username;
		} else {
			loginData.credentialId = '';
		}
	}

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
		<Checkbox bind:checked={useAsCredentialID} on:change={handleUseAsCredentialIDCheckbox}>
			Use as Credential ID
		</Checkbox>
	</Col>
	<Col>
		<TextField bind:value={loginData.credentialId} required>Credential ID</TextField>
	</Col>
	<Col>
		<TextField bind:value={loginData.secret} type="password" required>Secret</TextField>
	</Col>
</form>
