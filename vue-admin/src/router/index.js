import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/',
    component: () => import('@/layouts/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '系统概览' }
      },
      {
        path: 'dept',
        name: 'Dept',
        component: () => import('@/views/Dept.vue'),
        meta: { title: '科室管理' }
      },
      {
        path: 'doctor',
        name: 'Doctor',
        component: () => import('@/views/Doctor.vue'),
        meta: { title: '医生管理' }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/User.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'log',
        name: 'Log',
        component: () => import('@/views/Log.vue'),
        meta: { title: '系统日志' }
      },
      {
        path: 'setting',
        name: 'Setting',
        component: () => import('@/views/Setting.vue'),
        meta: { title: '系统配置' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  if (to.path !== '/login' && !userStore.token) {
    next('/login')
  } else {
    next()
  }
})

export default router