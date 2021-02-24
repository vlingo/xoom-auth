let data = [
	{
		name: 'Role1',
		description: 'Description of Role1',
	},
	{
		name: 'Role2',
		description: 'Description of Role2',
	},
	{
		name: 'Role3',
		description: 'Description of Role3',
	},
	{
		name: 'Role4',
		description: 'Description of Role4',
	},
	{
		name: 'Role5',
		description: 'Description of Role5',
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
