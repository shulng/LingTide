import { defineStore } from 'pinia'
import api from '@/api'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user') || 'null'),
    token: localStorage.getItem('token') || ''
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.user?.role === 'ADMIN'
  },
  actions: {
    async login(credentials) {
      const res = await api.auth.login(credentials)
      this.token = res.data.token
      this.user = res.data.user
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('user', JSON.stringify(res.data.user))
      return res.data
    },
    async register(userData) {
      const res = await api.auth.register(userData)
      return res.data
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    },
    loadUser() {
      const user = localStorage.getItem('user')
      if (user) {
        this.user = JSON.parse(user)
      }
    },
    async updateUser(data) {
      const res = await api.users.updateMe(data)
      this.user = res.data
      localStorage.setItem('user', JSON.stringify(res.data))
      return res.data
    }
  }
})
