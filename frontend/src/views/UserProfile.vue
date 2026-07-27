<template>
  <div class="user-profile-page" v-loading="loading">
    <div class="profile-header" v-if="user">
      <el-avatar :size="80" :src="user.avatar">
        {{ user.nickname?.[0] || user.username?.[0] }}
      </el-avatar>
      <div class="user-info">
        <h1>{{ user.nickname || user.username }}</h1>
        <p class="bio">{{ user.bio || '这个人很懒，什么都没留下' }}</p>
        <p class="email" v-if="user.email">📧 {{ user.email }}</p>
        <p class="join-time">加入时间: {{ formatTime(user.createTime) }}</p>
      </div>
      <el-button 
        v-if="isSelf" 
        type="primary" 
        @click="showEditDialog = true"
      >
        编辑资料
      </el-button>
    </div>

    <div class="user-videos" v-if="user">
      <h2>📹 视频 ({{ total }})</h2>
      <div class="video-grid">
        <div v-for="video in videos" :key="video.id" class="video-card" @click="$router.push(`/video/${video.id}`)">
          <div class="video-cover">
            <span>🎬</span>
          </div>
          <div class="video-info">
            <div class="video-title">{{ video.title }}</div>
            <div class="video-meta">
              <span>👁 {{ formatNum(video.views) }}</span>
              <span>👍 {{ video.likes }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="empty-state" v-if="videos.length === 0">
        <div class="icon">📭</div>
        <p>还没有上传视频</p>
      </div>
    </div>

    <el-dialog v-model="showEditDialog" title="编辑资料" v-if="isSelf">
      <el-form :model="editForm" label-position="top">
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" />
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input v-model="editForm.bio" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="saveProfile">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import api from '@/api'

const route = useRoute()
const userStore = useUserStore()

const loading = ref(true)
const user = ref(null)
const videos = ref([])
const total = ref(0)
const showEditDialog = ref(false)
const editForm = ref({
  nickname: '',
  email: '',
  bio: ''
})

const userId = computed(() => route.params.id)
const isSelf = computed(() => userStore.user?.id === Number(userId.value))

const formatNum = (num) => {
  if (!num) return 0
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleDateString('zh-CN')
}

const fetchUser = async () => {
  loading.value = true
  try {
    const res = await api.users.getById(userId.value)
    user.value = res.data
    if (isSelf.value) {
      editForm.value = {
        nickname: res.data.nickname || '',
        email: res.data.email || '',
        bio: res.data.bio || ''
      }
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const fetchUserVideos = async () => {
  try {
    const res = await api.videos.getByUser(userId.value, { current: 1, size: 12 })
    videos.value = res.data.records
    total.value = res.data.total
  } catch (e) {
    console.error(e)
  }
}

const saveProfile = async () => {
  try {
    await userStore.updateUser(editForm.value)
    ElMessage.success('更新成功')
    showEditDialog.value = false
    fetchUser()
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  fetchUser()
  fetchUserVideos()
})
</script>

<style lang="scss" scoped>
.user-profile-page {
  max-width: 1000px;
  margin: 0 auto;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 24px;
  background: #1a1a2e;
  padding: 32px;
  border-radius: 12px;
  margin-bottom: 32px;
  
  .user-info {
    flex: 1;
    
    h1 {
      margin-bottom: 8px;
    }
    
    .bio {
      color: #999;
      margin-bottom: 8px;
    }
    
    .email, .join-time {
      font-size: 14px;
      color: #666;
    }
  }
}

.user-videos h2 {
  margin-bottom: 16px;
}
</style>
