<template>
    <v-container id="name-description-input">
        <v-text-field
                :disabled="isInEditMode"
                v-model="value.name"
                label="Name"
                required
                id="nameField"
                :rules="[v => !!v || 'Please, enter a name.']"
        ></v-text-field>
        <v-textarea
                boxstyle
                id="descriptionField"
                v-model="value.description"
                label="Description">
        </v-textarea>
    </v-container>
</template>

<script>
    export default {
        props: {
            value: {
                type: Object,
                default: () => ({})
            },
            mode: {
                type: String
            }
        },
        data:() => ({
           isInEditMode: false
        }),
        watch: {
            value: function() {
                this.isInEditMode = false
                this.isInEditMode = this.mode === 'edit'
            }
        }
    }
</script>
