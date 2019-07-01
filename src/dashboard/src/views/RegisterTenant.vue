<template>
    <v-container>
        <v-form v-model="valid">
            <v-card>
                <v-card-text>
                    <name-description-input :value="tenant"></name-description-input>
                </v-card-text>
                <v-card-actions>
                    <v-btn :disabled="!valid" @click="register()">Register</v-btn>
                    <v-btn @click="cancel()">Cancel</v-btn>
                </v-card-actions>
            </v-card>
        </v-form>
    </v-container>
</template>

<script>
    export default {
        data: () => ({
            tenant: {},
            valid: false
        }),
        methods: {
            register() {
                let data = 'tenant: ' + JSON.stringify(this.tenant)
                alert(data)
                this.$emit("registered")
            },
            cancel() {
                this.tenant = {}
            }
        }
    }
</script>

<style scoped>

</style>