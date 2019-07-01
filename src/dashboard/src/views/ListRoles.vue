<template>
    <v-container fluid id="roles-list">
        <v-card>
            <v-toolbar class="teal darken-1 white--text">

                <span class="headline">Roles</span>

                <v-spacer></v-spacer>
                <v-text-field dark
                              v-model="search"
                              append-icon="search"
                              label="Search"
                              single-line
                              hide-details
                ></v-text-field>
                <v-spacer></v-spacer>
                <v-btn @click="addDialogVisible = true"
                       color="teal darken-1"
                       dark
                       small
                       absolute
                       bottom
                       right
                       fab
                       id="addRoleButton">
                    <v-icon>add</v-icon>
                </v-btn>
            </v-toolbar>

            <!-- role list -->
            <v-data-table :headers="headers" :items="roles" :loading="isLoading" :search="search">
                <v-alert slot="no-results" :value="true" color="error" icon="warning">
                    Your search for "{{ search }}" found no results.
                </v-alert>
                <template slot="items" slot-scope="props">
                    <tr @click="props.expanded = !props.expanded">
                        <td>{{ props.item.name }}</td>
                        <td class="text-xs-left">{{ props.item.description }}</td>
                        <td class="justify-center layout px-0">
                            <v-icon small
                                    class="mr-2"
                                    @click="showEditDialog(props.item)">
                                edit
                            </v-icon>
                            <v-icon small
                                    @click="askToDelete(props.item)">
                                delete
                            </v-icon>
                        </td>
                    </tr>
                </template>

                <!-- Expanded View-->
                <template slot="expand" slot-scope="props">
                    <v-card flat class="grey lighten-3">
                        <v-card-text>
                            <v-layout>
                                <v-flex xs6>
                                   <!-- <credential-list :credentials="props.item.credentials"
                                                     :user="props.item"></credential-list> -->
                                </v-flex>
                            </v-layout>
                        </v-card-text>
                    </v-card>

                </template>
            </v-data-table>
        </v-card>

        <!-- Delete confirmation -->
        <delete-confirmation :showDialog="deleteConfirmationVisible"
                             :identifier="selectedRole.name"
                             v-on:canceled="deleteConfirmationVisible = false"
                             v-on:confirmed="deleteItem()">
        </delete-confirmation>

        <!-- Add dialog -->
        <v-dialog v-model="addDialogVisible" width="400">
            <create-or-edit-role :role="editableRole" mode="create" v-on:saved="savedEditedRole" v-on:canceled="closeEditDialog"></create-or-edit-role>
        </v-dialog>

        <!-- Edit role dialog -->
        <v-dialog v-model="editRoleDialogVisible" width="400">
            <create-or-edit-role :role="editableRole" mode="edit" v-on:saved="savedEditedRole" v-on:canceled="closeEditDialog"></create-or-edit-role>
        </v-dialog>
    </v-container>
</template>

<script>
    import {mapState} from "vuex"
    import {DELETE_ROLE, INITIALIZE_ROLE_MODULE, ROLE_MODULE} from "../store/role.module";
    import CreateOrEditRole from "./CreateOrEditRole";

    export default {
        components: {
            CreateOrEditRole
        },
        created() {
            this.$store.dispatch(INITIALIZE_ROLE_MODULE)
        },
        methods: {
            registeredUser() {
                this.addDialogVisible = false
            },
            showEditDialog(item) {
                // deep copy to prevent direct edit of the actual list item.
                this.editableRole = JSON.parse(JSON.stringify(item))
                this.editRoleDialogVisible = true
            },
            askToDelete(item) {
                this.deleteConfirmationVisible = true
                this.selectedRole = item
            },
            deleteItem() {
                this.$store.dispatch(DELETE_ROLE, this.selectedRole)
                this.closeConfirmation()
            },
            closeConfirmation() {
                this.deleteConfirmationVisible = false
            },
            closeEditDialog() {
                this.addDialogVisible = false
                this.editRoleDialogVisible = false
            },
            savedEditedRole() {
                this.closeEditDialog()
                this.editableRole = {}
            }
        },
        computed: mapState(ROLE_MODULE, {
            roles: state => state.roles,
            isLoading: state => state.isLoading
        }),
        data: () => ({
            search: "",
            showLoadingBar: false,
            editableRole: {},
            selectedRole: {},
            deleteConfirmationVisible: false,
            addDialogVisible: false,
            editRoleDialogVisible: false,
            headers: [{
                text: 'Name',
                align: 'left',
                sortable: true,
                value: 'name'
            },
            {
                 text: 'Description',
                 align: 'left',
                 sortable: true,
                 value: 'description'
            },
            {
                text: 'Actions',
                align: 'Center',
                sortable: false
            }]
        })
    }
</script>
