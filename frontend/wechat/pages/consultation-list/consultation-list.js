// pages/consultation-list/consultation-list.js
const app = getApp();

Page({
  data: {
    recordList: [],
    keyword: '',
    isRefreshing: false
  },

  onLoad() {
    this.loadRecords();
  },

  onShow() {
    this.loadRecords();
  },

  async loadRecords() {
    const userInfo = wx.getStorageSync('userInfo');
    if (!userInfo) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }

    const { keyword } = this.data;

    try {
      let url = `/api/user/appointment/consultation/list?userId=${userInfo.id}`;
      if (keyword) {
        url += `&keyword=${encodeURIComponent(keyword)}`;
      }

      const res = await app.request({ url, method: 'GET' });

      if (res.code === 200) {
        this.setData({ recordList: res.data || [] });
      }
    } catch (error) {
      console.error('加载问诊记录失败:', error);
      this.setData({ recordList: [] });
    }
  },

  onKeywordInput(e) {
    this.setData({ keyword: e.detail.value });
  },

  doSearch() {
    this.loadRecords();
  },

  onRefresh() {
    this.setData({ isRefreshing: true });
    this.loadRecords().finally(() => {
      this.setData({ isRefreshing: false });
    });
  },

  viewDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/consultation-detail/consultation-detail?appointmentId=${id}`
    });
  },

  goToBooking() {
    wx.switchTab({ url: '/pages/index/index' });
  }
});
