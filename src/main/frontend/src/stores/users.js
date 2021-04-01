import { writable } from 'svelte/store';

export const users = writable([]);

export function fetchUsers() {
	process.browser &&
		fetch('/api/tenants/users').then(async (response) => {
			const data = await response.json();
			users.set(data);
		});
}

export function create(user) {
	return fetch('/api/tenants/users', {
		method: 'post',
		body: JSON.stringify(user),
	}).then((response) => {
		if (response.status === 200) {
			users.update((existingUsers) => [...existingUsers, user]);
		}
		return response;
	});
}

export function update(index, user) {
	return fetch(`/api/tenants/users/${index}`, {
		headers: { 'Content-Type': 'application/json' },
		method: 'PATCH',
		body: JSON.stringify(user),
	}).then((response) => {
		if (response.status === 200) {
			users.update((existingUsers) => {
				existingUsers[index] = user;
				return existingUsers;
			});
		}
		return response;
	});
}

export function remove(index) {
	return fetch(`/api/tenants/users/${index}`, {
		method: 'delete',
	}).then((response) => {
		users.update((existingUsers) => {
			existingUsers.splice(index, 1);
			return existingUsers;
		});
		return response;
	});
}
