<template>
  <div class="notification-list-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">通知中心</h1>
        <p class="page-description">管理您的所有通知消息</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="goToSettings">
          <el-icon><Setting /></el-icon>
          通知设置
        </el-button>
      </div>
    </div>

    <!-- 筛选工具栏 -->
    <div class="filter-toolbar">
      <div class="filter-left">
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <el-tab-pane label="全部" name="all" />
          <el-tab-pane label="未读" name="unread" />
          <el-tab-pane label="已读" name="read" />
        </el-tabs>
      </div>
      <div class="filter-right">
        <el-select
          v-model="filterType"
          placeholder="通知类型"
          clearable
          style="width: 150px; margin-right: 12px"
          @change="handleFilterChange"
        >
          <el-option
            v-for="type in notificationTypes"
            :key="type.value"
            :label="type.label"
            :value="type.value"
          />
        </el-select>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          style="width: 240px; margin-right: 12px"
          @change="handleFilterChange"
        />
        <el-button
          v-if="activeTab === 'unread' && notifications.length > 0"
          type="primary"
          @click="markAllAsRead"
        >
          全部已读
        </el-button>
        <el-button
          v-if="activeTab === 'read' && notifications.length > 0"
          @click="clearReadNotifications"
        >
          清空已读
        </el-button>
      </div>
    </div>

    <!-- 统计信息 -->
    <div class="stats-bar">
      <div class="stats-item">
        <span class="stats-label">总通知：</span>
        <span class="stats-value">{{ totalCount }}</span>
      </div>
      <div class="stats-item">
        <span class="stats-label">未读：</span>
        <span class="stats-value unread-count">{{ unreadCount }}</span>
      </div>
      <div class="stats-item">
        <span class="stats-label">已读：</span>
        <span class="stats-value">{{ totalCount - unreadCount }}</span>
      </div>
    </div>

    <!-- 通知列表 -->
    <div class="notification-content" v-loading="loading">
      <div v-if="notifications.length === 0" class="empty-state">
        <el-empty :description="getEmptyDescription()" />
      </div>
      <div v-else class="notification-grid">
        <div
          v-for="notification in notifications"
          :key="notification.id"
          class="notification-card"
          :class="{
            'unread': !notification.isRead,
            'read': notification.isRead,
            'high-priority': notification.priority >= 8
          }"
        >
          <!-- 卡片头部 -->
          <div class="card-header">
            <div class="header-left">
              <div class="notification-icon">
                <el-icon :size="18" :color="getNotificationColor(notification.notificationType)">
                  <component :is="getNotificationIcon(notification.notificationType)" />
                </el-icon>
              </div>
              <div class="notification-meta">
                <span class="notification-type">{{ getNotificationTypeName(notification.notificationType) }}</span>
                <span class="notification-time">{{ formatTime(notification.createTime) }}</span>
              </div>
            </div>
            <div class="header-right">
              <el-tag
                v-if="notification.priority >= 8"
                type="danger"
                size="small"
                effect="plain"
              >
                重要
              </el-tag>
              <el-tag
                v-else-if="notification.priority >= 6"
                type="warning"
                size="small"
                effect="plain"
              >
                普通
              </el-tag>
              <el-dropdown trigger="click" @command="handleActionCommand">
                <el-icon class="action-trigger">
                  <MoreFilled />
                </el-icon>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item
                      v-if="!notification.isRead"
                      :command="{ action: 'markRead', id: notification.id }"
                    >
                      <el-icon><Check /></el-icon>
                      标记已读
                    </el-dropdown-item>
                    <el-dropdown-item
                      :command="{ action: 'detail', id: notification.id }"
                    >
                      <el-icon><View /></el-icon>
                      查看详情
                    </el-dropdown-item>
                    <el-dropdown-item
                      :command="{ action: 'delete', id: notification.id }"
                      divided
                    >
                      <el-icon><Delete /></el-icon>
                      删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>

          <!-- 卡片内容 -->
          <div class="card-content" @click="handleNotificationClick(notification)">
            <h3 class="notification-title">{{ notification.title }}</h3>
            <p class="notification-text">{{ notification.content }}</p>
          </div>

          <!-- 卡片底部 -->
          <div class="card-footer">
            <div class="footer-left">
              <el-tag size="small" effect="plain">
                {{ getSendChannelName(notification.sendChannel) }}
              </el-tag>
            </div>
            <div class="footer-right">
              <span v-if="notification.isRead" class="read-time">
                已读于 {{ formatTime(notification.readTime!) }}
              </span>
              <span v-else class="unread-indicator">未读</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="totalCount > 0" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="totalCount"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 通知详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      :title="selectedNotification?.title"
      width="600px"
      :before-close="handleDetailClose"
    >
      <div v-if="selectedNotification" class="notification-detail">
        <div class="detail-header">
          <div class="detail-meta">
            <div class="meta-item">
              <el-icon :color="getNotificationColor(selectedNotification.notificationType)">
                <component :is="getNotificationIcon(selectedNotification.notificationType)" />
              </el-icon>
              <span class="meta-type">{{ getNotificationTypeName(selectedNotification.notificationType) }}</span>
            </div>
            <div class="meta-item">
              <el-tag
                v-if="selectedNotification.priority >= 8"
                type="danger"
                size="small"
              >
                重要
              </el-tag>
              <el-tag
                v-else-if="selectedNotification.priority >= 6"
                type="warning"
                size="small"
              >
                普通
              </el-tag>
            </div>
          </div>
        </div>
        
        <div class="detail-content">
          <div class="content-text">
            {{ selectedNotification.content }}
          </div>
        </div>
        
        <div class="detail-footer">
          <div class="footer-info">
            <div class="info-item">
              <span class="info-label">发送渠道：</span>
              <span class="info-value">{{ getSendChannelName(selectedNotification.sendChannel) }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">创建时间：</span>
              <span class="info-value">{{ formatFullTime(selectedNotification.createTime) }}</span>
            </div>
            <div v-if="selectedNotification.readTime" class="info-item">
              <span class="info-label">阅读时间：</span>
              <span class="info-value">{{ formatFullTime(selectedNotification.readTime) }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">状态：</span>
              <el-tag
                :type="selectedNotification.isRead ? 'success' : 'info'"
                size="small"
              >
                {{ selectedNotification.isRead ? '已读' : '未读' }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button
          v-if="selectedNotification && !selectedNotification.isRead"
          type="primary"
          @click="markNotificationAsRead(selectedNotification.id)"
        >
          标记已读
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Setting,
  Bell,
  Message,
  Warning,
  SuccessFilled,
  CircleClose,
  InfoFilled,
  MoreFilled,
  Check,
  View,
  Delete
} from '@element-plus/icons-vue'
import {
  NotificationAPI,
  type Notification,
  type NotificationQueryParams,
  type PageResponse,
  NotificationType,
  SendChannel
} from '@/api/notification'
import { formatDistanceToNow, format } from 'date-fns'
import { zhCN } from 'date-fns/locale'

// 路由
const router = useRouter()

// 响应式数据
const loading = ref(false)
const detailVisible = ref(false)
const activeTab = ref('all')
const filterType = ref<NotificationType | undefined>()
const dateRange = ref<[string, string] | null>(null)
const currentPage = ref(1)
const pageSize = ref(20)
const totalCount = ref(0)
const unreadCount = ref(0)
const notifications = ref<Notification[]>([])
const selectedNotification = ref<Notification | null>(null)

// 通知类型选项
const notificationTypes = [
  { label: '交换申请', value: NotificationType.EXCHANGE_APPLICATION },
  { label: '交换通过', value: NotificationType.EXCHANGE_APPROVAL },
  { label: '交换拒绝', value: NotificationType.EXCHANGE_REJECTION },
  { label: '交换完成', value: NotificationType.EXCHANGE_COMPLETION },
  { label: '系统公告', value: NotificationType.SYSTEM_ANNOUNCEMENT },
  { label: '线索到期提醒', value: NotificationType.LEAD_EXPIRY_REMINDER },
  { label: '评级更新', value: NotificationType.RATING_UPDATE }
]

// 计算属性
const queryParams = computed((): NotificationQueryParams => {
  const params: NotificationQueryParams = {
    page: currentPage.value - 1, // 后端从0开始
    size: pageSize.value
  }
  
  if (activeTab.value === 'unread') {
    params.isRead = false
  } else if (activeTab.value === 'read') {
    params.isRead = true
  }
  
  if (filterType.value) {
    params.notificationType = filterType.value
  }
  
  if (dateRange.value) {
    params.startDate = dateRange.value[0]
    params.endDate = dateRange.value[1]
  }
  
  return params
})

// 方法
const loadNotifications = async () => {
  try {
    loading.value = true
    const response: PageResponse<Notification> = await NotificationAPI.getNotifications(queryParams.value)
    notifications.value = response.content
    totalCount.value = response.totalElements
  } catch (error) {
    console.error('加载通知失败:', error)
    ElMessage.error('加载通知失败')
  } finally {
    loading.value = false
  }
}

const loadUnreadCount = async () => {
  try {
    unreadCount.value = await NotificationAPI.getUnreadCount()
  } catch (error) {
    console.error('加载未读数量失败:', error)
  }
}

const handleTabChange = () => {
  currentPage.value = 1
  loadNotifications()
}

const handleFilterChange = () => {
  currentPage.value = 1
  loadNotifications()
}

const handleCurrentChange = () => {
  loadNotifications()
}

const handleSizeChange = () => {
  currentPage.value = 1
  loadNotifications()
}

const handleNotificationClick = (notification: Notification) => {
  selectedNotification.value = notification
  detailVisible.value = true
  
  // 如果是未读通知，自动标记为已读
  if (!notification.isRead) {
    markNotificationAsRead(notification.id)
  }
}

const handleDetailClose = () => {
  detailVisible.value = false
  selectedNotification.value = null
}

const markNotificationAsRead = async (id: number) => {
  try {
    await NotificationAPI.markAsRead(id)
    
    // 更新本地状态
    const notification = notifications.value.find(n => n.id === id)
    if (notification) {
      notification.isRead = true
      notification.readTime = new Date().toISOString()
    }
    
    if (selectedNotification.value?.id === id) {
      selectedNotification.value.isRead = true
      selectedNotification.value.readTime = new Date().toISOString()
    }
    
    // 更新未读数量
    loadUnreadCount()
    
  } catch (error) {
    console.error('标记已读失败:', error)
    ElMessage.error('标记已读失败')
  }
}

const markAllAsRead = async () => {
  try {
    const count = await NotificationAPI.markAllAsRead()
    
    // 更新本地状态
    notifications.value.forEach(n => {
      if (!n.isRead) {
        n.isRead = true
        n.readTime = new Date().toISOString()
      }
    })
    
    // 更新未读数量
    unreadCount.value = 0
    
    ElMessage.success(`已标记 ${count} 条通知为已读`)
    
  } catch (error) {
    console.error('标记全部已读失败:', error)
    ElMessage.error('标记全部已读失败')
  }
}

const clearReadNotifications = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要清空所有已读通知吗？此操作不可恢复。',
      '确认清空',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const count = await NotificationAPI.clearReadNotifications()
    
    // 重新加载通知列表
    loadNotifications()
    
    ElMessage.success(`已清空 ${count} 条已读通知`)
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清空已读通知失败:', error)
      ElMessage.error('清空已读通知失败')
    }
  }
}

const handleActionCommand = async (command: { action: string; id: number }) => {
  const { action, id } = command
  
  if (action === 'markRead') {
    await markNotificationAsRead(id)
  } else if (action === 'detail') {
    const notification = notifications.value.find(n => n.id === id)
    if (notification) {
      handleNotificationClick(notification)
    }
  } else if (action === 'delete') {
    try {
      await ElMessageBox.confirm(
        '确定要删除这条通知吗？',
        '确认删除',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      
      await NotificationAPI.deleteNotification(id)
      
      // 重新加载列表
      loadNotifications()
      loadUnreadCount()
      
      ElMessage.success('删除成功')
      
    } catch (error) {
      if (error !== 'cancel') {
        console.error('删除通知失败:', error)
        ElMessage.error('删除通知失败')
      }
    }
  }
}

const goToSettings = () => {
  router.push('/notifications/settings')
}

// 工具方法
const getEmptyDescription = () => {
  switch (activeTab.value) {
    case 'unread':
      return '暂无未读通知'
    case 'read':
      return '暂无已读通知'
    default:
      return '暂无通知'
  }
}

const getNotificationIcon = (type: NotificationType) => {
  switch (type) {
    case NotificationType.EXCHANGE_APPLICATION:
      return Message
    case NotificationType.EXCHANGE_APPROVAL:
      return SuccessFilled
    case NotificationType.EXCHANGE_REJECTION:
      return CircleClose
    case NotificationType.EXCHANGE_COMPLETION:
      return SuccessFilled
    case NotificationType.SYSTEM_ANNOUNCEMENT:
      return InfoFilled
    case NotificationType.LEAD_EXPIRY_REMINDER:
      return Warning
    default:
      return InfoFilled
  }
}

const getNotificationColor = (type: NotificationType) => {
  switch (type) {
    case NotificationType.EXCHANGE_APPLICATION:
      return '#409EFF'
    case NotificationType.EXCHANGE_APPROVAL:
      return '#67C23A'
    case NotificationType.EXCHANGE_REJECTION:
      return '#F56C6C'
    case NotificationType.EXCHANGE_COMPLETION:
      return '#67C23A'
    case NotificationType.SYSTEM_ANNOUNCEMENT:
      return '#909399'
    case NotificationType.LEAD_EXPIRY_REMINDER:
      return '#E6A23C'
    default:
      return '#909399'
  }
}

const getNotificationTypeName = (type: NotificationType) => {
  const typeNames = {
    [NotificationType.EXCHANGE_APPLICATION]: '交换申请',
    [NotificationType.EXCHANGE_APPROVAL]: '交换通过',
    [NotificationType.EXCHANGE_REJECTION]: '交换拒绝',
    [NotificationType.EXCHANGE_COMPLETION]: '交换完成',
    [NotificationType.SYSTEM_ANNOUNCEMENT]: '系统公告',
    [NotificationType.LEAD_EXPIRY_REMINDER]: '线索到期提醒',
    [NotificationType.RATING_UPDATE]: '评级更新'
  }
  return typeNames[type] || '未知类型'
}

const getSendChannelName = (channel: SendChannel) => {
  const channelNames = {
    [SendChannel.SYSTEM]: '系统通知',
    [SendChannel.EMAIL]: '邮件',
    [SendChannel.SMS]: '短信'
  }
  return channelNames[channel] || '未知渠道'
}

const formatTime = (time: string) => {
  return formatDistanceToNow(new Date(time), {
    addSuffix: true,
    locale: zhCN
  })
}

const formatFullTime = (time: string) => {
  return format(new Date(time), 'yyyy-MM-dd HH:mm:ss')
}

// 生命周期
onMounted(() => {
  loadNotifications()
  loadUnreadCount()
})

// 监听查询参数变化
watch(queryParams, () => {
  loadNotifications()
}, { deep: true })
</script>

<style scoped>
.notification-list-page {
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

.filter-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--el-bg-color);
  padding: 16px 24px;
  border-radius: 8px;
  margin-bottom: 16px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
}

.filter-left {
  flex: 1;
}

.filter-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stats-bar {
  display: flex;
  gap: 24px;
  background: var(--el-bg-color);
  padding: 16px 24px;
  border-radius: 8px;
  margin-bottom: 16px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
}

.stats-item {
  display: flex;
  align-items: center;
  gap: 4px;
  
  .stats-label {
    color: var(--el-text-color-regular);
    font-size: 14px;
  }
  
  .stats-value {
    color: var(--el-text-color-primary);
    font-weight: 500;
    
    &.unread-count {
      color: var(--el-color-primary);
    }
  }
}

.notification-content {
  background: var(--el-bg-color);
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
}

.empty-state {
  padding: 60px 20px;
}

.notification-grid {
  display: grid;
  gap: 16px;
}

.notification-card {
  border: 1px solid var(--el-border-color-light);
  border-radius: 8px;
  padding: 16px;
  transition: all 0.3s;
  cursor: pointer;
  position: relative;
}

.notification-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.notification-card.unread {
  background: linear-gradient(90deg, var(--el-color-primary-light-9) 0%, var(--el-bg-color) 100%);
  border-left: 4px solid var(--el-color-primary);
}

.notification-card.unread::before {
  content: '';
  position: absolute;
  top: 16px;
  right: 16px;
  width: 8px;
  height: 8px;
  background-color: var(--el-color-primary);
  border-radius: 50%;
}

.notification-card.high-priority {
  border-left-color: var(--el-color-danger);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.notification-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 6px;
  background-color: var(--el-fill-color-light);
}

.notification-meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
  
  .notification-type {
    font-size: 12px;
    color: var(--el-text-color-regular);
    font-weight: 500;
  }
  
  .notification-time {
    font-size: 11px;
    color: var(--el-text-color-placeholder);
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-trigger {
  cursor: pointer;
  color: var(--el-text-color-placeholder);
  transition: color 0.3s;
  padding: 4px;
}

.action-trigger:hover {
  color: var(--el-text-color-regular);
}

.card-content {
  margin-bottom: 12px;
  
  .notification-title {
    font-size: 16px;
    font-weight: 500;
    color: var(--el-text-color-primary);
    margin: 0 0 8px 0;
    line-height: 1.4;
  }
  
  .notification-text {
    color: var(--el-text-color-regular);
    line-height: 1.5;
    margin: 0;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid var(--el-border-color-lighter);
}

.footer-right {
  .read-time {
    font-size: 12px;
    color: var(--el-text-color-placeholder);
  }
  
  .unread-indicator {
    font-size: 12px;
    color: var(--el-color-primary);
    font-weight: 500;
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

.notification-detail {
  .detail-header {
    margin-bottom: 16px;
    
    .detail-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .meta-item {
        display: flex;
        align-items: center;
        gap: 8px;
        
        .meta-type {
          font-weight: 500;
          color: var(--el-text-color-primary);
        }
      }
    }
  }
  
  .detail-content {
    background-color: var(--el-fill-color-lighter);
    padding: 20px;
    border-radius: 8px;
    margin-bottom: 20px;
    
    .content-text {
      line-height: 1.6;
      color: var(--el-text-color-primary);
    }
  }
  
  .detail-footer {
    .footer-info {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 12px;
      
      .info-item {
        display: flex;
        align-items: center;
        
        .info-label {
          color: var(--el-text-color-regular);
          width: 80px;
          flex-shrink: 0;
        }
        
        .info-value {
          color: var(--el-text-color-primary);
        }
      }
    }
  }
}
</style>