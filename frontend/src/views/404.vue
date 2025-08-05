<template>
  <div class="error-page">
    <div class="error-content">
      <!-- 404图标 -->
      <div class="error-icon">
        <el-icon :size="120" color="#409EFF">
          <WarningFilled />
        </el-icon>
      </div>
      
      <!-- 错误信息 -->
      <div class="error-info">
        <h1 class="error-code">404</h1>
        <h2 class="error-title">页面不存在</h2>
        <p class="error-description">
          抱歉，您访问的页面不存在或已被删除。
        </p>
      </div>
      
      <!-- 操作按钮 -->
      <div class="error-actions">
        <el-button type="primary" size="large" @click="goHome">
          <el-icon><HomeFilled /></el-icon>
          返回首页
        </el-button>
        <el-button size="large" @click="goBack">
          <el-icon><Back /></el-icon>
          返回上页
        </el-button>
      </div>
      
      <!-- 建议链接 -->
      <div class="error-suggestions">
        <h3>您可能想要访问：</h3>
        <ul class="suggestion-list">
          <li>
            <el-link type="primary" @click="$router.push('/dashboard')">
              数据看板
            </el-link>
          </li>
          <li>
            <el-link type="primary" @click="$router.push('/leads/list')">
              线索列表
            </el-link>
          </li>
          <li>
            <el-link type="primary" @click="$router.push('/exchange/market')">
              交换市场
            </el-link>
          </li>
          <li>
            <el-link type="primary" @click="$router.push('/analytics/personal')">
              数据分析
            </el-link>
          </li>
        </ul>
      </div>
    </div>
    
    <!-- 背景装饰 -->
    <div class="error-background">
      <div class="bg-circle circle-1"></div>
      <div class="bg-circle circle-2"></div>
      <div class="bg-circle circle-3"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { WarningFilled, HomeFilled, Back } from '@element-plus/icons-vue'

const router = useRouter()

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
</script>

<style scoped>
.error-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  position: relative;
  overflow: hidden;
  padding: 20px;
}

.error-content {
  text-align: center;
  position: relative;
  z-index: 1;
  max-width: 600px;
  width: 100%;
}

.error-icon {
  margin-bottom: 32px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  60% {
    transform: translateY(-5px);
  }
}

.error-info {
  margin-bottom: 40px;
}

.error-code {
  font-size: 120px;
  font-weight: 700;
  color: #409EFF;
  margin: 0;
  line-height: 1;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.error-title {
  font-size: 32px;
  font-weight: 600;
  color: #303133;
  margin: 16px 0 8px 0;
}

.error-description {
  font-size: 16px;
  color: #606266;
  margin: 0;
  line-height: 1.6;
}

.error-actions {
  margin-bottom: 40px;
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
}

.error-suggestions {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.error-suggestions h3 {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px 0;
}

.suggestion-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 12px;
}

.suggestion-list li {
  text-align: center;
}

.error-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(64, 158, 255, 0.1);
  animation: float 8s ease-in-out infinite;
}

.circle-1 {
  width: 200px;
  height: 200px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.circle-2 {
  width: 150px;
  height: 150px;
  top: 50%;
  right: 15%;
  animation-delay: 2s;
}

.circle-3 {
  width: 100px;
  height: 100px;
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  33% {
    transform: translateY(-20px) rotate(120deg);
  }
  66% {
    transform: translateY(10px) rotate(240deg);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .error-code {
    font-size: 80px;
  }
  
  .error-title {
    font-size: 24px;
  }
  
  .error-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .suggestion-list {
    grid-template-columns: 1fr;
  }
}
</style>