// pages/schedule/schedule.js
const app = getApp();

Page({
  data: {
    doctorId: null,
    doctor: {},
    numberSourceList: [],
    showModal: false,
    selectedSource: null,
    patientName: '',
    patientIdCard: ''
  },

  onLoad(options) {
    console.log('排班页面参数:', options);
    const doctorId = options.doctorId;
    
    if (doctorId) {
      this.setData({ doctorId: parseInt(doctorId) });
      this.loadDoctorInfo(doctorId);
      this.loadNumberSourceList(doctorId);
    }
  },

  // 加载医生信息
  async loadDoctorInfo(doctorId) {
    try {
      const res = await app.request({
        url: '/api/user/doctor/list',
        method: 'GET'
      });

      if (res.code === 200) {
        const doctor = res.data.find(d => d.id == doctorId);
        if (doctor) {
          this.setData({ doctor });
        }
      }
    } catch (error) {
      console.error('加载医生信息失败:', error);
    }
  },

  // 加载号源列表
  async loadNumberSourceList(doctorId) {
    wx.showLoading({ title: '加载中...' });

    try {
      const res = await app.request({
        url: '/api/user/appointment/numberSource/list',
        method: 'GET',
        data: { doctorId }
      });

      if (res.code === 200) {
        this.setData({
          numberSourceList: res.data
        });
      }
    } catch (error) {
      console.error('加载号源列表失败:', error);
    } finally {
      wx.hideLoading();
    }
  },

  // 选择号源
  selectSource(e) {
    const item = e.currentTarget.dataset.item;
    
    if (item.remainNum <= 0) {
      wx.showToast({
        title: '该号源已约满',
        icon: 'none'
      });
      return;
    }

    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo) {
      wx.showModal({
        title: '提示',
        content: '请先登录后再预约',
        confirmText: '去登录',
        success: (res) => {
          if (res.confirm) {
            wx.navigateTo({
              url: '/pages/login/login'
            });
          }
        }
      });
      return;
    }

    this.setData({
      showModal: true,
      selectedSource: item,
      patientName: userInfo.realName || '',
      patientIdCard: userInfo.idCard || ''
    });
  },

  // 隐藏弹窗
  hideModal() {
    this.setData({
      showModal: false,
      selectedSource: null
    });
  },

  // 患者姓名输入
  onPatientNameInput(e) {
    this.setData({ patientName: e.detail.value });
  },

  // 身份证号输入
  onPatientIdCardInput(e) {
    this.setData({ patientIdCard: e.detail.value });
  },

  // 确认预约
  async confirmAppointment() {
    const { selectedSource, patientName, patientIdCard } = this.data;
    const userInfo = wx.getStorageSync('userInfo');

    if (!patientName) {
      wx.showToast({ title: '请输入患者姓名', icon: 'none' });
      return;
    }

    if (!patientIdCard || patientIdCard.length < 15) {
      wx.showToast({ title: '请输入正确的身份证号', icon: 'none' });
      return;
    }

    wx.showLoading({ title: '预约中...' });

    try {
      const res = await app.request({
        url: '/api/user/appointment/create',
        method: 'POST',
        data: {
          numberSourceId: selectedSource.id,
          userId: userInfo.id,
          patientName,
          patientIdCard
        }
      });

      wx.hideLoading();

      if (res.code === 200) {
        this.hideModal();
        
        wx.showModal({
          title: '预约成功',
          content: `预约成功！挂号费用：¥0.01，是否立即支付？`,
          confirmText: '立即支付',
          cancelText: '稍后支付',
          success: async (modalRes) => {
            if (modalRes.confirm) {
              await this.payAppointment(res.data.appointmentId);
            } else {
              wx.showToast({ title: '请尽快完成支付', icon: 'none' });
              wx.switchTab({
                url: '/pages/appointment-list/appointment-list'
              });
            }
          }
        });

        this.loadNumberSourceList(this.data.doctorId);
      } else {
        wx.showToast({ title: res.msg || '预约失败', icon: 'none' });
      }
    } catch (error) {
      wx.hideLoading();
      console.error('预约失败:', error);
      wx.showToast({ title: '预约失败，请重试', icon: 'none' });
    }
  },

  // 支付预约
  async payAppointment(appointmentId) {
    wx.showLoading({ title: '支付中...' });

    try {
      const res = await app.request({
        url: '/api/user/appointment/pay',
        method: 'POST',
        data: {
          appointmentId,
          payType: 'wechat'
        }
      });

      if (res.code === 200) {
        wx.showToast({ title: '支付成功', icon: 'success' });
        
        setTimeout(() => {
          wx.switchTab({
            url: '/pages/appointment-list/appointment-list'
          });
        }, 1500);
      } else {
        wx.showToast({ title: res.msg || '支付失败', icon: 'none' });
      }
    } catch (error) {
      console.error('支付失败:', error);
      wx.showToast({ title: '支付失败，请重试', icon: 'none' });
    } finally {
      wx.hideLoading();
    }
  },

  onReady() {},
  onShow() {},
  onHide() {},
  onUnload() {},

  onPullDownRefresh() {
    this.loadNumberSourceList(this.data.doctorId);
    wx.stopPullDownRefresh();
  },

  onReachBottom() {},
  onShareAppMessage() {}
});
