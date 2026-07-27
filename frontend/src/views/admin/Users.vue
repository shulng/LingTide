<template>
  <div class="admin-users">
    <h1 class="page-title">用户管理</h1>

    <el-table :data="users" v-loading="loading" style="width: 100%" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="150" />
      <el-table-column prop="nickname" label="昵称" width="150" />
      <el-table-column prop="email" label="邮箱" width="200" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'info'">
            {{ row.role === 'ADMIN' ? '管理员' : '普通用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="180">
        <template #default="{ row }">
          {{ formatTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button 
            v-if="row.status === 1" 
            size="small" 
            type="warning"
            @click="handleUpdateStatus(row.id, 0)"
          >
            禁用
          </el-button>
          <el-button 
            v-else 
            size="small" 
            type="success"
            @click="handleUpdateStatus(row.id, 1)"
          >
            启用
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
        @current-change="fetchUsers"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/api'

const loading = ref(false)
const users = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN')
}

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await api.admin.getUsers({ current: currentPage.value, size: pageSize.value })
    users.value = res.data.records
    total.value = res.data.total
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleUpdateStatus = async (id, status) => {
  try {
    await api.admin.updateUserStatus(id, status)
    ElMessage.success(status === 1 ? '已启用' : '已禁用')
    fetchUsers()
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<style lang="scss" scoped>
.admin-users {
  max-width: 1200px;
  margin: 0 auto;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
