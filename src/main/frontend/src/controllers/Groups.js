let data = [
	{
		name: 'Group1',
		description: 'Description of Group1',
		members: ['zoe.doe@tenant1.com', 'john.cook@tenant1.com'],
	},
	{
		name: 'Group2',
		description: 'Description of Group2',
		members: ['sjones@tenant1.com', 'gwens@tenant1.com', 'henry.plank@tenant1.com'],
	},
	{
		name: 'Group3',
		description: 'Description of Group3',
		members: [],
	},
	{
		name: 'Group4',
		description: 'Description of Group4',
		members: [],
	},
	{
		name: 'Group5',
		description: 'Description of Group5',
		members: [],
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
