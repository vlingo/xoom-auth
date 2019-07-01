<template>
    <v-container id="edit-user-details">
        <v-layout>
            <v-flex md5>
                <v-text-field
                        v-model="user.userName"
                        label="Username"
                ></v-text-field>
            </v-flex>
            <v-flex md1>
            </v-flex>
            <v-flex>
                <v-text-field
                        v-model="user.email"
                        label="Email Address"
                ></v-text-field>
            </v-flex>
        </v-layout>

        <v-layout>
            <v-flex md5>
                <v-text-field
                        v-model="user.givenName"
                        label="Given Name"
                ></v-text-field>
            </v-flex>
            <v-flex md1>

            </v-flex>
            <v-flex>
                <v-text-field
                        v-model="user.secondName"
                        label="Second Name"
                ></v-text-field>
            </v-flex>
        </v-layout>

        <v-layout>
            <v-flex md5>
                <v-text-field
                        v-model="user.familyName"
                        label="Family Name"
                ></v-text-field>
            </v-flex>
            <v-flex md1></v-flex>

            <v-flex>
                <v-text-field
                        v-model="user.phone"
                        label="Phone"
                ></v-text-field>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script>
    export default {
        props: {
            user: {
                type: Object,
                default: () => ({})
            }
        },
        methods: {
            mailAsUserName() {
                this.user.userName = this.user.email
            }
        }
    }
</script>