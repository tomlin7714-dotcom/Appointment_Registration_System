import { createRouter, createWebHistory } from 'vue-router'
import { useDoctorStore } from '@/stores/doctor'

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
        meta: { title: '工作台首页' }
      },
      {
        path: 'schedule',
        name: 'Schedule',
        component: () => import('@/views/Schedule.vue'),
        meta: { title: '排班管理' }
      },
      {
        path: 'number-source',
        name: 'NumberSource',
        component: () => import('@/views/NumberSource.vue'),
        meta: { title: '号源管理' }
      },
      {
        path: 'appointment',
        name: 'Appointment',
        component: () => import('@/views/Appointment.vue'),
        meta: { title: '预约列表' }
      },
      {
        path: 'call',
        name: 'Call',
        component: () => import('@/views/Call.vue'),
        meta: { title: '诊间叫号' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人信息' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const doctorStore = useDoctorStore()
  if (to.path !== '/login' && !doctorStore.token) {
    next('/login')
  } else {
    next()
  }
})

export default router