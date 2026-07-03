import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('admin_token') || '')
  const username = ref(localStorage.getItem('admin_username') || '')
  const role = ref(localStorage.getItem('admin_role') || '')

  function login(tokenVal, usernameVal, roleVal) {
    token.value = tokenVal
    username.value = usernameVal
    role.value = roleVal
    localStorage.setItem('admin_token', tokenVal)
    localStorage.setItem('admin_username', usernameVal)
    localStorage.setItem('admin_role', roleVal)
  }

  function logout() {
    token.value = ''
    username.value = ''
    role.value = ''
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_username')
    localStorage.removeItem('admin_role')
  }

  return { token, username, role, login, logout }
})