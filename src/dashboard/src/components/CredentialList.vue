<template>
    <v-card>
        <v-subheader class="secondary">
            Credentials
            <v-spacer></v-spacer>
            <v-btn icon>
                <v-icon color="primary" @click="createCredential">add_circle_outline</v-icon>
            </v-btn>
        </v-subheader>
            <v-list two-line dense>
                
                <template v-if="credentials.length === 0">
                    <v-list-tile>
                        <v-list-tile-content>
                            <v-list-tile-title class="text-xs-center grey--text">
                                No credentials selected
                            </v-list-tile-title>
                        </v-list-tile-content>
                    </v-list-tile>
                </template>
                
                <template v-else v-for="credential in credentials">
                    <v-list-tile :key="credential.id">
                        <v-list-tile-content>
                            <v-list-tile-title>{{credential.id}}</v-list-tile-title>
                            <v-list-tile-sub-title>{{credential.authority}}
                            </v-list-tile-sub-title>
                        </v-list-tile-content>

                        <v-list-tile-action>
                            <v-btn icon @click="editCredential(credential)">
                                <v-icon small>edit</v-icon>
                            </v-btn>
                        </v-list-tile-action>
                        <v-list-tile-action>
                            <v-btn icon @click="askForCredentialDeletion(credential)">
                                <v-icon small>delete</v-icon>
                            </v-btn>
                        </v-list-tile-action>
                    </v-list-tile>
                </template>
            </v-list>

        <!-- Edit credential dialog -->
        <v-dialog v-model="editCredentialDialogVisible" width="800">
            <v-card>
                <v-card-title class="headline teal darken-3 white--text" primary-title>
                    {{inputMode}} credentials
                </v-card-title>
                <edit-credential :user="selectedUser"
                                 :mode="inputMode"
                                 :credential="selectedCredential"
                                 v-on:canceled="editCredentialDialogVisible = false"
                                 v-on:saved="editCredentialDialogVisible = false"></edit-credential>
            </v-card>
        </v-dialog>

        <!-- Delete confirmation -->
        <v-dialog v-model="deleteConfirmationVisible" width="400">
            <v-card>
                <v-card-title class="headline" primary-title>
                    Delete?
                </v-card-title>
                <v-card-text>
                    Do you really want to delete credential <b>{{selectedCredential.id}}</b>?
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn flat @click="deleteConfirmationVisible = false">No</v-btn>
                    <v-btn color="error" flat @click="deleteCredential()">Yes</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-card>
</template>

<script>
    import EditCredential from "../views/EditCredential";
    import {DELETE_CREDENTIAL_OF_USER} from "../store/user.module"

    export default {
        components: {
            EditCredential
        },
        props: {
            credentials: {
                type: Array,
                default: () => ([])
            },
            user: {
                type: Object,
                default: () => ({})
            }
        },
        computed: {
            selectedRole() {
                return this.user
            }
        },
        data: () => ({
            editCredentialDialogVisible: false,
            deleteConfirmationVisible: false,
            selectedCredential: {},
            inputMode: ""
        }),
        methods: {
            createCredential() {
                this.inputMode = "create"
                this.selectedCredential = {}
                this.editCredentialDialogVisible = true
            },
            editCredential(credential) {
                // deep copy to prevent direct edit of the actual list item.
                this.inputMode = "update"
                this.selectedCredential = JSON.parse(JSON.stringify(credential))
                this.editCredentialDialogVisible = true
            },
            askForCredentialDeletion(credential) {
                this.selectedCredential = credential
                this.deleteConfirmationVisible = true
            },
            deleteCredential() {
                this.deleteConfirmationVisible = false
                this.$store.dispatch(DELETE_CREDENTIAL_OF_USER, {user: this.user, credential: this.selectedCredential})
            }
        }
    }
</script>

<style scoped>

</style>