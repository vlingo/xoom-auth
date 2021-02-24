<script>
	import UserForm from './UserForm.svelte';

	let user = {
		username: '',
		email: '',
		givenName: '',
		secondName: '',
		familyName: '',
		phone: '',
		credential: {
			authority: 'vlingo',
			id: '',
			secret: '',
		},
	};

	function register() {
		//
	}
</script>

<form class="s-col" id="register-and-login-form" on:submit|preventDefault={register}>
	<UserForm bind:user />
</form>
