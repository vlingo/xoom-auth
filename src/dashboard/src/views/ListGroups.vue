<template>
    <v-container fluid id="groups-list">
        <v-card>
            <v-toolbar class="teal darken-3 white--text">
                <span class="headline">Groups</span>

                <v-spacer></v-spacer>
                <v-text-field dark
                              v-model="search"
                              append-icon="search"
                              label="Search"
                              single-line
                              hide-details
                ></v-text-field>
                <v-spacer></v-spacer>
                <v-btn @click="createDialogVisible = true"
                       color="teal darken-2"
                       dark
                       small
                       absolute
                       bottom
                       right
                       fab
                       id="addGroupButton"
                >
                    <v-icon>add</v-icon>
                </v-btn>
            </v-toolbar>

            <!-- group list -->
            <v-data-table :headers="headers" :items="groups" :loading="isLoading" :search="search">
                <v-alert slot="no-results" :value="true" color="error" icon="warning">
                    Your search for "{{ search }}" found no results.
                </v-alert>
                <template slot="items" slot-scope="props">
                    <tr @click="props.expanded = !props.expanded">
                        <td class="text-xs-left">{{ props.item.name }}</td>
                        <td class="text-xs-left">{{ props.item.description }}</td>
                        <td class="justify-center layout px-0">
                            <v-icon
                                    small
                                    class="mr-2"
                                    @click="showEditGroupMembersDialog(props.item)">
                                supervisor_account
                            </v-icon>
                            <v-icon
                                    small
                                    class="mr-2"
                                    @click="showEditInnerGroupsDialog(props.item)">
                                group
                            </v-icon>
                            <v-icon small
                                    id="editGroupButton"
                                    class="mr-2"
                                    @click="showEditDialog(props.item)">
                                edit
                            </v-icon>
                            <v-icon small
                                    @click="askToDelete(props.item)"
                            >
                                delete
                            </v-icon>
                        </td>
                    </tr>
                </template>
                
                <!-- Expanded View-->
               <!-- <template slot="expand" slot-scope="props">
                    <v-card flat class="grey lighten-3">
                        <v-card-text>
                            <v-layout>
                                <v-flex xs6>
                                    <sub-collection :add="showEditInnerGroupsDialog(props.item)">
                                        
                                    </sub-collection>
                                </v-flex>
                            </v-layout>
                        </v-card-text>
                    </v-card>
                </template>-->
            </v-data-table>
        </v-card>

        <!-- Delete confirmation -->
        <delete-confirmation :showDialog="deleteConfirmationVisible" 
                             :identifier="editableGroup.name"
                             v-on:canceled="deleteConfirmationVisible = false"
                             v-on:confirmed="deleteItem()">            
        </delete-confirmation> 

        <!-- Create group dialog -->
        <v-dialog v-model="createDialogVisible" width="400">
            <v-card fluid>
                <CreateOrEditGroup mode="create"
                                   v-on:canceled="createDialogVisible = false"
                                   v-on:saved="createDialogVisible = false"></CreateOrEditGroup>
            </v-card>
        </v-dialog>

        <!-- Edit group dialog -->
        <v-dialog v-model="editGroupDialogVisible" width="400">
            <CreateOrEditGroup mode="edit" :group="editableGroup"
                               v-on:canceled="closeEditGroupDialogs"
                               v-on:saved="closeEditGroupDialogs"></CreateOrEditGroup>
        </v-dialog>

        <!-- Manage inner groups dialog -->
        <v-dialog v-model="editInnerGroupsDialogVisible" width="400">
            <EditInnerGroups :group="editableGroup"
                              v-on:canceled="closeEditGroupDialogs"
                              v-on:saved="closeEditGroupDialogs"></EditInnerGroups>
        </v-dialog>

        <!-- Manage group members dialog -->
        <v-dialog v-model="editGroupMembersDialogVisible" width="400">
            <EditGroupMembers :group="editableGroup"
                              v-on:canceled="closeEditGroupDialogs"
                              v-on:saved="closeEditGroupDialogs"></EditGroupMembers>
        </v-dialog>


    </v-container>
</template>

<script>
    import {mapState} from "vuex"
    import {GROUP_MODULE, INITIALIZE_GROUPS, DELETE_GROUP} from "../store/group.module"
    import CreateOrEditGroup from "./CreateOrEditGroup";
    import EditInnerGroups from "./EditInnerGroups";
    import EditGroupMembers from "./EditGroupMembers";
    import DeleteConfirmation from "../components/DeleteConfirmation";

    export default {
        components: {
            DeleteConfirmation,
            EditInnerGroups,
            CreateOrEditGroup,
            EditGroupMembers
        },
        mounted() {
            this.$store.dispatch(INITIALIZE_GROUPS)
        },
        methods: {
            setEditableGroupToClone(item) {
                // deep copy to prevent direct edit of the actual list item.
                this.editableGroup = JSON.parse(JSON.stringify(item))
            },
            showEditGroupMembersDialog(item) {
                this.setEditableGroupToClone(item)
                this.editGroupMembersDialogVisible = true
            },
            showEditInnerGroupsDialog(item) {
                this.setEditableGroupToClone(item)
                this.editInnerGroupsDialogVisible = true
            },
            showEditDialog(item) {
                this.setEditableGroupToClone(item)
                this.editGroupDialogVisible = true
            },
            askToDelete(item) {
                this.editableGroup = item
                this.deleteConfirmationVisible = true
            },
            deleteItem() {
                this.deleteConfirmationVisible = false
                this.$store.dispatch(DELETE_GROUP, this.editableGroup)
            },
            closeEditGroupDialogs() {
                this.editGroupDialogVisible = false
                this.editInnerGroupsDialogVisible = false
                this.editGroupMembersDialogVisible = false
                this.editableGroup = {}
            }
        },
        computed: mapState(GROUP_MODULE, {
            groups: state => state.groups,
            isLoading: state => state.isLoading
        }),
        data: () => ({
            search: "",
            showLoadingBar: false,
            editableGroup: {},
            deleteConfirmationVisible: false,
            createDialogVisible: false,
            editGroupDialogVisible: false,
            editInnerGroupsDialogVisible: false,
            editGroupMembersDialogVisible: false,
            headers: [
                {
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
