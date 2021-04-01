let data = [
	{
		name: 'Group1',
		description: 'Description of Group1',
		members: {
			groups: ['Group2', 'Group3', 'Group4'],
			users: ['zoe.doe@tenant1.com', 'john.cook@tenant1.com'],
		},
	},
	{
		name: 'Group2',
		description: 'Description of Group2',
		members: {
			groups: ['Group4', 'Group5', 'Group6'],
			users: ['sjones@tenant1.com', 'gwens@tenant1.com', 'henry.plank@tenant1.com'],
		},
	},
	{
		name: 'Group3',
		description: 'Description of Group3',
		members: {
			groups: [],
			users: [],
		},
	},
	{
		name: 'Group4',
		description: 'Description of Group4',
		members: {
			groups: [],
			users: [],
		},
	},
	{
		name: 'Group5',
		description: 'Description of Group5',
		members: {
			groups: [],
			users: [],
		},
	},
	{
		name: 'Group6',
		description: 'Description of Group6',
		members: {
			groups: [],
			users: [],
		},
	},
	{
		name: 'Group7',
		description: 'Description of Group7',
		members: {
			groups: [],
			users: [],
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
