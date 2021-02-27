let data = [
	{
		name: 'Permission1',
		description: 'Description of Permission1',
		constraints: [
			{
				name: 'name1',
				value: 'name1',
				type: 'string',
			},
			{
				name: 'name2',
				value: 'name2',
				type: 'string',
			},
			{
				name: 'name3',
				value: 'name3',
				type: 'string',
			},
		],
	},
	{
		name: 'Permission2',
		description: 'Description of Permission2',
		constraints: [
			{
				name: 'name4',
				value: 'name4',
				type: 'string',
			},
			{
				name: 'name5',
				value: 'name5',
				type: 'string',
			},
		],
	},
	{
		name: 'Permission3',
		description: 'Description of Permission3',
		constraints: [
			{
				name: 'name6',
				value: 'name6',
				type: 'string',
			},
			{
				name: 'name7',
				value: 'name7',
				type: 'string',
			},
			{
				name: 'name8',
				value: 'name8',
				type: 'string',
			},
		],
	},
	{
		name: 'Permission4',
		description: 'Description of Permission4',
		constraints: [
			{
				name: 'name9',
				value: 'name9',
				type: 'string',
			},
			{
				name: 'name5',
				value: 'name5',
				type: 'string',
			},
		],
	},
	{
		name: 'Permission5',
		description: 'Description of Permission5',
		constraints: [
			{
				name: 'name10',
				value: 'name10',
				type: 'string',
			},
			{
				name: 'name11',
				value: 'name11',
				type: 'string',
			},
		],
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
