package com.appointment.interceptor;

import com.appointment.util.JwtUtil;
import com.appointment.vo.ResponseVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            returnJson(response, ResponseVo.error(401, "未登录或Token过期"));
            return false;
        }

        try {
            if (jwtUtil.isTokenExpired(token)) {
                returnJson(response, ResponseVo.error(401, "Token已过期"));
                return false;
            }

            // 验证权限...
            // 这里可以根据请求路径和用户角色进行权限验证

        } catch (Exception e) {
            returnJson(response, ResponseVo.error(401, "Token无效"));
            return false;
        }

        return true;
    }

    private void returnJson(HttpServletResponse response, ResponseVo responseVo) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(responseVo));
    }
}