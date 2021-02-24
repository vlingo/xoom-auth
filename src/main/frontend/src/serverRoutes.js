import controllers from './controllers';

export default [
	{
		method: 'get',
		url: '/api/tenants',
		handler: controllers.Tenants.get,
	},
	{
		method: 'post',
		url: '/api/tenants',
		handler: controllers.Tenants.create,
	},
	{
		method: 'PATCH',
		url: '/api/tenants/:id',
		handler: controllers.Tenants.update,
	},
	{
		method: 'delete',
		url: '/api/tenants/:id',
		handler: controllers.Tenants.remove,
	},

	/* ------------------------------ USERS ROUTES ------------------------------ */

	{
		method: 'get',
		url: '/api/users',
		handler: controllers.Users.get,
	},
	{
		method: 'post',
		url: '/api/users',
		handler: controllers.Users.create,
	},
	{
		method: 'PATCH',
		url: '/api/users/:id',
		handler: controllers.Users.update,
	},
	{
		method: 'delete',
		url: '/api/users/:id',
		handler: controllers.Users.remove,
	},
];
