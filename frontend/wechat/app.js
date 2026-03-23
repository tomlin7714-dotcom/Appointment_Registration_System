// app.js
App({
  globalData: {
    userInfo: null,
    token: null,
    apiBaseUrl: 'http://localhost:8083'
  },

  onLaunch() {
    // 从本地存储获取用户信息
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');
    
    if (token && userInfo) {
      this.globalData.token = token;
      this.globalData.userInfo = userInfo;
    }
    
    console.log('小程序启动');
  },

  // 全局请求方法
  request(options) {
    const { url, method = 'GET', data, header = {} } = options;
    
    return new Promise((resolve, reject) => {
      wx.request({
        url: this.globalData.apiBaseUrl + url,
        method,
        data,
        header: {
          'Content-Type': 'application/json',
          'token': this.globalData.token || '',
          ...header
        },
        success: (res) => {
          if (res.statusCode === 200) {
            resolve(res.data);
          } else {
            reject(res);
          }
        },
        fail: reject
      });
    });
  }
});