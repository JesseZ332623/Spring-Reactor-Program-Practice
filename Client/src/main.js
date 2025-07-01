import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

import './style.css'
import './assets/dark.css'

createApp(App).use(router)          // 使用路由配置
              .use(createPinia())   // 使用 Pinia 进行状态管理
              .mount('#app')        // 挂载本应用到一个 DOM 上
