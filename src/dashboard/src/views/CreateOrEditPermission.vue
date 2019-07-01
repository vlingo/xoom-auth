<template>
    <v-card fluid id="create-or-edit-permission">
        <v-card-title class="headline teal darken-4 white--text" primary-title>{{title}}</v-card-title>

        <v-form v-model="valid">
                <v-card-text>
                    <name-and-description-input :mode="mode" :value="permission"></name-and-description-input>

                    <div v-if="mode !== 'edit'">
                        <v-divider></v-divider>

                        <v-subheader>Constraint</v-subheader>
                        <edit-permission-constraints-details :constraint="constraint"></edit-permission-constraints-details>

                    </div>
                </v-card-text>
            <v-divider></v-divider>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn flat @click="cancel()">Cancel</v-btn>
                <v-btn color="accent" flat :disabled="!valid" @click="save()" id="savePermissionButton">Save</v-btn>
            </v-card-actions>
        </v-form>
    </v-card>
</template>

<script>
    import NameAndDescriptionInput from "../components/NameDescriptionInput"
    import EditPermissionConstraintsDetails from "../components/EditConstraintsDetails"
    import {ADD_PERMISSION, UPDATE_PERMISSION} from "../store/permission.module"

    export default {
        components: {
            NameAndDescriptionInput,
            EditPermissionConstraintsDetails
        },
        props: {
            permission: {
                type: Object,
                default: () => ({})
            },
            mode: {
                type: String
            }
        },
        data: () => ({
            valid: false,
            constraint: {}
        }),
        computed: {
            title: function() {
                if (this.mode === "edit") {
                    return "Edit Permission"
                } else {
                    return "Create Permission"
                }
            }
        },
        methods: {
            save() {

                if (this.mode === "edit") {
                     this.$store.dispatch(UPDATE_PERMISSION, this.permission)
                } else {
                    this.permission.constraints = [this.constraint]                    
                    this.$store.dispatch(ADD_PERMISSION, this.permission)
                }

                this.$emit("saved", this.permission)
            },
            cancel() {
                this.$emit("canceled")
            }
        }
    }
</script>
