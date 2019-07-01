<template>
    <v-card fluid width="400" id="create-or-edit-group">
        <v-card-title class="headline teal darken-4 white--text" primary-title>{{title}}</v-card-title>

        <v-card-text>
            <v-form v-model="valid">
                <name-description-input :mode="mode" :value="group"></name-description-input>
            </v-form>
        </v-card-text>
        <v-divider></v-divider>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn flat @click="cancel()">Cancel</v-btn>
            <v-btn color="accent" flat :disabled="!valid" @click="save()">Save</v-btn>
        </v-card-actions>

    </v-card>
</template>

<script>
    import {ADD_GROUP, UPDATE_GROUP} from "../store/group.module";

    export default {
        props: {
            group: {
                type: Object,
                default: () => ({})
            },
            mode: {
                type: String
            }
        },
        data: () => ({
            valid: false
        }),
        computed: {
          title: function() {
              if (this.mode === "edit") {
                  return "Edit Group"
              } else {
                  return "Create Group"
              }
          }
        },
        methods: {
            save() {

                if (this.mode === "edit") {
                    this.$store.dispatch(UPDATE_GROUP, this.group)
                } else {
                    this.$store.dispatch(ADD_GROUP, this.group)
                }

                this.$emit("saved", this.group)
            },
            cancel() {
                this.$emit("canceled")
            }
        }
    }
</script>
