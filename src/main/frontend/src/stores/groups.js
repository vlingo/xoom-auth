import { writable } from 'svelte/store';

export const groups = writable([]);

export function create(group) {
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

export function update(index, group) {
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

export function remove(index) {
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
