package com.hsbc.common.aop;

import com.hsbc.common.base.RbcsException;
import com.hsbc.common.base.RbcsResponse;
import com.hsbc.common.constant.CommonErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler<T> {
    @ModelAttribute
    public void setResponse(HttpServletResponse response) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    }

    @ExceptionHandler(NullPointerException.class)
    @Order(0)
    public RbcsResponse<Void> nullPointerExceptionExceptionHandler(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        log.error("nullPointerExceptionExceptionHandler 请求地址:{}", request.getRequestURI(), ex);
        return RbcsResponse.with(RbcsException.of(CommonErrorCode.REQUIRED_PARAM_NULL));
    }

    /**
     * spring-validate 相关异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @Order(10)
    public RbcsResponse<Void> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex, HttpServletRequest request, HttpServletResponse response) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.error("clientAbortExceptionHandler exMessage:{}, 请求地址:{}", message, request.getRequestURI(), ex);
        return RbcsResponse.with(RbcsException.of(CommonErrorCode.REQUIRED_PARAM_NULL, message));
    }

    /**
     * mvc 接口数据绑定异常
     */
    @ExceptionHandler(BindException.class)
    @Order(11)
    public RbcsResponse<Void> bindExceptionHandler(BindException ex, HttpServletRequest request, HttpServletResponse response) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.error("bindExceptionHandler exMessage:{}, 请求地址:{}", message, request.getRequestURI(), ex);
        return RbcsResponse.error(message);
    }

    /**
     * spring-validate 相关异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @Order(12)
    public RbcsResponse<Void> constraintViolationExceptionHandler(ConstraintViolationException ex, HttpServletRequest request, HttpServletResponse response) {
        String message = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(","));
        log.warn("constraintViolationExceptionHandler exMessage:{}, 请求地址:{}", message, request.getRequestURI());
        return RbcsResponse.with(RbcsException.of(CommonErrorCode.REQUIRED_PARAM_NULL, message));
    }

    @ExceptionHandler(ValidationException.class)
    @Order(13)
    public RbcsResponse<Void> validationExceptionHandler(ValidationException ex, HttpServletRequest request, HttpServletResponse response) {
        String message = ex.getMessage();
        String messageTemplate = "messageTemplate='";
        int beginIndex = message.indexOf(messageTemplate) + messageTemplate.length();
        String temp = message.substring(beginIndex);
        String result = temp.substring(0, temp.indexOf("'"));
        log.warn("validationExceptionHandler exMessage:{}, 请求地址:{}", result, request.getRequestURI(), ex);
        return RbcsResponse.error(result);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @Order(14)
    public RbcsResponse<Void> illegalArgumentExceptionExceptionHandler(IllegalArgumentException ex, HttpServletRequest request, HttpServletResponse response) {
        String message = "处理失败，参数不合法！";
        log.warn("illegalArgumentExceptionExceptionHandler exMessage:{}, 请求地址:{}", message, request.getRequestURI(), ex);
        return RbcsResponse.error(message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @Order(15)
    public RbcsResponse<Void> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException ex, HttpServletRequest request, HttpServletResponse response) {
        log.warn("missingServletRequestParameterExceptionHandler 缺少请求参数:{}, 请求地址:{}", ex.getParameterName(), request.getRequestURI(), ex);
        return RbcsResponse.with(RbcsException.of(CommonErrorCode.REQUIRED_PARAM_NULL, "缺少请求参数"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @Order(16)
    public RbcsResponse<Void> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException ex, HttpServletRequest request, HttpServletResponse response) {
        String message = "参数类型不匹配";
        log.warn("httpMessageNotReadableExceptionHandler exMessage:{}, 请求地址:{}", message, request.getRequestURI(), ex);
        return RbcsResponse.error(message);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @Order(17)
    public RbcsResponse<Void> missingRequestHeaderExceptionHandler(MissingRequestHeaderException ex, HttpServletRequest request, HttpServletResponse response) {
        String message = "缺少请求头参数".concat(ex.getHeaderName());
        log.warn("missingRequestHeaderExceptionHandler exMessage:{}, 请求地址:{}", message, request.getRequestURI(), ex);
        return RbcsResponse.error(message);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @Order(20)
    public RbcsResponse<Void> httpRequestMethodNotSupportedExceptionHandler(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        String message = "请求方式不支持!";
        log.warn("httpRequestMethodNotSupportedExceptionHandler exMessage:{}, 请求地址:{}", message, request.getRequestURI(), ex);
        return RbcsResponse.error(message);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @Order(30)
    public RbcsResponse<Void> maxUploadSizeExceededExceptionHandler(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        String message = "上传文件大小超出限制!";
        log.error("maxUploadSizeExceededExceptionHandler exMessage:{}, 请求地址:{}", message, request.getRequestURI(), ex);
        return RbcsResponse.error(message);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @Order(40)
    public RbcsResponse<Void> noHandlerFoundExceptionHandler(NoHandlerFoundException ex, HttpServletRequest request, HttpServletResponse response) {
        String message = "接口不存在";
        log.warn("noHandlerFoundExceptionHandler exMessage:{}, 请求地址:{}", message, request.getRequestURI(), ex);
        return RbcsResponse.error(message);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @Order(41)
    public RbcsResponse<Void> noResourceFoundExceptionHandler(NoResourceFoundException ex, HttpServletRequest request, HttpServletResponse response) {
        String message = "接口不存在";
        log.warn("noResourceFoundExceptionHandler exMessage:{}, 请求地址:{}", message, request.getRequestURI(), ex);
        return RbcsResponse.error(message);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @Order(50)
    public RbcsResponse<Void> httpMediaTypeNotSupportedExceptionHandler(HttpMediaTypeNotSupportedException ex, HttpServletRequest request, HttpServletResponse response) {
        String message = "http媒体类型不支持";
        log.warn("httpMediaTypeNotSupportedExceptionHandler exMessage:{}, 请求地址:{}", message, request.getRequestURI(), ex);
        return RbcsResponse.error(message);
    }

    @ExceptionHandler(MultipartException.class)
    @Order(60)
    public RbcsResponse<Void> multipartExceptionHandler(MultipartException ex, HttpServletRequest request, HttpServletResponse response) {
        String message = "文件上传失败";
        log.warn("multipartExceptionHandler exMessage:{}, 请求地址:{}", message, request.getRequestURI(), ex);
        return RbcsResponse.error(message);
    }

    /**
     * 专门处理自定义的{@link RbcsException}异常。
     *
     * @param e 自定义的业务异常，包含了具体的错误代码和消息
     * @return 返回一个封装了异常信息的响应对象，状态码对应自定义错误代码
     */
    @Order(998)
    @ExceptionHandler(RbcsException.class)
    public RbcsResponse<T> handleRbcsException(RbcsException e) {
        if (e.getCause() != null) {
            log.error("[执行业务异常]", e);
        }
        return RbcsResponse.with(e);
    }

    /**
     * 处理所有未被特定异常处理器捕获的异常。
     *
     * @param e 抛出的异常对象，包含了错误信息
     * @return 返回一个包含错误信息的响应对象，状态码通常表示错误
     */
    @Order(999)
    @ExceptionHandler(Throwable.class)
    public RbcsResponse<T> handleException(Throwable e) {
        log.error("[系统执行异常]", e);
        // 将异常信息转化为错误响应，返回给客户端
        return RbcsResponse.error();
    }
}
