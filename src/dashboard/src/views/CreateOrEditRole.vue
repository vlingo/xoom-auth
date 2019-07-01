<template>
    <v-card fluid width="400" id="create-or-edit-role">
        <v-card-title class="headline teal darken-4 white--text" primary-title>{{title}}</v-card-title>

        <v-card-text>
            <v-form v-model="valid">
                <name-description-input :mode="mode" :value="role"></name-description-input>
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
    import {CREATE_ROLE, UPDATE_ROLE} from "../store/role.module";

    export default {
        props: {
            role: {
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
                  return "Edit Role"
              } else {
                  return "Add Role"
              }
          }
        },
        methods: {
            save() {

                if (this.mode === "edit") {
                    this.$store.dispatch(UPDATE_ROLE, this.role)
                } else {
                    this.$store.dispatch(CREATE_ROLE, this.role)
                }

                this.$emit("saved", this.role)
            },
            cancel() {
                this.$emit("canceled")
            }
        }
    }
</script>
