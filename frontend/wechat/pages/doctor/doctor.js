// pages/doctor/doctor.js
const app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    deptId: null,
    deptName: '',
    doctorList: []
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    console.log('医生页面参数:', options);
    const deptId = options.deptId;
    const deptName = options.deptName || '医生列表';
    
    this.setData({
      deptId: deptId,
      deptName: deptName
    });

    this.loadDoctorList(deptId);
  },

  // 加载医生列表
  async loadDoctorList(deptId) {
    wx.showLoading({
      title: '加载中...',
    });

    try {
      const res = await app.request({
        url: '/api/user/appointment/doctor/list',
        method: 'GET',
        data: {
          deptId: deptId
        }
      });

      if (res.code === 200) {
        this.setData({
          doctorList: res.data
        });
      } else {
        wx.showToast({
          title: res.msg || '加载失败',
          icon: 'none'
        });
      }
    } catch (error) {
      console.error('加载医生列表失败:', error);
      // 使用模拟数据
      this.setData({
        doctorList: [
          { id: 1, name: '张医生', title: '主任医师', deptName: '内科', intro: '从医20年，擅长内科各种疾病的诊治', specialty: '心血管疾病、呼吸系统疾病' },
          { id: 2, name: '李医生', title: '副主任医师', deptName: '内科', intro: '从医15年，擅长消化系统疾病', specialty: '胃肠疾病、肝病' }
        ]
      });
    } finally {
      wx.hideLoading();
    }
  },

  // 选择医生
  selectDoctor(e) {
    const doctorId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/schedule/schedule?doctorId=${doctorId}`
    });
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {
    this.loadDoctorList(this.data.deptId);
    wx.stopPullDownRefresh();
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {

  }
})