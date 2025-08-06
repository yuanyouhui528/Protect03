<template>
  <!-- 应用根组件，包含路由视图 -->
  <div id="app">
    <!-- 路由视图组件，用于渲染当前路由对应的页面组件 -->
    <router-view />
  </div>
</template>

<script setup lang="ts">
// 应用根组件
// 主要职责：提供路由视图容器，渲染当前路由对应的页面组件
</script>

<style>
/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
  background-color: #f5f5f5;
  color: #333;
}

#app {
  width: 100%;
  min-height: 100vh;
}

/* Element Plus 组件样式覆盖 */
.el-button {
  border-radius: 6px;
}

.el-input__wrapper {
  border-radius: 6px;
}

.el-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
</style>
