<template>
  <div class="call-page">
    <h2 class="page-title">诊间叫号</h2>

    <div class="call-panel">
      <div class="call-info">
        <div class="current-label">当前叫号</div>
        <div class="current-number">{{ currentNumber }}</div>
        <div class="current-details" v-if="currentPatient">
          <div>患者姓名：{{ currentPatient.patientName }}</div>
          <div>联系电话：{{ currentPatient.patientPhone }}</div>
        </div>
        <div class="waiting-count">等待人数：{{ waitingList.length }} 人</div>
        <div class="call-buttons">
          <el-button type="primary" size="large" @click="callNext">🔊 叫下一个</el-button>
          <el-button type="warning" size="large" @click="recall">🔁 重叫</el-button>
          <el-button type="info" size="large" @click="skipPatient">⏭ 跳过</el-button>
        </div>
      </div>
    </div>

    <el-card style="margin-top: 20px;">
      <template #header>
        <span>等待队列</span>
      </template>
      <el-table :data="waitingList" border stripe>
        <el-table-column prop="queueNum" label="排队号" width="80" />
        <el-table-column prop="patientName" label="患者姓名" />
        <el-table-column prop="appointmentTime" label="预约时间" width="180" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="success" size="small" @click="callPatient(row)">叫号</el-button>
            <el-button type="danger" size="small" @click="removePatient(row)">移除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { useDoctorStore } from '@/stores/doctor'

const doctorStore = useDoctorStore()
const currentNumber = ref('---')
const currentPatient = ref(null)
const waitingList = ref([])

const loadWaitingList = async () => {
  try {
    const doctorId = doctorStore.info?.id
    if (!doctorId) {
      console.error('医生ID为空')
      return
    }
    const res = await request.get('/doctor/work/waitingList', { params: { doctorId } })
    waitingList.value = res.data || []
  } catch (error) {
    console.error('加载等待队列失败:', error)
  }
}

const callNext = async () => {
  try {
    const doctorId = doctorStore.info?.id
    if (!doctorId) {
      console.error('医生ID为空')
      return
    }
    const res = await request.post('/doctor/work/callNext', { doctorId })
    if (res.data) {
      currentNumber.value = res.data.appointmentNo || res.data.queueNum
      currentPatient.value = res.data
      ElMessage.success(`叫号：${currentNumber.value}`)
    } else {
      ElMessage.info('没有等待的患者')
    }
    loadWaitingList()
  } catch (error) {
    console.error('叫号失败:', error)
  }
}

const recall = async () => {
  if (!currentNumber.value || currentNumber.value === '---') {
    ElMessage.warning('没有正在就诊的患者')
    return
  }
  try {
    await request.post('/doctor/work/recall')
    ElMessage.success(`重叫：${currentNumber.value}`)
  } catch (error) {
    console.error('重叫失败:', error)
  }
}

const skipPatient = async () => {
  if (!currentPatient.value) {
    ElMessage.warning('没有正在就诊的患者')
    return
  }
  try {
    await request.post('/doctor/work/skip', { id: currentPatient.value.id })
    ElMessage.success('已跳过')
    currentNumber.value = '---'
    currentPatient.value = null
    loadWaitingList()
  } catch (error) {
    console.error('跳过失败:', error)
  }
}

const callPatient = async (row) => {
  try {
    await request.post('/doctor/work/callPatient', { id: row.id })
    currentNumber.value = row.appointmentNo || row.queueNum
    currentPatient.value = row
    ElMessage.success(`叫号：${currentNumber.value}`)
    loadWaitingList()
  } catch (error) {
    console.error('叫号失败:', error)
  }
}

const removePatient = async (row) => {
  try {
    await request.post('/doctor/work/removePatient', { id: row.id })
    ElMessage.success('已移除')
    loadWaitingList()
  } catch (error) {
    console.error('移除失败:', error)
  }
}

onMounted(() => {
  loadWaitingList()
})
</script>

<style scoped>
.page-title {
  color: #333;
  font-size: 24px;
  margin-bottom: 25px;
}

.call-panel {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  padding: 40px;
  color: white;
  text-align: center;
}

.call-info {
  max-width: 500px;
  margin: 0 auto;
}

.current-label {
  font-size: 18px;
  opacity: 0.9;
}

.current-number {
  font-size: 72px;
  font-weight: bold;
  margin: 20px 0;
}

.current-details {
  font-size: 16px;
  margin-bottom: 20px;
  line-height: 1.8;
}

.waiting-count {
  font-size: 20px;
  margin-bottom: 20px;
}

.call-buttons {
  display: flex;
  justify-content: center;
  gap: 15px;
}
</style>