<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-header">
        <h1>创建账号</h1>
        <p>加入 LingTide 分享你的精彩</p>
      </div>
      
      <el-form :model="form" :rules="rules" ref="formRef" class="auth-form">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名 (3-50位)" size="large">
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码 (6-100位)" size="large" show-password>
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>

        <el-form-item prop="nickname">
          <el-input v-model="form.nickname" placeholder="昵称 (选填)" size="large">
            <template #prefix><el-icon><UserFilled /></el-icon></template>
          </el-input>
        </el-form-item>

        <el-form-item prop="email">
          <el-input v-model="form.email" placeholder="邮箱 (选填)" size="large">
            <template #prefix><el-icon><Message /></el-icon></template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" style="width: 100%" @click="handleRegister" :loading="loading">
            注册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="auth-footer">
        <p>已有账号？<router-link to="/login">立即登录</router-link></p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '@/api'

const router = useRouter()

const formRef = ref()
const loading = ref(false)
const form = ref({
  username: '',
  password: '',
  nickname: '',
  email: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度3-50位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 100, message: '密码长度6-100位', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await api.auth.register(form.value)
        ElMessage.success('注册成功，请登录')
        router.push({ name: 'Login' })
      } catch (e) {
        // Error handled by interceptor
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.auth-page {
  min-height: 80vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.auth-container {
  background: #1a1a2e;
  border-radius: 16px;
  padding: 40px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.auth-header {
  text-align: center;
  margin-bottom: 32px;
  
  h1 {
    font-size: 28px;
    margin-bottom: 8px;
  }
  
  p {
    color: #999;
  }
}

.auth-footer {
  text-align: center;
  margin-top: 16px;
  
  a {
    color: #ff6b6b;
    text-decoration: none;
  }
}
</style>
