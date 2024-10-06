import { createRouter, createWebHistory } from 'vue-router';
import UserManagement from '../components/UserManagement.vue'; // 用户管理组件
import FileManager from '../components/FileManager.vue'; // 文件管理组件

const routes = [
  {
    path: '/',
    name: 'Home',
    redirect: '/jothubUserManagement', // 默认重定向到用户管理
  },
  {
    path: '/jothubUserManagement',
    name: 'UserManagement',
    component: UserManagement, // 用户管理路由
  },
  {
    path: '/explorer',
    name: 'FileManager',
    component: FileManager, // 文件管理路由
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
