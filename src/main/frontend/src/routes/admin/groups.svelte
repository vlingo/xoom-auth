<script context="module">
	export async function preload(/* page, session */) {
		const fetchGroups = await this.fetch('/api/tenants/groups');
		const groups = await fetchGroups.json();
		return { groups };
	}
</script>

<script>
	import Title from '../../components/title.svelte';
	import { mdiDelete, mdiPencil, mdiPlus } from '@mdi/js';
	import { Button, Col, Icon, Row, Table, TextField } from 'svelte-materialify/src';
	import { groups as groupsStore, create, update, remove } from '../../stores/groups.js';
	import DeleteDialog from '../../components/DeleteDialog.svelte';
	import CreateUpdateDialog from '../../components/CreateUpdateDialog.svelte';

	export let groups;
	$groupsStore = groups;

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

	function _create() {
		loading.createOrUpdate = true;
		create(group)
			.then(({ status }) => status === 200 && reset())
			.finally(() => {
				loading.createOrUpdate = false;
				dialogState.createOrUpdate = false;
			});
	}

	function _update() {
		loading.createOrUpdate = true;
		update(indexToUpdateOrDelete, group)
			.then(({ status }) => status === 200 && reset())
			.finally(() => {
				loading.createOrUpdate = false;
				dialogState.createOrUpdate = false;
			});
	}

	function _remove() {
		loading.remove = true;
		remove(indexToUpdateOrDelete)
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
		group = $groupsStore[index];
		dialogState.createOrUpdate = true;
	}

	function openDeleteDialog(index) {
		indexToUpdateOrDelete = index;
		group = { ...$groupsStore[index] };
		dialogState.remove = true;
	}

	function handleFormPost() {
		if (updateMode) {
			_update();
		} else {
			_create();
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
<CreateUpdateDialog
	on:form-submit={handleFormPost}
	bind:active={dialogState.createOrUpdate}
	loading={loading.createOrUpdate}
	submitButtonCaption={updateMode ? 'Update' : 'Add Group'}
	submitButtonCaptionOnLoading={updateMode ? 'Updating...' : 'Adding Group...'}
	title="{updateMode ? 'Update' : 'Add'} Group">
	<Row class="mt-3">
		<Col>
			<TextField bind:value={group.name} required>Name</TextField>
		</Col>
	</Row>
	<Row>
		<Col>
			<TextField bind:value={group.description} required>Description</TextField>
		</Col>
	</Row>
</CreateUpdateDialog>

<!-- DIALOG REMOVE GROUP -->
<DeleteDialog
	bind:active={dialogState.remove}
	loading={loading.remove}
	title="Delete Group"
	on:remove-button-clicked={_remove}>
	Are you sure want to delete <b>{group.name}</b> from groups?
</DeleteDialog>

{#if $groupsStore.length}
	<Table class="p-5 mt-5 s-card">
		<thead>
			<tr style="font-weight:bold">
				<td>Name</td>
				<td>Description</td>
				<td class="text-center">Actions</td>
			</tr>
		</thead>
		<tbody>
			{#each $groupsStore as group, index}
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
