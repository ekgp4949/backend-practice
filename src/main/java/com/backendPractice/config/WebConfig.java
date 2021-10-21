package com.backendPractice.config;

import com.backendPractice.config.auth.LoginUser;
import com.backendPractice.config.auth.dto.SessionUser;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserArgumentResolver());
    }

    public HandlerMethodArgumentResolver loginUserArgumentResolver() {
        return new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                System.out.println(parameter.getParameterType());

                // 파라미터에 붙어있는 어노테이션이 LoginUser 어노테이션인가?
                boolean isLoginUserAnnotation = parameter.getMethodAnnotation(LoginUser.class) != null;

                // 해당 파라미터의 타입이 SessionUser 인가?
                boolean isUserClass = SessionUser.class.equals(parameter.getParameterType());
                return isLoginUserAnnotation && isUserClass;
            }

            @Override
            public Object resolveArgument(MethodParameter parameter,
                                          ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest,
                                          WebDataBinderFactory binderFactory) throws Exception {
                return webRequest.getAttribute("user", WebRequest.SCOPE_SESSION);
            }
        };
    }
}
