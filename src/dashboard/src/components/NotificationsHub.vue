<template>
    <v-menu :disabled="!showBadge">

        <v-btn icon slot="activator" :disabled="!showBadge">
            <v-badge color="red" overlap v-model="showBadge">
                <span slot="badge">{{notifications.length}}</span>
                <v-icon>notifications</v-icon>
            </v-badge>
        </v-btn>

        <v-card>
            <v-card-text>
                <v-list>
                    <v-list-tile
                            v-for="(notification, index) in notifications"
                            :key="index"
                    >
                        <v-list-tile-action>
                            <v-btn icon ripple @click="remove(index)">
                                <v-icon color="grey lighten-1">highlight_off</v-icon>
                            </v-btn>
                        </v-list-tile-action>
                        <v-list-tile-content>
                            <v-list-tile-title>{{ notification }}</v-list-tile-title>
                        </v-list-tile-content>
                    </v-list-tile>
                </v-list>
            </v-card-text>

            <v-divider></v-divider>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn small flat @click="notifications=[]">Clear</v-btn>
            </v-card-actions>
        </v-card>

        <!-- notification -->
        <v-snackbar
                v-model="notificationVisible"
                :right="true"
                :timeout="3000"
                :top="true"
        >
            {{ notificationText }}
            <v-btn
                    color="red"
                    flat
                    @click="notificationVisible = false"
            >
                Close
            </v-btn>
        </v-snackbar>
    </v-menu>
</template>

<script>
    import {EventBus, NOTIFICATION, CONNECTIONERROR} from "../plugins/EventBus";

    export default {
        name: "NotificationsHub",
        data: () => ({
            notifications: [],
            notificationVisible: false,
            notificationText: ""

        }),
        mounted() {
            EventBus.$on(NOTIFICATION, data => {
                this.notifications.push(data)
                this.notificationText = data
                this.notificationVisible = true
            });
            
            EventBus.$on(CONNECTIONERROR, data => {
                this.notifications.push(data)
                this.notificationText = data
                this.notificationVisible = true
            });
        },
        computed: {
            showBadge: function () {
                if (this.notifications !== undefined) {
                    return this.notifications.length > 0
                }

                return false
            }
        },
        methods: {
            remove(index) {
                this.notifications.splice(index, 1);
            }
        }
    }
</script>

<style scoped>

</style>