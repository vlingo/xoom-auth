import { writable } from 'svelte/store';

export function createLocalStore(key, initialValue) {
	const localValue = process.browser ? localStorage.getItem(key) : initialValue;
	const { subscribe, set } = writable(localValue);

	return {
		subscribe,
		set: (value) => {
			if (process.browser) {
				localStorage.setItem(key, value);
			}
			set(value)
		},
	};
}


export const isLoggedIn = writable(true)
export const theme = createLocalStore('theme', 'light')
