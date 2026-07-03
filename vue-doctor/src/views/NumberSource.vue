<template>
  <div class="number-source-page">
    <h2 class="page-title">号源管理</h2>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>号源列表</span>
          <div style="display: flex; gap: 10px;">
            <el-date-picker
              v-model="searchDate"
              type="date"
              placeholder="选择日期"
              style="width: 150px;"
            />
            <el-button type="primary" @click="loadData">查询</el-button>
            <el-button type="primary" @click="openAddDialog">+ 发布号源</el-button>
          </div>
        </div>
      </template>
      <el-table :data="tableData" border stripe>
        <el-table-column prop="scheduleDate" label="日期" width="120" />
        <el-table-column prop="timeRange" label="时间段" />
        <el-table-column prop="totalNum" label="总号数" width="100" />
        <el-table-column prop="usedNum" label="已预约" width="100" />
        <el-table-column prop="remainNum" label="剩余" width="100">
          <template #default="{ row }">
            <span :style="{ color: row.remainNum > 0 ? '#67c23a' : '#f56c6c' }">
              {{ row.remainNum }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'">
              {{ row.status === 0 ? '可预约' : '已满' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="发布号源" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="选择排班" prop="scheduleId">
          <el-select v-model="form.scheduleId" placeholder="请选择排班" style="width: 100%;">
            <el-option
              v-for="item in scheduleOptions"
              :key="item.id"
              :label="`${item.date} ${item.periodName}`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="时间段" prop="timeRange">
          <el-input v-model="form.timeRange" placeholder="如：09:00-09:30" />
        </el-form-item>
        <el-form-item label="号源数量" prop="totalNum">
          <el-input-number v-model="form.totalNum" :min="1" :max="50" />
        </el-form-item>
        <el-form-item label="挂号费">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="handleSave">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useDoctorStore } from '@/stores/doctor'

const doctorStore = useDoctorStore()
const tableData = ref([])
const scheduleOptions = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const searchDate = ref('')
const dialogVisible = ref(false)
const loading = ref(false)
const formRef = ref()

const form = reactive({
  scheduleId: null,
  timeRange: '',
  totalNum: 5,
  price: 0
})

const rules = {
  scheduleId: [{ required: true, message: '请选择排班', trigger: 'change' }],
  timeRange: [{ required: true, message: '请输入时间段', trigger: 'blur' }],
  totalNum: [{ required: true, message: '请输入号源数量', trigger: 'blur' }]
}

const loadData = async () => {
  try {
    const doctorId = doctorStore.info?.id
    if (!doctorId) {
      console.error('医生ID为空')
      return
    }
    const res = await request.get('/doctor/numberSource/list', {
      params: {
        doctorId,
        date: searchDate.value,
        page: page.value,
        pageSize: pageSize.value
      }
    })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载号源列表失败:', error)
  }
}

const loadScheduleOptions = async () => {
  try {
    const res = await request.get('/doctor/schedule/options')
    scheduleOptions.value = res.data || []
  } catch (error) {
    console.error('加载排班选项失败:', error)
  }
}

const openAddDialog = () => {
  form.scheduleId = null
  form.timeRange = ''
  form.totalNum = 5
  form.price = 0
  loadScheduleOptions()
  dialogVisible.value = true
}

const handleSave = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await request.post('/doctor/numberSource/save', form)
    ElMessage.success('发布成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('发布失败:', error)
  } finally {
    loading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该号源吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await request.post('/doctor/numberSource/remove', { id: row.id })
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>