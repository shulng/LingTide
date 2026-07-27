<template>
  <div class="search-page">
    <div class="search-header">
      <h1 class="page-title">搜索: {{ keyword }}</h1>
      <p class="result-count">找到 {{ total }} 个结果</p>
    </div>

    <div class="video-grid" v-loading="loading">
      <div v-for="video in videos" :key="video.id" class="video-card" @click="$router.push(`/video/${video.id}`)">
        <div class="video-cover">
          <span>🎬</span>
        </div>
        <div class="video-info">
          <div class="video-title">{{ video.title }}</div>
          <div class="video-meta">
            <span>{{ video.username }}</span>
            <div class="video-stats">
              <span class="stat-item">👁 {{ formatNum(video.views) }}</span>
              <span class="stat-item">👍 {{ video.likes }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="empty-state" v-if="!loading && videos.length === 0">
      <div class="icon">🔍</div>
      <p>没有找到相关视频</p>
      <p class="hint">试试其他关键词吧</p>
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
import { useRoute } from 'vue-router'
import api from '@/api'

const route = useRoute()

const loading = ref(false)
const videos = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

const keyword = computed(() => route.query.keyword || '')
const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

const formatNum = (num) => {
  if (!num) return 0
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num
}

const fetchVideos = async () => {
  loading.value = true
  try {
    const res = await api.videos.getList({
      keyword: keyword.value,
      current: currentPage.value,
      size: pageSize.value
    })
    videos.value = res.data.records
    total.value = res.data.total
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (keyword.value) {
    fetchVideos()
  }
})
</script>

<style lang="scss" scoped>
.search-page {
  max-width: 1200px;
  margin: 0 auto;
}

.search-header {
  margin-bottom: 24px;
  
  .result-count {
    color: #999;
  }
}

.hint {
  color: #666;
  margin-top: 8px;
}
</style>
