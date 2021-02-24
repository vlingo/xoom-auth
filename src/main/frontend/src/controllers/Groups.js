let data = [
	{
		name: 'Group1',
		description: 'Description of Group1',
	},
	{
		name: 'Group2',
		description: 'Description of Group2',
	},
	{
		name: 'Group3',
		description: 'Description of Group3',
	},
	{
		name: 'Group4',
		description: 'Description of Group4',
	},
	{
		name: 'Group5',
		description: 'Description of Group5',
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
