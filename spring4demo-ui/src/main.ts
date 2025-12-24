import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import '@/styles/index.scss'
import App from './App.vue'
import router from './router'
import { setupI18n } from './locales/i18n'
import { setupPermission } from './permission'
import './permission'

const app = createApp(App)

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 全局错误处理
app.config.errorHandler = (err, vm, info) => {
  console.error('Global error:', err)
  console.error('Error Info:', info)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus)
app.use(setupI18n())
app.use(setupPermission())

app.mount('#app')