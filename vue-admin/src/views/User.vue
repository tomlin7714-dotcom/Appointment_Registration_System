<template>
  <div class="user-page">
    <h2 class="page-title">用户管理</h2>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <div style="display: flex; gap: 10px;">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索用户姓名或手机号"
              style="width: 200px;"
              clearable
              @clear="loadData"
            />
            <el-button type="primary" @click="loadData">搜索</el-button>
          </div>
        </div>
      </template>
      <el-table :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="idCard" label="身份证号" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'">
              {{ row.status === 0 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button :type="row.status === 0 ? 'warning' : 'success'" size="small" @click="toggleStatus(row)">
              {{ row.status === 0 ? '禁用' : '启用' }}
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

const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')

const loadData = async () => {
  try {
    const res = await request.get('/admin/user/list', {
      params: {
        keyword: searchKeyword.value || null,
        page: page.value,
        pageSize: pageSize.value
      }
    })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载用户列表失败:', error)
  }
}

const toggleStatus = (row) => {
  const action = row.status === 0 ? '禁用' : '启用'
  ElMessageBox.confirm(`确定要${action}该用户吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await request.post('/admin/user/modify', {
      id: row.id,
      status: row.status === 0 ? 1 : 0
    })
    ElMessage.success('操作成功')
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