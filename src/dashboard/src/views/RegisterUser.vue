<template>
    <v-form v-model="valid" id="register-user">
        <v-card fluid>
            <v-card-text>
                <edit-user-details :user="user"></edit-user-details>
                <v-divider></v-divider>

                <edit-credential-details :credential="credential"></edit-credential-details>
            </v-card-text>

            <v-divider></v-divider>

            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn flat @click="cancel()">Cancel</v-btn>
                <v-btn id="registerButton" color="accent" flat :disabled="!valid" @click="register()">Register</v-btn>
            </v-card-actions>
        </v-card>
    </v-form>
</template>

<script>
    import EditUserDetails from "../components/EditUserDetails"
    import EditCredentialDetails from "../components/EditCredentialDetails"
    import {CREATE_USER} from "../store/user.module"

    export default {
        name: "register-user",
        components: {
            EditUserDetails,
            EditCredentialDetails
        },
        data: () => ({
            user: {},
            credential: {},
            valid: false
        }),
        methods: {
            register() {
                let credentials = new Array()
                credentials.push(this.credential)

                this.user.credentials = credentials
                this.$store.dispatch(CREATE_USER, this.user)
                this.$emit("registered", this.user)
            },
            cancel() {
                this.user = {}
                this.credential = {}
                this.$emit("canceled")
            }
        }
    }
</script>
