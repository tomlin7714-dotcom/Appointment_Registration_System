// pages/appointment/appointment.js
const app = getApp();

Page({
  data: {
    doctorId: null,
    doctorInfo: {},
    dateList: [],
    timeSlots: [],
    selectedDate: '',
    selectedTime: '',
    selectedPrice: 0,
    selectedNumberSourceId: null,
    userInfo: {},
    canSubmit: false
  },

  onLoad(options) {
    const doctorId = options.doctorId;
    if (!doctorId) {
      wx.showToast({
        title: '参数错误',
        icon: 'none'
      });
      wx.navigateBack();
      return;
    }

    this.setData({ doctorId });
    
    // 获取用户信息
    const userInfo = wx.getStorageSync('userInfo') || {};
    this.setData({ userInfo });

    // 加载医生信息
    this.loadDoctorInfo(doctorId);
    
    // 生成日期列表
    this.generateDateList();
  },

  // 加载医生信息
  async loadDoctorInfo(doctorId) {
    try {
      const res = await app.request({
        url: `/api/user/doctor/detail?id=${doctorId}`,
        method: 'GET'
      });

      if (res.code === 200) {
        this.setData({
          doctorInfo: res.data
        });
      }
    } catch (error) {
      console.error('加载医生信息失败:', error);
      // 使用模拟数据
      this.setData({
        doctorInfo: {
          id: doctorId,
          name: '张医生',
          title: '主任医师',
          deptName: '内科',
          remark: '擅长心血管疾病诊治'
        }
      });
    }
  },

  // 生成日期列表
  generateDateList() {
    const dates = [];
    const today = new Date();
    const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];

    for (let i = 0; i < 7; i++) {
      const date = new Date(today);
      date.setDate(today.getDate() + i);
      
      const month = date.getMonth() + 1;
      const day = date.getDate();
      const weekDay = i === 0 ? '今天' : weekDays[date.getDay()];
      
      dates.push({
        date: `${date.getFullYear()}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`,
        week: weekDay,
        day: `${month}/${day}`,
        hasSource: i < 5 // 模拟前5天有号
      });
    }

    this.setData({
      dateList: dates,
      selectedDate: dates[0].date
    });

    // 加载第一天的号源
    this.loadNumberSource(dates[0].date);
  },

  // 加载号源
  async loadNumberSource(date) {
    const { doctorId } = this.data;
    
    try {
      const res = await app.request({
        url: `/api/user/numbersource/list?doctorId=${doctorId}&date=${date}`,
        method: 'GET'
      });

      if (res.code === 200) {
        this.setData({
          timeSlots: res.data
        });
      }
    } catch (error) {
      console.error('加载号源失败:', error);
      // 使用模拟数据
      this.setData({
        timeSlots: [
          { id: 1, timeRange: '08:00-08:30', price: 50, remain: 3 },
          { id: 2, timeRange: '08:30-09:00', price: 50, remain: 5 },
          { id: 3, timeRange: '09:00-09:30', price: 50, remain: 0 },
          { id: 4, timeRange: '09:30-10:00', price: 50, remain: 2 },
          { id: 5, timeRange: '10:00-10:30', price: 50, remain: 4 },
          { id: 6, timeRange: '10:30-11:00', price: 50, remain: 1 }
        ]
      });
    }
  },

  // 选择日期
  selectDate(e) {
    const date = e.currentTarget.dataset.date;
    this.setData({
      selectedDate: date,
      selectedTime: '',
      selectedPrice: 0,
      selectedNumberSourceId: null,
      canSubmit: false
    });
    this.loadNumberSource(date);
  },

  // 选择时间段
  selectTime(e) {
    const item = e.currentTarget.dataset.item;
    if (item.remain <= 0) return;

    this.setData({
      selectedTime: item.timeRange,
      selectedPrice: item.price,
      selectedNumberSourceId: item.id,
      canSubmit: true
    });
  },

  // 提交预约
  async submitAppointment() {
    const { doctorId, selectedDate, selectedNumberSourceId, userInfo } = this.data;

    if (!selectedNumberSourceId) {
      wx.showToast({
        title: '请选择时间段',
        icon: 'none'
      });
      return;
    }

    wx.showLoading({ title: '提交中...' });

    try {
      const res = await app.request({
        url: '/api/user/appointment/save',
        method: 'POST',
        data: {
          doctorId: doctorId,
          numberSourceId: selectedNumberSourceId,
          appointDate: selectedDate,
          patientName: userInfo.name,
          patientPhone: userInfo.phone,
          patientIdCard: userInfo.idCard
        }
      });

      wx.hideLoading();

      if (res.code === 200) {
        wx.showModal({
          title: '预约成功',
          content: '您已成功预约，请按时就诊',
          showCancel: false,
          success: () => {
            wx.switchTab({
              url: '/pages/appointment-list/appointment-list'
            });
          }
        });
      } else {
        wx.showToast({
          title: res.msg || '预约失败',
          icon: 'none'
        });
      }
    } catch (error) {
      wx.hideLoading();
      console.error('预约错误:', error);
      wx.showToast({
        title: '网络错误，请稍后重试',
        icon: 'none'
      });
    }
  }
});