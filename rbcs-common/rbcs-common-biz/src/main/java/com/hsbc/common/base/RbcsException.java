package com.hsbc.common.base;

import com.hsbc.common.constant.CommonErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

@Getter
@Setter
@ToString
public class RbcsException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 8520226586785256249L;

    /**
     * 错误码（parentErrCode + 3位自编码）
     */
    private final long errCode;

    /**
     * 错误信息
     */
    private final String errMsg;

    RbcsException(Throwable ex) {
        super(ex);
        this.errCode = CommonErrorCode.SERVICE_ERROR.getErrCode();
        this.errMsg = ex.getMessage();
    }

    RbcsException(String message) {
        super(message);
        this.errCode = CommonErrorCode.SERVICE_ERROR.getErrCode();
        this.errMsg = message;
    }

    RbcsException(String message, Exception ex) {
        super(message, ex);
        this.errCode = CommonErrorCode.SERVICE_ERROR.getErrCode();
        this.errMsg = message;
    }

    RbcsException(long code, String message) {
        super(message);
        this.errCode = code;
        this.errMsg = message;
    }

    RbcsException(long code, String message, Exception ex) {
        super(message, ex);
        this.errCode = code;
        this.errMsg = message;
    }

    public static RbcsException of(RbcsResponse<?> response) {
        return new RbcsException(response.getCode(), response.getMessage());
    }

    public static RbcsException of(String message) {
        return new RbcsException(message);
    }

    public static RbcsException of(String message, Exception ex) {
        return new RbcsException(message, ex);
    }

    public static RbcsException of(ErrorCode enums) {
        return new RbcsException(enums.getErrCode(), enums.getErrMsg());
    }

    public static RbcsException of(ErrorCode enums, Exception ex) {
        return new RbcsException(enums.getErrCode(), enums.getErrMsg(), ex);
    }

    public static RbcsException of(ErrorCode enums, String message) {
        return new RbcsException(enums.getErrCode(), message);
    }

    public static RbcsException of(ErrorCode enums, String message, Exception ex) {
        return new RbcsException(enums.getErrCode(), message, ex);
    }

    public static RbcsException of(Throwable ex) {
        return new RbcsException(ex);
    }

}
