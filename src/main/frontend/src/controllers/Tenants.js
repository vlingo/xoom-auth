let data = [
	{
		name: 'John Doe',
		description: 'Description of the tenant',
		active: false,
	},
	{
		name: 'Kevin Wu',
		description: 'Description of the tenant',
		active: true,
	},
	{
		name: 'Lisa Smit',
		description: 'Description of the tenant',
		active: false,
	},
	{
		name: 'David Willish',
		description: 'Description of the tenant',
		active: true,
	},
	{
		name: 'Samantha Bryan',
		description: 'Description of the tenant',
		active: false,
	},
];

export async function get(req, res, next) {
	res.end(JSON.stringify(data));
	next();
}

export async function create(req, res, next) {
	res.end('OK');
	next();
}

export function remove(req, res, next) {
	res.end('OK');
	next();
}
