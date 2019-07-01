import Vue from "vue"
import Router from "vue-router"
import RegisterUser from "./views/RegisterUser"
import DummyView from "./views/DummyView"
import SignOn from "./views/SignOn"
import ListUsers from "./views/ListUsers"
import ListGroups from "./views/ListGroups"
import ListPermissions from "./views/ListPermissions"
import ListRoles from "./views/ListRoles" 

Vue.use(Router)

export default new Router({
    routes: [
        {
            path: "/",
            name: "home",
            component: ListUsers
        },
        {
            path: "/registerUser",
            name: "registerUser",
            component: RegisterUser
        },
        {
            path: "/listUsers",
            name: "listUsers",
            component: ListUsers
        },
        {
            path: "/listGroups",
            name: "listGroups",
            component: ListGroups
        },
        {
            path: "/listPermissions",
            name: "listPermissions",
            component: ListPermissions
        },
        {
            path: "/listRoles",
            name: "listRoles",
            component: ListRoles
        },
        {
            path: "/dummy",
            name: "dummy",
            component: DummyView
        },
        {
            path: "/signOn",
            name: "signOn",
            component: SignOn
        }
    ]
})
