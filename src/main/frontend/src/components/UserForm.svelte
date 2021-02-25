<script>
	import { Checkbox, Col, Divider, Row, Select, TextField } from 'svelte-materialify/src';

	const authorities = [
		{ name: 'Vlingo', value: 'vlingo' },
		{ name: 'oAuth', value: 'oauth' },
	];

	export let user = {
		username: '',
		email: '',
		givenName: '',
		secondName: '',
		familyName: '',
		phone: '',
		credential: {
			authority: authorities[0].value,
			id: '',
			secret: '',
		},
	};

	let usernameSameAsEmailAddress = false;

	function handleCheckbox() {
		if (!!user.username) {
			user.email = user.username;
		} else if (!!user.email) {
			user.username = user.email;
		}
	}
</script>

<Row class="flex-column lg:flex-row">
	<Col>
		<TextField bind:value={user.username} autofocus required>Username</TextField>
	</Col>
	<Col>
		<TextField bind:value={user.email} required>Email Address</TextField>
	</Col>
</Row>
<Row>
	<Col>
		<Checkbox on:change={handleCheckbox} bind:usernameSameAsEmailAddress>
			Username same as email address
		</Checkbox>
	</Col>
</Row>
<Row class="flex-column lg:flex-row">
	<Col>
		<TextField bind:value={user.givenName} required>Given Name</TextField>
	</Col>
	<Col>
		<TextField bind:value={user.secondName}>Second Name</TextField>
	</Col>
</Row>
<Row class="flex-column lg:flex-row">
	<Col>
		<TextField bind:value={user.familyName} required>Family Name</TextField>
	</Col>
	<Col>
		<TextField bind:value={user.phone} required>Phone</TextField>
	</Col>
</Row>
<Divider class="mt-5 mb-3" />
<h6>Credential</h6>
<Row class="mt-5 flex-column lg:flex-row">
	<Col class="md:max-width-initial" style="max-width:150px">
		<Select bind:value={user.credential.authority} items={authorities} required>
			Authority
		</Select>
	</Col>
	<Col>
		<TextField bind:value={user.credential.id} required>ID</TextField>
	</Col>
</Row>
<Row>
	<Col>
		<TextField bind:value={user.credential.secret} type="password" required>Secret</TextField>
	</Col>
</Row>
