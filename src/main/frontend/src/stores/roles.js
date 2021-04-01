import { writable } from 'svelte/store';

export const roles = writable([]);

export function create(role) {
	return fetch('/api/tenants/roles', {
		method: 'post',
		body: JSON.stringify(role),
	}).then((response) => {
		if (response.status === 200) {
			roles.update((existingRoles) => [...existingRoles, role]);
		}
		return response;
	});
}

export function update(index, role) {
	return fetch(`/api/tenants/roles/${index}`, {
		headers: { 'Content-Type': 'application/json' },
		method: 'PATCH',
		body: JSON.stringify(role),
	}).then((response) => {
		if (response.status === 200) {
			roles.update((existingRoles) => {
				existingRoles[index] = role;
				return existingRoles;
			});
		}
		return response;
	});
}

export function remove(index) {
	return fetch(`/api/tenants/roles/${index}`, {
		method: 'delete',
	}).then((response) => {
		roles.update((existingRoles) => {
			existingRoles.splice(index, 1);
			return existingRoles;
		});
		return response;
	});
}
