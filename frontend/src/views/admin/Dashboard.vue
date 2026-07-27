<template>
  <div class="admin-dashboard">
    <h1 class="page-title">管理后台</h1>
    
    <div class="stats-cards" v-loading="loading">
      <div class="stat-card">
        <div class="stat-icon users">👥</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalUsers }}</div>
          <div class="stat-label">总用户数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon videos">📹</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalVideos }}</div>
          <div class="stat-label">总视频数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon published">✅</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.publishedVideos }}</div>
          <div class="stat-label">已发布视频</div>
        </div>
      </div>
    </div>

    <div class="admin-actions">
      <h2>快捷操作</h2>
      <div class="action-buttons">
        <el-button type="primary" @click="$router.push('/admin/users')">
          👥 用户管理
        </el-button>
        <el-button type="primary" @click="$router.push('/admin/videos')">
          📹 视频管理
        </el-button>
        <el-button @click="showCategoryDialog = true">
          ➕ 添加分类
        </el-button>
      </div>
    </div>

    <el-dialog v-model="showCategoryDialog" title="添加分类">
      <el-form :model="categoryForm" label-position="top">
        <el-form-item label="分类名称">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类描述">
          <el-input v-model="categoryForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCategoryDialog = false">取消</el-button>
        <el-button type="primary" @click="createCategory">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/api'

const loading = ref(false)
const stats = ref({ totalUsers: 0, totalVideos: 0, publishedVideos: 0 })
const showCategoryDialog = ref(false)
const categoryForm = reactive({ name: '', description: '' })

const fetchStats = async () => {
  loading.value = true
  try {
    const res = await api.admin.getStats()
    stats.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const createCategory = async () => {
  if (!categoryForm.name) {
    ElMessage.warning('请输入分类名称')
    return
  }
  try {
    await api.admin.createCategory(categoryForm.name, categoryForm.description)
    ElMessage.success('创建成功')
    showCategoryDialog.value = false
    categoryForm.name = ''
    categoryForm.description = ''
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<style lang="scss" scoped>
.admin-dashboard {
  max-width: 1200px;
  margin: 0 auto;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  background: #1a1a2e;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  font-size: 48px;
}

.stat-info {
  .stat-value {
    font-size: 28px;
    font-weight: bold;
    color: #ff6b6b;
  }
  
  .stat-label {
    color: #999;
    font-size: 14px;
  }
}

.admin-actions {
  background: #1a1a2e;
  border-radius: 12px;
  padding: 24px;
  
  h2 {
    margin-bottom: 16px;
  }
  
  .action-buttons {
    display: flex;
    gap: 12px;
  }
}
</style>
