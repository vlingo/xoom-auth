<template>
    <v-card>
        <v-card-title class="headline teal darken-4 white--text" primary-title>Edit Members for <b> {{group.name}}</b></v-card-title>
        <v-card-text style="height: 300px;">
            <v-switch :key="idx" @click="addChangedValue(user)" v-for="(user, idx) in users" v-model="user.isMember" :label="user.userName"
            ></v-switch>
        </v-card-text>

        <v-divider></v-divider>

        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn flat @click="cancel()">Cancel</v-btn>
            <v-btn color="accent" flat @click="save()">Save</v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>
    import {UPDATE_MEMBER_STATUS} from "../store/group.module"
    import {GET_ALL_USERS} from "../store/user.module";
    import _ from "lodash"

    export default {
        name: "EditGroupMembers",
        data: () => ({
            changedMembers: []
        }),
        props: {
            group: {
                type: Object,
                default: () => ({}),
                required: true
            }
        },
        methods: {
            addChangedValue(member) {
                // value was changed back, thus we have no change
                let index = this.changedMembers.indexOf(member)
                if( index <= -1){
                    this.changedMembers.splice(index, 1)
                }
                else {
                    this.changedMembers.push(member)
                }
            },
            save() {
                this.$store.dispatch(UPDATE_MEMBER_STATUS, {parentGroup: this.group, changedMembers: this.changedMembers})
                this.$emit("saved")
            },
            cancel() {
                this.$emit("canceled")
            }
        },
        computed: {
            users: function () {
                let allUsers = this.$store.getters[GET_ALL_USERS]

                let resultUsers = []
                _.forEach(allUsers, existingUser => {
                    existingUser.isMember = _.includes(this.group.groupMembers, existingUser.id)
                    resultUsers.push(existingUser)
                })

                return _.orderBy(resultUsers, ["userName"], ["asc"])
            }
        }
    }
</script>
