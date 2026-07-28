<template>
  <div class="admin-users">
    <div class="page-header">
      <h1 class="page-title">用户管理</h1>
      <el-button type="primary" @click="showCreateDialog = true">
        <el-icon><Plus /></el-icon>
        新增用户
      </el-button>
    </div>

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
      <el-table-column label="操作" width="280">
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
          <el-button 
            size="small" 
            type="primary"
            @click="openPasswordDialog(row)"
          >
            改密码
          </el-button>
          <el-button 
            size="small" 
            type="primary"
            @click="openRoleDialog(row)"
          >
            改角色
          </el-button>
          <el-button 
            v-if="row.username !== 'admin'"
            size="small" 
            type="danger"
            @click="handleDelete(row.id)"
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
        @current-change="fetchUsers"
      />
    </div>

    <el-dialog v-model="showCreateDialog" title="新增用户" width="400px">
      <el-form :model="createForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="createForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="createForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="createForm.email" placeholder="请输入邮箱（可选）" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="createForm.nickname" placeholder="请输入昵称（可选）" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="createForm.role">
            <el-option label="普通用户" value="USER" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showPasswordDialog" title="修改密码" width="400px">
      <el-form :model="passwordForm" label-width="80px">
        <el-form-item label="新密码">
          <el-input v-model="passwordForm.password" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpdatePassword">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showRoleDialog" title="修改角色" width="400px">
      <el-form :model="roleForm" label-width="80px">
        <el-form-item label="角色">
          <el-select v-model="roleForm.role">
            <el-option label="普通用户" value="USER" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRoleDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpdateRole">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import api from '@/api'

const loading = ref(false)
const users = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const showCreateDialog = ref(false)
const showPasswordDialog = ref(false)
const showRoleDialog = ref(false)

const currentUserId = ref(null)

const createForm = reactive({
  username: '',
  password: '',
  email: '',
  nickname: '',
  role: 'USER'
})

const passwordForm = reactive({
  password: ''
})

const roleForm = reactive({
  role: 'USER'
})

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

const handleCreate = async () => {
  if (!createForm.username || !createForm.password) {
    ElMessage.warning('用户名和密码不能为空')
    return
  }
  try {
    await api.admin.createUser({
      username: createForm.username,
      password: createForm.password,
      email: createForm.email,
      nickname: createForm.nickname,
      role: createForm.role
    })
    ElMessage.success('创建成功')
    showCreateDialog.value = false
    createForm.username = ''
    createForm.password = ''
    createForm.email = ''
    createForm.nickname = ''
    createForm.role = 'USER'
    fetchUsers()
  } catch (e) {
    console.error(e)
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

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该用户吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.admin.deleteUser(id)
    ElMessage.success('删除成功')
    fetchUsers()
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
    }
  }
}

const openPasswordDialog = (row) => {
  currentUserId.value = row.id
  passwordForm.password = ''
  showPasswordDialog.value = true
}

const handleUpdatePassword = async () => {
  if (!passwordForm.password) {
    ElMessage.warning('密码不能为空')
    return
  }
  try {
    await api.admin.updateUserPassword(currentUserId.value, passwordForm.password)
    ElMessage.success('密码修改成功')
    showPasswordDialog.value = false
  } catch (e) {
    console.error(e)
  }
}

const openRoleDialog = (row) => {
  currentUserId.value = row.id
  roleForm.role = row.role
  showRoleDialog.value = true
}

const handleUpdateRole = async () => {
  try {
    await api.admin.updateUserRole(currentUserId.value, roleForm.role)
    ElMessage.success('角色修改成功')
    showRoleDialog.value = false
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

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  margin: 0;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
