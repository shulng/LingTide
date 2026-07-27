<template>
  <div class="video-player-page" v-loading="loading">
    <div class="video-container" v-if="video">
      <div class="video-player">
        <video 
          ref="videoElement"
          class="video-js"
          controls
          autoplay
        >
          <source :src="videoUrl" :type="'video/' + (video.format || 'mp4')" />
          您的浏览器不支持视频播放
        </video>
      </div>

      <div class="video-info-panel">
        <h1 class="video-title">{{ video.title }}</h1>
        
        <div class="video-actions">
          <div class="action-buttons">
            <el-button 
              :type="video.liked ? 'primary' : ''" 
              @click="handleLike"
              :disabled="!userStore.isLoggedIn"
            >
              <el-icon><Star /></el-icon>
              {{ video.likes }}
            </el-button>
            <el-button 
              :type="video.favorited ? 'primary' : ''" 
              @click="handleFavorite"
              :disabled="!userStore.isLoggedIn"
            >
              <el-icon><Collection /></el-icon>
              收藏
            </el-button>
          </div>
        </div>

        <div class="video-meta-info">
          <div class="uploader" @click="$router.push(`/user/${video.userId}`)">
            <el-avatar :size="40" :src="video.userAvatar">
              {{ video.username?.[0] }}
            </el-avatar>
            <div class="uploader-info">
              <span class="uploader-name">{{ video.username }}</span>
              <span class="upload-time">{{ formatTime(video.createTime) }}</span>
            </div>
          </div>
          <div class="video-stats-detail">
            <span>👁 {{ formatNum(video.views) }} 播放</span>
            <span>📝 {{ video.comments }} 评论</span>
          </div>
        </div>

        <div class="video-description" v-if="video.description">
          <p>{{ video.description }}</p>
        </div>

        <div class="comments-section">
          <h3>评论 ({{ comments.length }})</h3>
          
          <div class="comment-input" v-if="userStore.isLoggedIn">
            <el-input 
              v-model="newComment" 
              placeholder="发表评论..."
              type="textarea"
              :rows="2"
              @keyup.enter="submitComment"
            />
            <el-button type="primary" @click="submitComment" style="margin-top: 8px">发送评论</el-button>
          </div>
          <div v-else class="login-tip">
            <p>请<router-link to="/login">登录</router-link>后发表评论</p>
          </div>

          <div class="comments-list">
            <div v-for="comment in comments" :key="comment.id" class="comment-item">
              <el-avatar :size="36" :src="comment.userAvatar">
                {{ comment.username?.[0] }}
              </el-avatar>
              <div class="comment-content">
                <div class="comment-header">
                  <span class="commenter-name">{{ comment.username }}</span>
                  <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
                </div>
                <p class="comment-text">{{ comment.content }}</p>
              </div>
            </div>
            <div v-if="comments.length === 0" class="empty-comments">
              暂无评论，快来发表第一条吧！
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import api from '@/api'

const route = useRoute()
const userStore = useUserStore()

const loading = ref(true)
const video = ref(null)
const comments = ref([])
const newComment = ref('')
const videoElement = ref(null)

const videoId = computed(() => route.params.id)
const videoUrl = computed(() => `/api/videos/stream/${videoId.value}`)

const formatNum = (num) => {
  if (!num) return 0
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w'
  if (num >= 1000) return (num / 1000).toFixed(1) + 'k'
  return num
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN')
}

const fetchVideo = async () => {
  loading.value = true
  try {
    const res = await api.videos.getById(videoId.value)
    video.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const fetchComments = async () => {
  try {
    const res = await api.comments.getByVideo(videoId.value)
    comments.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const handleLike = async () => {
  try {
    const res = await api.videos.toggleLike(videoId.value)
    video.value.liked = res.data
    video.value.likes += res.data ? 1 : -1
  } catch (e) {
    console.error(e)
  }
}

const handleFavorite = async () => {
  try {
    const res = await api.videos.toggleFavorite(videoId.value)
    video.value.favorited = res.data
    video.value.favorites += res.data ? 1 : -1
  } catch (e) {
    console.error(e)
  }
}

const submitComment = async () => {
  if (!newComment.value.trim()) return
  try {
    await api.comments.create(videoId.value, { content: newComment.value })
    ElMessage.success('评论成功')
    newComment.value = ''
    fetchComments()
  } catch (e) {
    console.error(e)
  }
}

onMounted(async () => {
  await fetchVideo()
  await fetchComments()
  
  await nextTick()
  if (videoElement.value) {
    // Video.js initialization if needed
  }
})
</script>

<style lang="scss" scoped>
.video-player-page {
  max-width: 1200px;
  margin: 0 auto;
}

.video-container {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
}

.video-player {
  background: #000;
  border-radius: 12px;
  overflow: hidden;
  
  video {
    width: 100%;
    max-height: 70vh;
  }
}

.video-info-panel {
  background: #1a1a2e;
  border-radius: 12px;
  padding: 24px;
}

.video-title {
  font-size: 20px;
  margin-bottom: 16px;
}

.video-actions {
  margin-bottom: 24px;
  
  .action-buttons {
    display: flex;
    gap: 12px;
  }
}

.video-meta-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-top: 1px solid #333;
  border-bottom: 1px solid #333;
  
  .uploader {
    display: flex;
    align-items: center;
    gap: 12px;
    cursor: pointer;
  }
  
  .uploader-name {
    display: block;
    font-weight: 500;
  }
  
  .upload-time {
    font-size: 12px;
    color: #999;
  }
  
  .video-stats-detail {
    color: #999;
    font-size: 14px;
    
    span {
      margin-left: 16px;
    }
  }
}

.video-description {
  margin: 16px 0;
  padding: 16px;
  background: #0f0f23;
  border-radius: 8px;
  line-height: 1.6;
}

.comments-section {
  margin-top: 24px;
  
  h3 {
    margin-bottom: 16px;
  }
}

.comment-input {
  margin-bottom: 24px;
  
  .login-tip {
    padding: 16px;
    text-align: center;
    background: #0f0f23;
    border-radius: 8px;
    
    a {
      color: #ff6b6b;
      text-decoration: none;
    }
  }
}

.comments-list {
  .comment-item {
    display: flex;
    gap: 12px;
    padding: 16px 0;
    border-bottom: 1px solid #333;
    
    .comment-content {
      flex: 1;
    }
    
    .comment-header {
      margin-bottom: 8px;
      
      .commenter-name {
        font-weight: 500;
        margin-right: 8px;
      }
      
      .comment-time {
        font-size: 12px;
        color: #999;
      }
    }
    
    .comment-text {
      line-height: 1.6;
    }
  }
}

.empty-comments {
  text-align: center;
  padding: 32px;
  color: #999;
}
</style>
