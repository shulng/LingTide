import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const request = axios.create({
  baseURL: '/api',
  timeout: 60000
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200) {
      return res
    } else if (res.code === 401) {
      const userStore = useUserStore()
      userStore.logout()
      ElMessage.error('登录已过期，请重新登录')
      window.location.href = '/login'
      return Promise.reject(new Error(res.message))
    } else {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
  },
  (error) => {
    if (error.response?.status === 401) {
      const userStore = useUserStore()
      userStore.logout()
      ElMessage.error('登录已过期，请重新登录')
      window.location.href = '/login'
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default {
  auth: {
    login: (data) => request.post('/auth/login', data),
    register: (data) => request.post('/auth/register', data)
  },
  users: {
    getById: (id) => request.get(`/users/${id}`),
    getMe: () => request.get('/users/me'),
    updateMe: (data) => request.put('/users/me', data)
  },
  videos: {
    upload: (formData) => request.post('/videos/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    }),
    getList: (params) => request.get('/videos/public/list', { params }),
    getRecommended: () => request.get('/videos/public/recommended'),
    getLatest: () => request.get('/videos/public/latest'),
    getById: (id) => request.get(`/videos/${id}`),
    getByUser: (userId, params) => request.get(`/videos/user/${userId}`, { params }),
    getMine: (params) => request.get('/videos/my', { params }),
    getFavorites: (params) => request.get('/videos/favorites', { params }),
    delete: (id) => request.delete(`/videos/${id}`),
    toggleLike: (id) => request.post(`/videos/${id}/like`),
    toggleFavorite: (id) => request.post(`/videos/${id}/favorite`)
  },
  comments: {
    create: (videoId, data) => request.post('/comments', data, { params: { videoId } }),
    getByVideo: (videoId) => request.get(`/comments/video/${videoId}`),
    getCount: (videoId) => request.get(`/comments/video/${videoId}/count`)
  },
  categories: {
    getAll: () => request.get('/categories'),
    getById: (id) => request.get(`/categories/${id}`)
  },
  admin: {
    getStats: () => request.get('/admin/stats'),
    getUsers: (params) => request.get('/admin/users', { params }),
    updateUserStatus: (id, status) => request.put(`/admin/users/${id}/status`, null, { params: { status } }),
    getVideos: (params) => request.get('/admin/videos', { params }),
    updateVideoStatus: (id, status) => request.put(`/admin/videos/${id}/status`, null, { params: { status } }),
    createCategory: (name, description) => request.post('/admin/categories', null, { params: { name, description } })
  }
}
