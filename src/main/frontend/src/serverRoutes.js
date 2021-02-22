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
];
