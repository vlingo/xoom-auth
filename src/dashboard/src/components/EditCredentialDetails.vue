<template>
    <v-container id="edit-credential-details">
        <v-layout>
            <v-flex md3>
                <v-select id="authority" v-model="credential.authority"
                          :items="authorities"
                          label="Authority"
                ></v-select>
            </v-flex>
            <v-flex md1></v-flex>
            <v-text-field
                    v-model="credential.id"
                    label="Id"
            ></v-text-field>
        </v-layout>
        <v-layout>
            <v-text-field
                    v-model="credential.secret"
                    label="Secret"
                    type="password"
            ></v-text-field>
        </v-layout>
    </v-container>
</template>

<script>
    export default {
        props: {
            credential: {
                type: Object,
                default: () => ({})
            }
        },
        data: () => ({
            authorities: [
                'vlingo',
                'oAuth'
            ]
        })
    }
</script>
