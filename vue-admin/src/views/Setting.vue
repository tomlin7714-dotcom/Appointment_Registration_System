<template>
  <div class="setting-page">
    <h2 class="page-title">系统配置</h2>
    <el-card>
      <template #header>
        <span>参数设置</span>
      </template>
      <el-form ref="formRef" :model="form" label-width="150px" style="max-width: 500px;">
        <el-form-item label="预约开始时间">
          <el-time-select
            v-model="form.appointStartTime"
            start="08:00"
            step="00:30"
            end="12:00"
            placeholder="选择开始时间"
          />
        </el-form-item>
        <el-form-item label="预约结束时间">
          <el-time-select
            v-model="form.appointEndTime"
            start="14:00"
            step="00:30"
            end="18:00"
            placeholder="选择结束时间"
          />
        </el-form-item>
        <el-form-item label="每日最大预约数">
          <el-input-number v-model="form.maxAppointmentsPerDay" :min="1" :max="500" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSave">保存设置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const formRef = ref()

const form = reactive({
  appointStartTime: '08:00',
  appointEndTime: '17:00',
  maxAppointmentsPerDay: 100
})

const loadSettings = async () => {
  try {
    const res = await request.get('/admin/sysParam/get')
    if (res.data) {
      Object.assign(form, res.data)
    }
  } catch (error) {
    console.error('加载设置失败:', error)
  }
}

const handleSave = async () => {
  loading.value = true
  try {
    await request.post('/admin/sysParam/save', form)
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存设置失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadSettings()
})
</script>

<style scoped>
.page-title {
  color: #333;
  font-size: 24px;
  margin-bottom: 25px;
}
</style>