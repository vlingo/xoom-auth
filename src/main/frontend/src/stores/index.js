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

/* ---------------------------------- MISC ---------------------------------- */
export const theme = createLocalStore('theme', 'light');
export const title = writable('');

/* ----------------------------- AUTHENTICATION ----------------------------- */
export const isLoggedIn = writable(true);

export function logoout() {
	isLoggedIn.set(false);
	location.assign('/');
}

export function login() {
	isLoggedIn.set(true);
	location.assign('/admin');
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

export function addUser(user) {
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

export function updateUser(index, user) {
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

export function removeUser(index) {
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

/* --------------------------------- GROUPS --------------------------------- */
export const groups = writable([]);

export function addGroup(group) {
	return fetch('/api/tenants/groups', {
		method: 'post',
		body: JSON.stringify(group),
	}).then((response) => {
		if (response.status === 200) {
			groups.update((existingGroups) => [...existingGroups, group]);
		}
		return response;
	});
}

export function updateGroup(index, group) {
	return fetch(`/api/tenants/groups/${index}`, {
		headers: { 'Content-Type': 'application/json' },
		method: 'PATCH',
		body: JSON.stringify(group),
	}).then((response) => {
		if (response.status === 200) {
			groups.update((existingGroups) => {
				existingGroups[index] = group;
				return existingGroups;
			});
		}
		return response;
	});
}

export function removeGroup(index) {
	return fetch(`/api/tenants/groups/${index}`, {
		method: 'delete',
	}).then((response) => {
		groups.update((existingGroups) => {
			existingGroups.splice(index, 1);
			return existingGroups;
		});
		return response;
	});
}

/* ---------------------------------- ROLES --------------------------------- */
export const roles = writable([]);

export function addRole(role) {
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

export function updateRole(index, role) {
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

export function removeRole(index) {
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
