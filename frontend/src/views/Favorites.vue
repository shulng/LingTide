<template>
  <div class="favorites-page">
    <div class="page-header">
      <h1 class="page-title">我的收藏</h1>
    </div>

    <div class="video-grid" v-loading="loading">
      <div v-for="item in favorites" :key="item.id" class="video-card" @click="$router.push(`/video/${item.videoId}`)">
        <div class="video-cover">
          <span>🎬</span>
        </div>
        <div class="video-info">
          <div class="video-title">{{ item.videoTitle }}</div>
          <div class="video-meta">
            <span>{{ item.username }}</span>
            <span>收藏于 {{ formatTime(item.createTime) }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="empty-state" v-if="!loading && favorites.length === 0">
      <div class="icon">⭐</div>
      <p>还没有收藏任何视频</p>
      <router-link to="/" class="go-home">去首页发现精彩</router-link>
    </div>

    <div class="pagination" v-if="totalPages > 1">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        background
        @current-change="fetchFavorites"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api'

const router = useRouter()

const loading = ref(false)
const favorites = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleDateString('zh-CN')
}

const fetchFavorites = async () => {
  loading.value = true
  try {
    const res = await api.videos.getFavorites({ current: currentPage.value, size: pageSize.value })
    favorites.value = res.data.records
    total.value = res.data.total
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchFavorites()
})
</script>

<style lang="scss" scoped>
.favorites-page {
  max-width: 1200px;
  margin: 0 auto;
}

.go-home {
  color: #ff6b6b;
  text-decoration: none;
  font-size: 16px;
}
</style>
