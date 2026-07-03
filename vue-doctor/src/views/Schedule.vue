<template>
  <div class="schedule-page">
    <h2 class="page-title">排班管理</h2>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的排班（系统自动维护未来两周排班）</span>
          <el-button type="primary" @click="openAddDialog">+ 新增排班</el-button>
        </div>
      </template>
      <div class="schedule-calendar">
        <div v-for="item in scheduleList" :key="item.date" class="schedule-item">
          <div class="schedule-date">{{ item.date }}</div>
          <div class="schedule-periods">
            <div v-if="item.morning" class="period-tag morning">上午</div>
            <div v-if="item.afternoon" class="period-tag afternoon">下午</div>
            <div v-if="item.evening" class="period-tag evening">晚上</div>
            <span v-if="!item.morning && !item.afternoon && !item.evening" class="no-schedule">休息</span>
          </div>
        </div>
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="新增排班" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="排班日期" prop="date">
          <el-date-picker
            v-model="form.date"
            type="date"
            placeholder="选择日期"
            style="width: 100%;"
            :disabled-date="disabledDate"
          />
        </el-form-item>
        <el-form-item label="排班时段" prop="period">
          <el-select v-model="form.period" placeholder="请选择" style="width: 100%;">
            <el-option label="上午 (08:00-12:00)" value="morning" />
            <el-option label="下午 (14:00-17:00)" value="afternoon" />
            <el-option label="晚上 (18:00-21:00)" value="evening" />
          </el-select>
        </el-form-item>
        <el-form-item label="就诊地址">
          <el-input v-model="form.address" placeholder="如：门诊楼3楼内科诊室" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="其他说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { useDoctorStore } from '@/stores/doctor'

const doctorStore = useDoctorStore()
const scheduleList = ref([])
const dialogVisible = ref(false)
const loading = ref(false)
const formRef = ref()

const form = reactive({
  date: '',
  period: '',
  address: '',
  remark: ''
})

const rules = {
  date: [{ required: true, message: '请选择日期', trigger: 'change' }],
  period: [{ required: true, message: '请选择时段', trigger: 'change' }]
}

const disabledDate = (date) => {
  return date < new Date()
}

const loadData = async () => {
  try {
    const doctorId = doctorStore.info?.id
    if (!doctorId) {
      console.error('医生ID为空')
      return
    }
    const res = await request.get('/doctor/schedule/list', { params: { doctorId } })
    scheduleList.value = res.data || []
  } catch (error) {
    console.error('加载排班失败:', error)
  }
}

const openAddDialog = () => {
  form.date = ''
  form.period = ''
  form.address = ''
  form.remark = ''
  dialogVisible.value = true
}

const handleSave = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await request.post('/doctor/schedule/save', form)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    loading.value = false
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.schedule-calendar {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 15px;
}

.schedule-item {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 15px;
  text-align: center;
}

.schedule-date {
  font-weight: bold;
  margin-bottom: 10px;
  color: #333;
}

.schedule-periods {
  display: flex;
  flex-direction: column;
  gap: 5px;
  align-items: center;
}

.period-tag {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  color: white;
}

.period-tag.morning {
  background: #67c23a;
}

.period-tag.afternoon {
  background: #e6a23c;
}

.period-tag.evening {
  background: #909399;
}

.no-schedule {
  color: #999;
  font-size: 14px;
}
</style>