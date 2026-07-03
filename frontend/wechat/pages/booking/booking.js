// pages/booking/booking.js
const app = getApp();

Page({
  data: {
    currentStep: 1,
    deptList: [],
    dateList: [],
    doctorList: [],
    scheduleList: [],
    
    selectedDeptId: null,
    selectedDeptName: '',
    selectedDate: '',
    selectedDoctorId: null,
    selectedDoctorName: '',
    selectedScheduleId: null,
    selectedTime: '',
    selectedNumberSource: null,

    // 问诊信息
    consultationTypeList: ['初诊', '复诊', '急诊'],
    consultationType: '',
    chiefComplaint: '',
    medicalHistory: '',
    recoveryHistory: ''
  },

  onLoad(options) {
    console.log('预约页面参数:', options);

    if (options.deptId) {
      const deptId = parseInt(options.deptId);
      this.setData({
        selectedDeptId: deptId,
        currentStep: 2
      });
    }

    this.loadDeptList();
    this.generateDateList();
  },

  // ==================== 问诊信息 ====================
  onConsultationTypeChange(e) {
    const index = e.detail.value;
    const types = ['初诊', '复诊', '急诊'];
    this.setData({ consultationType: types[index] });
  },

  onChiefComplaintInput(e) {
    this.setData({ chiefComplaint: e.detail.value });
  },

  onMedicalHistoryInput(e) {
    this.setData({ medicalHistory: e.detail.value });
  },

  onRecoveryHistoryInput(e) {
    this.setData({ recoveryHistory: e.detail.value });
  },

  // 加载科室列表
  async loadDeptList() {
    try {
      const res = await app.request({
        url: '/api/user/dept/list',
        method: 'GET'
      });

      if (res.code === 200) {
        const deptList = res.data || [];
        this.setData({ deptList });
        
        if (this.data.selectedDeptId) {
          const selectedDept = deptList.find(d => d.id === this.data.selectedDeptId);
          if (selectedDept) {
            this.setData({ selectedDeptName: selectedDept.deptName });
          }
        }
      }
    } catch (error) {
      console.error('加载科室列表失败:', error);
      wx.showToast({ title: '加载失败', icon: 'none' });
    }
  },

  // 生成未来7天日期列表
  generateDateList() {
    const dates = [];
    const today = new Date();
    const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
    
    for (let i = 0; i < 7; i++) {
      const date = new Date(today);
      date.setDate(today.getDate() + i);
      
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const dateStr = `${year}-${month}-${day}`;
      
      dates.push({
        date: dateStr,
        day: `${month}-${day}`,
        week: i === 0 ? '今天' : i === 1 ? '明天' : weekDays[date.getDay()]
      });
    }
    
    this.setData({ dateList: dates });
  },

  // 选择科室
  selectDept(e) {
    const { id, name } = e.currentTarget.dataset;
    this.setData({
      selectedDeptId: id,
      selectedDeptName: name
    });
  },

  // 选择日期
  selectDate(e) {
    const { date } = e.currentTarget.dataset;
    this.setData({ selectedDate: date });
  },

  // 选择医生
  selectDoctor(e) {
    const { id, name } = e.currentTarget.dataset;
    this.setData({
      selectedDoctorId: id,
      selectedDoctorName: name
    });
  },

  // 选择号源
  selectSchedule(e) {
    const { id, time, nsid, fee } = e.currentTarget.dataset;
    const schedule = this.data.scheduleList.find(s => s.id === id);
    
    if (schedule && schedule.remainNum > 0) {
      this.setData({
        selectedScheduleId: id,
        selectedTime: time,
        selectedNumberSource: {
          ...schedule,
          numberSourceId: nsid,
          fee: fee
        }
      });
    }
  },

  // 加载医生列表
  async loadDoctorList() {
    const { selectedDeptId, selectedDate } = this.data;
    
    wx.showLoading({ title: '加载中...' });
    
    try {
      const res = await app.request({
        url: `/api/user/doctor/list?deptId=${selectedDeptId}&date=${selectedDate}`,
        method: 'GET'
      });

      if (res.code === 200) {
        const doctors = res.data || [];
        this.setData({ doctorList: doctors });
      }
    } catch (error) {
      console.error('加载医生列表失败:', error);
      wx.showToast({ title: '加载失败', icon: 'none' });
    } finally {
      wx.hideLoading();
    }
  },

  // 加载排班和号源
  async loadScheduleList() {
    const { selectedDoctorId, selectedDate } = this.data;
    
    wx.showLoading({ title: '加载中...' });
    
    try {
      const res = await app.request({
        url: `/api/user/schedule/list?doctorId=${selectedDoctorId}&date=${selectedDate}`,
        method: 'GET'
      });

      if (res.code === 200 && res.data) {
        const schedules = res.data;
        const allTimeSlots = [];
        
        for (const schedule of schedules) {
          const timeSlots = schedule.timeSlots || [];
          for (const slot of timeSlots) {
            allTimeSlots.push({
              id: slot.id,
              scheduleId: schedule.id,
              visitTime: schedule.visitTime,
              fee: slot.fee || 0.01,
              remainNum: slot.remainNum,
              numberSourceId: slot.id
            });
          }
        }
        
        this.setData({
          scheduleList: allTimeSlots
        });
      } else {
        this.setData({ scheduleList: [] });
      }
    } catch (error) {
      console.error('加载排班列表失败:', error);
      this.setData({ scheduleList: [] });
    } finally {
      wx.hideLoading();
    }
  },

  // 下一步
  goNext() {
    const { currentStep, selectedDeptId, selectedDate, selectedDoctorId, selectedScheduleId, selectedNumberSource } = this.data;
    
    if (currentStep === 1) {
      if (!selectedDeptId) {
        wx.showToast({ title: '请选择科室', icon: 'none' });
        return;
      }
      this.setData({ currentStep: 2 });
    } else if (currentStep === 2) {
      if (!selectedDate) {
        wx.showToast({ title: '请选择日期', icon: 'none' });
        return;
      }
      this.setData({ currentStep: 3 });
      this.loadDoctorList();
    } else if (currentStep === 3) {
      if (!selectedDoctorId) {
        wx.showToast({ title: '请选择医生', icon: 'none' });
        return;
      }
      this.setData({ currentStep: 4 });
      this.loadScheduleList();
    } else if (currentStep === 4) {
      if (!selectedScheduleId || !selectedNumberSource) {
        wx.showToast({ title: '请选择时间段', icon: 'none' });
        return;
      }
      this.confirmBooking();
    }
  },

  // 上一步
  goBack() {
    const { currentStep } = this.data;
    if (currentStep > 1) {
      this.setData({ currentStep: currentStep - 1 });
    }
  },

  // 返回选择日期
  goBackToDate() {
    this.setData({ currentStep: 2 });
  },

  // 返回选择医生
  goBackToDoctor() {
    this.setData({ currentStep: 3 });
  },

  // 取消预约
  cancelBooking() {
    wx.navigateBack();
  },

  // 确认预约
  async confirmBooking() {
    const { selectedNumberSource } = this.data;
    const userInfo = wx.getStorageSync('userInfo');
    
    if (!userInfo) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }

    wx.showLoading({ title: '预约中...' });

    try {
      const userRes = await app.request({
        url: `/api/user/info?userId=${userInfo.id}`,
        method: 'GET'
      });

      let patientName = userInfo.realName;
      let patientIdCard = userInfo.idCard;

      if (!patientName || !patientIdCard) {
        wx.hideLoading();
        wx.showModal({
          title: '需要完善信息',
          content: '预约需要实名信息，请先完善个人资料',
          confirmText: '去完善',
          success: (res) => {
            if (res.confirm) {
              wx.navigateTo({ url: '/pages/edit-profile/edit-profile' });
            }
          }
        });
        return;
      }

      const res = await app.request({
        url: '/api/user/appointment/create',
        method: 'POST',
        data: {
          numberSourceId: selectedNumberSource.numberSourceId,
          userId: userInfo.id,
          patientName,
          patientIdCard
        }
      });

      if (res.code === 200) {
        // 保存问诊单到 consultation_form
        const appointmentId = res.data.appointmentId;
        if (appointmentId && (this.data.consultationType || this.data.chiefComplaint)) {
          await app.request({
            url: '/api/user/consultation/save',
            method: 'POST',
            data: {
              appointmentId: appointmentId,
              formType: this.data.consultationType,
              chiefComplaint: this.data.chiefComplaint,
              presentIllness: this.data.recoveryHistory,
              pastHistory: this.data.medicalHistory
            }
          });
        }

        wx.hideLoading();

        console.log('预约创建返回数据:', res);
        // appointmentId already declared above when saving consultation
        console.log('提取的appointmentId:', appointmentId);
        
        if (!appointmentId) {
          wx.showToast({ title: '预约ID获取失败', icon: 'none' });
          return;
        }
        
        wx.showModal({
          title: '预约成功',
          content: `预约成功！挂号费用：¥${selectedNumberSource.fee || 0.01}，是否立即支付？`,
          confirmText: '立即支付',
          cancelText: '稍后支付',
          success: async (modalRes) => {
            if (modalRes.confirm) {
              await this.payAppointment(appointmentId);
            } else {
              wx.showToast({ title: '请尽快完成支付', icon: 'none' });
              wx.switchTab({ url: '/pages/appointment-list/appointment-list' });
            }
          }
        });
      } else {
        wx.hideLoading();
        wx.showToast({ title: res.msg || '预约失败', icon: 'none' });
      }
    } catch (error) {
      wx.hideLoading();
      console.error('预约失败:', error);
      wx.showToast({ title: '预约失败，请重试', icon: 'none' });
    }
  },

  // 支付
  async payAppointment(appointmentId) {
    console.log('支付函数接收到的appointmentId:', appointmentId);
    
    if (!appointmentId) {
      wx.showToast({ title: '预约ID不能为空', icon: 'none' });
      return;
    }
    
    wx.showLoading({ title: '支付中...' });

    try {
      const res = await app.request({
        url: '/api/user/appointment/pay',
        method: 'POST',
        data: {
          appointmentId: appointmentId,
          payType: 'wechat'
        }
      });

      if (res.code === 200) {
        wx.showToast({ title: '支付成功', icon: 'success' });
        setTimeout(() => {
          wx.switchTab({ url: '/pages/appointment-list/appointment-list' });
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
  }
});
