<template>
    <v-container id="sign-on">
        <v-form v-model="valid">
            <v-card width="400">
                <v-card-text>
                    <v-text-field
                            id="tenantId"
                            v-model="loginData.tenantId"
                            label="Tenant Id"
                            required
                            :rules="[v => !!v || 'Please, enter the id of the tenant.']"
                    ></v-text-field>
                    <v-text-field
                            id="username"
                            v-model="loginData.username"
                            label="Username"
                            required
                            :rules="[v => !!v || 'Please, enter a username.']"
                    ></v-text-field>

                    <v-text-field
                            id="credentialId"
                            v-model="loginData.credentialId"
                            label="Credential Id"
                            required
                            :rules="[v => !!v || 'Please, enter the id of the credentials.']"
                    ></v-text-field>

                    <v-text-field
                            id="secret"
                            v-model="loginData.secret"
                            label="Secret"
                            required
                            type="password"
                            :rules="[v => !!v || 'Please, enter the secret.']"
                    ></v-text-field>

                    <v-alert
                            :value="error"
                            type="error"
                    >
                        {{error}}
                    </v-alert>                    
                    
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn id="loginButton"
                           :disabled="!valid" @click="signOn()">Sign On
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-form>
    </v-container>
</template>

<script>
    import {LOGIN} from "../store/applicationState.module"
    import Cookie from "js-cookie"

    const cookieName = "vlingo-auth-tenant-info"
    
    export default {
        name: "sing-on",
        data: () => ({
            valid: false,
            error: null,
            loginData: {
                tenantId: "",
                username: "",
                credentialId: "",
                secret: ""
            }
        }),
        mounted: function() {
            let tenantInfo = Cookie.get(cookieName)
            
            if(tenantInfo !== null && tenantInfo !== undefined) {
                tenantInfo = JSON.parse(tenantInfo)
                this.loginData.tenantId = tenantInfo.tenantId
            }
        },
        methods: {
            signOn() {           
                var that = this   
                this.$store.dispatch(LOGIN, this.loginData).then( () => {
                    Cookie.set(cookieName, { "tenantId": that.loginData.tenantId })
                    this.error = null
                }).catch(error => {
                    console.log(error)
                    this.error = "User could not be logged in. Please check username and password." })                    
            }
        }
    }
</script>

<style scoped>

</style>