package com.quartz.demo.config;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;

/**
 * 自定义拦截器
 *
 * @author XiaoXuxuy
 */
@Slf4j
public class MyInterceptor implements HandlerInterceptor {

    private static final String ENCODING = "UTF-8";
    private static final String CONTENT_TYPE = "application/json; charset=utf-8";
    private static final String SWAGGER = "swagger";
    private static final String SWAGGER_API_DOC = "/v2/api-docs";
    private static final String SWAGGER_INDEX = "/doc.html";
    private static final String SWAGGER_API_ICO = ".ico";
    private static final String SWAGGER_WEBJARS = "webjars";
    private static final String SWAGGER_ERROR_PATH = "/error";

    /**
     * 在执行目标方法之前执行
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("[/]+$", "");

        InetAddress address = InetAddress.getLocalHost();
        String ip = address.getHostAddress();
        log.info(ip + " 请求: " + path);

        response.setCharacterEncoding(ENCODING);
        response.setContentType(CONTENT_TYPE);

        // 允许请求swagger文档
        if (path.contains(SWAGGER_ERROR_PATH) || path.equals(SWAGGER_INDEX) || path.contains(SWAGGER) || path.contains(SWAGGER_API_ICO) || path.contains(SWAGGER_API_DOC) || path.contains(SWAGGER_WEBJARS)) {
            return true;
        }

        // do something

        return true;
    }

    private boolean errorMsg(HttpServletResponse response, JSONObject res, String code, String message) throws IOException {
        PrintWriter writer;
        res.put("message", message);
        res.put("code", code);
        writer = response.getWriter();
        writer.write(String.valueOf(res));
        writer.close();
        return false;
    }

    /**
     * 执行目标方法之后执行
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
    }

    /**
     * 在请求已经返回之后执行
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }
}
