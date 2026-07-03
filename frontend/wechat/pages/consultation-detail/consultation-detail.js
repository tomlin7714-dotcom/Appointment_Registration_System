// pages/consultation-detail/consultation-detail.js
const app = getApp();

Page({
  data: {
    appointmentId: null,
    detail: {},
    loading: true
  },

  onLoad(options) {
    const appointmentId = options.appointmentId;
    if (!appointmentId) {
      wx.showToast({ title: '参数错误', icon: 'none' });
      wx.navigateBack();
      return;
    }
    this.setData({ appointmentId });
    this.loadDetail(appointmentId);
  },

  async loadDetail(appointmentId) {
    try {
      const res = await app.request({
        url: `/api/user/appointment/consultation/detail?appointmentId=${appointmentId}`,
        method: 'GET'
      });

      if (res.code === 200) {
        this.setData({ detail: res.data || {} });
      } else {
        wx.showToast({ title: res.msg || '加载失败', icon: 'none' });
      }
    } catch (error) {
      console.error('加载问诊详情失败:', error);
      wx.showToast({ title: '加载失败', icon: 'none' });
    } finally {
      this.setData({ loading: false });
    }
  }
});
