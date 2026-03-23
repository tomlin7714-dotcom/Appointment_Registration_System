// pages/register/register.js
const app = getApp();

Page({
  data: {
    phone: '',
    password: '',
    confirmPassword: '',
    realName: '',
    idCard: '',
    isLoading: false
  },

  onLoad(options) {},

  // 手机号输入
  onPhoneInput(e) {
    this.setData({ phone: e.detail.value });
  },

  // 密码输入
  onPasswordInput(e) {
    this.setData({ password: e.detail.value });
  },

  // 确认密码输入
  onConfirmPasswordInput(e) {
    this.setData({ confirmPassword: e.detail.value });
  },

  // 真实姓名输入
  onRealNameInput(e) {
    this.setData({ realName: e.detail.value });
  },

  // 身份证号输入
  onIdCardInput(e) {
    this.setData({ idCard: e.detail.value });
  },

  // 注册
  async register() {
    const { phone, password, confirmPassword, realName, idCard } = this.data;

    if (!phone || !password || !confirmPassword || !realName || !idCard) {
      wx.showToast({ title: '请填写完整信息', icon: 'none' });
      return;
    }

    if (phone.length !== 11) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' });
      return;
    }

    if (password.length < 6) {
      wx.showToast({ title: '密码至少6位', icon: 'none' });
      return;
    }

    if (password !== confirmPassword) {
      wx.showToast({ title: '两次密码不一致', icon: 'none' });
      return;
    }

    if (idCard.length < 15) {
      wx.showToast({ title: '请输入正确的身份证号', icon: 'none' });
      return;
    }

    this.setData({ isLoading: true });

    try {
      const res = await app.request({
        url: '/api/user/register',
        method: 'POST',
        data: {
          phone,
          pwd: password,
          realName,
          idCard
        }
      });

      if (res.code === 200) {
        wx.showToast({ title: '注册成功', icon: 'success' });
        
        setTimeout(() => {
          wx.navigateBack();
        }, 1500);
      } else {
        wx.showToast({ title: res.msg || '注册失败', icon: 'none' });
      }
    } catch (error) {
      console.error('注册错误:', error);
      wx.showToast({ title: '网络错误，请稍后重试', icon: 'none' });
    } finally {
      this.setData({ isLoading: false });
    }
  },

  // 跳转到登录页面
  goToLogin() {
    wx.navigateBack();
  },

  onReady() {},
  onShow() {},
  onHide() {},
  onUnload() {},
  onPullDownRefresh() {},
  onReachBottom() {},
  onShareAppMessage() {}
});
