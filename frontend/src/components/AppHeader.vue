<template>
  <header class="app-header">
    <div class="header-content">
      <div class="logo" @click="$router.push('/')">
        <span class="logo-icon">🎬</span>
        <span class="logo-text">LingTide</span>
      </div>
      
      <nav class="nav-menu" v-if="userStore.isLoggedIn">
        <router-link to="/" class="nav-item">首页</router-link>
        <router-link to="/my" class="nav-item">我的视频</router-link>
        <router-link to="/favorites" class="nav-item">收藏</router-link>
        <router-link v-if="userStore.isAdmin" to="/admin" class="nav-item admin-link">管理后台</router-link>
      </nav>

      <div class="search-box">
        <el-input
          v-model="searchQuery"
          placeholder="搜索视频..."
          @keyup.enter="doSearch"
          clearable
          size="default"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>

      <div class="user-area">
        <template v-if="userStore.isLoggedIn">
          <el-button type="primary" @click="$router.push('/upload')">
            <el-icon><Upload /></el-icon>
            上传
          </el-button>
          <el-dropdown>
            <div class="user-info" @click.stop>
              <el-avatar :size="36" :src="userStore.user?.avatar">
                {{ userStore.user?.nickname?.[0] || 'U' }}
              </el-avatar>
              <span class="username">{{ userStore.user?.nickname || userStore.user?.username }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push(`/user/${userStore.user?.id}`)">
                  个人主页
                </el-dropdown-item>
                <el-dropdown-item @click="$router.push('/my')">
                  我的视频
                </el-dropdown-item>
                <el-dropdown-item @click="$router.push('/favorites')">
                  我的收藏
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button @click="$router.push('/login')">登录</el-button>
          <el-button type="primary" @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const searchQuery = ref('')

const doSearch = () => {
  if (searchQuery.value.trim()) {
    router.push({ name: 'Search', query: { keyword: searchQuery.value.trim() } })
  }
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style lang="scss" scoped>
.app-header {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  padding: 0 20px;
  height: 64px;
  position: sticky;
  top: 0;
  z-index: 1000;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  gap: 20px;
}

.logo {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: transform 0.2s;
}

.logo:hover {
  transform: scale(1.05);
}

.logo-icon {
  font-size: 28px;
}

.logo-text {
  font-size: 22px;
  font-weight: bold;
  color: #fff;
  background: linear-gradient(90deg, #ff6b6b, #ffd93d);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.nav-menu {
  display: flex;
  gap: 4px;
}

.nav-item {
  color: #ccc;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.2s;
}

.nav-item:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
}

.nav-item.router-link-active {
  color: #fff;
  background: rgba(255, 107, 107, 0.2);
}

.admin-link {
  color: #ffd93d;
}

.search-box {
  flex: 1;
  max-width: 400px;
}

.user-area {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 12px;
  border-radius: 20px;
  transition: background 0.2s;
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.1);
}

.username {
  color: #fff;
  font-size: 14px;
}

:deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.1);
  box-shadow: none;
}

:deep(.el-input__inner) {
  color: #fff;
}

:deep(.el-input__inner::placeholder) {
  color: #999;
}
</style>
