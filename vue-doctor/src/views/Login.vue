<template>
  <div class="login-container">
    <div class="login-box">
      <h1 class="title">🏥 医疗预约管理系统</h1>
      <h2 class="subtitle">医生登录</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0">
        <el-form-item prop="doctorAccount">
          <el-input
            v-model="form.doctorAccount"
            placeholder="请输入工号"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="pwd">
          <el-input
            v-model="form.pwd"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            style="width: 100%;"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useDoctorStore } from '@/stores/doctor'

const router = useRouter()
const doctorStore = useDoctorStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  doctorAccount: '',
  pwd: ''
})

const rules = {
  doctorAccount: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  pwd: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await request.post('/doctor/login', form)
    if (res.code === 200) {
      doctorStore.login(res.data.token, res.data.doctorInfo)
      ElMessage.success('登录成功')
      router.push('/')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.title {
  text-align: center;
  font-size: 24px;
  color: #333;
  margin-bottom: 10px;
}

.subtitle {
  text-align: center;
  font-size: 16px;
  color: #666;
  margin-bottom: 30px;
}
</style>