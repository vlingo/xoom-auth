<template>
    <v-card>
        <v-card-title class="headline teal darken-4 white--text" primary-title>Edit Inner Groups for <b> {{group.name}}</b></v-card-title>
{{group.text}}
        <v-card-text style="height: 300px;">
            <v-switch :key="idx" @click="addChangedValue(innerGroup)" v-for="(innerGroup, idx) in innerGroups" v-model="innerGroup.isInnerGroup" :label="innerGroup.name"
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
    import {GET_ALL_GROUPS, UPDATE_INNER_GROUP_STATUS} from "../store/group.module"
    import _ from "lodash"

    export default {
        name: "EditInnerGroups",
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
            addChangedValue(innerGroup) {
                // value was changed back, thus we have no change
                let index = this.changedMembers.indexOf(innerGroup)
                if( index <= -1){
                    this.changedMembers.splice(index, 1)
                }
                else {
                    this.changedMembers.push(innerGroup)
                }
            },
            save() {
                this.$store.dispatch(UPDATE_INNER_GROUP_STATUS, {parentGroup: this.group, changedInnerGroups: this.changedMembers})
                this.$emit("saved")
            },
            cancel() {
                this.$emit("canceled")
            }
        },
        computed: {
            innerGroups: function () {
                let groups = this.$store.getters[GET_ALL_GROUPS]

                let resultGroups = []
                _.forEach(groups, existingGroup => {

                    if (existingGroup.id === this.group.id) {
                        return
                    }

                    // TODO: That is not enought to protect us from circular references!
                    // TODO: We have to check if any of the children is a parent of the current group...
                    if (_.includes(existingGroup.innerGroups, this.group.id)) {
                        return
                    }

                    existingGroup.isInnerGroup = _.includes(this.group.innerGroups, existingGroup.id)

                    resultGroups.push(existingGroup)
                })

                return _.orderBy(resultGroups, ["name"], ["asc"])
            }
        }
    }
</script>
