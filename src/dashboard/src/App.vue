<template>
    <v-app id="inspire">

        <v-container>

            <div v-if="loggedIn">
                <ApplicationFrame></ApplicationFrame>
            </div>

            <v-layout v-else align-center justify-center row fill-height>
                <v-flex></v-flex>

                <v-flex>
                    <v-layout align-center justify-center column fill-height>
                        <v-flex></v-flex>
                        <v-flex>
                            <div v-if="register">

                                <v-container>
                                    <img height="100px" width="100px" src="./assets/logo.png">
                                    <RegisterUser v-on:canceled="register=false" v-on:registered="logIn"></RegisterUser>
                                </v-container>

                            </div>
                            <div v-else>
                                <div style="text-align: center;">
                                    <img height="100px" width="100px" src="./assets/logo.png">
                                    <SignOn></SignOn>
                                    <v-btn id="registerUserButton" flat @click="register=true">register</v-btn>
                                </div>
                            </div>
                        </v-flex>
                        <v-flex></v-flex>
                    </v-layout>
                </v-flex>
                <v-flex></v-flex>

            </v-layout>

        </v-container>
    </v-app>
</template>

<script>
    import ApplicationFrame from "./views/ApplicationFrame"
    import RegisterUser from "./views/RegisterUser"
    import SignOn from "./views/SignOn"
    import {mapState} from "vuex"
    import {LOGIN, APPLICATION_STATE_MODULE} from "./store/applicationState.module"

    export default {
        components: {
            ApplicationFrame,
            RegisterUser,
            SignOn
        },
        computed: mapState(APPLICATION_STATE_MODULE, {
            loggedIn: state => state.isLoggedIn
        }),
        data: () => ({
            register: false
        }),
        methods: {
            logIn(user) {
                this.register = false
                this.$store.dispatch(LOGIN, user)
            }
        }
    }
</script>
