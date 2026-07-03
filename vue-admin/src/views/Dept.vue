<template>
  <div class="dept-page">
    <h2 class="page-title">科室管理</h2>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>科室列表</span>
          <el-button type="primary" @click="openAddDialog">+ 新增科室</el-button>
        </div>
      </template>
      <el-table :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="科室名称" />
        <el-table-column prop="remark" label="科室简介" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openEditDialog(row)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="科室名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入科室名称" />
        </el-form-item>
        <el-form-item label="科室简介" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="4" placeholder="请输入科室简介" />
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
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const loading = ref(false)
const formRef = ref()
const dialogTitle = ref('新增科室')
const isEdit = ref(false)

const form = reactive({
  id: null,
  name: '',
  remark: ''
})

const rules = {
  name: [{ required: true, message: '请输入科室名称', trigger: 'blur' }]
}

const loadData = async () => {
  try {
    const res = await request.get('/admin/dept/list', {
      params: { page: page.value, pageSize: pageSize.value }
    })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载科室列表失败:', error)
  }
}

const openAddDialog = () => {
  dialogTitle.value = '新增科室'
  isEdit.value = false
  form.id = null
  form.name = ''
  form.remark = ''
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  dialogTitle.value = '编辑科室'
  isEdit.value = true
  form.id = row.id
  form.name = row.name
  form.remark = row.remark
  dialogVisible.value = true
}

const handleSave = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    if (isEdit.value) {
      await request.post('/admin/dept/modify', {
        id: form.id,
        name: form.name,
        remark: form.remark
      })
      ElMessage.success('修改成功')
    } else {
      await request.post('/admin/dept/save', {
        name: form.name,
        remark: form.remark
      })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    loading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该科室吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await request.post('/admin/dept/remove', { id: row.id })
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