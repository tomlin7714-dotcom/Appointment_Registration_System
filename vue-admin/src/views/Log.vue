<template>
  <div class="log-page">
    <h2 class="page-title">系统日志</h2>
    <el-card>
      <template #header>
        <span>操作日志</span>
      </template>
      <el-table :data="tableData" border stripe max-height="600">
        <el-table-column prop="operateTime" label="时间" width="180" />
        <el-table-column prop="operatorName" label="用户" width="120" />
        <el-table-column prop="content" label="操作" />
        <el-table-column prop="operateIp" label="IP地址" width="150" />
      </el-table>
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[20, 50, 100]"
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
import request from '@/utils/request'

const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)

const loadData = async () => {
  try {
    const res = await request.get('/admin/log/list', {
      params: { page: page.value, pageSize: pageSize.value }
    })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载日志列表失败:', error)
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>