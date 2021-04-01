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
