<template>
  <div class="error-container">
    <div class="error-content">
      <!-- 错误图标 -->
      <div class="error-icon">
        <el-icon :size="120" color="#f56c6c">
          <WarningFilled />
        </el-icon>
      </div>
      
      <!-- 错误信息 -->
      <div class="error-info">
        <h1 class="error-code">403</h1>
        <h2 class="error-title">访问被拒绝</h2>
        <p class="error-description">
          抱歉，您没有权限访问此页面。<br>
          请联系管理员获取相应权限，或返回首页继续浏览。
        </p>
      </div>
      
      <!-- 操作按钮 -->
      <div class="error-actions">
        <el-button type="primary" @click="goHome">
          <el-icon><HomeFilled /></el-icon>
          返回首页
        </el-button>
        <el-button @click="goBack">
          <el-icon><Back /></el-icon>
          返回上页
        </el-button>
        <el-button @click="contactAdmin">
          <el-icon><Service /></el-icon>
          联系管理员
        </el-button>
      </div>
      
      <!-- 权限说明 -->
      <div class="permission-info">
        <el-card class="info-card">
          <template #header>
            <div class="card-header">
              <el-icon><InfoFilled /></el-icon>
              <span>权限说明</span>
            </div>
          </template>
          <div class="permission-list">
            <p><strong>可能的原因：</strong></p>
            <ul>
              <li>您的账户权限不足</li>
              <li>该功能需要特定角色权限</li>
              <li>您的账户已被限制访问</li>
              <li>系统正在维护中</li>
            </ul>
            <p><strong>解决方案：</strong></p>
            <ul>
              <li>联系系统管理员申请权限</li>
              <li>检查您的账户状态</li>
              <li>稍后重试访问</li>
            </ul>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  WarningFilled,
  HomeFilled,
  Back,
  Service,
  InfoFilled
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

// 路由
const router = useRouter()

// 用户状态管理
const userStore = useUserStore()

// 返回首页
const goHome = () => {
  router.push('/')
}

// 返回上一页
const goBack = () => {
  if (window.history.length > 1) {
    router.go(-1)
  } else {
    router.push('/')
  }
}

// 联系管理员
const contactAdmin = () => {
  ElMessage.info('请通过以下方式联系管理员：\n邮箱：admin@example.com\n电话：400-123-4567')
}
</script>

<style scoped>
.error-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}

.error-content {
  text-align: center;
  max-width: 600px;
  width: 100%;
}

.error-icon {
  margin-bottom: 30px;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
  100% {
    transform: scale(1);
  }
}

.error-info {
  margin-bottom: 40px;
}

.error-code {
  font-size: 72px;
  font-weight: bold;
  color: #f56c6c;
  margin: 0 0 16px 0;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.error-title {
  font-size: 32px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px 0;
}

.error-description {
  font-size: 16px;
  color: #606266;
  line-height: 1.6;
  margin: 0;
}

.error-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-bottom: 40px;
  flex-wrap: wrap;
}

.error-actions .el-button {
  padding: 12px 24px;
  font-size: 14px;
  border-radius: 8px;
}

.permission-info {
  text-align: left;
}

.info-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #303133;
}

.permission-list {
  color: #606266;
  line-height: 1.6;
}

.permission-list p {
  margin: 0 0 12px 0;
}

.permission-list ul {
  margin: 0 0 20px 0;
  padding-left: 20px;
}

.permission-list li {
  margin-bottom: 8px;
}

.permission-list strong {
  color: #303133;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .error-code {
    font-size: 56px;
  }
  
  .error-title {
    font-size: 24px;
  }
  
  .error-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .error-actions .el-button {
    width: 200px;
  }
}
</style>