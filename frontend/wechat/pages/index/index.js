// pages/index/index.js
const app = getApp();

Page({
  data: {
    deptList: [],
    doctorList: [],
    searchKeyword: '',
    searchResults: null,
    showSearchResults: false
  },

  onLoad() {
    this.loadDeptList();
    this.loadDoctorList();
  },

  async loadDeptList() {
    try {
      const res = await app.request({
        url: '/api/user/dept/list',
        method: 'GET'
      });

      if (res.code === 200) {
        const icons = ['🏥', '🔪', '👶', '👩', '👁️', '🦷'];
        const deptList = (res.data || []).slice(0, 6).map((dept, index) => ({
          id: dept.id,
          name: dept.deptName,
          icon: icons[index] || '🏥'
        }));
        this.setData({ deptList });
      }
    } catch (error) {
      console.error('加载科室列表失败:', error);
    }
  },

  onShow() {
    const token = wx.getStorageSync('token');
    if (!token) {
      console.log('用户未登录');
    }
  },

  async loadDoctorList() {
    try {
      const res = await app.request({
        url: '/api/user/doctor/list',
        method: 'GET'
      });

      if (res.code === 200) {
        this.setData({
          doctorList: res.data.slice(0, 5)
        });
      }
    } catch (error) {
      console.error('加载医生列表失败:', error);
      this.setData({
        doctorList: [
          { id: 1, name: '张医生', title: '主任医师', deptName: '内科', remark: '擅长心血管疾病诊治' },
          { id: 2, name: '李医生', title: '副主任医师', deptName: '外科', remark: '擅长微创手术' },
          { id: 3, name: '王医生', title: '主任医师', deptName: '儿科', remark: '擅长儿童常见病诊治' }
        ]
      });
    }
  },

  onSearchInput(e) {
    const keyword = e.detail.value.trim();
    this.setData({ searchKeyword: keyword });
    
    if (keyword.length === 0) {
      this.setData({ 
        showSearchResults: false,
        searchResults: null
      });
      return;
    }
    
    this.performSearch(keyword);
  },

  async performSearch(keyword) {
    wx.showLoading({ title: '搜索中...' });
    
    try {
      const [deptRes, doctorRes] = await Promise.all([
        app.request({
          url: '/api/user/dept/list',
          method: 'GET'
        }),
        app.request({
          url: '/api/user/doctor/list',
          method: 'GET'
        })
      ]);
      
      let matchedDepts = [];
      let matchedDoctors = [];
      
      if (deptRes.code === 200 && deptRes.data) {
        matchedDepts = deptRes.data.filter(dept => 
          dept.deptName && dept.deptName.toLowerCase().includes(keyword.toLowerCase())
        );
      }
      
      if (doctorRes.code === 200 && doctorRes.data) {
        matchedDoctors = doctorRes.data.filter(doctor => 
          (doctor.name && doctor.name.toLowerCase().includes(keyword.toLowerCase())) ||
          (doctor.deptName && doctor.deptName.toLowerCase().includes(keyword.toLowerCase())) ||
          (doctor.title && doctor.title.toLowerCase().includes(keyword.toLowerCase()))
        );
      }
      
      this.setData({
        searchResults: {
          depts: matchedDepts,
          doctors: matchedDoctors
        },
        showSearchResults: true
      });
      
    } catch (error) {
      console.error('搜索失败:', error);
      wx.showToast({ title: '搜索失败', icon: 'none' });
    } finally {
      wx.hideLoading();
    }
  },

  clearSearch() {
    this.setData({
      searchKeyword: '',
      searchResults: null,
      showSearchResults: false
    });
  },

  goToDept() {
    wx.switchTab({
      url: '/pages/dept/dept'
    });
  },

  goToDoctor() {
    wx.navigateTo({
      url: '/pages/doctor/doctor'
    });
  },

  goToAppointment() {
    wx.switchTab({
      url: '/pages/appointment-list/appointment-list'
    });
  },

  goToProfile() {
    wx.switchTab({
      url: '/pages/profile/profile'
    });
  },

  goToAppointmentBooking() {
    wx.navigateTo({
      url: '/pages/booking/booking'
    });
  },

  selectDept(e) {
    const deptId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/booking/booking?deptId=${deptId}`
    });
  },

  selectDoctor(e) {
    const doctorId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/schedule/schedule?doctorId=${doctorId}`
    });
  },

  selectSearchDept(e) {
    const deptId = e.currentTarget.dataset.id;
    this.clearSearch();
    wx.navigateTo({
      url: `/pages/booking/booking?deptId=${deptId}`
    });
  },

  selectSearchDoctor(e) {
    const doctorId = e.currentTarget.dataset.id;
    this.clearSearch();
    wx.navigateTo({
      url: `/pages/schedule/schedule?doctorId=${doctorId}`
    });
  }
});