package com.hsbc.common.base;

import com.hsbc.common.constant.CommonErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class RbcsResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -4417426577607089898L;

    @Schema(description = "接口响应吗，200-成功，其他-失败")
    private long code;

    @Schema(description = "接口失败提示")
    private String message;

    @Schema(description = "接口返回数据")
    private T data;

    private RbcsResponse() {
    }

    public static <T> RbcsResponse<T> cloneWithOutData(RbcsResponse<?> source) {
        RbcsResponse<T> rbcsResponse = new RbcsResponse<>();
        return rbcsResponse.setCode(source.getCode()).setMessage(source.getMessage());
    }

    private RbcsResponse<T> setCode(long code) {
        this.code = code;
        return this;
    }

    public static <T> RbcsResponse<T> error() {
        return with(CommonErrorCode.SERVICE_ERROR);
    }

    public static <T> RbcsResponse<T> error(String message) {
        RbcsResponse<T> rbcsResponse = new RbcsResponse<>();
        return rbcsResponse.setCode(CommonErrorCode.SERVICE_ERROR.getErrCode()).setMessage(message);
    }

    public static <T> RbcsResponse<T> error(T data, String message) {
        RbcsResponse<T> rbcsResponse = new RbcsResponse<>();
        return rbcsResponse.setData(data).setCode(CommonErrorCode.SERVICE_ERROR.getErrCode()).setMessage(message);
    }

    public static <T> RbcsResponse<T> success() {
        return with(CommonErrorCode.OK);
    }

    public static <T> RbcsResponse<T> success(T data) {
        RbcsResponse<T> rbcsResponse = new RbcsResponse<>();
        return rbcsResponse.setCode(CommonErrorCode.OK.getErrCode()).setMessage(CommonErrorCode.OK.getErrMsg()).setData(data);
    }

    public static <T> RbcsResponse<T> success(String message) {
        RbcsResponse<T> rbcsResponse = new RbcsResponse<>();
        return rbcsResponse.setCode(CommonErrorCode.OK.getErrCode()).setMessage(message);
    }

    public static <T> RbcsResponse<T> with(RbcsException ex) {
        RbcsResponse<T> rbcsResponse = new RbcsResponse<>();
        return rbcsResponse.setCode(ex.getErrCode()).setMessage(ex.getErrMsg());
    }

    public static <T> RbcsResponse<T> with(ErrorCode errorCode) {
        RbcsResponse<T> rbcsResponse = new RbcsResponse<>();
        return rbcsResponse.setCode(errorCode.getErrCode()).setMessage(errorCode.getErrMsg());
    }
}
