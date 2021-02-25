let data = [
	{
		name: 'Permission1',
		description: 'Description of Permission1',
		constraints: ['name1', 'name2', 'name3'],
	},
	{
		name: 'Permission2',
		description: 'Description of Permission2',
		constraints: ['name4', 'name5'],
	},
	{
		name: 'Permission3',
		description: 'Description of Permission3',
		constraints: ['name6', 'name7', 'name8'],
	},
	{
		name: 'Permission4',
		description: 'Description of Permission4',
		constraints: ['name9', 'name5'],
	},
	{
		name: 'Permission5',
		description: 'Description of Permission5',
		constraints: ['name10', 'name11'],
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
