import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { useUserStore } from '@/stores/modules/user'
import { usePermissionStore } from '@/stores/modules/permission'

NProgress.configure({ showSpinner: false })

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: {
      title: '登录',
      hidden: true,
      noAuth: true
    }
  },
  {
    path: '/404',
    name: '404',
    component: () => import('@/views/error/404.vue'),
    meta: {
      title: '页面不存在',
      hidden: true,
      noAuth: true
    }
  },
  {
    path: '/403',
    name: '403',
    component: () => import('@/views/error/403.vue'),
    meta: {
      title: '访问被拒绝',
      hidden: true,
      noAuth: true
    }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: {
          title: '仪表板',
          icon: 'Dashboard',
          affix: true
        }
      }
    ]
  },
  {
    path: '/system',
    component: () => import('@/layout/index.vue'),
    redirect: '/system/user',
    meta: {
      title: '系统管理',
      icon: 'Setting',
      roles: ['ADMIN', 'MANAGER']
    },
    children: [
      {
        path: '/system/user',
        name: 'User',
        component: () => import('@/views/system/user/index.vue'),
        meta: {
          title: '用户管理',
          icon: 'User',
          roles: ['ADMIN']
        }
      },
      {
        path: '/system/role',
        name: 'Role',
        component: () => import('@/views/system/role/index.vue'),
        meta: {
          title: '角色管理',
          icon: 'UserFilled',
          roles: ['ADMIN']
        }
      },
      {
        path: '/system/permission',
        name: 'Permission',
        component: () => import('@/views/system/permission/index.vue'),
        meta: {
          title: '权限管理',
          icon: 'Key',
          roles: ['ADMIN']
        }
      },
      {
        path: '/system/config',
        name: 'Config',
        component: () => import('@/views/system/config/index.vue'),
        meta: {
          title: '系统配置',
          icon: 'Tools',
          roles: ['ADMIN']
        }
      }
    ]
  },
  {
    path: '/monitor',
    component: () => import('@/layout/index.vue'),
    redirect: '/monitor/log',
    meta: {
      title: '系统监控',
      icon: 'Monitor',
      roles: ['ADMIN', 'MANAGER']
    },
    children: [
      {
        path: '/monitor/log',
        name: 'Log',
        component: () => import('@/views/monitor/log/index.vue'),
        meta: {
          title: '操作日志',
          icon: 'Document',
          roles: ['ADMIN', 'MANAGER']
        }
      },
      {
        path: '/monitor/metrics',
        name: 'Metrics',
        component: () => import('@/views/monitor/metrics/index.vue'),
        meta: {
          title: '系统指标',
          icon: 'TrendCharts',
          roles: ['ADMIN', 'MANAGER']
        }
      }
    ]
  },
  // 404页面必须放在最后
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404',
    meta: {
      hidden: true
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
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
router.beforeEach(async (to, from, next) => {
  NProgress.start()
  
  const userStore = useUserStore()
  const permissionStore = usePermissionStore()
  
  const hasToken = userStore.token
  
  if (hasToken) {
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else {
      const hasRoles = userStore.roles && userStore.roles.length > 0
      
      if (hasRoles) {
        // 检查权限
        if (to.meta.roles && !to.meta.roles.some(role => userStore.roles.includes(role))) {
          next('/403')
          NProgress.done()
        } else {
          next()
        }
      } else {
        try {
          // 获取用户信息
          await userStore.getUserInfo()
          
          // 生成可访问的路由
          const accessRoutes = permissionStore.generateRoutes(userStore.roles)
          
          // 动态添加路由
          accessRoutes.forEach(route => {
            router.addRoute(route)
          })
          
          next({ ...to, replace: true })
        } catch (error) {
          userStore.resetToken()
          next(`/login?redirect=${to.path}`)
          NProgress.done()
        }
      }
    }
  } else {
    if (to.meta.noAuth) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})

export default router