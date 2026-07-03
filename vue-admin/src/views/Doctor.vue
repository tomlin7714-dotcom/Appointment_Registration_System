<template>
  <div class="doctor-page">
    <h2 class="page-title">医生管理</h2>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>医生列表</span>
          <div style="display: flex; gap: 10px;">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索医生姓名、科室或手机号"
              style="width: 200px;"
              clearable
              @clear="loadData"
            />
            <el-button type="primary" @click="loadData">搜索</el-button>
            <el-button type="primary" @click="openAddDialog">+ 新增医生</el-button>
          </div>
        </div>
      </template>
      <el-table :data="tableData" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="doctorAccount" label="账号" />
        <el-table-column prop="deptName" label="科室" />
        <el-table-column prop="title" label="职称" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'">
              {{ row.status === 0 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openEditDialog(row)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="医生姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入医生姓名" />
        </el-form-item>
        <el-form-item label="登录账号" prop="doctorAccount">
          <el-input v-model="form.doctorAccount" placeholder="请输入登录账号" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="登录密码" prop="doctorPwd">
          <el-input v-model="form.doctorPwd" type="password" placeholder="请输入登录密码" />
        </el-form-item>
        <el-form-item label="所属科室" prop="deptId">
          <el-select v-model="form.deptId" placeholder="请选择科室" style="width: 100%;">
            <el-option v-for="dept in deptList" :key="dept.id" :label="dept.name" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="职称" prop="title">
          <el-input v-model="form.title" placeholder="如：主任医师" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入电话号码" />
        </el-form-item>
        <el-form-item label="简介" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入医生简介" />
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
const deptList = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const dialogVisible = ref(false)
const loading = ref(false)
const formRef = ref()
const dialogTitle = ref('新增医生')
const isEdit = ref(false)

const form = reactive({
  id: null,
  name: '',
  doctorAccount: '',
  doctorPwd: '',
  deptId: null,
  title: '',
  phone: '',
  remark: ''
})

const rules = {
  name: [{ required: true, message: '请输入医生姓名', trigger: 'blur' }],
  doctorAccount: [{ required: true, message: '请输入登录账号', trigger: 'blur' }],
  doctorPwd: [{ required: true, message: '请输入登录密码', trigger: 'blur' }],
  deptId: [{ required: true, message: '请选择科室', trigger: 'change' }]
}

const loadData = async () => {
  try {
    const res = await request.get('/admin/doctor/list', {
      params: {
        keyword: searchKeyword.value || null,
        page: page.value,
        pageSize: pageSize.value
      }
    })
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载医生列表失败:', error)
  }
}

const loadDeptList = async () => {
  try {
    const res = await request.get('/admin/dept/list', { params: { page: 1, pageSize: 100 } })
    deptList.value = res.data?.list || []
  } catch (error) {
    console.error('加载科室列表失败:', error)
  }
}

const openAddDialog = () => {
  dialogTitle.value = '新增医生'
  isEdit.value = false
  form.id = null
  form.name = ''
  form.doctorAccount = ''
  form.doctorPwd = ''
  form.deptId = null
  form.title = ''
  form.phone = ''
  form.remark = ''
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  dialogTitle.value = '编辑医生'
  isEdit.value = true
  form.id = row.id
  form.name = row.name
  form.doctorAccount = row.doctorAccount
  form.doctorPwd = ''
  form.deptId = row.deptId
  form.title = row.title
  form.phone = row.phone
  form.remark = row.remark
  dialogVisible.value = true
}

const handleSave = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    if (isEdit.value) {
      await request.post('/admin/doctor/modify', {
        id: form.id,
        name: form.name,
        deptId: form.deptId,
        title: form.title,
        phone: form.phone,
        remark: form.remark
      })
      ElMessage.success('修改成功')
    } else {
      await request.post('/admin/doctor/save', {
        name: form.name,
        doctorAccount: form.doctorAccount,
        doctorPwd: form.doctorPwd,
        deptId: form.deptId,
        title: form.title,
        phone: form.phone,
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

const toggleStatus = (row) => {
  const action = row.status === 0 ? '禁用' : '启用'
  ElMessageBox.confirm(`确定要${action}该医生吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await request.post('/admin/doctor/modify', {
      id: row.id,
      status: row.status === 0 ? 1 : 0
    })
    ElMessage.success('操作成功')
    loadData()
  }).catch(() => {})
}

onMounted(() => {
  loadData()
  loadDeptList()
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