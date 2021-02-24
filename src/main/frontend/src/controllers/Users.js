let data = [
	{
		username: 'zoe-doe@tenant1.com',
		email: 'zoe-doe@tenant1.com',
		givenName: 'zoe-doe',
		secondName: 'angella',
		familyName: 'doe',
		phone: '480-555-1212',
		credential: {
			authority: 'vlingo',
			id: 'zoe-doe@tenant1.com',
			secret: 'some__secret',
		},
	},
	{
		username: 'john-doe@tenant1.com',
		email: 'john-doe@tenant1.com',
		givenName: 'john',
		secondName: 'william',
		familyName: 'doe',
		phone: '480-555-1212',
		credential: {
			authority: 'vlingo',
			id: 'john-doe@tenant1.com',
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
