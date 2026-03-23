// pages/profile/profile.js
const app = getApp();

Page({
  data: {
    userInfo: {},
    isLoggedIn: false,
    stats: {
      pending: 0,
      completed: 0,
      total: 0
    }
  },

  onLoad() {
    this.checkLoginStatus();
  },

  onShow() {
    this.checkLoginStatus();
    if (this.data.isLoggedIn) {
      this.loadUserStats();
    }
  },

  // 检查登录状态
  checkLoginStatus() {
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo') || {};
    
    this.setData({
      isLoggedIn: !!token,
      userInfo: userInfo
    });
  },

  // 加载用户统计
  async loadUserStats() {
    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo || !userInfo.id) {
      return;
    }
    
    try {
      const res = await app.request({
        url: `/api/user/appointment/stats?userId=${userInfo.id}`,
        method: 'GET'
      });

      if (res.code === 200) {
        this.setData({
          stats: res.data
        });
      }
    } catch (error) {
      console.error('加载统计数据失败:', error);
    }
  },

  // 编辑个人信息
  editProfile() {
    if (!this.data.isLoggedIn) {
      this.goToLogin();
      return;
    }
    
    wx.navigateTo({
      url: '/pages/edit-profile/edit-profile'
    });
  },

  // 跳转到预约列表
  goToAppointment() {
    if (!this.data.isLoggedIn) {
      this.goToLogin();
      return;
    }
    
    wx.switchTab({
      url: '/pages/appointment-list/appointment-list'
    });
  },

  // 跳转到收藏
  goToFavorites() {
    if (!this.data.isLoggedIn) {
      this.goToLogin();
      return;
    }
    
    wx.showToast({
      title: '功能开发中',
      icon: 'none'
    });
  },

  // 跳转到就诊记录
  goToRecords() {
    if (!this.data.isLoggedIn) {
      this.goToLogin();
      return;
    }
    
    wx.showToast({
      title: '功能开发中',
      icon: 'none'
    });
  },

  // 联系客服
  contactService() {
    wx.showModal({
      title: '联系客服',
      content: '客服电话：400-123-4567\n工作时间：9:00-18:00',
      showCancel: false
    });
  },

  // 关于我们
  aboutUs() {
    wx.showModal({
      title: '关于我们',
      content: '医疗预约系统 v1.0.0\n\n专业的医疗预约服务平台，为您提供便捷的挂号预约服务。',
      showCancel: false
    });
  },

  // 跳转到登录
  goToLogin() {
    wx.navigateTo({
      url: '/pages/login/login'
    });
  },

  // 退出登录
  logout() {
    wx.showModal({
      title: '确认退出',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          // 清除登录信息
          wx.removeStorageSync('token');
          wx.removeStorageSync('userInfo');
          app.globalData.token = null;
          app.globalData.userInfo = null;
          
          this.setData({
            isLoggedIn: false,
            userInfo: {},
            stats: {
              pending: 0,
              completed: 0,
              total: 0
            }
          });
          
          wx.showToast({
            title: '已退出登录',
            icon: 'success'
          });
        }
      }
    });
  }
});