import { createApp } from 'vue'
import { Quasar } from 'quasar'
import 'quasar/dist/quasar.css'

import App from './App.vue'
import { registerAuthBootstrap } from './bootstrap/auth'
import { pinia } from './pinia'
import { router } from './router'

const app = createApp(App)

app.use(pinia)
app.use(router)
app.use(Quasar, {})

registerAuthBootstrap(router)

app.mount('#app')