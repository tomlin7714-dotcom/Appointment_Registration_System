<template>
  <div class="dashboard">
    <h2 class="page-title">系统概览</h2>
    <el-row :gutter="20">
      <el-col :span="6">
        <div class="stat-card" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
          <div class="stat-value">{{ stats.deptCount }}</div>
          <div class="stat-label">科室数量</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
          <div class="stat-value">{{ stats.doctorCount }}</div>
          <div class="stat-label">医生数量</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
          <div class="stat-value">{{ stats.userCount }}</div>
          <div class="stat-label">用户数量</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
          <div class="stat-value">{{ stats.appointmentCount }}</div>
          <div class="stat-label">今日预约</div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const stats = ref({
  deptCount: 0,
  doctorCount: 0,
  userCount: 0,
  appointmentCount: 0
})

const loadStats = async () => {
  try {
    const [deptRes, doctorRes, userRes, appointRes] = await Promise.all([
      request.get('/admin/dept/list', { params: { page: 1, pageSize: 1 } }),
      request.get('/admin/doctor/list', { params: { page: 1, pageSize: 1 } }),
      request.get('/admin/user/list', { params: { page: 1, pageSize: 1 } }),
      request.get('/admin/stats/todayCount')
    ])

    stats.value = {
      deptCount: deptRes.data?.total || 0,
      doctorCount: doctorRes.data?.total || 0,
      userCount: userRes.data?.total || 0,
      appointmentCount: appointRes.data || 0
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.page-title {
  color: #333;
  font-size: 24px;
  margin-bottom: 25px;
}

.stat-card {
  text-align: center;
  padding: 30px;
  color: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
}

.stat-label {
  margin-top: 10px;
  font-size: 14px;
}
</style>