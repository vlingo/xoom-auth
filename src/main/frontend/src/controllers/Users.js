let data = [
	{
		username: 'zoe.doe@tenant1.com',
		email: 'zoe.doe@tenant1.com',
		givenName: 'Zoe',
		secondName: '',
		familyName: 'Doe',
		phone: '480-555-1212',
		credential: {
			authority: 'vlingo',
			id: 'zoe.doe@tenant1.com',
			secret: 'some__secret',
		},
	},
	{
		username: 'john.cook@tenant1.com',
		email: 'john.cook@tenant1.com',
		givenName: 'John',
		secondName: '',
		familyName: 'Cook',
		phone: '480-555-1212',
		credential: {
			authority: 'vlingo',
			id: 'john.cook@tenant1.com',
			secret: 'some__secret',
		},
	},
	{
		username: 'sjones@tenant1.com',
		email: 'sjones@tenant1.com',
		givenName: 'Sussie',
		secondName: '',
		familyName: 'Jones',
		phone: '480-555-1212',
		credential: {
			authority: 'vlingo',
			id: 'sjones@tenant1.com',
			secret: 'some__secret',
		},
	},
	{
		username: 'gwens@tenant1.com',
		email: 'gwens@tenant1.com',
		givenName: 'Gwen',
		secondName: '',
		familyName: 'Smith',
		phone: '480-555-1212',
		credential: {
			authority: 'vlingo',
			id: 'gwens@tenant1.com',
			secret: 'some__secret',
		},
	},
	{
		username: 'henry.plank@tenant1.com',
		email: 'henry.plank@tenant1.com',
		givenName: 'Henry',
		secondName: '',
		familyName: 'Plank',
		phone: '480-555-1212',
		credential: {
			authority: 'vlingo',
			id: 'henry.plank@tenant1.com',
			secret: 'some__secret',
		},
	},
];

export async function get(req, res, next) {
	res.end(JSON.stringify(data));
}

export async function create(req, res, next) {
	res.end('OK');
}

export async function update(req, res, next) {
	res.end('OK');
}

export function remove(req, res, next) {
	res.end('OK');
}
