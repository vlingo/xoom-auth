import _ from "lodash"
import {EventBus, NOTIFICATION} from "../plugins/EventBus"

/* Vuex makes heavily use of magic strings.
   We have to compensate this with string constants.
   Unfortunatly, that gets a bit messy if you use also namespaces.
   The following part will help to keep most of the mess in this file.
 */

// "private" member
const SET_USERS_MUTATION = "setLocalUsers"
const SET_ISLOADING = "setIsLoading"
const INITIALIZE_USERS_ACTION = "initializeUserStore"
const ALL_USERS_GETTER = "getAllUsers"

const ADD_USER_ACTION = "createUserAction"
const DELETE_USER_ACTION = "deleteUserAction"
const UPDATE_USER_ACTION = "updateUserAction"

const ADD_CREDENTIAL_ACTION = "addCredentialAction"
const UPDATE_CREDENTIAL_ACTION = "updateCredentialAction"
const DELETE_CREDENTIAL_ACTION = "deleteCredentialAction"

const ADD_USER_MUTATION = "addUserMutation"
const DELETE_USER_MUTATION = "deleteUserMutation"
const UPDATE_USER_MUTATION = "updateUserMutation"

const ADD_CREDENTIAL_MUTATION = "addCredentialMutation"
const UPDATE_CREDENTIAL_MUTATION = "updateCredentialMutation"
const DELETE_CREDENTIAL_MUTATION = "deleteCredentialMutation"

// "public" member
export const USER_MODULE = "userModule"
export const INITIALIZE_USER_MODULE = USER_MODULE + "/" + INITIALIZE_USERS_ACTION
export const CREATE_USER = USER_MODULE + "/" + ADD_USER_ACTION
export const DELETE_USER = USER_MODULE + "/" + DELETE_USER_ACTION
export const UPDATE_USER = USER_MODULE + "/" + UPDATE_USER_ACTION
export const ADD_CREDENTIAL_TO_USER = USER_MODULE + "/" + ADD_CREDENTIAL_ACTION
export const UPDATE_CREDENTIAL_OF_USER = USER_MODULE + "/" + UPDATE_CREDENTIAL_ACTION
export const DELETE_CREDENTIAL_OF_USER = USER_MODULE + "/" + DELETE_CREDENTIAL_ACTION
export const GET_ALL_USERS = USER_MODULE + "/" + ALL_USERS_GETTER

export const userModule = {
    isUserModuleInitialized: false,
    namespaced: true,
    state: {
        users: [],
        isLoading: false,
        idTreshold: 4
    },
    mutations: {
        [SET_USERS_MUTATION](state, users) {
            state.users = users
        },
        [ADD_USER_MUTATION](state, user) {

            state.idTreshold += 1
            user.id = state.idTreshold

            state.users.push(user)
        },
        [DELETE_USER_MUTATION](state, user) {
            const index = state.users.indexOf(user)
            state.users.splice(index, 1)
        },
        [UPDATE_USER_MUTATION](state, user) {
            let existingUser = _.find(state.users, {id: user.id})

            existingUser.userName = user.userName
            existingUser.email = user.email
            existingUser.familyName = user.familyName
            existingUser.givenName = user.givenName
            existingUser.secondName = user.secondName
            existingUser.phone = user.phone
        },
        [SET_ISLOADING](state, isLoading) {
            state.isLoading = isLoading
        },
        [ADD_CREDENTIAL_MUTATION](state, data) {
            let existingUser = _.find(state.users, {id: data.user.id})

            if (existingUser === null) {
                // TODO: Replace by notification or something
                console.log("User was not found.")
                return
            }

            existingUser.credentials.push(data.credential)
        },
        [UPDATE_CREDENTIAL_MUTATION](state, updateData) {
            let existingUser = _.find(state.users, {id: updateData.user.id})

            if (existingUser === null) {
                // TODO: Replace by notification or something
                console.log("User was not found.")
                return
            }
            let existingCredential = _.find(existingUser.credentials, {id: updateData.credential.id})

            // It is currently not possible to change the id!
            existingCredential.authority = updateData.credential.authority
            existingCredential.secret = updateData.credential.secret
        },
        [DELETE_CREDENTIAL_MUTATION](state, updateData) {
            let existingUser = _.find(state.users, {id: updateData.user.id})

            if (existingUser === null) {
                // TODO: Replace by notification or something
                console.log("User was not found.")

                return
            }

            _.remove(existingUser.credentials, {id: updateData.credential.id})
        }
    },
    getters: {
        [GET_ALL_USERS]: (state) => {

            let resultUsers = []
            _.forEach(state.users, user => {
                // create a clone to avoid unmanaged changes to the state
                let clone = JSON.parse(JSON.stringify(user))
                resultUsers.push(clone)
            })

            return resultUsers
        }
    },
    actions: {
        [INITIALIZE_USERS_ACTION]: function ({commit}) {
            if (this.isUserModuleInitialized || this.state.isLoading) {
                return
            }
            commit(SET_ISLOADING, true)

            // just for testing purpose
            commit(SET_USERS_MUTATION, [{
                email: "max@mustermail.de",
                familyName: "Mustermann",
                givenName: "Max",
                userName: "Peter",
                phone: "5351 7878916",
                id: 1,
                credentials: [
                    {
                        id: "test",
                        authority: "vlingo",
                        secret: "123"
                    },
                    {
                        id: "another one",
                        authority: "vlingo",
                        secret: "444"
                    },
                ]
            },
                {
                    email: "maria@mustermail.de",
                    familyName: "Mustermann",
                    givenName: "Maria",
                    userName: "maria@mustermail.de",
                    phone: "7351 7874916",
                    id: 2,
                    credentials: [
                        {
                            id: "one",
                            authority: "vlingo",
                            secret: "123"
                        },
                        {
                            id: "two",
                            authority: "oAuth",
                            secret: "444"
                        },
                    ]
                },
                {
                    email: "sabiene@Leutheusser-Schnarrenberger.de",
                    familyName: "Leutheusser-Schnarrenberger",
                    givenName: "Sabine",
                    secondName: "Marie",
                    userName: "sabineL@gmx.de",
                    phone: "7351 7876816",
                    id: 3,
                    credentials: [
                        {
                            id: "test",
                            authority: "vlingo",
                            secret: ""
                        },
                        {
                            id: "another one",
                            authority: "vlingo",
                            secret: ""
                        },
                    ]
                }

            ])
            commit(SET_ISLOADING, false)
            this.isUserModuleInitialized = true
        },
        [ADD_USER_ACTION]: function ({commit}, user) {

            if (!this.isUserModuleInitialized) {
                this.dispatch(INITIALIZE_USER_MODULE)
            }

            /* TODO: Add actual API call */

            commit(ADD_USER_MUTATION, user)
            EventBus.$emit(NOTIFICATION, "Added new user.")
        },
        [DELETE_USER_ACTION]: function ({commit}, user) {
            /* TODO: Add actual API call */

            commit(DELETE_USER_MUTATION, user)
            EventBus.$emit(NOTIFICATION, "User was deleted.")
        },
        [UPDATE_USER_ACTION]: function ({commit}, newData) {
            /* TODO: Add actual API call */

            commit(UPDATE_USER_MUTATION, newData)
            EventBus.$emit(NOTIFICATION, "User information changed.")
        },
        [ADD_CREDENTIAL_ACTION]: function ({commit}, data) {
            /* TODO: Add actual API call */

            commit(ADD_CREDENTIAL_MUTATION, data)
            EventBus.$emit(NOTIFICATION, "Added new credential.")
        },
        [UPDATE_CREDENTIAL_ACTION]: function ({commit}, updateData) {
            /* TODO: Add actual API call */

            commit(UPDATE_CREDENTIAL_MUTATION, updateData)
            EventBus.$emit(NOTIFICATION, "Credential data changed.")
        },
        [DELETE_CREDENTIAL_ACTION]: function ({commit}, data) {
            /* TODO: Add actual API call */

            commit(DELETE_CREDENTIAL_MUTATION, data)
            EventBus.$emit(NOTIFICATION, "Credential was deleted.")
        }
    }
}
