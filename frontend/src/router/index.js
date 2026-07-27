import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册', guest: true }
  },
  {
    path: '/video/:id',
    name: 'VideoPlayer',
    component: () => import('@/views/VideoPlayer.vue'),
    meta: { title: '视频播放' }
  },
  {
    path: '/upload',
    name: 'Upload',
    component: () => import('@/views/Upload.vue'),
    meta: { title: '上传视频', requiresAuth: true }
  },
  {
    path: '/user/:id',
    name: 'UserProfile',
    component: () => import('@/views/UserProfile.vue'),
    meta: { title: '用户主页' }
  },
  {
    path: '/my',
    name: 'MyVideos',
    component: () => import('@/views/MyVideos.vue'),
    meta: { title: '我的视频', requiresAuth: true }
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: () => import('@/views/Favorites.vue'),
    meta: { title: '我的收藏', requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/admin/Dashboard.vue'),
    meta: { title: '管理后台', requiresAdmin: true }
  },
  {
    path: '/admin/users',
    name: 'AdminUsers',
    component: () => import('@/views/admin/Users.vue'),
    meta: { title: '用户管理', requiresAdmin: true }
  },
  {
    path: '/admin/videos',
    name: 'AdminVideos',
    component: () => import('@/views/admin/Videos.vue'),
    meta: { title: '视频管理', requiresAdmin: true }
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('@/views/Search.vue'),
    meta: { title: '搜索' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = localStorage.getItem('token')
  
  if (token && !userStore.isLoggedIn) {
    userStore.loadUser()
  }

  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else if (to.meta.requiresAdmin && (!userStore.isLoggedIn || !userStore.isAdmin)) {
    next({ name: 'Home' })
  } else if (to.meta.guest && userStore.isLoggedIn) {
    next({ name: 'Home' })
  } else {
    next()
  }
})

router.afterEach((to) => {
  if (to.meta.title) {
    document.title = `${to.meta.title} - LingTide`
  }
})

export default router
