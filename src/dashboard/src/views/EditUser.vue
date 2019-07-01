<template>
    <v-container fluid width="600">
        <v-form v-model="valid">
            <v-card flat>
                <v-card-text>
                    <edit-user-details :user="user"></edit-user-details>
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
    import {UPDATE_USER} from "../store/user.module"

    export default {
        components: {},
        props: {
            user: {
                type: Object,
                default: () => ({})
            }
        },
        data: () => ({
            valid: false
        }),
        methods: {
            save() {
                this.$store.dispatch(UPDATE_USER, this.user)
                this.$emit("saved", this.user)
            },
            cancel() {
                this.$emit("canceled")
            }
        }
    }
</script>
