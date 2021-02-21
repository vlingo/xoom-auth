import sirv from 'sirv';
import polka from 'polka';
import compression from 'compression';
import * as sapper from '@sapper/server';
const { json } = require('body-parser');
// // Uncomment this if you want to connect with java backend
// const { createProxyMiddleware } = require('http-proxy-middleware');
import routes from './serverRoutes';

const { PORT, NODE_ENV } = process.env;
const dev = NODE_ENV === 'development';

const server = polka(); // You can also use Express

// For front-end simulation
routes.forEach((route) => server[route.method.toLowerCase()](route.url, route.handler));

server
	// Uncomment this if you want to connect with java backend
	// .use('/api', createProxyMiddleware({ target: 'http://localhost:9019', changeOrigin: true }))
	.use(json())
	.use(compression({ threshold: 0 }), sirv('static', { dev }), sapper.middleware())
	.listen(PORT, (err) => err && console.log('error', err));

module.exports = server;
