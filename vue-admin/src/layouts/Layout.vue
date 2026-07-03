<template>
  <div class="layout-container">
    <div class="header">
      <h1>🏥 医疗预约管理系统</h1>
      <div class="header-right">
        <span class="admin-info">👤 {{ userStore.username || '管理员' }}</span>
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
            <span>📊</span> 概览
          </el-menu-item>
          <el-menu-item index="/dept">
            <span>🏥</span> 科室管理
          </el-menu-item>
          <el-menu-item index="/doctor">
            <span>👨‍⚕️</span> 医生管理
          </el-menu-item>
          <el-menu-item index="/user">
            <span>👥</span> 用户管理
          </el-menu-item>
          <el-menu-item index="/log">
            <span>📝</span> 系统日志
          </el-menu-item>
          <el-menu-item index="/setting">
            <span>⚙️</span> 系统配置
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
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
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

.admin-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.main-container {
  display: flex;
  min-height: calc(100vh - 60px);
}

.sidebar {
  width: 220px;
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