<template>
  <div id="app">
    <router-view></router-view> <!-- 渲染当前路由组件 -->
  </div>
</template>

<script>
export default {
  name: 'App',
}
</script>

<style>
/* 可以添加全局样式 */
body {
  margin: 0;
  font-family: 'Arial', sans-serif;
}
</style>
