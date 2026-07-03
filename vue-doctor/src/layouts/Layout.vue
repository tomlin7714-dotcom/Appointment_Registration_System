<template>
  <div class="layout-container">
    <div class="header">
      <h1>🏥 医疗预约管理系统 - 医生工作台</h1>
      <div class="header-right">
        <span class="doctor-info">👨‍⚕️ {{ doctorStore.info?.name || '医生' }}</span>
        <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
      </div>
    </div>
    <div class="main-container">
      <div class="sidebar">
        <el-menu
          :default-active="activeMenu"
          router
          background-color="#fff"
          text-color="#666"
          active-text-color="#667eea"
        >
          <el-menu-item index="/dashboard">
            <span>📊</span> 工作台
          </el-menu-item>
          <el-menu-item index="/schedule">
            <span>📅</span> 排班管理
          </el-menu-item>
          <el-menu-item index="/number-source">
            <span>🎫</span> 号源管理
          </el-menu-item>
          <el-menu-item index="/appointment">
            <span>📋</span> 预约列表
          </el-menu-item>
          <el-menu-item index="/call">
            <span>🔊</span> 诊间叫号
          </el-menu-item>
          <el-menu-item index="/profile">
            <span>👤</span> 个人信息
          </el-menu-item>
        </el-menu>
      </div>
      <div class="content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useDoctorStore } from '@/stores/doctor'

const router = useRouter()
const route = useRoute()
const doctorStore = useDoctorStore()

const activeMenu = computed(() => route.path)

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    doctorStore.logout()
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0 20px;
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.header h1 {
  font-size: 20px;
  font-weight: 500;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.doctor-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.main-container {
  display: flex;
  min-height: calc(100vh - 60px);
}

.sidebar {
  width: 200px;
  background: white;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.05);
}

.sidebar .el-menu {
  border-right: none;
}

.sidebar .el-menu-item {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 56px;
  line-height: 56px;
  padding: 0 25px;
}

.sidebar .el-menu-item span {
  font-size: 18px;
}

.content {
  flex: 1;
  padding: 30px;
  overflow-y: auto;
}
</style>