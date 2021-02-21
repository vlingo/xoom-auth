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
];
