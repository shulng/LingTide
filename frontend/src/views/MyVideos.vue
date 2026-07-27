<template>
  <div class="my-videos-page">
    <div class="page-header">
      <h1 class="page-title">我的视频</h1>
      <el-button type="primary" @click="$router.push('/upload')">
        <el-icon><Plus /></el-icon>
        上传新视频
      </el-button>
    </div>

    <div class="video-grid" v-loading="loading">
      <div v-for="video in videos" :key="video.id" class="video-card">
        <div class="video-cover" @click="$router.push(`/video/${video.id}`)">
          <span>🎬</span>
          <el-tag class="status-tag" :type="statusTagType(video.status)">
            {{ statusLabel(video.status) }}
          </el-tag>
        </div>
        <div class="video-info">
          <div class="video-title" @click="$router.push(`/video/${video.id}`)">{{ video.title }}</div>
          <div class="video-meta">
            <span>👁 {{ formatNum(video.views) }}</span>
            <span>👍 {{ video.likes }}</span>
            <span>📝 {{ video.comments }}</span>
          </div>
          <div class="video-actions">
            <el-button size="small" @click="goToVideo(video.id)">查看</el-button>
            <el-button size="small" type="danger" @click="handleDelete(video.id)">删除</el-button>
          </div>
        </div>
      </div>
    </div>

    <div class="empty-state" v-if="!loading && videos.length === 0">
      <div class="icon">📹</div>
      <p>还没有上传视频</p>
      <el-button type="primary" @click="$router.push('/upload')">立即上传</el-button>
    </div>

    <div class="pagination" v-if="totalPages > 1">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        background
        @current-change="fetchVideos"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import api from '@/api'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const videos = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

const formatNum = (num) => {
  if (!num) return 0
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num
}

const statusTagType = (status) => {
  switch (status) {
    case 'PUBLISHED': return 'success'
    case 'DRAFT': return 'warning'
    case 'DELETED': return 'danger'
    default: return 'info'
  }
}

const statusLabel = (status) => {
  switch (status) {
    case 'PUBLISHED': return '已发布'
    case 'DRAFT': return '草稿'
    case 'DELETED': return '已删除'
    default: return status
  }
}

const fetchVideos = async () => {
  loading.value = true
  try {
    const res = await api.videos.getMine({ current: currentPage.value, size: pageSize.value })
    videos.value = res.data.records
    total.value = res.data.total
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const goToVideo = (id) => {
  router.push({ name: 'VideoPlayer', params: { id } })
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个视频吗？', '删除确认', {
      type: 'warning'
    })
    await api.videos.delete(id)
    ElMessage.success('删除成功')
    fetchVideos()
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
    }
  }
}

onMounted(() => {
  fetchVideos()
})
</script>

<style lang="scss" scoped>
.my-videos-page {
  max-width: 1200px;
  margin: 0 auto;
}

.video-info {
  position: relative;
}

.video-actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

.status-tag {
  position: absolute;
  top: 8px;
  right: 8px;
}
</style>
