<template>
    <v-dialog v-model="showDialog" width="400">
        <v-card>
            <v-card-title class="headline" primary-title>
                Delete?
            </v-card-title>
            <v-card-text>
                {{deletionText}}
            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn flat @click="cancel()">No</v-btn>
                <v-btn color="error" flat @click="confirmDeletion()">Yes</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    export default {
        props: {
            identifier: {
                type: String,
                default: () => {""}
            },
            showDialog: {
                type: Boolean,
                default: () => (false)
            }
        },   
        computed: {
            deletionText: function() {
                return "Do you really want to delete " + this.identifier + "?"
            }
        },
        methods: {
            confirmDeletion() {
                this.$emit("confirmed")
            },
            cancel() {
                this.$emit("canceled")
            }
        }
    }
</script>

<style scoped>

</style>