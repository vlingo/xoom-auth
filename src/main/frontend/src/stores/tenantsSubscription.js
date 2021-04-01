import { writable } from 'svelte/store';

export const tenants = writable([]);

export function create(tenant) {
	return fetch('/api/tenants', {
		method: 'post',
		body: JSON.stringify(tenant),
	}).then((response) => {
		if (response.status === 200) {
			tenants.update((existingTenants) => [...existingTenants, tenant]);
		}
		return response;
	});
}

export function update(index, tenant) {
	return fetch(`/api/tenants/${index}`, {
		headers: { 'Content-Type': 'application/json' },
		method: 'PATCH',
		body: JSON.stringify(tenant),
	}).then((response) => {
		if (response.status === 200) {
			tenants.update((existingTenants) => {
				existingTenants[index] = tenant;
				return existingTenants;
			});
		}
		return response;
	});
}

export function remove(index) {
	return fetch(`/api/tenants/${index}`, {
		method: 'delete',
	}).then((response) => {
		tenants.update((existingTenants) => {
			existingTenants.splice(index, 1);
			return existingTenants;
		});
		return response;
	});
}
