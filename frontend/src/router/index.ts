import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

// 布局组件
const DefaultLayout = () => import('@/layouts/DefaultLayout.vue')

// 页面组件
const Dashboard = () => import('@/views/Dashboard.vue')
const Login = () => import('@/views/Login.vue')
const Register = () => import('@/views/Register.vue')
const Forbidden = () => import('@/views/403.vue')

// 线索管理模块
const LeadList = () => import('@/views/leads/LeadList.vue')
const LeadCreate = () => import('@/views/leads/LeadCreate.vue')
const LeadDetail = () => import('@/views/leads/LeadDetail.vue')
const LeadFavorites = () => import('@/views/leads/LeadFavorites.vue')

// 交换中心模块
const ExchangeMarket = () => import('@/views/exchange/ExchangeMarket.vue')
const ExchangeHistory = () => import('@/views/exchange/ExchangeHistory.vue')
const ExchangeRules = () => import('@/views/exchange/ExchangeRules.vue')

// 数据分析模块
const PersonalAnalytics = () => import('@/views/analytics/PersonalAnalytics.vue')
const SystemAnalytics = () => import('@/views/analytics/SystemAnalytics.vue')

// 用户管理模块
const UserProfile = () => import('@/views/UserProfile.vue')

// 通知管理模块
const NotificationList = () => import('@/views/notification/NotificationList.vue')
const NotificationSettings = () => import('@/views/notification/NotificationSettings.vue')

// 路由配置
const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: {
      title: '登录',
      requiresAuth: false
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: {
      title: '注册',
      requiresAuth: false
    }
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: Forbidden,
    meta: {
      title: '访问被拒绝',
      requiresAuth: false
    }
  },
  {
    path: '/',
    component: DefaultLayout,
    redirect: '/dashboard',
    meta: {
      requiresAuth: true
    },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: {
          title: '数据看板',
          icon: 'DataBoard'
        }
      },
      {
        path: 'leads',
        name: 'Leads',
        redirect: '/leads/list',
        meta: {
          title: '线索管理',
          icon: 'Document'
        },
        children: [
          {
            path: 'list',
            name: 'LeadList',
            component: LeadList,
            meta: {
              title: '线索列表'
            }
          },
          {
            path: 'create',
            name: 'LeadCreate',
            component: LeadCreate,
            meta: {
              title: '发布线索'
            }
          },
          {
            path: 'detail/:id',
            name: 'LeadDetail',
            component: LeadDetail,
            meta: {
              title: '线索详情'
            }
          },
          {
            path: 'favorites',
            name: 'LeadFavorites',
            component: LeadFavorites,
            meta: {
              title: '我的收藏'
            }
          }
        ]
      },
      {
        path: 'exchange',
        name: 'Exchange',
        redirect: '/exchange/market',
        meta: {
          title: '交换中心',
          icon: 'Switch'
        },
        children: [
          {
            path: 'market',
            name: 'ExchangeMarket',
            component: ExchangeMarket,
            meta: {
              title: '交换市场'
            }
          },
          {
            path: 'history',
            name: 'ExchangeHistory',
            component: ExchangeHistory,
            meta: {
              title: '交换记录'
            }
          },
          {
            path: 'rules',
            name: 'ExchangeRules',
            component: ExchangeRules,
            meta: {
              title: '交换规则'
            }
          },
          {
            path: 'applications',
            name: 'ExchangeApplications',
            component: () => import('@/views/exchange/ExchangeApplications.vue'),
            meta: {
              title: '交换申请管理'
            }
          }
        ]
      },
      {
        path: 'analytics',
        name: 'Analytics',
        redirect: '/analytics/personal',
        meta: {
          title: '数据分析',
          icon: 'DataAnalysis'
        },
        children: [
          {
            path: 'personal',
            name: 'PersonalAnalytics',
            component: PersonalAnalytics,
            meta: {
              title: '个人看板'
            }
          },
          {
            path: 'system',
            name: 'SystemAnalytics',
            component: SystemAnalytics,
            meta: {
              title: '系统看板'
            }
          }
        ]
      },
      {
        path: 'profile',
        name: 'UserProfile',
        component: UserProfile,
        meta: {
          title: '个人设置',
          icon: 'User'
        }
      },
      {
        path: 'notifications',
        name: 'Notifications',
        redirect: '/notifications/list',
        meta: {
          title: '通知中心',
          icon: 'Bell'
        },
        children: [
          {
            path: 'list',
            name: 'NotificationList',
            component: NotificationList,
            meta: {
              title: '消息列表'
            }
          },
          {
            path: 'settings',
            name: 'NotificationSettings',
            component: NotificationSettings,
            meta: {
              title: '消息设置'
            }
          }
        ]
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/404.vue'),
    meta: {
      title: '页面不存在'
    }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// 白名单路由（不需要登录验证的路由）
const whiteList = ['/login', '/register', '/404']

// 全局前置守卫
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  if (to.meta?.title) {
    document.title = `${to.meta.title} - 招商线索流通平台`
  }
  
  const userStore = useUserStore()
  
  // 如果在白名单中，直接放行
  if (whiteList.includes(to.path)) {
    // 如果已登录用户访问登录页或注册页，重定向到首页
    if ((to.path === '/login' || to.path === '/register') && userStore.isLoggedIn) {
      next('/')
      return
    }
    next()
    return
  }
  
  // 检查是否需要登录（临时禁用认证检查）
  const requiresAuth = false // 临时设置为 false，允许所有页面访问
  
  if (requiresAuth) {
    // 检查登录状态
    if (!userStore.isLoggedIn) {
      ElMessage.warning('请先登录')
      next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
      return
    }
    
    // 如果用户信息不存在，尝试获取
    if (!userStore.userInfo) {
      try {
        await userStore.getUserInfo()
      } catch (error) {
        console.error('获取用户信息失败:', error)
        ElMessage.error('获取用户信息失败，请重新登录')
        userStore.logout()
        next('/login')
        return
      }
    }
    
    // 检查权限
    const requiredPermissions = to.meta?.permissions as string[] | undefined
    if (requiredPermissions && requiredPermissions.length > 0) {
      const hasPermission = userStore.hasAllPermissions(requiredPermissions)
      if (!hasPermission) {
        ElMessage.error('您没有访问该页面的权限')
        next('/403')
        return
      }
    }
    
    // 检查角色
    const requiredRoles = to.meta?.roles as string[] | undefined
    if (requiredRoles && requiredRoles.length > 0) {
      const hasRole = requiredRoles.some(role => userStore.hasRole(role))
      if (!hasRole) {
        ElMessage.error('您没有访问该页面的权限')
        next('/403')
        return
      }
    }
  }
  
  next()
})

// 全局后置守卫
router.afterEach((to) => {
  // 页面加载完成后的处理
  // 可以在这里添加页面访问统计等功能
  console.log(`页面访问: ${to.path}`)
})

export default router
