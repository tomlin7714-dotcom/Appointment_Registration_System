package com.appointment.entity;

import java.util.Date;

public class OperateLog {
    private Integer id;
    private Integer operatorId;
    private Integer operatorRole;
    private String content;
    private String operateIp;
    private Date operateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getOperatorRole() {
        return operatorRole;
    }

    public void setOperatorRole(Integer operatorRole) {
        this.operatorRole = operatorRole;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOperateIp() {
        return operateIp;
    }

    public void setOperateIp(String operateIp) {
        this.operateIp = operateIp;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }
}
