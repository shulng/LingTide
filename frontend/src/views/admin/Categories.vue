<template>
  <div class="admin-categories">
    <div class="page-header">
      <h1 class="page-title">分类管理</h1>
      <el-button type="primary" @click="openCreateDialog">
        <el-icon><Plus /></el-icon>
        新增分类
      </el-button>
    </div>

    <el-table :data="categories" v-loading="loading" style="width: 100%" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分类名称" width="200" />
      <el-table-column prop="description" label="描述" min-width="250">
        <template #default="{ row }">
          {{ row.description || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="sortOrder" label="排序" width="100" />
      <el-table-column prop="videoCount" label="视频数" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button 
            size="small" 
            type="primary"
            @click="openEditDialog(row)"
          >
            编辑
          </el-button>
          <el-button 
            v-if="row.status === 1" 
            size="small" 
            type="warning"
            @click="handleToggleStatus(row.id, 0)"
          >
            禁用
          </el-button>
          <el-button 
            v-else 
            size="small" 
            type="success"
            @click="handleToggleStatus(row.id, 1)"
          >
            启用
          </el-button>
          <el-button 
            size="small" 
            type="danger"
            @click="handleDelete(row.id)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="showDialog" :title="isEdit ? '编辑分类' : '新增分类'" width="450px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="分类名称">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入分类描述（可选）" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch 
            v-model="form.status" 
            :active-value="1" 
            :inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
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
const categories = ref([])

const showDialog = ref(false)
const isEdit = ref(false)
const currentId = ref(null)

const form = reactive({
  name: '',
  description: '',
  sortOrder: 0,
  status: 1
})

const fetchCategories = async () => {
  loading.value = true
  try {
    const res = await api.admin.getCategories()
    categories.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const openCreateDialog = () => {
  isEdit.value = false
  currentId.value = null
  form.name = ''
  form.description = ''
  form.sortOrder = 0
  form.status = 1
  showDialog.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  currentId.value = row.id
  form.name = row.name
  form.description = row.description || ''
  form.sortOrder = row.sortOrder || 0
  form.status = row.status
  showDialog.value = true
}

const handleSubmit = async () => {
  if (!form.name) {
    ElMessage.warning('分类名称不能为空')
    return
  }
  try {
    if (isEdit.value) {
      await api.admin.updateCategory(currentId.value, {
        name: form.name,
        description: form.description,
        sortOrder: form.sortOrder,
        status: form.status
      })
      ElMessage.success('更新成功')
    } else {
      await api.admin.createCategory(form.name, form.description)
      ElMessage.success('创建成功')
    }
    showDialog.value = false
    fetchCategories()
  } catch (e) {
    console.error(e)
  }
}

const handleToggleStatus = async (id, status) => {
  try {
    await api.admin.updateCategory(id, { status })
    ElMessage.success(status === 1 ? '已启用' : '已禁用')
    fetchCategories()
  } catch (e) {
    console.error(e)
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该分类吗？删除后不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await api.admin.deleteCategory(id)
    ElMessage.success('删除成功')
    fetchCategories()
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
    }
  }
}

onMounted(() => {
  fetchCategories()
})
</script>

<style lang="scss" scoped>
.admin-categories {
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
</style>
