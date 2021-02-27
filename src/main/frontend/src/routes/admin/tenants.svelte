<script>
	import Title from '../../components/title.svelte';
	import { mdiDelete, mdiPencil, mdiPlus } from '@mdi/js';
	import {
		Button,
		Checkbox,
		Col,
		Icon,
		Table,
		Row,
		TextField,
		Textarea,
	} from 'svelte-materialify/src';
	import DeleteDialog from '../../components/DeleteDialog.svelte';
	import { tenants, create, update, remove } from '../../stores/tenantsSubscription.js';
	import CommonDialog from '../../components/CommonDialog.svelte';
	import { dialogState, loading } from '../../shared/common.js';
	import SmallButton from '../../components/SmallButton.svelte';

	let initialTenant = {
		name: '',
		description: '',
		active: false,
	};

	let indexToUpdateOrDelete = 0;
	let updateMode = false;

	let tenant = { ...initialTenant };

	function _create() {
		loading.createOrUpdate = true;
		create(tenant)
			.then(({ status }) => status === 200 && reset())
			.finally(() => {
				loading.createOrUpdate = false;
				dialogState.createOrUpdate = false;
			});
	}

	function _update() {
		loading.createOrUpdate = true;
		update(indexToUpdateOrDelete, tenant)
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
		tenant = $tenants[index];
		dialogState.createOrUpdate = true;
	}

	function openDeleteDialog(index) {
		indexToUpdateOrDelete = index;
		tenant = { ...$tenants[index] };
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
		tenant = { ...initialTenant };
	}

	$: if (dialogState.createOrUpdate == false && !dialogState.remove) {
		updateMode = false;
		reset();
	}
</script>

<Title title="Tenants Subscribtion" />

<h6>Tenants Subscribtion</h6>

<!-- DIALOG CREATE/UPDATE TENANT SUBSCRIBTION -->
<CommonDialog
	on:form-submit={handleFormPost}
	bind:active={dialogState.createOrUpdate}
	loading={loading.createOrUpdate}
	submitButtonCaption={updateMode ? 'Update' : 'Subscribe'}
	submitButtonCaptionOnLoading={updateMode ? 'Updating...' : 'Subscribing...'}
	title="{updateMode ? 'Update' : ''} Tenant Subscribtion">
	<Row class="mt-3">
		<Col>
			<TextField autofocus bind:value={tenant.name} required>Name</TextField>
		</Col>
	</Row>
	<Row>
		<Col>
			<Textarea bind:value={tenant.description} required>Description</Textarea>
		</Col>
	</Row>
	<Row class="mb-2">
		<Col>
			<Checkbox bind:checked={tenant.active}>Active</Checkbox>
		</Col>
	</Row>
</CommonDialog>

<!-- DIALOG REMOVE TENANT SUBSCRIBTION -->
<DeleteDialog
	bind:active={dialogState.remove}
	loading={loading.remove}
	title="Delete Tenant Subscribtion"
	on:remove-button-clicked={_remove}>
	Are you sure want to delete <b>{tenant.name}</b> from tenants?
</DeleteDialog>

{#if $tenants.length}
	<Table class="p-5 mt-5 s-card">
		<thead>
			<tr style="font-weight:bold">
				<td>Name</td>
				<td>Description</td>
				<td class="text-center">Active</td>
				<td class="text-center">Actions</td>
			</tr>
		</thead>
		<tbody>
			{#each $tenants as tenant, index}
				<tr>
					<td>{tenant.name}</td>
					<td>{tenant.description}</td>
					<td class="justify-center d-flex">
						<!-- TODO: Add the following props to the checkbox
									when svelte-materialify allow title and aria-label prop -->
						<!-- aria-label="Toggle active" -->
						<!-- title="Toggle active" -->
						<Checkbox class="m-0" bind:checked={tenant.active} />
					</td>
					<td class="text-center table-row-actions">
						<SmallButton
							on:click={() => openUpdateDialog(index)}
							iconPath={mdiPencil}
							title="Update/edit tenant" />
						<SmallButton
							on:click={() => openDeleteDialog(index)}
							iconPath={mdiDelete}
							title="Remove/delete tenant" />
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
