<script>
	import { Button, ButtonGroup, ButtonGroupItem, Icon, TextField } from 'svelte-materialify/src';
	import { mdiMinus, mdiPlus } from '@mdi/js';
	import { tick } from 'svelte';

	export let constraints = [];

	let initialConstraint = {
		name: '',
		type: '',
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
		focusOnLastOption();
	}

	function removeSelectedConstraint() {
		if (selectElement.selectedIndex !== -1) {
			const index = selectElement.selectedIndex;
			const isLastIndex = index === constraints.length - 1;

			let copiedConstraints = [...constraints];
			copiedConstraints.splice(index, 1);
			constraints = [...copiedConstraints];

			if (isLastIndex) {
				focusOnLastOption();
			}
		}
	}

	async function focusOnLastOption() {
		await tick();
		selectElement.selectedIndex = selectedIndex = constraints.length - 1;
	}

	$: validSelectedIndex = !!constraints[selectedIndex];
	$: if (validSelectedIndex) {
		selectedConstraint = constraints[selectedIndex];
	} else {
		selectedConstraint = { ...initialConstraint };
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
</script>

<div class="d-flex flex-column">
	<label class="mt-1 mb-1" for="constraints">Constraints</label>
	<select
		use:handleSelectChangesAction
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
			<Button
				fab
				aria-label="Add constraint"
				on:click={createNewConstraint}
				rounded
				size="small"
				text
				title="Add constraint">
				<Icon path={mdiPlus} />
			</Button>
			<Button
				fab
				aria-label="Remove selected constraint"
				on:click={removeSelectedConstraint}
				rounded
				size="small"
				text
				title="Remove selected constraint">
				<Icon path={mdiMinus} />
			</Button>
		</div>
	</div>
	<!-- COSNTSRAINT FORM -->
	<div class="d-flex flex-column">
		<label class="mb-2" for="constraint-type">Type</label>
		<div class="d-flex">
			<ButtonGroup activeClass="primary-color" bind:value={selectedConstraint.type}>
				<ButtonGroupItem disabled={!validSelectedIndex} value="string">
					String
				</ButtonGroupItem>
				<ButtonGroupItem disabled={!validSelectedIndex} value="integer">
					Integer
				</ButtonGroupItem>
				<ButtonGroupItem disabled={!validSelectedIndex} value="double">
					Double
				</ButtonGroupItem>
				<ButtonGroupItem disabled={!validSelectedIndex} value="boolean">
					Boolean
				</ButtonGroupItem>
			</ButtonGroup>
		</div>
		<TextField class="mt-5" disabled={!validSelectedIndex} bind:value={selectedConstraint.name}>
			Name
		</TextField>
		<TextField
			class="mt-5"
			disabled={!validSelectedIndex}
			bind:value={selectedConstraint.value}>
			Value
		</TextField>
	</div>
</div>
