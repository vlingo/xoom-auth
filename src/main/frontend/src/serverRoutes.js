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
		url: '/api/tenants/users',
		handler: controllers.Users.get,
	},
	{
		method: 'post',
		url: '/api/tenants/users',
		handler: controllers.Users.create,
	},
	{
		method: 'PATCH',
		url: '/api/tenants/users/:id',
		handler: controllers.Users.update,
	},
	{
		method: 'delete',
		url: '/api/tenants/users/:id',
		handler: controllers.Users.remove,
	},

	/* ------------------------------ GROUPS ROUTES ----------------------------- */
	{
		method: 'get',
		url: '/api/tenants/groups',
		handler: controllers.Groups.get,
	},
	{
		method: 'post',
		url: '/api/tenants/groups',
		handler: controllers.Groups.create,
	},
	{
		method: 'PATCH',
		url: '/api/tenants/groups/:id',
		handler: controllers.Groups.update,
	},
	{
		method: 'delete',
		url: '/api/tenants/groups/:id',
		handler: controllers.Groups.remove,
	},

	/* ------------------------------ ROLES ROUTES ------------------------------ */
	{
		method: 'get',
		url: '/api/tenants/roles',
		handler: controllers.Roles.get,
	},
	{
		method: 'post',
		url: '/api/tenants/roles',
		handler: controllers.Roles.create,
	},
	{
		method: 'PATCH',
		url: '/api/tenants/roles/:id',
		handler: controllers.Roles.update,
	},
	{
		method: 'delete',
		url: '/api/tenants/roles/:id',
		handler: controllers.Roles.remove,
	},

	{
		method: 'get',
		url: '/api/tenants/groups',
		handler: controllers.Groups.get,
	},
	{
		method: 'post',
		url: '/api/tenants/groups',
		handler: controllers.Groups.create,
	},
	{
		method: 'PATCH',
		url: '/api/tenants/groups/:id',
		handler: controllers.Groups.update,
	},
	{
		method: 'delete',
		url: '/api/tenants/groups/:id',
		handler: controllers.Groups.remove,
	},

	/* --------------------------- PERMISSIONS ROUTES --------------------------- */
	{
		method: 'get',
		url: '/api/tenants/permissions',
		handler: controllers.Permissions.get,
	},
	{
		method: 'get',
		url: '/api/tenants/permissions/names',
		handler: controllers.Permissions.getNames,
	},
	{
		method: 'post',
		url: '/api/tenants/permissions',
		handler: controllers.Permissions.create,
	},
	{
		method: 'PATCH',
		url: '/api/tenants/permissions/:id',
		handler: controllers.Permissions.update,
	},
	{
		method: 'delete',
		url: '/api/tenants/permissions/:id',
		handler: controllers.Permissions.remove,
	},
];
