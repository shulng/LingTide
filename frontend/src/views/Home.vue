<template>
  <div class="home-page">
    <div class="page-header">
      <h1 class="page-title">发现精彩</h1>
    </div>

    <div class="category-tabs" ref="categoryTabs">
      <button 
        v-for="cat in categories" 
        :key="cat.id"
        :class="['category-tab', { active: selectedCategory === cat.id }]"
        @click="selectCategory(cat.id)"
      >
        {{ cat.name }}
      </button>
    </div>

    <div class="content-section" v-if="!selectedCategory && !searchQuery">
      <h2 class="section-title">🔥 热门推荐</h2>
      <div class="video-grid" v-loading="loading">
        <div v-for="video in recommendedVideos" :key="video.id" class="video-card" @click="goToVideo(video.id)">
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
    </div>

    <div class="content-section">
      <h2 class="section-title" v-if="searchQuery">🔍 搜索结果: {{ searchQuery }}</h2>
      <h2 class="section-title" v-else-if="selectedCategory">📂 {{ currentCategoryName }}</h2>
      <h2 class="section-title" v-else>🕐 最新上传</h2>
      
      <div class="video-grid" v-loading="loading">
        <div v-for="video in videos" :key="video.id" class="video-card" @click="goToVideo(video.id)">
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
        <div class="icon">📭</div>
        <p>暂无视频</p>
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
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/api'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const categories = ref([])
const recommendedVideos = ref([])
const videos = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const selectedCategory = ref(null)
const searchQuery = ref('')

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))
const currentCategoryName = computed(() => {
  const cat = categories.value.find(c => c.id === selectedCategory.value)
  return cat ? cat.name : ''
})

const formatNum = (num) => {
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num
}

const fetchCategories = async () => {
  try {
    const res = await api.categories.getAll()
    categories.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const fetchRecommended = async () => {
  try {
    const res = await api.videos.getRecommended()
    recommendedVideos.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const fetchVideos = async () => {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value
    }
    if (selectedCategory.value) {
      params.categoryId = selectedCategory.value
    }
    if (searchQuery.value) {
      params.keyword = searchQuery.value
    }
    
    if (!selectedCategory.value && !searchQuery.value) {
      const res = await api.videos.getLatest(params)
      videos.value = res.data
      total.value = 0
    } else {
      const res = await api.videos.getList(params)
      videos.value = res.data.records
      total.value = res.data.total
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const selectCategory = (id) => {
  selectedCategory.value = selectedCategory.value === id ? null : id
  currentPage.value = 1
  fetchVideos()
}

const goToVideo = (id) => {
  router.push({ name: 'VideoPlayer', params: { id } })
}

watch(() => route.query.keyword, (val) => {
  searchQuery.value = val || ''
  currentPage.value = 1
  fetchVideos()
})

onMounted(async () => {
  await fetchCategories()
  await fetchRecommended()
  searchQuery.value = route.query.keyword || ''
  fetchVideos()
})
</script>

<style lang="scss" scoped>
.home-page {
  .section-title {
    font-size: 18px;
    margin-bottom: 16px;
    color: #fff;
  }
  .content-section {
    margin-top: 32px;
  }
}
</style>
