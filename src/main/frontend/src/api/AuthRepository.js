import Repository from './Repository'

const resources = Object.freeze({
	tenants: () => '/tenants'
})

async function ensure(response, status) {
	if (response.status !== status) {
		const contentType = response.headers.get("content-type");
		let message;
		if (contentType === "application/json") {
			message = JSON.stringify(await response.json());
		} else {
			message = await response.text();
		}
		throw Error(`HTTP ${response.status}: ${response.statusText} (${message}).`); //${response.config.url}
	}
	return response;
}

function ensureOk(response) {
	return ensure(response, 200);
}

function ensureCreated(response) {
	return ensure(response, 201);
}

let fetch;

function setFetchFunction(fetchFunc) {
	fetch = fetchFunc;
}

async function repoGet(path) {
	return Repository.get(path, fetch)
		.then(ensureOk)
		.then(res => res.json());
}

async function repoPost(path, body) {
	return Repository.post(path, body)
		.then(ensureCreated)
		.then(res => res.json());
}

async function repoPut(path, body) {
	return Repository.put(path, body)
		.then(ensureOk)
		.then(res => res.json());
}


export default {
	setFetchFunction,
	getAllTenants() {
		return repoGet(resources.tenants())
	}
	// createOrganization(name, description) {
	// 	return repoPost(resources.tenants(), {
	// 		organizationId: '',
	// 		name: name,
	// 		description: description
	// 	});
	// },
	// updateOrganization(id, name, description) {
	// 	return repoPut(resources.organization(id), {
	// 		organizationId: id,
	// 		name: name,
	// 		description: description
	// 	});
	// },
}
