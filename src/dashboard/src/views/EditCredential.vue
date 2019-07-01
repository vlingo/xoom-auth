<template>
    <v-container id="edit-credential">
        <v-form v-model="valid">
            <v-card flat>
                <v-card-text>
                    <edit-credential-details :credential="selectedCredential"></edit-credential-details>
                </v-card-text>

                <v-divider></v-divider>

                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn flat @click="cancel()">Cancel</v-btn>
                    <v-btn color="accent" flat :disabled="!valid" @click="save()">Save</v-btn>
                </v-card-actions>
            </v-card>
        </v-form>
    </v-container>
</template>

<script>
    import {UPDATE_CREDENTIAL_OF_USER, ADD_CREDENTIAL_TO_USER} from "../store/user.module"

    export default {
        data: () => ({
            valid: false
        }),
        props: {
            credential: {
                type: Object,
                default: () => ({})
            },
            user: {
                type: Object,
                default: () => ({}),
                required: true
            },
            mode: {
                type: String,
                default: () => ("create")
            }
        },
        computed: {
          selectedCredential() {
              return this.credential
          }
        },
        methods: {
            save() {

                let data = {user: this.user, credential: this.credential}

                if (this.mode == "update") {
                    this.$store.dispatch(UPDATE_CREDENTIAL_OF_USER, data)
                } else {
                    this.$store.dispatch(ADD_CREDENTIAL_TO_USER, data)
                }

                this.$emit("saved", this.credential, this.user)
            },
            cancel() {
                this.$emit("canceled")
            }
        }
    }
</script>

<style scoped>

</style>