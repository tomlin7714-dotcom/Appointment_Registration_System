// pages/ai-triage/ai-triage.js
const app = getApp();

Page({
  data: {
    messages: [],
    inputValue: '',
    isLoading: false,
    showQuickSymptoms: true,
    scrollToId: '',
    msgCounter: 0
  },

  onInput(e) {
    this.setData({ inputValue: e.detail.value });
  },

  sendQuickSymptom(e) {
    const symptom = e.currentTarget.dataset.symptom;
    this.setData({ inputValue: symptom });
    this.sendMessage();
  },

  async sendMessage() {
    const content = this.data.inputValue.trim();
    if (!content || this.data.isLoading) return;

    const msgId = this.data.msgCounter + 1;

    // 添加用户消息
    const userMsg = { id: msgId, role: 'user', content };
    this.setData({
      messages: [...this.data.messages, userMsg],
      inputValue: '',
      isLoading: true,
      showQuickSymptoms: false,
      msgCounter: msgId
    });

    // 滚动到底部
    this.scrollToBottom(msgId);

    try {
      const res = await app.request({
        url: '/api/ai/triage',
        method: 'POST',
        data: { symptom: content }
      });

      const aiMsgId = this.data.msgCounter + 1;

      if (res.code === 200) {
        const aiMsg = {
          id: aiMsgId,
          role: 'ai',
          content: res.data.advice || '请稍等，我正在分析您的症状...',
          depts: res.data.depts || []
        };
        this.setData({
          messages: [...this.data.messages, aiMsg],
          isLoading: false,
          msgCounter: aiMsgId
        });
      } else {
        const errMsg = {
          id: aiMsgId,
          role: 'ai',
          content: res.msg || '服务异常，请稍后重试'
        };
        this.setData({
          messages: [...this.data.messages, errMsg],
          isLoading: false,
          msgCounter: aiMsgId
        });
      }
    } catch (error) {
      console.error('AI导诊请求失败:', error);
      const errId = this.data.msgCounter + 1;
      this.setData({
        messages: [...this.data.messages, {
          id: errId,
          role: 'ai',
          content: '网络连接失败，请检查网络后重试'
        }],
        isLoading: false,
        msgCounter: errId
      });
    }

    this.scrollToBottom(this.data.msgCounter);
  },

  scrollToBottom(id) {
    setTimeout(() => {
      this.setData({ scrollToId: `msg-${id}` });
    }, 100);
  },

  bookDept(e) {
    const deptId = e.currentTarget.dataset.deptid;
    if (!deptId) {
      wx.showToast({ title: '暂不支持直接预约该科室', icon: 'none' });
      return;
    }
    wx.navigateTo({
      url: `/pages/booking/booking?deptId=${deptId}`
    });
  }
});
