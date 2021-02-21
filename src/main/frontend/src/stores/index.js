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
	tenants.update((existingTenants) => [...existingTenants, tenant]);
	return fetch('/api/tenants', {
		method: 'post',
		body: JSON.stringify(tenant),
	});
}

export function removeTenant(index) {
	tenants.update((existingTenants) => {
		existingTenants.splice(index, 1);
		return existingTenants;
	});
}
