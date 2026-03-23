// pages/appointment-list/appointment-list.js
const app = getApp();

Page({
  data: {
    appointmentList: [],
    currentFilter: '',
    isRefreshing: false
  },

  onLoad() {
    this.loadAppointmentList();
  },

  onShow() {
    this.loadAppointmentList();
  },

  // 加载预约列表
  async loadAppointmentList() {
    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      return;
    }

    const { currentFilter } = this.data;
    
    try {
      let url = `/api/user/appointment/list?userId=${userInfo.id}`;
      if (currentFilter !== '') {
        url += `&status=${currentFilter}`;
      }

      const res = await app.request({
        url: url,
        method: 'GET'
      });

      if (res.code === 200) {
        const list = res.data.map(item => ({
          ...item,
          statusText: this.getStatusText(item.status),
          fee: item.fee || 0.01
        }));
        
        this.setData({
          appointmentList: list
        });
      }
    } catch (error) {
      console.error('加载预约列表失败:', error);
      this.setData({
        appointmentList: []
      });
    }
  },

  // 获取状态文本
  getStatusText(status) {
    const statusMap = {
      0: '待支付',
      1: '待就诊',
      2: '就诊中',
      3: '已完成',
      4: '已取消'
    };
    return statusMap[status] || '未知';
  },

  // 按状态筛选
  filterByStatus(e) {
    const status = e.currentTarget.dataset.status;
    this.setData({
      currentFilter: status
    });
    this.loadAppointmentList();
  },

  // 下拉刷新
  onRefresh() {
    this.setData({ isRefreshing: true });
    this.loadAppointmentList().finally(() => {
      this.setData({ isRefreshing: false });
    });
  },

  // 查看详情
  viewDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/appointment-detail/appointment-detail?id=${id}`
    });
  },

  // 取消预约
  cancelAppointment(e) {
    const id = e.currentTarget.dataset.id;
    
    wx.showModal({
      title: '确认取消',
      content: '确定要取消这个预约吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            const result = await app.request({
              url: '/api/user/appointment/cancel',
              method: 'POST',
              data: { appointmentId: id }
            });

            if (result.code === 200) {
              wx.showToast({
                title: '取消成功',
                icon: 'success'
              });
              this.loadAppointmentList();
            } else {
              wx.showToast({
                title: result.msg || '取消失败',
                icon: 'none'
              });
            }
          } catch (error) {
            console.error('取消预约错误:', error);
            wx.showToast({
              title: '网络错误，请稍后重试',
              icon: 'none'
            });
          }
        }
      }
    });
  },

  // 支付预约
  async payAppointment(e) {
    const id = e.currentTarget.dataset.id;
    
    wx.showLoading({ title: '支付中...' });
    
    try {
      const res = await app.request({
        url: '/api/user/appointment/pay',
        method: 'POST',
        data: {
          appointmentId: id,
          payType: 'wechat'
        }
      });

      if (res.code === 200) {
        wx.showToast({ title: '支付成功', icon: 'success' });
        this.loadAppointmentList();
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

  // 返回首页
  goToHome() {
    wx.switchTab({
      url: '/pages/index/index'
    });
  }
});
