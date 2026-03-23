// pages/login/login.js
const app = getApp();

Page({
  data: {
    phone: '',
    password: '',
    isLoading: false
  },

  onLoad() {
    // 检查是否已登录
    const token = wx.getStorageSync('token');
    if (token) {
      wx.switchTab({
        url: '/pages/index/index'
      });
    }
  },

  // 手机号输入
  onPhoneInput(e) {
    this.setData({
      phone: e.detail.value
    });
  },

  // 密码输入
  onPasswordInput(e) {
    this.setData({
      password: e.detail.value
    });
  },

  // 登录
  async login() {
    const { phone, password } = this.data;

    if (!phone || !password) {
      wx.showToast({
        title: '请输入手机号和密码',
        icon: 'none'
      });
      return;
    }

    if (phone.length !== 11) {
      wx.showToast({
        title: '请输入正确的手机号',
        icon: 'none'
      });
      return;
    }

    this.setData({ isLoading: true });

    try {
      const res = await app.request({
        url: '/api/user/login',
        method: 'POST',
        data: {
          phone: phone,
          pwd: password
        }
      });

      if (res.code === 200) {
        // 保存登录信息
        wx.setStorageSync('token', res.data.token);
        wx.setStorageSync('userInfo', res.data.userInfo);
        app.globalData.token = res.data.token;
        app.globalData.userInfo = res.data.userInfo;

        wx.showToast({
          title: '登录成功',
          icon: 'success'
        });

        setTimeout(() => {
          wx.switchTab({
            url: '/pages/index/index'
          });
        }, 1500);
      } else {
        wx.showToast({
          title: res.msg || '登录失败',
          icon: 'none'
        });
      }
    } catch (error) {
      console.error('登录错误:', error);
      wx.showToast({
        title: '网络错误，请稍后重试',
        icon: 'none'
      });
    } finally {
      this.setData({ isLoading: false });
    }
  },

  // 跳转到注册页面
  goToRegister() {
    wx.navigateTo({
      url: '/pages/register/register'
    });
  }
});