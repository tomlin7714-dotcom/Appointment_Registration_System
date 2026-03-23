package com.appointment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AppointmentApplication {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("医疗预约系统正在启动...");
        System.out.println("========================================");
        SpringApplication.run(AppointmentApplication.class, args);
        System.out.println("========================================");
        System.out.println("医疗预约系统启动成功！");
        System.out.println("访问地址：");
        System.out.println("  - 管理员端：http://localhost:8083/admin/login.html");
        System.out.println("  - 医生端：http://localhost:8083/doctor/login.html");
        System.out.println("========================================");
    }
}
