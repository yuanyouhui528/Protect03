<template>
  <div class="notification-settings-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <el-button type="text" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <div class="header-content">
          <h1 class="page-title">通知设置</h1>
          <p class="page-description">管理您的通知偏好和接收方式</p>
        </div>
      </div>
      <div class="header-right">
        <el-button type="primary" :loading="saving" @click="saveSettings">
          <el-icon><Check /></el-icon>
          保存设置
        </el-button>
      </div>
    </div>

    <!-- 设置内容 -->
    <div class="settings-content" v-loading="loading">
      <!-- 全局设置 -->
      <el-card class="settings-card">
        <template #header>
          <div class="card-header">
            <el-icon><Setting /></el-icon>
            <span>全局设置</span>
          </div>
        </template>
        
        <div class="setting-group">
          <div class="setting-item">
            <div class="setting-label">
              <span class="label-text">启用通知</span>
              <span class="label-desc">关闭后将不会收到任何通知</span>
            </div>
            <div class="setting-control">
              <el-switch
                v-model="globalSettings.enabled"
                size="large"
                @change="handleGlobalToggle"
              />
            </div>
          </div>
          
          <div class="setting-item" v-show="globalSettings.enabled">
            <div class="setting-label">
              <span class="label-text">免打扰时间</span>
              <span class="label-desc">在此时间段内不会收到通知提醒</span>
            </div>
            <div class="setting-control">
              <el-switch
                v-model="globalSettings.doNotDisturbEnabled"
                size="large"
              />
              <div v-if="globalSettings.doNotDisturbEnabled" class="time-range">
                <el-time-picker
                  v-model="globalSettings.doNotDisturbStart"
                  placeholder="开始时间"
                  format="HH:mm"
                  value-format="HH:mm"
                  style="width: 120px"
                />
                <span class="time-separator">至</span>
                <el-time-picker
                  v-model="globalSettings.doNotDisturbEnd"
                  placeholder="结束时间"
                  format="HH:mm"
                  value-format="HH:mm"
                  style="width: 120px"
                />
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 通知类型设置 -->
      <el-card class="settings-card">
        <template #header>
          <div class="card-header">
            <el-icon><Bell /></el-icon>
            <span>通知类型设置</span>
          </div>
        </template>
        
        <div class="notification-types">
          <div
            v-for="typeConfig in notificationTypeConfigs"
            :key="typeConfig.type"
            class="type-config"
          >
            <div class="type-header">
              <div class="type-info">
                <el-icon :color="typeConfig.color">
                  <component :is="typeConfig.icon" />
                </el-icon>
                <div class="type-text">
                  <span class="type-name">{{ typeConfig.name }}</span>
                  <span class="type-desc">{{ typeConfig.description }}</span>
                </div>
              </div>
              <div class="type-toggle">
                <el-switch
                  v-model="typeConfig.enabled"
                  size="large"
                  :disabled="!globalSettings.enabled"
                />
              </div>
            </div>
            
            <div v-if="typeConfig.enabled && globalSettings.enabled" class="type-channels">
              <div class="channels-label">接收方式：</div>
              <div class="channels-options">
                <el-checkbox
                  v-model="typeConfig.systemEnabled"
                  :disabled="!globalSettings.enabled"
                >
                  系统通知
                </el-checkbox>
                <el-checkbox
                  v-model="typeConfig.emailEnabled"
                  :disabled="!globalSettings.enabled"
                >
                  邮件通知
                </el-checkbox>
                <el-checkbox
                  v-model="typeConfig.smsEnabled"
                  :disabled="!globalSettings.enabled"
                >
                  短信通知
                </el-checkbox>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 高级设置 -->
      <el-card class="settings-card">
        <template #header>
          <div class="card-header">
            <el-icon><Tools /></el-icon>
            <span>高级设置</span>
          </div>
        </template>
        
        <div class="setting-group">
          <div class="setting-item">
            <div class="setting-label">
              <span class="label-text">声音提醒</span>
              <span class="label-desc">收到新通知时播放提示音</span>
            </div>
            <div class="setting-control">
              <el-switch
                v-model="advancedSettings.soundEnabled"
                size="large"
                :disabled="!globalSettings.enabled"
              />
            </div>
          </div>
          
          <div class="setting-item">
            <div class="setting-label">
              <span class="label-text">桌面通知</span>
              <span class="label-desc">在桌面显示通知弹窗</span>
            </div>
            <div class="setting-control">
              <el-switch
                v-model="advancedSettings.desktopEnabled"
                size="large"
                :disabled="!globalSettings.enabled"
                @change="handleDesktopToggle"
              />
            </div>
          </div>
          
          <div class="setting-item">
            <div class="setting-label">
              <span class="label-text">通知合并</span>
              <span class="label-desc">相同类型的通知将合并显示</span>
            </div>
            <div class="setting-control">
              <el-switch
                v-model="advancedSettings.groupEnabled"
                size="large"
                :disabled="!globalSettings.enabled"
              />
            </div>
          </div>
          
          <div class="setting-item">
            <div class="setting-label">
              <span class="label-text">自动清理</span>
              <span class="label-desc">自动清理超过指定天数的已读通知</span>
            </div>
            <div class="setting-control">
              <el-switch
                v-model="advancedSettings.autoCleanEnabled"
                size="large"
                :disabled="!globalSettings.enabled"
              />
              <div v-if="advancedSettings.autoCleanEnabled" class="auto-clean-days">
                <el-input-number
                  v-model="advancedSettings.autoCleanDays"
                  :min="1"
                  :max="365"
                  size="small"
                  style="width: 120px"
                />
                <span class="days-label">天</span>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 联系方式设置 -->
      <el-card class="settings-card">
        <template #header>
          <div class="card-header">
            <el-icon><Message /></el-icon>
            <span>联系方式</span>
          </div>
        </template>
        
        <div class="setting-group">
          <div class="setting-item">
            <div class="setting-label">
              <span class="label-text">邮箱地址</span>
              <span class="label-desc">用于接收邮件通知</span>
            </div>
            <div class="setting-control">
              <el-input
                v-model="contactSettings.email"
                placeholder="请输入邮箱地址"
                style="width: 300px"
                :disabled="!globalSettings.enabled"
              >
                <template #suffix>
                  <el-button
                    v-if="contactSettings.email && !contactSettings.emailVerified"
                    type="text"
                    size="small"
                    @click="verifyEmail"
                  >
                    验证
                  </el-button>
                  <el-icon v-else-if="contactSettings.emailVerified" color="#67C23A">
                    <CircleCheck />
                  </el-icon>
                </template>
              </el-input>
            </div>
          </div>
          
          <div class="setting-item">
            <div class="setting-label">
              <span class="label-text">手机号码</span>
              <span class="label-desc">用于接收短信通知</span>
            </div>
            <div class="setting-control">
              <el-input
                v-model="contactSettings.phone"
                placeholder="请输入手机号码"
                style="width: 300px"
                :disabled="!globalSettings.enabled"
              >
                <template #suffix>
                  <el-button
                    v-if="contactSettings.phone && !contactSettings.phoneVerified"
                    type="text"
                    size="small"
                    @click="verifyPhone"
                  >
                    验证
                  </el-button>
                  <el-icon v-else-if="contactSettings.phoneVerified" color="#67C23A">
                    <CircleCheck />
                  </el-icon>
                </template>
              </el-input>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft,
  Check,
  Setting,
  Bell,
  Tools,
  Message,
  CircleCheck,
  Warning,
  SuccessFilled,
  CircleClose,
  InfoFilled
} from '@element-plus/icons-vue'
import {
  NotificationAPI,
  type NotificationSettings,
  NotificationType,
  SendChannel
} from '@/api/notification'

// 路由
const router = useRouter()

// 响应式数据
const loading = ref(false)
const saving = ref(false)

// 全局设置
const globalSettings = reactive({
  enabled: true,
  doNotDisturbEnabled: false,
  doNotDisturbStart: '22:00',
  doNotDisturbEnd: '08:00'
})

// 高级设置
const advancedSettings = reactive({
  soundEnabled: true,
  desktopEnabled: false,
  groupEnabled: true,
  autoCleanEnabled: false,
  autoCleanDays: 30
})

// 联系方式设置
const contactSettings = reactive({
  email: '',
  emailVerified: false,
  phone: '',
  phoneVerified: false
})

// 通知类型配置
const notificationTypeConfigs = reactive([
  {
    type: NotificationType.EXCHANGE_APPLICATION,
    name: '交换申请',
    description: '有新的线索交换申请时通知',
    icon: Message,
    color: '#409EFF',
    enabled: true,
    systemEnabled: true,
    emailEnabled: true,
    smsEnabled: false
  },
  {
    type: NotificationType.EXCHANGE_APPROVAL,
    name: '交换通过',
    description: '线索交换申请被通过时通知',
    icon: SuccessFilled,
    color: '#67C23A',
    enabled: true,
    systemEnabled: true,
    emailEnabled: true,
    smsEnabled: true
  },
  {
    type: NotificationType.EXCHANGE_REJECTION,
    name: '交换拒绝',
    description: '线索交换申请被拒绝时通知',
    icon: CircleClose,
    color: '#F56C6C',
    enabled: true,
    systemEnabled: true,
    emailEnabled: true,
    smsEnabled: false
  },
  {
    type: NotificationType.EXCHANGE_COMPLETION,
    name: '交换完成',
    description: '线索交换完成时通知',
    icon: SuccessFilled,
    color: '#67C23A',
    enabled: true,
    systemEnabled: true,
    emailEnabled: true,
    smsEnabled: true
  },
  {
    type: NotificationType.SYSTEM_ANNOUNCEMENT,
    name: '系统公告',
    description: '系统发布重要公告时通知',
    icon: InfoFilled,
    color: '#909399',
    enabled: true,
    systemEnabled: true,
    emailEnabled: false,
    smsEnabled: false
  },
  {
    type: NotificationType.LEAD_EXPIRY_REMINDER,
    name: '线索到期提醒',
    description: '线索即将到期时提醒',
    icon: Warning,
    color: '#E6A23C',
    enabled: true,
    systemEnabled: true,
    emailEnabled: true,
    smsEnabled: false
  },
  {
    type: NotificationType.RATING_UPDATE,
    name: '评级更新',
    description: '用户评级发生变化时通知',
    icon: InfoFilled,
    color: '#409EFF',
    enabled: true,
    systemEnabled: true,
    emailEnabled: false,
    smsEnabled: false
  }
])

// 方法
const goBack = () => {
  router.back()
}

const loadSettings = async () => {
  try {
    loading.value = true
    const settings = await NotificationAPI.getNotificationSettings()
    
    // 更新全局设置
    globalSettings.enabled = settings.enabled
    globalSettings.doNotDisturbEnabled = settings.doNotDisturbEnabled || false
    globalSettings.doNotDisturbStart = settings.doNotDisturbStart || '22:00'
    globalSettings.doNotDisturbEnd = settings.doNotDisturbEnd || '08:00'
    
    // 更新高级设置
    advancedSettings.soundEnabled = settings.soundEnabled ?? true
    advancedSettings.desktopEnabled = settings.desktopEnabled ?? false
    advancedSettings.groupEnabled = settings.groupEnabled ?? true
    advancedSettings.autoCleanEnabled = settings.autoCleanEnabled ?? false
    advancedSettings.autoCleanDays = settings.autoCleanDays ?? 30
    
    // 更新联系方式
    contactSettings.email = settings.email || ''
    contactSettings.emailVerified = settings.emailVerified ?? false
    contactSettings.phone = settings.phone || ''
    contactSettings.phoneVerified = settings.phoneVerified ?? false
    
    // 更新通知类型配置
    if (settings.typeSettings) {
      notificationTypeConfigs.forEach(config => {
        const typeSetting = settings.typeSettings![config.type]
        if (typeSetting) {
          config.enabled = typeSetting.enabled
          config.systemEnabled = typeSetting.systemEnabled ?? true
          config.emailEnabled = typeSetting.emailEnabled ?? false
          config.smsEnabled = typeSetting.smsEnabled ?? false
        }
      })
    }
    
  } catch (error) {
    console.error('加载通知设置失败:', error)
    ElMessage.error('加载通知设置失败')
  } finally {
    loading.value = false
  }
}

const saveSettings = async () => {
  try {
    saving.value = true
    
    // 构建设置对象
    const settings: NotificationSettings = {
      enabled: globalSettings.enabled,
      doNotDisturbEnabled: globalSettings.doNotDisturbEnabled,
      doNotDisturbStart: globalSettings.doNotDisturbStart,
      doNotDisturbEnd: globalSettings.doNotDisturbEnd,
      soundEnabled: advancedSettings.soundEnabled,
      desktopEnabled: advancedSettings.desktopEnabled,
      groupEnabled: advancedSettings.groupEnabled,
      autoCleanEnabled: advancedSettings.autoCleanEnabled,
      autoCleanDays: advancedSettings.autoCleanDays,
      email: contactSettings.email,
      emailVerified: contactSettings.emailVerified,
      phone: contactSettings.phone,
      phoneVerified: contactSettings.phoneVerified,
      typeSettings: {}
    }
    
    // 添加通知类型设置
    notificationTypeConfigs.forEach(config => {
      settings.typeSettings![config.type] = {
        enabled: config.enabled,
        systemEnabled: config.systemEnabled,
        emailEnabled: config.emailEnabled,
        smsEnabled: config.smsEnabled
      }
    })
    
    await NotificationAPI.updateNotificationSettings(settings)
    ElMessage.success('设置保存成功')
    
  } catch (error) {
    console.error('保存通知设置失败:', error)
    ElMessage.error('保存通知设置失败')
  } finally {
    saving.value = false
  }
}

const handleGlobalToggle = (enabled: boolean) => {
  if (!enabled) {
    // 关闭全局通知时，禁用所有类型通知
    notificationTypeConfigs.forEach(config => {
      config.enabled = false
    })
  }
}

const handleDesktopToggle = async (enabled: boolean) => {
  if (enabled) {
    // 请求桌面通知权限
    if ('Notification' in window) {
      const permission = await Notification.requestPermission()
      if (permission !== 'granted') {
        advancedSettings.desktopEnabled = false
        ElMessage.warning('桌面通知权限被拒绝，无法启用桌面通知')
      }
    } else {
      advancedSettings.desktopEnabled = false
      ElMessage.warning('当前浏览器不支持桌面通知')
    }
  }
}

const verifyEmail = async () => {
  try {
    if (!contactSettings.email) {
      ElMessage.warning('请先输入邮箱地址')
      return
    }
    
    // 这里应该调用邮箱验证API
    ElMessage.info('验证邮件已发送，请查收')
    
    // 模拟验证成功（实际应该通过邮件链接验证）
    setTimeout(() => {
      contactSettings.emailVerified = true
      ElMessage.success('邮箱验证成功')
    }, 2000)
    
  } catch (error) {
    console.error('邮箱验证失败:', error)
    ElMessage.error('邮箱验证失败')
  }
}

const verifyPhone = async () => {
  try {
    if (!contactSettings.phone) {
      ElMessage.warning('请先输入手机号码')
      return
    }
    
    // 这里应该调用手机验证API
    ElMessage.info('验证短信已发送，请查收')
    
    // 模拟验证成功（实际应该通过短信验证码验证）
    setTimeout(() => {
      contactSettings.phoneVerified = true
      ElMessage.success('手机号验证成功')
    }, 2000)
    
  } catch (error) {
    console.error('手机号验证失败:', error)
    ElMessage.error('手机号验证失败')
  }
}

// 生命周期
onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
.notification-settings-page {
  padding: 24px;
  background-color: var(--el-bg-color-page);
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.header-left {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.header-content {
  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: var(--el-text-color-primary);
    margin: 0 0 8px 0;
  }
  
  .page-description {
    color: var(--el-text-color-regular);
    margin: 0;
  }
}

.settings-content {
  max-width: 800px;
}

.settings-card {
  margin-bottom: 24px;
  
  .card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 500;
    color: var(--el-text-color-primary);
  }
}

.setting-group {
  .setting-item {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    padding: 20px 0;
    border-bottom: 1px solid var(--el-border-color-lighter);
    
    &:last-child {
      border-bottom: none;
    }
  }
}

.setting-label {
  flex: 1;
  
  .label-text {
    display: block;
    font-weight: 500;
    color: var(--el-text-color-primary);
    margin-bottom: 4px;
  }
  
  .label-desc {
    display: block;
    font-size: 14px;
    color: var(--el-text-color-regular);
    line-height: 1.4;
  }
}

.setting-control {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
  
  .time-range {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-left: 12px;
    
    .time-separator {
      color: var(--el-text-color-regular);
      font-size: 14px;
    }
  }
  
  .auto-clean-days {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-left: 12px;
    
    .days-label {
      color: var(--el-text-color-regular);
      font-size: 14px;
    }
  }
}

.notification-types {
  .type-config {
    padding: 20px 0;
    border-bottom: 1px solid var(--el-border-color-lighter);
    
    &:last-child {
      border-bottom: none;
    }
  }
}

.type-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.type-info {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .type-text {
    .type-name {
      display: block;
      font-weight: 500;
      color: var(--el-text-color-primary);
      margin-bottom: 2px;
    }
    
    .type-desc {
      display: block;
      font-size: 14px;
      color: var(--el-text-color-regular);
    }
  }
}

.type-channels {
  margin-left: 36px;
  padding: 16px;
  background-color: var(--el-fill-color-lighter);
  border-radius: 6px;
  
  .channels-label {
    font-size: 14px;
    color: var(--el-text-color-regular);
    margin-bottom: 12px;
  }
  
  .channels-options {
    display: flex;
    gap: 24px;
  }
}
</style>