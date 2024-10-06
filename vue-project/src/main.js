import { createApp } from 'vue';
import App from './App.vue'; // 引入根组件
import router from './router'; // 引入路由配置
import ElementPlus from 'element-plus'; // 引入 Element Plus
import 'element-plus/dist/index.css'; // 引入 Element Plus 样式
import axios from 'axios';

// 配置 Axios 拦截器
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers['token'] = token;
  }
  return config;
}, error => {
  return Promise.reject(error);
});

// 创建并挂载 Vue 应用
const app = createApp(App);
app.use(ElementPlus);
app.use(router);
app.mount('#app');
