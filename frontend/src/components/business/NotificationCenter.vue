<template>
  <div class="notification-center">
    <!-- 通知铃铛图标 -->
    <div class="notification-trigger" @click="togglePanel">
      <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99">
        <el-icon :size="20" :class="{ 'has-unread': unreadCount > 0 }">
          <Bell />
        </el-icon>
      </el-badge>
    </div>

    <!-- 通知面板 -->
    <el-drawer
      v-model="panelVisible"
      title="通知中心"
      direction="rtl"
      size="400px"
      :before-close="handleClose"
    >
      <!-- 头部操作栏 -->
      <div class="notification-header">
        <div class="header-tabs">
          <el-tabs v-model="activeTab" @tab-change="handleTabChange">
            <el-tab-pane label="全部" name="all" />
            <el-tab-pane label="未读" name="unread" />
            <el-tab-pane label="已读" name="read" />
          </el-tabs>
        </div>
        <div class="header-actions">
          <el-button
            v-if="activeTab === 'unread' && notifications.length > 0"
            type="text"
            size="small"
            @click="markAllAsRead"
          >
            全部已读
          </el-button>
          <el-button
            v-if="activeTab === 'read' && notifications.length > 0"
            type="text"
            size="small"
            @click="clearReadNotifications"
          >
            清空已读
          </el-button>
        </div>
      </div>

      <!-- 通知列表 -->
      <div class="notification-list" v-loading="loading">
        <div v-if="notifications.length === 0" class="empty-state">
          <el-empty :description="getEmptyDescription()" />
        </div>
        <div v-else>
          <div
            v-for="notification in notifications"
            :key="notification.id"
            class="notification-item"
            :class="{
              'unread': !notification.isRead,
              'read': notification.isRead
            }"
            @click="handleNotificationClick(notification)"
          >
            <!-- 通知图标 -->
            <div class="notification-icon">
              <el-icon :size="16" :color="getNotificationColor(notification.notificationType)">
                <component :is="getNotificationIcon(notification.notificationType)" />
              </el-icon>
            </div>

            <!-- 通知内容 -->
            <div class="notification-content">
              <div class="notification-title">{{ notification.title }}</div>
              <div class="notification-text">{{ notification.content }}</div>
              <div class="notification-meta">
                <span class="notification-time">{{ formatTime(notification.createTime) }}</span>
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
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="notification-actions">
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
                      标记已读
                    </el-dropdown-item>
                    <el-dropdown-item
                      :command="{ action: 'delete', id: notification.id }"
                    >
                      删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </div>

        <!-- 加载更多 -->
        <div v-if="hasMore" class="load-more">
          <el-button
            type="text"
            :loading="loadingMore"
            @click="loadMore"
          >
            加载更多
          </el-button>
        </div>
      </div>

      <!-- 底部链接 -->
      <div class="notification-footer">
        <el-button type="text" @click="goToNotificationList">
          查看全部通知
        </el-button>
      </div>
    </el-drawer>

    <!-- 通知详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      :title="selectedNotification?.title"
      width="500px"
      :before-close="handleDetailClose"
    >
      <div v-if="selectedNotification" class="notification-detail">
        <div class="detail-content">
          {{ selectedNotification.content }}
        </div>
        <div class="detail-meta">
          <div class="meta-item">
            <span class="meta-label">通知类型：</span>
            <span class="meta-value">{{ getNotificationTypeName(selectedNotification.notificationType) }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">发送渠道：</span>
            <span class="meta-value">{{ getSendChannelName(selectedNotification.sendChannel) }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">创建时间：</span>
            <span class="meta-value">{{ formatFullTime(selectedNotification.createTime) }}</span>
          </div>
          <div v-if="selectedNotification.readTime" class="meta-item">
            <span class="meta-label">阅读时间：</span>
            <span class="meta-value">{{ formatFullTime(selectedNotification.readTime) }}</span>
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
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Bell,
  Message,
  Warning,
  SuccessFilled,
  CircleClose,
  InfoFilled,
  MoreFilled
} from '@element-plus/icons-vue'
import {
  NotificationAPI,
  type Notification,
  type NotificationQueryParams,
  NotificationType,
  SendChannel
} from '@/api/notification'
import { formatDistanceToNow, format } from 'date-fns'
import { zhCN } from 'date-fns/locale'

// 组件状态
const panelVisible = ref(false)
const detailVisible = ref(false)
const loading = ref(false)
const loadingMore = ref(false)
const activeTab = ref('all')
const unreadCount = ref(0)
const notifications = ref<Notification[]>([])
const selectedNotification = ref<Notification | null>(null)
const hasMore = ref(false)
const currentPage = ref(0)
const pageSize = 10

// 路由
const router = useRouter()

// 计算属性
const queryParams = computed((): NotificationQueryParams => {
  const params: NotificationQueryParams = {
    page: currentPage.value,
    size: pageSize
  }
  
  if (activeTab.value === 'unread') {
    params.isRead = false
  } else if (activeTab.value === 'read') {
    params.isRead = true
  }
  
  return params
})

// 方法
const togglePanel = () => {
  panelVisible.value = !panelVisible.value
  if (panelVisible.value) {
    loadNotifications()
  }
}

const handleClose = () => {
  panelVisible.value = false
}

const handleDetailClose = () => {
  detailVisible.value = false
  selectedNotification.value = null
}

const handleTabChange = () => {
  currentPage.value = 0
  notifications.value = []
  loadNotifications()
}

const loadNotifications = async (append = false) => {
  try {
    if (!append) {
      loading.value = true
    } else {
      loadingMore.value = true
    }
    
    const response = await NotificationAPI.getNotifications(queryParams.value)
    
    if (append) {
      notifications.value.push(...response.content)
    } else {
      notifications.value = response.content
    }
    
    hasMore.value = !response.last
    
  } catch (error) {
    console.error('加载通知失败:', error)
    ElMessage.error('加载通知失败')
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

const loadMore = () => {
  currentPage.value++
  loadNotifications(true)
}

const loadUnreadCount = async () => {
  try {
    unreadCount.value = await NotificationAPI.getUnreadCount()
  } catch (error) {
    console.error('加载未读数量失败:', error)
  }
}

const handleNotificationClick = (notification: Notification) => {
  selectedNotification.value = notification
  detailVisible.value = true
  
  // 如果是未读通知，自动标记为已读
  if (!notification.isRead) {
    markNotificationAsRead(notification.id)
  }
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
    currentPage.value = 0
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
      
      // 从列表中移除
      const index = notifications.value.findIndex(n => n.id === id)
      if (index > -1) {
        notifications.value.splice(index, 1)
      }
      
      // 更新未读数量
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

const goToNotificationList = () => {
  panelVisible.value = false
  router.push('/notifications')
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
  loadUnreadCount()
  
  // 定期更新未读数量
  const interval = setInterval(loadUnreadCount, 30000) // 30秒更新一次
  
  onUnmounted(() => {
    clearInterval(interval)
  })
})
</script>

<style scoped>
.notification-center {
  position: relative;
}

.notification-trigger {
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.notification-trigger:hover {
  background-color: var(--el-fill-color-light);
}

.notification-trigger .el-icon.has-unread {
  color: var(--el-color-primary);
  animation: bell-shake 2s infinite;
}

@keyframes bell-shake {
  0%, 50%, 100% { transform: rotate(0deg); }
  10%, 30% { transform: rotate(-10deg); }
  20%, 40% { transform: rotate(10deg); }
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  border-bottom: 1px solid var(--el-border-color-light);
  padding-bottom: 16px;
}

.header-tabs {
  flex: 1;
}

.header-actions {
  margin-left: 16px;
}

.notification-list {
  max-height: 500px;
  overflow-y: auto;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: background-color 0.3s;
  position: relative;
}

.notification-item:hover {
  background-color: var(--el-fill-color-light);
}

.notification-item.unread {
  background-color: var(--el-color-primary-light-9);
  border-left: 3px solid var(--el-color-primary);
}

.notification-item.unread::before {
  content: '';
  position: absolute;
  top: 16px;
  right: 12px;
  width: 8px;
  height: 8px;
  background-color: var(--el-color-primary);
  border-radius: 50%;
}

.notification-icon {
  margin-right: 12px;
  margin-top: 2px;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-title {
  font-weight: 500;
  color: var(--el-text-color-primary);
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notification-text {
  color: var(--el-text-color-regular);
  font-size: 14px;
  line-height: 1.4;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.notification-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.notification-time {
  color: var(--el-text-color-placeholder);
  font-size: 12px;
}

.notification-actions {
  margin-left: 8px;
}

.action-trigger {
  cursor: pointer;
  color: var(--el-text-color-placeholder);
  transition: color 0.3s;
}

.action-trigger:hover {
  color: var(--el-text-color-regular);
}

.load-more {
  text-align: center;
  padding: 16px;
}

.notification-footer {
  text-align: center;
  padding: 16px;
  border-top: 1px solid var(--el-border-color-light);
  margin-top: 16px;
}

.empty-state {
  padding: 40px 20px;
}

.notification-detail {
  .detail-content {
    background-color: var(--el-fill-color-lighter);
    padding: 16px;
    border-radius: 8px;
    margin-bottom: 16px;
    line-height: 1.6;
  }
  
  .detail-meta {
    .meta-item {
      display: flex;
      margin-bottom: 8px;
      
      .meta-label {
        color: var(--el-text-color-regular);
        width: 80px;
        flex-shrink: 0;
      }
      
      .meta-value {
        color: var(--el-text-color-primary);
      }
    }
  }
}
</style>