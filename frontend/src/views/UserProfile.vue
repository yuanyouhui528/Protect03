<template>
  <div class="user-profile-container">
    <div class="profile-header">
      <h1>个人设置</h1>
      <p>管理您的个人信息和账户设置</p>
    </div>
    
    <div class="profile-content">
      <el-row :gutter="24">
        <!-- 左侧：头像和基本信息 -->
        <el-col :span="8">
          <el-card class="profile-card">
            <div class="avatar-section">
              <div class="avatar-container">
                <el-avatar
                  :size="120"
                  :src="userInfo?.avatar"
                  class="user-avatar"
                >
                  <el-icon><Avatar /></el-icon>
                </el-avatar>
                <div class="avatar-overlay" @click="showAvatarUpload">
                  <el-icon><Camera /></el-icon>
                  <span>更换头像</span>
                </div>
              </div>
              
              <div class="user-basic-info">
                <h3>{{ userInfo?.realName || userInfo?.username }}</h3>
                <p class="user-role">{{ getRoleText() }}</p>
                <p class="user-status">
                  <el-tag :type="userInfo?.status === 1 ? 'success' : 'danger'" size="small">
                    {{ userInfo?.status === 1 ? '正常' : '禁用' }}
                  </el-tag>
                </p>
              </div>
            </div>
          </el-card>
          
          <!-- 账户统计 -->
          <el-card class="stats-card">
            <template #header>
              <div class="card-header">
                <el-icon><DataAnalysis /></el-icon>
                <span>账户统计</span>
              </div>
            </template>
            <div class="stats-content">
              <div class="stat-item">
                <div class="stat-value">{{ accountStats.loginCount }}</div>
                <div class="stat-label">登录次数</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ accountStats.lastLoginDays }}</div>
                <div class="stat-label">天前登录</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ accountStats.createDays }}</div>
                <div class="stat-label">注册天数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <!-- 右侧：详细信息表单 -->
        <el-col :span="16">
          <el-card class="form-card">
            <template #header>
              <div class="card-header">
                <el-icon><User /></el-icon>
                <span>基本信息</span>
              </div>
            </template>
            
            <el-form
              ref="profileFormRef"
              :model="profileForm"
              :rules="profileRules"
              label-width="100px"
              class="profile-form"
            >
              <el-form-item label="用户名" prop="username">
                <el-input
                  v-model="profileForm.username"
                  placeholder="请输入用户名"
                  :disabled="true"
                />
                <div class="form-tip">用户名不可修改</div>
              </el-form-item>
              
              <el-form-item label="真实姓名" prop="realName">
                <el-input
                  v-model="profileForm.realName"
                  placeholder="请输入真实姓名"
                  clearable
                />
              </el-form-item>
              
              <el-form-item label="手机号" prop="phone">
                <el-input
                  v-model="profileForm.phone"
                  placeholder="请输入手机号"
                  :disabled="true"
                />
                <div class="form-tip">手机号不可修改，如需更换请联系管理员</div>
              </el-form-item>
              
              <el-form-item label="邮箱" prop="email">
                <el-input
                  v-model="profileForm.email"
                  placeholder="请输入邮箱地址"
                  clearable
                />
              </el-form-item>
              
              <el-form-item label="注册时间">
                <el-input
                  :value="formatDate(userInfo?.createTime)"
                  :disabled="true"
                />
              </el-form-item>
              
              <el-form-item>
                <el-button type="primary" @click="updateProfile" :loading="updateLoading">
                  保存修改
                </el-button>
                <el-button @click="resetForm">重置</el-button>
              </el-form-item>
            </el-form>
          </el-card>
          
          <!-- 密码修改 -->
          <el-card class="form-card">
            <template #header>
              <div class="card-header">
                <el-icon><Lock /></el-icon>
                <span>修改密码</span>
              </div>
            </template>
            
            <el-form
              ref="passwordFormRef"
              :model="passwordForm"
              :rules="passwordRules"
              label-width="100px"
              class="password-form"
            >
              <el-form-item label="当前密码" prop="oldPassword">
                <el-input
                  v-model="passwordForm.oldPassword"
                  type="password"
                  placeholder="请输入当前密码"
                  show-password
                  clearable
                />
              </el-form-item>
              
              <el-form-item label="新密码" prop="newPassword">
                <el-input
                  v-model="passwordForm.newPassword"
                  type="password"
                  placeholder="请输入新密码"
                  show-password
                  clearable
                />
              </el-form-item>
              
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input
                  v-model="passwordForm.confirmPassword"
                  type="password"
                  placeholder="请确认新密码"
                  show-password
                  clearable
                />
              </el-form-item>
              
              <el-form-item>
                <el-button type="primary" @click="changePassword" :loading="passwordLoading">
                  修改密码
                </el-button>
                <el-button @click="resetPasswordForm">重置</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <!-- 头像上传对话框 -->
    <el-dialog
      v-model="avatarDialogVisible"
      title="更换头像"
      width="400px"
      :before-close="handleAvatarDialogClose"
    >
      <div class="avatar-upload-container">
        <el-upload
          ref="avatarUploadRef"
          class="avatar-uploader"
          :action="uploadAction"
          :headers="uploadHeaders"
          :show-file-list="false"
          :on-success="handleAvatarSuccess"
          :on-error="handleAvatarError"
          :before-upload="beforeAvatarUpload"
          accept="image/*"
        >
          <img v-if="previewAvatar" :src="previewAvatar" class="avatar-preview" />
          <div v-else class="avatar-upload-placeholder">
            <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
            <div class="upload-text">点击上传头像</div>
          </div>
        </el-upload>
        
        <div class="upload-tips">
          <p>• 支持 JPG、PNG 格式</p>
          <p>• 文件大小不超过 2MB</p>
          <p>• 建议尺寸 200x200 像素</p>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="avatarDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmAvatarUpload" :loading="avatarUploading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules, UploadProps } from 'element-plus'
import {
  User,
  Lock,
  Avatar,
  Camera,
  DataAnalysis,
  Plus
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { uploadAvatarApi, changePasswordApi } from '@/modules/user-management/api'

// 用户状态管理
const userStore = useUserStore()

// 表单引用
const profileFormRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()
const avatarUploadRef = ref()

// 加载状态
const updateLoading = ref(false)
const passwordLoading = ref(false)
const avatarUploading = ref(false)

// 头像上传
const avatarDialogVisible = ref(false)
const previewAvatar = ref('')
const uploadAction = '/api/user/avatar'
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${userStore.accessToken}`
}))

// 用户信息
const userInfo = computed(() => userStore.userInfo)

// 个人信息表单
const profileForm = reactive({
  username: '',
  realName: '',
  phone: '',
  email: ''
})

// 密码修改表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 账户统计数据
const accountStats = reactive({
  loginCount: 0,
  lastLoginDays: 0,
  createDays: 0
})

// 表单验证规则
const profileRules: FormRules = {
  realName: [
    { max: 20, message: '真实姓名不能超过20个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const passwordRules: FormRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' },
    { pattern: /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]+$/, message: '密码必须包含字母和数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 获取角色文本
const getRoleText = () => {
  const roles = userInfo.value?.roles || []
  if (roles.includes('admin')) return '管理员'
  if (roles.includes('manager')) return '经理'
  return '普通用户'
}

// 格式化日期
const formatDate = (dateString?: string) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleDateString('zh-CN')
}

// 计算账户统计
const calculateStats = () => {
  if (userInfo.value?.createTime) {
    const createDate = new Date(userInfo.value.createTime)
    const now = new Date()
    accountStats.createDays = Math.floor((now.getTime() - createDate.getTime()) / (1000 * 60 * 60 * 24))
  }
  
  // 这里可以调用API获取真实的统计数据
  accountStats.loginCount = 156
  accountStats.lastLoginDays = 1
}

// 初始化表单数据
const initFormData = () => {
  if (userInfo.value) {
    profileForm.username = userInfo.value.username
    profileForm.realName = userInfo.value.realName || ''
    profileForm.phone = userInfo.value.phone
    profileForm.email = userInfo.value.email || ''
  }
}

// 更新个人信息
const updateProfile = async () => {
  if (!profileFormRef.value) return
  
  try {
    await profileFormRef.value.validate()
    
    updateLoading.value = true
    
    const success = await userStore.updateUserInfo({
      realName: profileForm.realName,
      email: profileForm.email
    })
    
    if (success) {
      ElMessage.success('个人信息更新成功')
    }
  } catch (error) {
    console.error('更新个人信息失败:', error)
  } finally {
    updateLoading.value = false
  }
}

// 重置表单
const resetForm = () => {
  initFormData()
}

// 修改密码
const changePassword = async () => {
  if (!passwordFormRef.value) return
  
  try {
    await passwordFormRef.value.validate()
    
    passwordLoading.value = true
    
    await changePasswordApi(passwordForm.oldPassword, passwordForm.newPassword)
    
    ElMessage.success('密码修改成功，请重新登录')
    
    // 清空表单
    resetPasswordForm()
    
    // 延迟登出
    setTimeout(() => {
      userStore.logout()
    }, 1500)
    
  } catch (error) {
    console.error('修改密码失败:', error)
  } finally {
    passwordLoading.value = false
  }
}

// 重置密码表单
const resetPasswordForm = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.clearValidate()
}

// 显示头像上传对话框
const showAvatarUpload = () => {
  previewAvatar.value = userInfo.value?.avatar || ''
  avatarDialogVisible.value = true
}

// 关闭头像上传对话框
const handleAvatarDialogClose = () => {
  previewAvatar.value = ''
  avatarDialogVisible.value = false
}

// 头像上传前验证
const beforeAvatarUpload: UploadProps['beforeUpload'] = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  
  // 预览图片
  const reader = new FileReader()
  reader.onload = (e) => {
    previewAvatar.value = e.target?.result as string
  }
  reader.readAsDataURL(file)
  
  return false // 阻止自动上传
}

// 头像上传成功
const handleAvatarSuccess = (response: any) => {
  if (response.avatarUrl) {
    userStore.updateUserInfo({ avatar: response.avatarUrl })
    ElMessage.success('头像更新成功')
    avatarDialogVisible.value = false
  }
}

// 头像上传失败
const handleAvatarError = () => {
  ElMessage.error('头像上传失败，请重试')
}

// 确认头像上传
const confirmAvatarUpload = () => {
  const fileInput = avatarUploadRef.value?.$el.querySelector('input[type="file"]')
  const file = fileInput?.files?.[0]
  
  if (!file) {
    ElMessage.warning('请选择要上传的头像')
    return
  }
  
  avatarUploading.value = true
  
  // 手动上传
  uploadAvatarApi(file)
    .then((response) => {
      handleAvatarSuccess(response)
    })
    .catch((error) => {
      console.error('头像上传失败:', error)
      handleAvatarError()
    })
    .finally(() => {
      avatarUploading.value = false
    })
}

// 组件挂载时初始化
onMounted(() => {
  initFormData()
  calculateStats()
})
</script>

<style scoped>
.user-profile-container {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.profile-header {
  margin-bottom: 24px;
}

.profile-header h1 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.profile-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.profile-card {
  margin-bottom: 24px;
}

.avatar-section {
  text-align: center;
  padding: 20px 0;
}

.avatar-container {
  position: relative;
  display: inline-block;
  margin-bottom: 20px;
}

.user-avatar {
  border: 4px solid #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  cursor: pointer;
  color: white;
  font-size: 12px;
}

.avatar-container:hover .avatar-overlay {
  opacity: 1;
}

.user-basic-info h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.user-role {
  margin: 0 0 8px 0;
  color: #909399;
  font-size: 14px;
}

.user-status {
  margin: 0;
}

.stats-card {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.stats-content {
  display: flex;
  justify-content: space-around;
  text-align: center;
}

.stat-item {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

.form-card {
  margin-bottom: 24px;
}

.profile-form,
.password-form {
  max-width: 500px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.avatar-upload-container {
  text-align: center;
}

.avatar-uploader {
  margin-bottom: 20px;
}

.avatar-preview {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #dcdfe6;
}

.avatar-upload-placeholder {
  width: 120px;
  height: 120px;
  border: 2px dashed #dcdfe6;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: border-color 0.3s;
}

.avatar-upload-placeholder:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 12px;
  color: #8c939d;
}

.upload-tips {
  text-align: left;
  color: #909399;
  font-size: 12px;
  line-height: 1.5;
}

.upload-tips p {
  margin: 4px 0;
}

:deep(.el-card__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
}

:deep(.el-card__body) {
  padding: 20px;
}
</style>