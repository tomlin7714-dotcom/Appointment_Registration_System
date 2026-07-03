<template>
  <div class="appointment-page">
    <h2 class="page-title">预约列表</h2>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>全部预约</span>
          <div style="display: flex; gap: 10px; flex-wrap: wrap;">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索患者姓名或手机号"
              style="width: 180px;"
              clearable
            />
            <el-date-picker v-model="filterDate" type="date" placeholder="选择日期" style="width: 150px;" />
            <el-select v-model="filterStatus" placeholder="全部状态" style="width: 120px;">
              <el-option label="全部状态" value="" />
              <el-option label="待就诊" value="1" />
              <el-option label="已完成" value="3" />
              <el-option label="已取消" value="4" />
            </el-select>
            <el-button type="primary" @click="loadData">筛选</el-button>
          </div>
        </div>
      </template>
      <el-table :data="tableData" border stripe>
        <el-table-column prop="appointmentNo" label="预约号" width="100" />
        <el-table-column prop="patientName" label="患者姓名" />
        <el-table-column prop="patientPhone" label="手机号" width="130" />
        <el-table-column prop="appointmentDate" label="预约日期" width="120" />
        <el-table-column prop="timeRange" label="时间段" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" type="success" size="small" @click="completeAppoint(row)">
              完成
            </el-button>
            <el-button v-if="row.status === 1" type="danger" size="small" @click="cancelAppoint(row)">
              取消
            </el-button>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useDoctorStore } from '@/stores/doctor'

const doctorStore = useDoctorStore()
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const filterDate = ref('')
const filterStatus = ref('')

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
    const res = await request.get('/doctor/appointment/list', {
      params: {
        doctorId,
        keyword: searchKeyword.value || null,
        date: filterDate.value || null,
        status: filterStatus.value || null,
        page: page.value,
        pageSize: pageSize.value
      }
    })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载预约列表失败:', error)
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

const cancelAppoint = async (row) => {
  ElMessageBox.confirm('确定要取消该预约吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.post('/doctor/appointment/cancel', { id: row.id })
      ElMessage.success('取消成功')
      loadData()
    } catch (error) {
      console.error('取消失败:', error)
    }
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