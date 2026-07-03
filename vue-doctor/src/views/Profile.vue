<template>
  <div class="profile-page">
    <h2 class="page-title">个人信息</h2>
    <el-card>
      <template #header>
        <span>基本信息</span>
      </template>
      <el-form :model="profileForm" label-width="100px" style="max-width: 500px;">
        <el-form-item label="医生姓名">
          <el-input v-model="profileForm.name" readonly />
        </el-form-item>
        <el-form-item label="登录账号">
          <el-input v-model="profileForm.workNo" readonly />
        </el-form-item>
        <el-form-item label="所属科室">
          <el-input v-model="profileForm.deptName" readonly />
        </el-form-item>
        <el-form-item label="职称">
          <el-input v-model="profileForm.title" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="profileForm.phone" />
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input v-model="profileForm.remark" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="saveProfile">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px;">
      <template #header>
        <span>修改密码</span>
      </template>
      <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="100px" style="max-width: 500px;">
        <el-form-item label="旧密码" prop="oldPwd">
          <el-input v-model="pwdForm.oldPwd" type="password" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPwd">
          <el-input v-model="pwdForm.newPwd" type="password" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPwd">
          <el-input v-model="pwdForm.confirmPwd" type="password" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="changePwd">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useDoctorStore } from '@/stores/doctor'
import request from '@/utils/request'

const doctorStore = useDoctorStore()
const loading = ref(false)
const pwdFormRef = ref()

const profileForm = reactive({
  name: '',
  workNo: '',
  deptName: '',
  title: '',
  phone: '',
  remark: ''
})

const pwdForm = reactive({
  oldPwd: '',
  newPwd: '',
  confirmPwd: ''
})

const pwdRules = {
  oldPwd: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPwd: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
  confirmPwd: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.newPwd) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const loadProfile = async () => {
  try {
    const doctorId = doctorStore.info?.id
    if (!doctorId) {
      console.error('医生ID为空')
      profileForm.name = doctorStore.info?.name || ''
      profileForm.workNo = doctorStore.info?.workNo || ''
      profileForm.deptName = doctorStore.info?.deptName || ''
      profileForm.title = doctorStore.info?.title || ''
      profileForm.phone = doctorStore.info?.phone || ''
      profileForm.remark = doctorStore.info?.remark || ''
      return
    }
    const res = await request.get('/doctor/profile/get', { params: { doctorId } })
    if (res.data) {
      Object.assign(profileForm, res.data)
    }
  } catch (error) {
    console.error('加载个人信息失败:', error)
    profileForm.name = doctorStore.info?.name || ''
    profileForm.workNo = doctorStore.info?.workNo || ''
    profileForm.deptName = doctorStore.info?.deptName || ''
    profileForm.title = doctorStore.info?.title || ''
    profileForm.phone = doctorStore.info?.phone || ''
    profileForm.remark = doctorStore.info?.remark || ''
  }
}

const saveProfile = async () => {
  loading.value = true
  try {
    await request.post('/doctor/profile/update', profileForm)
    ElMessage.success('保存成功')
    doctorStore.updateInfo(profileForm)
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    loading.value = false
  }
}

const changePwd = async () => {
  const valid = await pwdFormRef.value.validate().catch(() => false)
  if (!valid) return

  try {
    const doctorId = doctorStore.info?.id
    if (!doctorId) {
      console.error('医生ID为空')
      return
    }
    await request.post('/doctor/profile/changePwd', {
      doctorId,
      oldPwd: pwdForm.oldPwd,
      newPwd: pwdForm.newPwd
    })
    ElMessage.success('密码修改成功')
    pwdForm.oldPwd = ''
    pwdForm.newPwd = ''
    pwdForm.confirmPwd = ''
  } catch (error) {
    console.error('修改密码失败:', error)
  }
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.page-title {
  color: #333;
  font-size: 24px;
  margin-bottom: 25px;
}
</style>