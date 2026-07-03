// pages/edit-profile/edit-profile.js
const app = getApp();

Page({
  data: {
    userInfo: {}
  },

  onLoad(options) {
    this.loadUserInfo();
  },

  onShow() {
    this.loadUserInfo();
  },

  // 加载用户信息
  loadUserInfo() {
    const localInfo = wx.getStorageSync('userInfo') || {};
    // 从API获取最新信息
    if (localInfo.id) {
      app.request({
        url: `/api/user/info?userId=${localInfo.id}`,
        method: 'GET'
      }).then(res => {
        if (res.code === 200) {
          const info = res.data;
          // 合并本地和远程数据
          const merged = { ...localInfo, ...info };
          this.setData({ userInfo: merged });
          wx.setStorageSync('userInfo', merged);
        }
      }).catch(() => {
        this.setData({ userInfo: localInfo });
      });
    } else {
      this.setData({ userInfo: localInfo });
    }
  },

  // 选择头像
  chooseAvatar() {
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const tempPath = res.tempFilePaths[0];
        // 上传头像到服务器
        wx.uploadFile({
          url: app.globalData.apiBaseUrl + '/api/user/avatar/upload',
          filePath: tempPath,
          name: 'file',
          header: {
            'token': wx.getStorageSync('token')
          },
          success: (uploadRes) => {
            try {
              const data = JSON.parse(uploadRes.data);
              if (data.code === 200) {
                const avatarUrl = data.data.avatarUrl;
                this.setData({
                  'userInfo.avatar': avatarUrl
                });
                wx.showToast({ title: '头像上传成功', icon: 'success' });
              } else {
                wx.showToast({ title: data.msg || '上传失败', icon: 'none' });
              }
            } catch (e) {
              wx.showToast({ title: '上传失败', icon: 'none' });
            }
          },
          fail: () => {
            wx.showToast({ title: '上传失败', icon: 'none' });
          }
        });
      }
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
      wx.showToast({ title: '请输入真实姓名', icon: 'none' });
      return;
    }

    if (!userInfo.idCard) {
      wx.showToast({ title: '请输入身份证号', icon: 'none' });
      return;
    }

    wx.showLoading({ title: '保存中...' });

    try {
      const res = await app.request({
        url: '/api/user/info/update',
        method: 'POST',
        data: {
          id: userInfo.id,
          realName: userInfo.realName,
          idCard: userInfo.idCard,
          avatar: userInfo.avatar || null
        }
      });

      if (res.code === 200) {
        // 更新本地存储
        wx.setStorageSync('userInfo', userInfo);
        app.globalData.userInfo = userInfo;

        wx.showToast({ title: '保存成功', icon: 'success' });

        setTimeout(() => {
          wx.navigateBack();
        }, 1500);
      } else {
        wx.showToast({ title: res.msg || '保存失败', icon: 'none' });
      }
    } catch (error) {
      console.error('保存个人信息失败:', error);
      wx.showToast({ title: '网络错误', icon: 'none' });
    } finally {
      wx.hideLoading();
    }
  }
});
