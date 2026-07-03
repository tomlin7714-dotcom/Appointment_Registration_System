<template>
  <div class="dashboard">
    <h2 class="page-title">工作台首页</h2>
    <el-row :gutter="20">
      <el-col :span="6">
        <div class="stat-card" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
          <div class="stat-value">{{ stats.todayAppoint }}</div>
          <div class="stat-label">今日预约</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
          <div class="stat-value">{{ stats.waitingCount }}</div>
          <div class="stat-label">待就诊</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
          <div class="stat-value">{{ stats.completedCount }}</div>
          <div class="stat-label">已完成</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
          <div class="stat-value">{{ stats.totalSchedule }}</div>
          <div class="stat-label">本周排班</div>
        </div>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>今日预约</span>
          <el-button type="primary" @click="$router.push('/call')">开始叫号</el-button>
        </div>
      </template>
      <el-table :data="todayList" border stripe>
        <el-table-column prop="appointmentNo" label="预约号" width="100" />
        <el-table-column prop="patientName" label="患者姓名" />
        <el-table-column prop="appointmentTime" label="预约时间" width="180" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" type="success" size="small" @click="completeAppoint(row)">
              完成就诊
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useDoctorStore } from '@/stores/doctor'
import request from '@/utils/request'

const doctorStore = useDoctorStore()

const stats = ref({
  todayAppoint: 0,
  waitingCount: 0,
  completedCount: 0,
  totalSchedule: 0
})

const todayList = ref([])

const getStatusType = (status) => {
  const types = { 1: 'warning', 2: 'primary', 3: 'success', 4: 'info' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 1: '待就诊', 2: '已报到', 3: '已完成', 4: '已取消' }
  return texts[status] || '未知'
}

const loadData = async () => {
  try {
    const doctorId = doctorStore.info?.id
    if (!doctorId) {
      console.error('医生ID为空')
      return
    }
    const res = await request.get('/doctor/work/dashboard', { params: { doctorId } })
    if (res.data) {
      stats.value = res.data.stats || stats.value
      todayList.value = res.data.todayList || []
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

const completeAppoint = async (row) => {
  try {
    await request.post('/doctor/appointment/complete', { appointmentId: row.id })
    ElMessage.success('操作成功')
    loadData()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

onMounted(() => {
  loadData()
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>