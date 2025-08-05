<template>
  <div class="register-container">
    <!-- 背景装饰 -->
    <div class="register-background">
      <div class="bg-shape shape-1"></div>
      <div class="bg-shape shape-2"></div>
      <div class="bg-shape shape-3"></div>
    </div>
    
    <!-- 注册表单 -->
    <div class="register-form-container">
      <div class="register-form">
        <!-- Logo和标题 -->
        <div class="register-header">
          <img src="/favicon.ico" alt="Logo" class="logo" />
          <h1 class="title">用户注册</h1>
          <p class="subtitle">招商线索流通平台</p>
        </div>
        
        <!-- 注册表单 -->
        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          class="form"
          @submit.prevent="handleRegister"
        >
          <!-- 手机号 -->
          <el-form-item prop="phone">
            <el-input
              v-model="registerForm.phone"
              placeholder="请输入手机号"
              size="large"
              :prefix-icon="Iphone"
              clearable
              maxlength="11"
            />
          </el-form-item>
          
          <!-- 短信验证码 -->
          <el-form-item prop="smsCode">
            <div class="sms-container">
              <el-input
                v-model="registerForm.smsCode"
                placeholder="请输入短信验证码"
                size="large"
                :prefix-icon="Message"
                clearable
                maxlength="6"
                style="flex: 1; margin-right: 12px;"
              />
              <el-button
                size="large"
                :disabled="smsCountdown > 0 || !isPhoneValid"
                @click="sendSmsCode"
                style="width: 120px;"
              >
                {{ smsCountdown > 0 ? `${smsCountdown}s后重发` : '发送验证码' }}
              </el-button>
            </div>
          </el-form-item>
          
          <!-- 用户名 -->
          <el-form-item prop="username">
            <el-input
              v-model="registerForm.username"
              placeholder="请输入用户名"
              size="large"
              :prefix-icon="User"
              clearable
            />
          </el-form-item>
          
          <!-- 真实姓名 -->
          <el-form-item prop="realName">
            <el-input
              v-model="registerForm.realName"
              placeholder="请输入真实姓名（可选）"
              size="large"
              :prefix-icon="Avatar"
              clearable
            />
          </el-form-item>
          
          <!-- 密码 -->
          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
              show-password
              clearable
            />
          </el-form-item>
          
          <!-- 确认密码 -->
          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请确认密码"
              size="large"
              :prefix-icon="Lock"
              show-password
              clearable
              @keyup.enter="handleRegister"
            />
          </el-form-item>
          
          <!-- 用户协议 -->
          <el-form-item prop="agreement">
            <el-checkbox v-model="registerForm.agreement">
              我已阅读并同意
              <el-link type="primary" @click="showUserAgreement">
                《用户服务协议》
              </el-link>
              和
              <el-link type="primary" @click="showPrivacyPolicy">
                《隐私政策》
              </el-link>
            </el-checkbox>
          </el-form-item>
          
          <!-- 注册按钮 -->
          <el-button
            type="primary"
            size="large"
            class="register-button"
            :loading="loading"
            @click="handleRegister"
          >
            {{ loading ? '注册中...' : '立即注册' }}
          </el-button>
          
          <!-- 登录链接 -->
          <div class="login-link">
            <span>已有账号？</span>
            <el-link type="primary" @click="goToLogin">
              立即登录
            </el-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import {
  User,
  Lock,
  Iphone,
  Message,
  Avatar
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

// 路由
const router = useRouter()

// 用户状态管理
const userStore = useUserStore()

// 表单引用
const registerFormRef = ref<FormInstance>()

// 注册状态
const loading = ref(false)
const smsCountdown = ref(0)
let smsTimer: NodeJS.Timeout | null = null

// 注册表单数据
const registerForm = reactive({
  phone: '',
  smsCode: '',
  username: '',
  realName: '',
  password: '',
  confirmPassword: '',
  agreement: false
})

// 手机号格式验证
const isPhoneValid = computed(() => {
  const phoneRegex = /^1[3-9]\d{9}$/
  return phoneRegex.test(registerForm.phone)
})

// 自定义验证规则
const validatePhone = (rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请输入手机号'))
  } else if (!isPhoneValid.value) {
    callback(new Error('请输入正确的手机号格式'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error('请确认密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const validateAgreement = (rule: any, value: boolean, callback: any) => {
  if (!value) {
    callback(new Error('请阅读并同意用户协议'))
  } else {
    callback()
  }
}

// 表单验证规则
const registerRules: FormRules = {
  phone: [
    { validator: validatePhone, trigger: 'blur' }
  ],
  smsCode: [
    { required: true, message: '请输入短信验证码', trigger: 'blur' },
    { len: 6, message: '验证码长度为6位', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3到20个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/, message: '用户名只能包含字母、数字、下划线和中文', trigger: 'blur' }
  ],
  realName: [
    { max: 20, message: '真实姓名不能超过20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' },
    { pattern: /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]+$/, message: '密码必须包含字母和数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  agreement: [
    { validator: validateAgreement, trigger: 'change' }
  ]
}

// 发送短信验证码
const sendSmsCode = async () => {
  if (!isPhoneValid.value) {
    ElMessage.error('请输入正确的手机号')
    return
  }
  
  try {
    const success = await userStore.sendSmsCode(registerForm.phone)
    if (success) {
      // 开始倒计时
      smsCountdown.value = 60
      smsTimer = setInterval(() => {
        smsCountdown.value--
        if (smsCountdown.value <= 0) {
          clearInterval(smsTimer!)
          smsTimer = null
        }
      }, 1000)
    }
  } catch (error) {
    console.error('发送验证码失败:', error)
  }
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    // 表单验证
    await registerFormRef.value.validate()
    
    loading.value = true
    
    // 调用注册API
    const success = await userStore.register({
      phone: registerForm.phone,
      smsCode: registerForm.smsCode,
      username: registerForm.username,
      realName: registerForm.realName || undefined,
      password: registerForm.password,
      confirmPassword: registerForm.confirmPassword
    })
    
    if (success) {
      ElMessage.success('注册成功，请登录')
      // 跳转到登录页面
      router.push('/login')
    }
    
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}

// 跳转到登录页面
const goToLogin = () => {
  router.push('/login')
}

// 显示用户协议
const showUserAgreement = () => {
  ElMessageBox.alert(
    '这里是用户服务协议的内容...',
    '用户服务协议',
    {
      confirmButtonText: '我知道了',
      dangerouslyUseHTMLString: true
    }
  )
}

// 显示隐私政策
const showPrivacyPolicy = () => {
  ElMessageBox.alert(
    '这里是隐私政策的内容...',
    '隐私政策',
    {
      confirmButtonText: '我知道了',
      dangerouslyUseHTMLString: true
    }
  )
}

// 组件销毁时清理定时器
const cleanup = () => {
  if (smsTimer) {
    clearInterval(smsTimer)
    smsTimer = null
  }
}

// 监听组件卸载
import { onUnmounted } from 'vue'
onUnmounted(cleanup)
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.register-background {
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

.register-form-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 450px;
  padding: 20px;
}

.register-form {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  max-height: 90vh;
  overflow-y: auto;
}

.register-header {
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

.sms-container {
  display: flex;
  align-items: center;
}

.register-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
}

.login-link {
  text-align: center;
  font-size: 14px;
  color: #909399;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
}

:deep(.el-button) {
  border-radius: 8px;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-checkbox__label) {
  font-size: 14px;
  color: #606266;
}
</style>