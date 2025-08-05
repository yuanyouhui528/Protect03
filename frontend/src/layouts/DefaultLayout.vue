<template>
  <div class="layout-container">
    <!-- 顶部导航栏 -->
    <el-header class="layout-header">
      <div class="header-content">
        <!-- Logo和标题 -->
        <div class="logo-section">
          <img src="/favicon.ico" alt="Logo" class="logo" />
          <h1 class="title">{{ appTitle }}</h1>
        </div>
        
        <!-- 导航菜单 -->
        <el-menu
          :default-active="activeIndex"
          class="header-menu"
          mode="horizontal"
          @select="handleMenuSelect"
        >
          <el-menu-item index="/dashboard">数据看板</el-menu-item>
          <el-menu-item index="/leads">线索管理</el-menu-item>
          <el-menu-item index="/exchange">交换中心</el-menu-item>
          <el-menu-item index="/analytics">数据分析</el-menu-item>
        </el-menu>
        
        <!-- 用户操作区 -->
        <div class="user-section">
          <!-- 通知中心 -->
          <el-badge :value="unreadCount" class="notification-badge">
            <el-button :icon="Bell" circle @click="showNotifications" />
          </el-badge>
          
          <!-- 用户头像和菜单 -->
          <el-dropdown @command="handleUserCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userInfo.avatar" />
              <span class="username">{{ userInfo.name }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="settings">系统设置</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>
    
    <!-- 主体内容区 -->
    <el-container class="main-container">
      <!-- 侧边栏 -->
      <el-aside :width="sidebarWidth" class="layout-sidebar">
        <el-menu
          :default-active="$route.path"
          class="sidebar-menu"
          :collapse="isCollapse"
          @select="handleSidebarSelect"
        >
          <!-- 线索管理 -->
          <el-sub-menu index="leads">
            <template #title>
              <el-icon><Document /></el-icon>
              <span>线索管理</span>
            </template>
            <el-menu-item index="/leads/list">线索列表</el-menu-item>
            <el-menu-item index="/leads/create">发布线索</el-menu-item>
            <el-menu-item index="/leads/favorites">我的收藏</el-menu-item>
          </el-sub-menu>
          
          <!-- 交换中心 -->
          <el-sub-menu index="exchange">
            <template #title>
              <el-icon><Switch /></el-icon>
              <span>交换中心</span>
            </template>
            <el-menu-item index="/exchange/market">交换市场</el-menu-item>
            <el-menu-item index="/exchange/history">交换记录</el-menu-item>
            <el-menu-item index="/exchange/rules">交换规则</el-menu-item>
          </el-sub-menu>
          
          <!-- 数据分析 -->
          <el-sub-menu index="analytics">
            <template #title>
              <el-icon><DataAnalysis /></el-icon>
              <span>数据分析</span>
            </template>
            <el-menu-item index="/analytics/personal">个人看板</el-menu-item>
            <el-menu-item index="/analytics/system">系统看板</el-menu-item>
          </el-sub-menu>
        </el-menu>
        
        <!-- 侧边栏折叠按钮 -->
        <div class="sidebar-toggle">
          <el-button
            :icon="isCollapse ? Expand : Fold"
            @click="toggleSidebar"
            text
          />
        </div>
      </el-aside>
      
      <!-- 主内容区 -->
      <el-main class="layout-main">
        <!-- 面包屑导航 -->
        <el-breadcrumb class="breadcrumb" separator="/">
          <el-breadcrumb-item
            v-for="item in breadcrumbList"
            :key="item.path"
            :to="item.path"
          >
            {{ item.title }}
          </el-breadcrumb-item>
        </el-breadcrumb>
        
        <!-- 页面内容 -->
        <div class="page-content">
          <router-view />
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Bell,
  ArrowDown,
  Document,
  Switch,
  DataAnalysis,
  Expand,
  Fold
} from '@element-plus/icons-vue'

// 路由相关
const route = useRoute()
const router = useRouter()

// 应用配置
const appTitle = import.meta.env.VITE_APP_TITLE

// 侧边栏状态
const isCollapse = ref(false)
const sidebarWidth = computed(() => isCollapse.value ? '64px' : '200px')

// 用户信息
const userInfo = ref({
  name: '张三',
  avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
})

// 通知数量
const unreadCount = ref(5)

// 当前激活的菜单项
const activeIndex = computed(() => {
  const path = route.path
  if (path.startsWith('/leads')) return '/leads'
  if (path.startsWith('/exchange')) return '/exchange'
  if (path.startsWith('/analytics')) return '/analytics'
  return '/dashboard'
})

// 面包屑导航
const breadcrumbList = computed(() => {
  const matched = route.matched.filter(item => item.meta && item.meta.title)
  return matched.map(item => ({
    title: item.meta?.title as string,
    path: item.path
  }))
})

// 菜单选择处理
const handleMenuSelect = (index: string) => {
  router.push(index)
}

const handleSidebarSelect = (index: string) => {
  router.push(index)
}

// 侧边栏折叠切换
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

// 显示通知
const showNotifications = () => {
  ElMessage.info('通知功能开发中...')
}

// 用户操作处理
const handleUserCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      router.push('/settings')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 清除本地存储的用户信息
    localStorage.removeItem('access_token')
    localStorage.removeItem('user_info')
    
    // 跳转到登录页
    router.push('/login')
    ElMessage.success('退出登录成功')
  } catch {
    // 用户取消操作
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.layout-header {
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0;
  height: 60px;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 20px;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  width: 32px;
  height: 32px;
}

.title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-menu {
  flex: 1;
  margin: 0 40px;
  border-bottom: none;
}

.user-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.notification-badge {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.username {
  font-size: 14px;
  color: #303133;
}

.main-container {
  flex: 1;
  overflow: hidden;
}

.layout-sidebar {
  background: #fff;
  border-right: 1px solid #e4e7ed;
  transition: width 0.3s;
  position: relative;
}

.sidebar-menu {
  border-right: none;
  height: calc(100vh - 60px - 50px);
  overflow-y: auto;
}

.sidebar-toggle {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-top: 1px solid #e4e7ed;
}

.layout-main {
  background: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}

.breadcrumb {
  margin-bottom: 20px;
}

.page-content {
  background: #fff;
  border-radius: 4px;
  padding: 20px;
  min-height: calc(100vh - 60px - 40px - 40px - 20px);
}
</style>