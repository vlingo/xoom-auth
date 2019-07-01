import _ from "lodash"
import {EventBus} from "../plugins/EventBus"

/* Vuex makes heavily use of magic strings.
   We have to compensate this with string constants.
   Unfortunatly, that gets a bit messy if you use also namespaces.
   The following part will help to keep most of the mess in this file.
 */

// "private" member
const SET_ISLOADING = "setIsLoading"
const INITIALIZE_ROLES_ACTION = "initializeROLEStore"
const ALL_ROLES_GETTER = "getAllRoles"

const ADD_ROLE_ACTION = "createRoleAction"
const DELETE_ROLE_ACTION = "deleteRoleAction"
const UPDATE_ROLE_ACTION = "updateRoleAction"

const ADD_ROLE_MUTATION = "addRoleMutation"
const DELETE_ROLE_MUTATION = "deleteRoleMutation"
const UPDATE_ROLE_MUTATION = "updateRoleMutation"

// "public" member
export const ROLE_MODULE = "roleModule"
export const INITIALIZE_ROLE_MODULE = ROLE_MODULE + "/" + INITIALIZE_ROLES_ACTION
export const CREATE_ROLE = ROLE_MODULE + "/" + ADD_ROLE_ACTION
export const DELETE_ROLE = ROLE_MODULE + "/" + DELETE_ROLE_ACTION
export const UPDATE_ROLE = ROLE_MODULE + "/" + UPDATE_ROLE_ACTION
export const GET_ALL_ROLES = ROLE_MODULE + "/" + ALL_ROLES_GETTER

export const roleModule = {
    isRoleModuleInitialized: false,
    namespaced: true,
    state: {
        roles: [],
        isLoading: false,
        idTreshold: 4
    },
    mutations: {
        [ADD_ROLE_MUTATION](state, role) {

            state.idTreshold += 1
            role.id = state.idTreshold

            state.roles.push(role)
        },
        [DELETE_ROLE_MUTATION](state, role) {
            const index = state.roles.indexOf(role)
            state.roles.splice(index, 1)
        },
        [UPDATE_ROLE_MUTATION](state, role) {
            let existingRole = _.find(state.roles, {id: role.id})

            existingRole.name = role.name
            existingRole.description = role.description
  
        },
        [SET_ISLOADING](state, isLoading) {
            state.isLoading = isLoading
        }
    },
    getters: {
        [GET_ALL_ROLES]: (state) => {

            let resultRoles = []
            _.forEach(state.roles, role => {
                // create a clone to avoid unmanaged changes to the state
                let clone = JSON.parse(JSON.stringify(role))
                resultRoles.push(clone)
            })

            return resultRoles
        }
    },
    actions: {
        [INITIALIZE_ROLES_ACTION]: function ({commit}) {
            if (this.isRoleModuleInitialized || this.state.isLoading) {
                return
            }
            commit(SET_ISLOADING, true)

            // just for testing purpose
            commit(ADD_ROLE_MUTATION, { name: "Role 1", description: "First Role" })
            commit(ADD_ROLE_MUTATION, { name: "Role 2", description: "Second Role" })
            
            commit(SET_ISLOADING, false)
            this.isRoleModuleInitialized = true
        },
        [ADD_ROLE_ACTION]: function ({commit}, role) {

            if (!this.isRoleModuleInitialized) {
                this.dispatch(INITIALIZE_ROLE_MODULE)
            }

            /* TODO: Add actual API call */

            commit(ADD_ROLE_MUTATION, role)
            EventBus.$emit("notification", "Added new role.")
        },
        [DELETE_ROLE_ACTION]: function ({commit}, role) {
            /* TODO: Add actual API call */

            commit(DELETE_ROLE_MUTATION, role)
            EventBus.$emit("notification", "Role was deleted.")
        },
        [UPDATE_ROLE_ACTION]: function ({commit}, newData) {
            /* TODO: Add actual API call */

            commit(UPDATE_ROLE_MUTATION, newData)
            EventBus.$emit("notification", "Role information changed.")
        }
    }
}
