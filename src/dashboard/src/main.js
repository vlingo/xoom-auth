import "@babel/polyfill"
import Vue from "vue"
import "./plugins/vuetify"
import App from "./App.vue"
import router from "./router"
import store from "./store/store"

import upperFirst from "lodash/upperFirst"
import camelCase from "lodash/camelCase"

// register all components by default
const requireComponent = require.context(
    // The relative path of the components folder
    "./components",
    // Whether or not to look in subfolders
    false,
    // The regular expression used to match base component filenames
    /[A-Z]\w+\.(vue|js)$/
)
requireComponent.keys().forEach(fileName => {

    // Get component config
    const componentConfig = requireComponent(fileName)

    // Get PascalCase name of component
    const componentName = upperFirst(
        camelCase(
            // Strip the leading `./` and extension from the filename
            fileName.replace(/^\.\/(.*)\.\w+$/, "$1")
        )
    )

    // Register component globally
    Vue.component(
        componentName,
        // Look for the component options on `.default`, which will
        // exist if the component was exported with `export default`,
        // otherwise fall back to module"s root.
        componentConfig.default || componentConfig
    )
})

Vue.config.productionTip = false

new Vue({
    router,
    store,
    render: h => h(App)
}).$mount("#app")
