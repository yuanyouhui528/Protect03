import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { ElMessage } from 'element-plus'

// 布局组件
const DefaultLayout = () => import('@/layouts/DefaultLayout.vue')

// 页面组件
const Dashboard = () => import('@/views/Dashboard.vue')
const Login = () => import('@/views/Login.vue')

// 线索管理模块
const LeadList = () => import('@/views/leads/LeadList.vue')
const LeadCreate = () => import('@/views/leads/LeadCreate.vue')
const LeadFavorites = () => import('@/views/leads/LeadFavorites.vue')

// 交换中心模块
const ExchangeMarket = () => import('@/views/exchange/ExchangeMarket.vue')
const ExchangeHistory = () => import('@/views/exchange/ExchangeHistory.vue')
const ExchangeRules = () => import('@/views/exchange/ExchangeRules.vue')

// 数据分析模块
const PersonalAnalytics = () => import('@/views/analytics/PersonalAnalytics.vue')
const SystemAnalytics = () => import('@/views/analytics/SystemAnalytics.vue')

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

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta?.title) {
    document.title = `${to.meta.title} - ${import.meta.env.VITE_APP_TITLE}`
  }
  
  // 检查是否需要登录
  if (to.meta?.requiresAuth !== false) {
    const token = localStorage.getItem('access_token')
    if (!token) {
      ElMessage.warning('请先登录')
      next('/login')
      return
    }
  }
  
  // 如果已登录用户访问登录页，重定向到首页
  if (to.path === '/login') {
    const token = localStorage.getItem('access_token')
    if (token) {
      next('/')
      return
    }
  }
  
  next()
})

export default router
