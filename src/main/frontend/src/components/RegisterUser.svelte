<script>
	import { Col, Divider, Row, Select, TextField } from 'svelte-materialify/src';

	const authorities = [
		{ name: 'Vlingo', value: 'vlingo' },
		{ name: 'oAuth', value: 'oauth' },
	];

	const data = {
		username: '',
		email: '',
		givenName: '',
		secondName: '',
		familyName: '',
		phone: '',
		authority: authorities[0].value,
		id: '',
		secret: '',
	};

	function register() {
		//
	}
</script>

<form class="s-col" id="register-and-login-form" on:submit|preventDefault={register}>
	<Row class="flex-column lg:flex-row">
		<Col>
			<TextField bind:value={data.username} autofocus required>Username</TextField>
		</Col>
		<Col>
			<TextField bind:value={data.email} required>Email Address</TextField>
		</Col>
	</Row>
	<Row class="flex-column lg:flex-row">
		<Col>
			<TextField bind:value={data.givenName} required>Given Name</TextField>
		</Col>
		<Col>
			<TextField bind:value={data.secondName} required>Second Name</TextField>
		</Col>
	</Row>
	<Row class="flex-column lg:flex-row">
		<Col>
			<TextField bind:value={data.familyName} required>Family Name</TextField>
		</Col>
		<Col>
			<TextField bind:value={data.phone} required>Phone</TextField>
		</Col>
	</Row>
	<Divider class="mt-5 mb-5" />
	<Row class="flex-column lg:flex-row">
		<Col class="md:max-width-initial" style="max-width:150px">
			<Select bind:value={data.authority} items={authorities} required>Authority</Select>
		</Col>
		<Col>
			<TextField bind:value={data.id} required>ID</TextField>
		</Col>
	</Row>
	<Row>
		<Col>
			<TextField bind:value={data.secret} required>Secret</TextField>
		</Col>
	</Row>
</form>
