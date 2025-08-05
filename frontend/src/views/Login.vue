<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="login-background">
      <div class="bg-shape shape-1"></div>
      <div class="bg-shape shape-2"></div>
      <div class="bg-shape shape-3"></div>
    </div>
    
    <!-- 登录表单 -->
    <div class="login-form-container">
      <div class="login-form">
        <!-- Logo和标题 -->
        <div class="login-header">
          <img src="/favicon.ico" alt="Logo" class="logo" />
          <h1 class="title">{{ appTitle }}</h1>
          <p class="subtitle">招商线索流通平台</p>
        </div>
        
        <!-- 登录表单 -->
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="form"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名/手机号/邮箱"
              size="large"
              :prefix-icon="User"
              clearable
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
              show-password
              clearable
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          
          <!-- 验证码 -->
          <el-form-item prop="captcha" v-if="showCaptcha">
            <div class="captcha-container">
              <el-input
                v-model="loginForm.captcha"
                placeholder="请输入验证码"
                size="large"
                :prefix-icon="Key"
                clearable
                style="flex: 1; margin-right: 12px;"
              />
              <div class="captcha-image" @click="refreshCaptcha">
                <img :src="captchaUrl" alt="验证码" />
              </div>
            </div>
          </el-form-item>
          
          <!-- 记住密码和忘记密码 -->
          <div class="form-options">
            <el-checkbox v-model="loginForm.rememberMe">
              记住密码
            </el-checkbox>
            <el-link type="primary" @click="showForgotPassword">
              忘记密码？
            </el-link>
          </div>
          
          <!-- 登录按钮 -->
          <el-button
            type="primary"
            size="large"
            class="login-button"
            :loading="loading"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
          
          <!-- 注册链接 -->
          <div class="register-link">
            <span>还没有账号？</span>
            <el-link type="primary" @click="showRegister">
              立即注册
            </el-link>
          </div>
        </el-form>
        
        <!-- 第三方登录 -->
        <div class="social-login">
          <el-divider>其他登录方式</el-divider>
          <div class="social-buttons">
            <el-button circle :icon="Message" @click="loginWithSMS" />
            <el-button circle :icon="ChatDotRound" @click="loginWithWechat" />
            <el-button circle :icon="Promotion" @click="loginWithDingTalk" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import {
  User,
  Lock,
  Key,
  Message,
  ChatDotRound,
  Promotion
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

// 路由
const router = useRouter()
const route = useRoute()

// 用户状态管理
const userStore = useUserStore()

// 应用配置
const appTitle = import.meta.env.VITE_APP_TITLE

// 表单引用
const loginFormRef = ref<FormInstance>()

// 登录状态
const loading = ref(false)
const showCaptcha = ref(false)
const captchaUrl = ref('')

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: '',
  captcha: '',
  rememberMe: false
})

// 表单验证规则
const loginRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度在 3 到 50 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 4, message: '验证码长度为 4 位', trigger: 'blur' }
  ]
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    // 表单验证
    await loginFormRef.value.validate()
    
    loading.value = true
    
    // 调用登录API
    const success = await userStore.login({
      username: loginForm.username,
      password: loginForm.password,
      captcha: showCaptcha.value ? loginForm.captcha : undefined,
      rememberMe: loginForm.rememberMe
    })
    
    if (success) {
      // 记住用户名
      if (loginForm.rememberMe) {
        localStorage.setItem('remembered_username', loginForm.username)
      } else {
        localStorage.removeItem('remembered_username')
      }
      
      // 获取重定向地址
      const redirect = route.query.redirect as string || '/'
      
      // 跳转到目标页面
      router.push(redirect)
    } else {
      // 登录失败后显示验证码
      showCaptcha.value = true
      refreshCaptcha()
    }
    
  } catch (error) {
    console.error('登录失败:', error)
    // 登录失败后显示验证码
    showCaptcha.value = true
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}

// 刷新验证码
const refreshCaptcha = () => {
  captchaUrl.value = `/api/captcha?t=${Date.now()}`
}

// 忘记密码
const showForgotPassword = () => {
  ElMessageBox.prompt('请输入您的邮箱地址', '找回密码', {
    confirmButtonText: '发送重置邮件',
    cancelButtonText: '取消',
    inputPattern: /^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/,
    inputErrorMessage: '邮箱格式不正确'
  }).then(({ value }) => {
    ElMessage.success(`重置密码邮件已发送到 ${value}`)
  }).catch(() => {
    // 用户取消操作
  })
}

// 注册
const showRegister = () => {
  router.push('/register')
}

// 短信登录
const loginWithSMS = () => {
  ElMessage.info('短信登录功能开发中...')
}

// 微信登录
const loginWithWechat = () => {
  ElMessage.info('微信登录功能开发中...')
}

// 钉钉登录
const loginWithDingTalk = () => {
  ElMessage.info('钉钉登录功能开发中...')
}

// 组件挂载时的初始化
onMounted(() => {
  // 检查是否有记住的用户名
  const rememberedUsername = localStorage.getItem('remembered_username')
  if (rememberedUsername) {
    loginForm.username = rememberedUsername
    loginForm.rememberMe = true
  }
  
  // 如果已经登录，直接跳转到首页
  if (userStore.isLoggedIn) {
    const redirect = route.query.redirect as string || '/'
    router.push(redirect)
  }
})
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.login-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
}

.bg-shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 200px;
  height: 200px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: 10%;
  animation-delay: 2s;
}

.shape-3 {
  width: 100px;
  height: 100px;
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}

.login-form-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 400px;
  padding: 20px;
}

.login-form {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo {
  width: 64px;
  height: 64px;
  margin-bottom: 16px;
}

.title {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}

.subtitle {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.form {
  margin-bottom: 24px;
}

.captcha-container {
  display: flex;
  align-items: center;
}

.captcha-image {
  width: 100px;
  height: 40px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  overflow: hidden;
}

.captcha-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
}

.register-link {
  text-align: center;
  font-size: 14px;
  color: #909399;
}

.social-login {
  margin-top: 24px;
}

.social-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 16px;
}

:deep(.el-divider__text) {
  background-color: rgba(255, 255, 255, 0.95);
  color: #909399;
  font-size: 12px;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
}

:deep(.el-button) {
  border-radius: 8px;
}
</style>