import controllers from './controllers';

export default [
	/* ----------------------- TENANT SUBSCRIPTION ROUTES ----------------------- */
	{
		method: 'get',
		url: '/api/tenants',
		handler: controllers.TenantSubscription.get,
	},
	{
		method: 'post',
		url: '/api/tenants',
		handler: controllers.TenantSubscription.create,
	},
	{
		method: 'PATCH',
		url: '/api/tenants/:id',
		handler: controllers.TenantSubscription.update,
	},
	{
		method: 'delete',
		url: '/api/tenants/:id',
		handler: controllers.TenantSubscription.remove,
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
