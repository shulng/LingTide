<template>
  <div class="admin-videos">
    <h1 class="page-title">视频管理</h1>

    <el-table :data="videos" v-loading="loading" style="width: 100%" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" width="250" show-overflow-tooltip />
      <el-table-column prop="username" label="上传者" width="120" />
      <el-table-column prop="categoryName" label="分类" width="100" />
      <el-table-column prop="views" label="播放量" width="100" />
      <el-table-column prop="likes" label="点赞" width="80" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">
            {{ statusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="上传时间" width="180">
        <template #default="{ row }">
          {{ formatTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button 
            v-if="row.status === 'PUBLISHED'" 
            size="small" 
            type="warning"
            @click="handleUpdateStatus(row.id, 'DRAFT')"
          >
            下架
          </el-button>
          <el-button 
            v-else-if="row.status === 'DRAFT'" 
            size="small" 
            type="success"
            @click="handleUpdateStatus(row.id, 'PUBLISHED')"
          >
            发布
          </el-button>
          <el-button 
            v-if="row.status !== 'DELETED'" 
            size="small" 
            type="danger"
            @click="handleUpdateStatus(row.id, 'DELETED')"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        background
        @current-change="fetchVideos"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/api'

const loading = ref(false)
const videos = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN')
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
    case 'DRAFT': return '草稿/下架'
    case 'DELETED': return '已删除'
    default: return status
  }
}

const fetchVideos = async () => {
  loading.value = true
  try {
    const res = await api.admin.getVideos({ current: currentPage.value, size: pageSize.value })
    videos.value = res.data.records
    total.value = res.data.total
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleUpdateStatus = async (id, status) => {
  try {
    await api.admin.updateVideoStatus(id, status)
    ElMessage.success('更新成功')
    fetchVideos()
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  fetchVideos()
})
</script>

<style lang="scss" scoped>
.admin-videos {
  max-width: 1400px;
  margin: 0 auto;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
