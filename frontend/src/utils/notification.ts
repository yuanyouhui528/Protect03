import { ElNotification } from 'element-plus'
import { NotificationType, type Notification } from '@/api/notification'

/**
 * 实时通知服务
 * 负责WebSocket连接、实时通知接收和桌面通知
 */
export class NotificationService {
  private ws: WebSocket | null = null
  private reconnectTimer: number | null = null
  private reconnectAttempts = 0
  private maxReconnectAttempts = 5
  private reconnectInterval = 5000
  private isConnecting = false
  private listeners: Array<(notification: Notification) => void> = []
  private soundEnabled = true
  private desktopEnabled = false
  private audioContext: AudioContext | null = null
  private notificationSound: AudioBuffer | null = null

  constructor() {
    this.initAudio()
    this.checkDesktopPermission()
  }

  /**
   * 初始化音频上下文
   */
  private async initAudio() {
    try {
      this.audioContext = new (window.AudioContext || (window as any).webkitAudioContext)()
      
      // 创建通知提示音
      const oscillator = this.audioContext.createOscillator()
      const gainNode = this.audioContext.createGain()
      
      oscillator.connect(gainNode)
      gainNode.connect(this.audioContext.destination)
      
      oscillator.frequency.setValueAtTime(800, this.audioContext.currentTime)
      oscillator.frequency.setValueAtTime(600, this.audioContext.currentTime + 0.1)
      
      gainNode.gain.setValueAtTime(0.3, this.audioContext.currentTime)
      gainNode.gain.exponentialRampToValueAtTime(0.01, this.audioContext.currentTime + 0.2)
      
    } catch (error) {
      console.warn('音频初始化失败:', error)
    }
  }

  /**
   * 检查桌面通知权限
   */
  private async checkDesktopPermission() {
    if ('Notification' in window) {
      if (Notification.permission === 'default') {
        const permission = await Notification.requestPermission()
        this.desktopEnabled = permission === 'granted'
      } else {
        this.desktopEnabled = Notification.permission === 'granted'
      }
    }
  }

  /**
   * 连接WebSocket
   */
  connect(token?: string) {
    if (this.isConnecting || (this.ws && this.ws.readyState === WebSocket.OPEN)) {
      return
    }

    this.isConnecting = true
    
    try {
      const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
      const host = window.location.host
      const wsUrl = `${protocol}//${host}/api/ws/notifications${token ? `?token=${token}` : ''}`
      
      this.ws = new WebSocket(wsUrl)
      
      this.ws.onopen = this.handleOpen.bind(this)
      this.ws.onmessage = this.handleMessage.bind(this)
      this.ws.onclose = this.handleClose.bind(this)
      this.ws.onerror = this.handleError.bind(this)
      
    } catch (error) {
      console.error('WebSocket连接失败:', error)
      this.isConnecting = false
      this.scheduleReconnect()
    }
  }

  /**
   * 断开连接
   */
  disconnect() {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
    
    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
    
    this.isConnecting = false
    this.reconnectAttempts = 0
  }

  /**
   * 添加通知监听器
   */
  addListener(listener: (notification: Notification) => void) {
    this.listeners.push(listener)
  }

  /**
   * 移除通知监听器
   */
  removeListener(listener: (notification: Notification) => void) {
    const index = this.listeners.indexOf(listener)
    if (index > -1) {
      this.listeners.splice(index, 1)
    }
  }

  /**
   * 设置声音开关
   */
  setSoundEnabled(enabled: boolean) {
    this.soundEnabled = enabled
  }

  /**
   * 设置桌面通知开关
   */
  setDesktopEnabled(enabled: boolean) {
    this.desktopEnabled = enabled
  }

  /**
   * 处理连接打开
   */
  private handleOpen() {
    console.log('WebSocket连接已建立')
    this.isConnecting = false
    this.reconnectAttempts = 0
    
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
  }

  /**
   * 处理消息接收
   */
  private handleMessage(event: MessageEvent) {
    try {
      const data = JSON.parse(event.data)
      
      if (data.type === 'notification') {
        const notification: Notification = data.payload
        this.handleNotification(notification)
      }
      
    } catch (error) {
      console.error('解析WebSocket消息失败:', error)
    }
  }

  /**
   * 处理连接关闭
   */
  private handleClose(event: CloseEvent) {
    console.log('WebSocket连接已关闭:', event.code, event.reason)
    this.ws = null
    this.isConnecting = false
    
    // 如果不是主动关闭，则尝试重连
    if (event.code !== 1000) {
      this.scheduleReconnect()
    }
  }

  /**
   * 处理连接错误
   */
  private handleError(event: Event) {
    console.error('WebSocket连接错误:', event)
    this.isConnecting = false
  }

  /**
   * 安排重连
   */
  private scheduleReconnect() {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.error('WebSocket重连次数已达上限，停止重连')
      return
    }
    
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
    }
    
    this.reconnectAttempts++
    const delay = this.reconnectInterval * Math.pow(2, this.reconnectAttempts - 1)
    
    console.log(`${delay / 1000}秒后尝试第${this.reconnectAttempts}次重连`)
    
    this.reconnectTimer = window.setTimeout(() => {
      this.connect()
    }, delay)
  }

  /**
   * 处理通知
   */
  private handleNotification(notification: Notification) {
    // 触发监听器
    this.listeners.forEach(listener => {
      try {
        listener(notification)
      } catch (error) {
        console.error('通知监听器执行失败:', error)
      }
    })

    // 显示系统通知
    this.showSystemNotification(notification)

    // 播放提示音
    if (this.soundEnabled) {
      this.playNotificationSound()
    }

    // 显示桌面通知
    if (this.desktopEnabled) {
      this.showDesktopNotification(notification)
    }
  }

  /**
   * 显示系统通知
   */
  private showSystemNotification(notification: Notification) {
    const config = this.getNotificationConfig(notification.notificationType)
    
    ElNotification({
      title: notification.title,
      message: notification.content,
      type: config.type,
      duration: config.duration,
      position: 'top-right',
      showClose: true,
      onClick: () => {
        // 可以在这里处理点击事件，比如跳转到详情页
        console.log('点击通知:', notification)
      }
    })
  }

  /**
   * 播放通知提示音
   */
  private playNotificationSound() {
    if (!this.audioContext) return
    
    try {
      // 恢复音频上下文（某些浏览器需要用户交互后才能播放音频）
      if (this.audioContext.state === 'suspended') {
        this.audioContext.resume()
      }
      
      const oscillator = this.audioContext.createOscillator()
      const gainNode = this.audioContext.createGain()
      
      oscillator.connect(gainNode)
      gainNode.connect(this.audioContext.destination)
      
      oscillator.frequency.setValueAtTime(800, this.audioContext.currentTime)
      oscillator.frequency.setValueAtTime(600, this.audioContext.currentTime + 0.1)
      
      gainNode.gain.setValueAtTime(0.3, this.audioContext.currentTime)
      gainNode.gain.exponentialRampToValueAtTime(0.01, this.audioContext.currentTime + 0.2)
      
      oscillator.start(this.audioContext.currentTime)
      oscillator.stop(this.audioContext.currentTime + 0.2)
      
    } catch (error) {
      console.warn('播放通知音效失败:', error)
    }
  }

  /**
   * 显示桌面通知
   */
  private showDesktopNotification(notification: Notification) {
    if (!('Notification' in window) || Notification.permission !== 'granted') {
      return
    }
    
    try {
      const config = this.getNotificationConfig(notification.notificationType)
      
      const desktopNotification = new Notification(notification.title, {
        body: notification.content,
        icon: config.icon,
        tag: `notification-${notification.id}`,
        requireInteraction: notification.priority >= 8, // 高优先级通知需要用户交互
        silent: false
      })
      
      // 自动关闭
      setTimeout(() => {
        desktopNotification.close()
      }, 5000)
      
      // 点击事件
      desktopNotification.onclick = () => {
        window.focus()
        desktopNotification.close()
        // 可以在这里处理点击事件
        console.log('点击桌面通知:', notification)
      }
      
    } catch (error) {
      console.warn('显示桌面通知失败:', error)
    }
  }

  /**
   * 获取通知配置
   */
  private getNotificationConfig(type: NotificationType) {
    const configs = {
      [NotificationType.EXCHANGE_APPLICATION]: {
        type: 'info' as const,
        duration: 4500,
        icon: '/icons/exchange-application.png'
      },
      [NotificationType.EXCHANGE_APPROVAL]: {
        type: 'success' as const,
        duration: 4500,
        icon: '/icons/exchange-approval.png'
      },
      [NotificationType.EXCHANGE_REJECTION]: {
        type: 'warning' as const,
        duration: 4500,
        icon: '/icons/exchange-rejection.png'
      },
      [NotificationType.EXCHANGE_COMPLETION]: {
        type: 'success' as const,
        duration: 4500,
        icon: '/icons/exchange-completion.png'
      },
      [NotificationType.SYSTEM_ANNOUNCEMENT]: {
        type: 'info' as const,
        duration: 6000,
        icon: '/icons/system-announcement.png'
      },
      [NotificationType.LEAD_EXPIRY_REMINDER]: {
        type: 'warning' as const,
        duration: 6000,
        icon: '/icons/lead-expiry.png'
      },
      [NotificationType.RATING_UPDATE]: {
        type: 'info' as const,
        duration: 4500,
        icon: '/icons/rating-update.png'
      }
    }
    
    return configs[type] || {
      type: 'info' as const,
      duration: 4500,
      icon: '/icons/default-notification.png'
    }
  }

  /**
   * 获取连接状态
   */
  getConnectionState(): 'connecting' | 'open' | 'closed' {
    if (this.isConnecting) return 'connecting'
    if (this.ws && this.ws.readyState === WebSocket.OPEN) return 'open'
    return 'closed'
  }

  /**
   * 手动发送心跳
   */
  sendHeartbeat() {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify({ type: 'heartbeat' }))
    }
  }
}

// 创建全局实例
export const notificationService = new NotificationService()

// 导出便捷方法
export const connectNotifications = (token?: string) => {
  notificationService.connect(token)
}

export const disconnectNotifications = () => {
  notificationService.disconnect()
}

export const addNotificationListener = (listener: (notification: Notification) => void) => {
  notificationService.addListener(listener)
}

export const removeNotificationListener = (listener: (notification: Notification) => void) => {
  notificationService.removeListener(listener)
}

export const setNotificationSound = (enabled: boolean) => {
  notificationService.setSoundEnabled(enabled)
}

export const setDesktopNotification = (enabled: boolean) => {
  notificationService.setDesktopEnabled(enabled)
}