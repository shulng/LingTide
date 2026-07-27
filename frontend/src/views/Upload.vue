<template>
  <div class="upload-page">
    <div class="form-section">
      <h1 class="page-title">上传视频</h1>
      
      <el-upload
        class="video-uploader"
        drag
        :auto-upload="false"
        :on-change="handleFileChange"
        :show-file-list="false"
        accept="video/*"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">拖拽视频文件到此，或<em>点击选择</em></div>
        <template #tip>
          <div class="el-upload__tip">支持 MP4, AVI, MOV, MKV 等格式，最大 500MB</div>
        </template>
      </el-upload>

      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="视频标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入视频标题" maxlength="200" show-word-limit />
        </el-form-item>

        <el-form-item label="视频分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="选择分类">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="视频描述">
          <el-input 
            v-model="form.description" 
            type="textarea" 
            :rows="4"
            placeholder="介绍一下你的视频吧" 
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" @click="handleUpload" :loading="uploading" :disabled="!selectedFile">
            {{ uploading ? '上传中...' : '发布视频' }}
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '@/api'

const router = useRouter()

const formRef = ref()
const selectedFile = ref(null)
const uploading = ref(false)
const categories = ref([])

const form = ref({
  title: '',
  description: '',
  categoryId: null
})

const rules = {
  title: [{ required: true, message: '请输入视频标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

const handleFileChange = (file) => {
  selectedFile.value = file.raw
  if (!form.value.title && file.raw) {
    form.value.title = file.raw.name.replace(/\.[^/.]+$/, '')
  }
}

const fetchCategories = async () => {
  try {
    const res = await api.categories.getAll()
    categories.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const handleUpload = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请先选择视频文件')
    return
  }
  
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      uploading.value = true
      try {
        const formData = new FormData()
        formData.append('file', selectedFile.value)
        formData.append('title', form.value.title)
        formData.append('description', form.value.description || '')
        formData.append('categoryId', form.value.categoryId)
        
        const res = await api.videos.upload(formData)
        ElMessage.success('上传成功！')
        router.push({ name: 'VideoPlayer', params: { id: res.data.id } })
      } catch (e) {
        // Error handled by interceptor
      } finally {
        uploading.value = false
      }
    }
  })
}

onMounted(() => {
  fetchCategories()
})
</script>

<style lang="scss" scoped>
.upload-page {
  max-width: 800px;
  margin: 0 auto;
}

.video-uploader {
  margin-bottom: 24px;
  
  :deep(.el-upload-dragger) {
    padding: 40px;
  }
}
</style>
