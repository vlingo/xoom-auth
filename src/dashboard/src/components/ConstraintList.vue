<template>
    <v-card id="constraint-list">
        <v-toolbar dense card class="secondary">
            Constraints

            <v-spacer></v-spacer>

            <v-btn icon>
                <v-icon color="primary" @click="createConstraint" id="addConstraintButton">add_circle_outline</v-icon>
            </v-btn>

        </v-toolbar>

        <v-list two-line dense>
            <template v-if="constraints.length === 0">
                <v-list-tile>
                    <v-list-tile-content>
                        <v-list-tile-title class="text-xs-center grey--text">
                            No constraints selected
                        </v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>
            </template>

            <template v-else v-for="constraint in constraints">
                <v-list-tile :key="constraint.id">
                    <v-list-tile-content>
                        <v-list-tile-title>{{constraint.name}}</v-list-tile-title>
                    </v-list-tile-content>

                    <v-list-tile-action>
                        <v-btn icon @click="editCredential(constraint)">
                            <v-icon small>edit</v-icon>
                        </v-btn>
                    </v-list-tile-action>
                    <v-list-tile-action>
                        <v-btn icon @click="askForConstraintDeletion(constraint)">
                            <v-icon small>delete</v-icon>
                        </v-btn>
                    </v-list-tile-action>
                </v-list-tile>
            </template>
        </v-list>

        <!-- Edit constraint dialog -->
        <v-dialog v-model="editConstraintDialogVisible" width="800">
            <v-card>
                <v-card-title class="headline teal darken-3 white--text" primary-title>
                    {{inputMode}} constraint
                </v-card-title>
                <edit-constraints-details
                        :constraint="selectedConstraint"
                        v-on:canceled="cancel()"
                        v-on:saved="save()"></edit-constraints-details>

                <v-divider></v-divider>

                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn flat @click="cancel()">Cancel</v-btn>
                    <v-btn color="accent" flat @click="save()">Save</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>

        <!-- Delete confirmation -->
        <v-dialog v-model="deleteConfirmationVisible" width="400">
            <v-card>
                <v-card-title class="headline" primary-title>
                    Delete?
                </v-card-title>
                <v-card-text>
                    Do you really want to delete constraint <b>{{selectedConstraint.name}}</b>?
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn flat @click="deleteConfirmationVisible = false">No</v-btn>
                    <v-btn color="error" flat @click="deleteConstraint()">Yes</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-card>
</template>

<script>
    import {ADD_CONSTRAINT, DELETE_CONSTRAINT, UPDATE_CONSTRAINT} from "../store/permission.module"
    import EditConstraintsDetails from "./EditConstraintsDetails";

    export default {
        components: {
            EditConstraintsDetails,
        },
        props: {
            constraints: {
                type: Array,
                default: () => ([])
            },
            permission: {
                type: Object,
                default: () => ({})
            }
        },
        computed: {
            selectedPermission() {
                return this.permission
            }
        },
        data: () => ({
            editConstraintDialogVisible: false,
            deleteConfirmationVisible: false,
            selectedConstraint: {},
            inputMode: ""
        }),
        methods: {
            createConstraint() {
                this.inputMode = "create"
                this.selectedConstraint = {}
                this.editConstraintDialogVisible = true
            },
            editCredential(credential) {
                // deep copy to prevent direct edit of the actual list item.
                this.inputMode = "update"
                this.selectedConstraint = JSON.parse(JSON.stringify(credential))
                this.editConstraintDialogVisible = true
            },
            askForConstraintDeletion(constraint) {
                this.selectedConstraint = constraint
                this.deleteConfirmationVisible = true
            },
            deleteConstraint() {
                this.deleteConfirmationVisible = false
                this.$store.dispatch(DELETE_CONSTRAINT, {
                    permission: this.selectedPermission,
                    constraint: this.selectedConstraint
                })
            },
            save() {
                if (this.inputMode === "create") {
                    this.$store.dispatch(ADD_CONSTRAINT, {
                        permission: this.selectedPermission,
                        constraint: this.selectedConstraint
                    })
                } else {
                    this.$store.dispatch(UPDATE_CONSTRAINT, {
                        permission: this.selectedPermission,
                        constraint: this.selectedConstraint
                    })
                }

                this.editConstraintDialogVisible = false
            },
            cancel() {
                this.editConstraintDialogVisible = false
            }
        }
    }
</script>

<style scoped>

</style>