import _ from "lodash"
import {EventBus} from "../plugins/EventBus"

/* Vuex makes heavily use of magic strings.
   We have to compensate this with string constants.
   Unfortunatly, that gets a bit messy if you use also namespaces.
   The following part will help to keep most of the mess in this file.
 */

// "private" member
const ADD_GROUP_MUTATION = "addGroupMutation"
const UPDATE_GROUP_MUTATION = "updateGroupMutation"
const DELETE_GROUP_MUTATION = "deleteGroupMutation"
const UPDATE_INNER_GROUPS_MUTATION = "updateInnerGroupMutation"
const UPDATE_MEMBERS_MUTATION = "updateGroupMembersMutation"

const ALL_GROUPS_GETTER = "getAllGroups"
const GROUP_BY_ID_GETTER = "getGroupById"
const GROUPS_BY_IDS_GETTER = "getGroupsByIds"

const ADD_GROUP_ACTION = "addGroupAction"
const UPDATE_GROUP_ACTION = "updateGroupAction"
const DELETE_GROUP_ACTION = "deleteGroupAction"
const UPDATE_INNER_GROUPS_ACTION = "updateInnerGroupsAction"
const UPDATE_MEMBERS_ACTION = "updateMembersAction"
const INITIALIZE_GROUPS_ACTION = "initializeGroups"
const SET_ISLOADING = "setIsLoading"

// "public" member
export const GROUP_MODULE = "groupModule"
export const INITIALIZE_GROUPS = GROUP_MODULE + "/" + INITIALIZE_GROUPS_ACTION
export const ADD_GROUP = GROUP_MODULE + "/" + ADD_GROUP_ACTION
export const UPDATE_GROUP = GROUP_MODULE + "/" + UPDATE_GROUP_ACTION
export const DELETE_GROUP = GROUP_MODULE + "/" + DELETE_GROUP_ACTION
export const UPDATE_INNER_GROUP_STATUS = GROUP_MODULE + "/" + UPDATE_INNER_GROUPS_ACTION
export const UPDATE_MEMBER_STATUS = GROUP_MODULE + "/" + UPDATE_MEMBERS_ACTION

export const GET_ALL_GROUPS = GROUP_MODULE + "/" + ALL_GROUPS_GETTER
export const GET_GROUP_BY_ID = GROUP_MODULE + "/" + GROUP_BY_ID_GETTER
export const GET_GROUPS_BY_IDS = GROUP_MODULE + "/" + GROUPS_BY_IDS_GETTER

export const groupModule = {
    namespaced: true,
    isGroupModuleInitialized: false,
    state: {
        isLoading: false,
        groups: [],
        idTreshold: 0
    },
    mutations: {
        [ADD_GROUP_MUTATION](state, group) {
            state.idTreshold += 1
            group.id = state.idTreshold
            state.groups.push(group)
        },
        [UPDATE_GROUP_MUTATION](state, group) {
            let existingGroup = _.find(state.groups, {id: group.id})
            existingGroup.description = group.description
        },
        [DELETE_GROUP_MUTATION](state, group) {
            let index = state.groups.indexOf(group)
            state.groups.splice(index, 1)
        },
        [SET_ISLOADING](state, isLoading) {
            state.isLoading = isLoading
        },
        [UPDATE_INNER_GROUPS_MUTATION] (state, updateData) {

            let parentGroup = state.groups.find(group => group.id === updateData.parentGroup.id)

            _.forEach(updateData.changedInnerGroups, (insideGroup) => {
                if (insideGroup.isInnerGroup) {
                    parentGroup.members.push(insideGroup)
                } else {
                    _.remove(parentGroup.members, id => id === insideGroup.id)
                }
            })
        },
        [UPDATE_MEMBERS_MUTATION] (state, updateData) {
            let parentGroup = state.groups.find(group => group.id === updateData.parentGroup.id)

            _.forEach(updateData.groupMembers, (member) => {
                if (member.isMember) {
                    parentGroup.members.push(member)
                } else {
                    _.remove(parentGroup.members, id => id === member.id)
                }
            })
        }
    },
    getters: {
        [ALL_GROUPS_GETTER]: (state) => {

            let resultGroups = []
            _.forEach(state.groups, group => {
                // create a clone to avoid unmanaged changes to the state
                let clone = JSON.parse(JSON.stringify(group))
                resultGroups.push(clone)
            })

          return resultGroups
        },
        [GROUP_BY_ID_GETTER]: (state) => (id) => {
          return state.groups.find(group => group.id === id)
        },
        [GROUPS_BY_IDS_GETTER]: (state, getters) => (ids) => {

            let innerGroups = []
            _.forEach(ids, (id) => {
                let group = getters.getGroupById(id)
                innerGroups.push(group)
            })

            return innerGroups
        }
    },
    actions: {
        [INITIALIZE_GROUPS_ACTION]: function ({commit}) {
            if (this.isGroupModuleInitialized) {
                return
            }

            commit(SET_ISLOADING, true)

            let firstGroup = {name: "Members Group", description: "Group with a member.", innerGroups: [], groupMembers: [1]}
            commit(ADD_GROUP_MUTATION, firstGroup)
            commit(ADD_GROUP_MUTATION, {name: "Parent Group", description: "Group with an inside group.", innerGroups: [firstGroup.id], groupMembers: []})
            commit(ADD_GROUP_MUTATION, {name: "Empty Group", description: "Group without any inside groups or members.", innerGroups: [], groupMembers: []})
            commit(ADD_GROUP_MUTATION, {name: "Another Empty Group", description: "Group without any inside groups or members.", innerGroups: [], groupMembers: []})
            commit(ADD_GROUP_MUTATION, {name: "And Another Empty Group", description: "Group without any inside groups or members.", innerGroups: [], groupMembers: []})

            commit(SET_ISLOADING, false)
            this.isGroupModuleInitialized = true
        },
        [ADD_GROUP_ACTION]: function ({commit}, group) {
            commit(ADD_GROUP_MUTATION, group)

            // add actual API call
            EventBus.$emit("notification", "Added new group " + group.name)
        },
        [UPDATE_GROUP_ACTION]: function ({commit}, group) {
            commit(UPDATE_GROUP_MUTATION, group)

            // add actual API call
            EventBus.$emit("notification", "Updated group " + group.name)
        },
        [DELETE_GROUP_ACTION]: function ({commit}, group) {
            commit(DELETE_GROUP_MUTATION, group)

            // add actual API call
            EventBus.$emit("notification", "Deleted group " + group.name)
        },
        [UPDATE_INNER_GROUPS_ACTION]: function({commit}, updateData){

            commit(UPDATE_INNER_GROUPS_MUTATION, updateData)

            // add actual API call
            EventBus.$emit("notification", "Updated inner groups of group" + updateData.parentGroup.name)
        },
        [UPDATE_MEMBERS_ACTION]: function({commit}, updateData){

            commit(UPDATE_MEMBERS_MUTATION, updateData)

            // add actual API call
            EventBus.$emit("notification", "Updated members of group" + updateData.parentGroup.name)
        }
    }
}
