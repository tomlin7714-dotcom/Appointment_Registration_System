import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useDoctorStore = defineStore('doctor', () => {
  const token = ref(localStorage.getItem('doctor_token') || '')
  const info = ref(JSON.parse(localStorage.getItem('doctor_info') || '{}'))

  function login(tokenVal, infoVal) {
    token.value = tokenVal
    info.value = infoVal
    localStorage.setItem('doctor_token', tokenVal)
    localStorage.setItem('doctor_info', JSON.stringify(infoVal))
  }

  function logout() {
    token.value = ''
    info.value = {}
    localStorage.removeItem('doctor_token')
    localStorage.removeItem('doctor_info')
  }

  function updateInfo(newInfo) {
    info.value = { ...info.value, ...newInfo }
    localStorage.setItem('doctor_info', JSON.stringify(info.value))
  }

  return { token, info, login, logout, updateInfo }
})