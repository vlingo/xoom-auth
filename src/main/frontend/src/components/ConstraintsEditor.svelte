<script>
	import { Button, Icon, Radio, TextField } from 'svelte-materialify/src';
	import { mdiMinus, mdiPlus } from '@mdi/js';
	import { tick } from 'svelte';

	export let constraints = [];

	let initialConstraint = {
		name: '',
		type: 'string',
		value: '',
	};

	let selectedConstraint = { ...initialConstraint };

	/**   @type {HTMLSelectElement} */
	let selectElement;
	let selectedIndex = -1;

	async function createNewConstraint() {
		constraints = [
			...constraints,
			{
				name: 'New constraint',
				type: 'string',
				value: 'constraint',
			},
		];
		await tick();
		selectElement.querySelector('option:last-child').focus({ force: true });
	}

	function removeSelectedConstraint() {
		if (selectElement.selectedIndex !== -1) {
			let copiedConstraints = [...constraints];
			copiedConstraints.splice(selectElement.selectedIndex, 1);
			constraints = [...copiedConstraints];
		}
	}

	$: if (selectedIndex != -1) {
		selectedConstraint = constraints[selectedIndex];
	}

	/** @param {Node} node */
	function handleSelectChangesAction(node) {
		const handler = () => (selectedIndex = node.selectedIndex);
		node.addEventListener('change', handler);
		return {
			onDestroy() {
				node.removeEventListener('change', handler);
			},
		};
	}

	/** @param {Node} node */
	function handleBlurAction(node) {
		const handler = () => (selectedIndex = -1);
		node.addEventListener('blur', handler);
		return {
			onDestroy() {
				node.removeEventListener('change', handler);
			},
		};
	}
</script>

<div class="d-flex flex-column">
	<label class="mt-1 mb-1" for="constraints">Constraints</label>
	<select
		use:handleSelectChangesAction
		use:handleBlurAction
		bind:this={selectElement}
		class="w-full"
		id="constraints"
		size="5">
		{#each constraints as constraint}
			<option class="pa-2" value={constraint.value}>
				{constraint.name}
			</option>
		{/each}
	</select>
	<div class="mt-2 d-flex flex-column">
		<div class="ml-auto">
			<Button fab on:click={createNewConstraint} rounded size="small" text>
				<Icon path={mdiPlus} />
			</Button>
			<Button fab on:click={removeSelectedConstraint} rounded size="small" text>
				<Icon path={mdiMinus} />
			</Button>
		</div>
	</div>
	<!-- COSNTSRAINT FORM -->
	<div class="d-flex flex-column">
		<label for="constraint-type mt-2 mb-3"> Type </label>
		<div class="d-flex">
			<Radio bind:group={selectedConstraint.type} value="string">String</Radio>
			<Radio bind:group={selectedConstraint.type} class="ml-3" value="integer">Integer</Radio>
			<Radio bind:group={selectedConstraint.type} class="ml-3" value="double">Double</Radio>
			<Radio bind:group={selectedConstraint.type} class="ml-3" value="boolean">Boolean</Radio>
		</div>
		<TextField class="mt-5" bind:value={selectedConstraint.name}>Name</TextField>
		<TextField class="mt-5" bind:value={selectedConstraint.value}>Value</TextField>
	</div>
</div>
