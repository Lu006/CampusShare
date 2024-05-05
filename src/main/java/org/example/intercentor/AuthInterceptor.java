package org.example.intercentor;

import cn.hutool.core.util.StrUtil;
import org.example.context.BaseContext;
import org.example.utils.JwtUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if(!StrUtil.isBlank(token)){
            Integer userId = JwtUtils.getUserId(token);
            if(userId!=null){
                BaseContext.set(userId);
            }
        }
        return true;
    }
}
