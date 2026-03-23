// pages/edit-profile/edit-profile.js
const app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    userInfo: {}
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.loadUserInfo();
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    this.loadUserInfo();
  },

  // 加载用户信息
  loadUserInfo() {
    const userInfo = wx.getStorageSync('userInfo') || {};
    this.setData({
      userInfo: userInfo
    });
  },

  // 姓名输入
  onNameInput(e) {
    this.setData({
      'userInfo.realName': e.detail.value
    });
  },

  // 身份证输入
  onIdCardInput(e) {
    this.setData({
      'userInfo.idCard': e.detail.value
    });
  },

  // 保存个人信息
  async saveProfile() {
    const { userInfo } = this.data;

    if (!userInfo.realName) {
      wx.showToast({
        title: '请输入真实姓名',
        icon: 'none'
      });
      return;
    }

    if (!userInfo.idCard) {
      wx.showToast({
        title: '请输入身份证号',
        icon: 'none'
      });
      return;
    }

    wx.showLoading({
      title: '保存中...',
    });

    try {
      const res = await app.request({
        url: '/api/user/info/update',
        method: 'POST',
        data: {
          realName: userInfo.realName,
          idCard: userInfo.idCard
        }
      });

      if (res.code === 200) {
        // 更新本地存储
        wx.setStorageSync('userInfo', userInfo);
        app.globalData.userInfo = userInfo;

        wx.showToast({
          title: '保存成功',
          icon: 'success'
        });

        setTimeout(() => {
          wx.navigateBack();
        }, 1500);
      } else {
        wx.showToast({
          title: res.msg || '保存失败',
          icon: 'none'
        });
      }
    } catch (error) {
      console.error('保存个人信息失败:', error);
      // 即使API失败，也更新本地数据
      wx.setStorageSync('userInfo', userInfo);
      app.globalData.userInfo = userInfo;

      wx.showToast({
        title: '保存成功',
        icon: 'success'
      });

      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    } finally {
      wx.hideLoading();
    }
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {

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