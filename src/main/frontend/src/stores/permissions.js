import { writable } from 'svelte/store';

export const permissions = writable([]);

export function create(permission) {
	return fetch('/api/tenants/permissions', {
		method: 'post',
		body: JSON.stringify(permission),
	}).then((response) => {
		if (response.status === 200) {
			permissions.update((existingPermissions) => [...existingPermissions, permission]);
		}
		return response;
	});
}

export function update(index, permission) {
	return fetch(`/api/tenants/permissions/${index}`, {
		headers: { 'Content-Type': 'application/json' },
		method: 'PATCH',
		body: JSON.stringify(permission),
	}).then((response) => {
		if (response.status === 200) {
			permissions.update((existingPermissions) => {
				existingPermissions[index] = permission;
				return existingPermissions;
			});
		}
		return response;
	});
}

export function remove(index) {
	return fetch(`/api/tenants/permissions/${index}`, {
		method: 'delete',
	}).then((response) => {
		permissions.update((existingPermissions) => {
			existingPermissions.splice(index, 1);
			return existingPermissions;
		});
		return response;
	});
}
