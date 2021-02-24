<script>
	import Title from '../../components/title.svelte';
	import { mdiDelete, mdiPencil, mdiPlus } from '@mdi/js';
	import {
		Button,
		Col,
		Dialog,
		Divider,
		Icon,
		Row,
		Table,
		TextField,
	} from 'svelte-materialify/src';
	import { groups, addGroup, updateGroup, removeGroup } from '../../stores/index.js';

	let initialGroup = {
		name: '',
		description: '',
	};

	let loading = {
		createOrUpdate: false,
		remove: false,
	};

	let dialogState = {
		createOrUpdate: false,
		remove: false,
	};

	let indexToUpdateOrDelete = 0;
	let updateMode = false;

	let group = { ...initialGroup };

	function _addGroup() {
		loading.createOrUpdate = true;
		addGroup(group)
			.then(({ status }) => status === 200 && reset())
			.finally(() => {
				loading.createOrUpdate = false;
				dialogState.createOrUpdate = false;
			});
	}

	function _updateGroup() {
		loading.createOrUpdate = true;
		updateGroup(indexToUpdateOrDelete, group)
			.then(({ status }) => status === 200 && reset())
			.finally(() => {
				loading.createOrUpdate = false;
				dialogState.createOrUpdate = false;
			});
	}

	function _removeGroup() {
		loading.remove = true;
		removeGroup(indexToUpdateOrDelete)
			.then(({ status }) => {
				if (status === 200) {
					dialogState.remove = false;
				}
			})
			.finally(() => {
				loading.remove = false;
			});
	}

	function openCreateDialog() {
		updateMode = false;
		dialogState.createOrUpdate = true;
	}

	function openUpdateDialog(index) {
		updateMode = true;
		indexToUpdateOrDelete = index;
		group = $groups[index];
		dialogState.createOrUpdate = true;
	}

	function openDeleteDialog(index) {
		indexToUpdateOrDelete = index;
		group = { ...$groups[index] };
		dialogState.remove = true;
	}

	function handleFormPost() {
		if (updateMode) {
			_updateGroup();
		} else {
			_addGroup();
		}
	}

	function reset() {
		group = { ...initialGroup };
	}

	$: if (dialogState.createOrUpdate == false && !dialogState.remove) {
		updateMode = false;
		reset();
	}
</script>

<Title title="Groups" />

<h6>Groups</h6>

<!-- DIALOG CREATE/UPDATE GROUP -->
<Dialog class="pa-4" bind:active={dialogState.createOrUpdate}>
	<form on:submit|preventDefault={handleFormPost} class="d-flex flex-column">
		<h6 class="mb-2">{updateMode ? 'Update' : 'Add'} Group</h6>
		<Divider class="mb-4" />
		<Row>
			<Col>
				<TextField bind:value={group.name} required>Name</TextField>
			</Col>
		</Row>
		<Row>
			<Col>
				<TextField bind:value={group.description} required>Description</TextField>
			</Col>
		</Row>
		<Divider />
		<div class="mt-3 d-flex">
			<Button class="ml-auto primary-color" disabled={loading.createOrUpdate} type="submit">
				{#if updateMode}
					{loading.createOrUpdate ? 'updating...' : 'update'}
				{:else}
					{loading.createOrUpdate ? 'Adding group...' : 'add'}
				{/if}
			</Button>
		</div>
	</form>
</Dialog>

<!-- DIALOG REMOVE GROUP -->
<Dialog class="pa-4" bind:active={dialogState.remove}>
	<h6 class="mb-2">Delete Group</h6>
	<Divider />
	<div class="mt-4 mb-4">
		Are you sure want to delete <b>{group.name}</b> from groups?
	</div>
	<Divider />
	<div class="mt-3 d-flex">
		<Button
			class="ml-auto red white-text"
			depressed
			disabled={loading.remove}
			on:click={_removeGroup}>
			{loading.remove ? 'deleting...' : 'delete'}
		</Button>
	</div>
</Dialog>

{#if $groups.length}
	<Table class="p-5 mt-5 s-card">
		<thead>
			<tr style="font-weight:bold">
				<td>Name</td>
				<td>Description</td>
				<td class="text-center">Actions</td>
			</tr>
		</thead>
		<tbody>
			{#each $groups as group, index}
				<tr>
					<td>{group.name}</td>
					<td>{group.description}</td>
					<td class="text-center table-row-actions">
						<Button
							fab
							depressed
							on:click={() => openUpdateDialog(index)}
							rounded
							size="x-small"
							text>
							<Icon path={mdiPencil} />
						</Button>
						<Button
							fab
							depressed
							on:click={() => openDeleteDialog(index)}
							rounded
							size="x-small"
							text>
							<Icon path={mdiDelete} />
						</Button>
					</td>
				</tr>
			{/each}
		</tbody>
	</Table>
{:else}
	<div style="padding-top: 2em">No data available</div>
{/if}

<Button
	class="primary-color"
	fab
	on:click={openCreateDialog}
	style="position: fixed; margin: 1em; right: 0; bottom: 0;"
	float>
	<Icon path={mdiPlus} />
</Button>
