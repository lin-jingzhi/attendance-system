<script setup>
import { ref } from 'vue'

const formData = ref({
  username: '',
  password: ''
})

const errorMessage = ref('')

function handleLogin() {
  // 简单的登录验证
  if (formData.value.username === 'admin' && formData.value.password === '123456') {
    // 登录成功，跳转到控制台
    window.location.href = '#'
  } else {
    errorMessage.value = '用户名或密码错误'
  }
}
</script>

<template>
  <div class="min-vh-100 d-flex align-items-center justify-content-center bg-light">
    <div class="col-md-4">
      <div class="card shadow-sm">
        <div class="card-header bg-primary text-white text-center">
          <h4>员工考勤管理系统</h4>
        </div>
        <div class="card-body p-5">
          <h5 class="card-title mb-4 text-center">用户登录</h5>
          <form @submit.prevent="handleLogin">
            <div class="mb-3">
              <label class="form-label">用户名</label>
              <input 
                type="text" 
                class="form-control" 
                v-model="formData.username" 
                placeholder="请输入用户名"
                required
              >
            </div>
            <div class="mb-3">
              <label class="form-label">密码</label>
              <input 
                type="password" 
                class="form-control" 
                v-model="formData.password" 
                placeholder="请输入密码"
                required
              >
            </div>
            <div v-if="errorMessage" class="mb-3 text-danger">
              {{ errorMessage }}
            </div>
            <button type="submit" class="btn btn-primary w-100">登录</button>
          </form>
        </div>
        <div class="card-footer text-center text-muted">
          © 2024 员工考勤管理系统
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.card {
  border-radius: 8px;
}
</style>