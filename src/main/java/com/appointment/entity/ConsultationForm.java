package com.appointment.entity;

import java.util.Date;

public class ConsultationForm {
    private Integer id;
    private String consultationNo;
    private Integer appointmentId;
    private Integer userId;
    private Integer doctorId;
    private String formType;           // 问诊类型（初诊/复诊/急诊）
    private String chiefComplaint;     // 主诉
    private String presentIllness;     // 现病史/康复史
    private String pastHistory;        // 既往史/就诊史
    private String examination;        // 检查
    private String diagnosis;          // 诊断
    private String treatment;          // 治疗方案
    private Integer status;            // 0-待诊断 1-已完成
    private Date createTime;
    private Date updateTime;

    // 非数据库字段 - 关联展示信息
    private String patientName;
    private String patientIdCard;
    private String doctorName;
    private String doctorTitle;
    private String deptName;
    private String visitDate;
    private String visitTime;
    private Integer appointmentStatus;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getConsultationNo() { return consultationNo; }
    public void setConsultationNo(String consultationNo) { this.consultationNo = consultationNo; }

    public Integer getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Integer appointmentId) { this.appointmentId = appointmentId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getDoctorId() { return doctorId; }
    public void setDoctorId(Integer doctorId) { this.doctorId = doctorId; }

    public String getFormType() { return formType; }
    public void setFormType(String formType) { this.formType = formType; }

    public String getChiefComplaint() { return chiefComplaint; }
    public void setChiefComplaint(String chiefComplaint) { this.chiefComplaint = chiefComplaint; }

    public String getPresentIllness() { return presentIllness; }
    public void setPresentIllness(String presentIllness) { this.presentIllness = presentIllness; }

    public String getPastHistory() { return pastHistory; }
    public void setPastHistory(String pastHistory) { this.pastHistory = pastHistory; }

    public String getExamination() { return examination; }
    public void setExamination(String examination) { this.examination = examination; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getPatientIdCard() { return patientIdCard; }
    public void setPatientIdCard(String patientIdCard) { this.patientIdCard = patientIdCard; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getDoctorTitle() { return doctorTitle; }
    public void setDoctorTitle(String doctorTitle) { this.doctorTitle = doctorTitle; }

    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

    public String getVisitDate() { return visitDate; }
    public void setVisitDate(String visitDate) { this.visitDate = visitDate; }

    public String getVisitTime() { return visitTime; }
    public void setVisitTime(String visitTime) { this.visitTime = visitTime; }

    public Integer getAppointmentStatus() { return appointmentStatus; }
    public void setAppointmentStatus(Integer appointmentStatus) { this.appointmentStatus = appointmentStatus; }
}
