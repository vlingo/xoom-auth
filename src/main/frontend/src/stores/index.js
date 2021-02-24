import { writable } from 'svelte/store';

export function createLocalStore(key, initialValue) {
	const localValue =
		process.browser && !!localStorage.getItem(key) ? localStorage.getItem(key) : initialValue;
	const { subscribe, set } = writable(localValue);

	return {
		subscribe,
		set: (value) => {
			if (process.browser) {
				localStorage.setItem(key, value);
			}
			set(value);
		},
	};
}

export const isLoggedIn = writable(true);
export const theme = createLocalStore('theme', 'light');

export function logoout() {
	isLoggedIn.set(false);
}

/* --------------------------- TENANTS SUBSRIPTION -------------------------- */
export const tenants = writable([]);

export function addTenant(tenant) {
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

export function updateTenant(index, tenant) {
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

export function removeTenant(index) {
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

/* ---------------------------------- USERS --------------------------------- */
export const users = writable([]);

export function addUser(tenant) {
	return fetch('/api/users', {
		method: 'post',
		body: JSON.stringify(tenant),
	}).then((response) => {
		if (response.status === 200) {
			users.update((existingUsers) => [...existingUsers, tenant]);
		}
		return response;
	});
}

export function updateUser(index, tenant) {
	return fetch(`/api/users/${index}`, {
		headers: { 'Content-Type': 'application/json' },
		method: 'PATCH',
		body: JSON.stringify(tenant),
	}).then((response) => {
		if (response.status === 200) {
			users.update((existingUsers) => {
				existingUsers[index] = tenant;
				return existingUsers;
			});
		}
		return response;
	});
}

export function removeUser(index) {
	return fetch(`/api/users/${index}`, {
		method: 'delete',
	}).then((response) => {
		users.update((existingUsers) => {
			existingUsers.splice(index, 1);
			return existingUsers;
		});
		return response;
	});
}
