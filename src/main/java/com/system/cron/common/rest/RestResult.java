package com.system.cron.common.rest;

import java.io.Serializable;

/**
 * 封装 HTTP REST 结果
 * Created by ctianlong on 2018/5/31.
 */
public class RestResult<T> implements Serializable
{
    private static final long serialVersionUID = -2181552353242608931L;

    private int code;

    private String msg;

    private T data;

    public RestResult(int code)
    {
        this(code, null, null);
    }

    public RestResult(int code, String msg)
    {
        this(code, msg, null);
    }

    public RestResult(int code, String msg, T data)
    {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public RestResult(ResultStatus status)
    {
        this(status, null);
    }

    public RestResult(ResultStatus status, T data)
    {
        this(status.code(), status.getMsg(), data);
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }

    @Override
    public String toString()
    {
        return "RestResult{" +
            "code=" + code +
            ", msg='" + msg + '\'' +
            ", data=" + data +
            '}';
    }

    public static <T> RestResult<T> custom(int code)
    {
        return new RestResult<>(code);
    }

    public static <T> RestResult<T> custom(int code, String msg)
    {
        return new RestResult<>(code, msg);
    }

    public static <T> RestResult<T> custom(int code, String msg, T data)
    {
        return new RestResult<>(code, msg, data);
    }

    public static <T> RestResult<T> success()
    {
        return new RestResult<>(ResultStatus.SUCCESS);
    }

    public static <T> RestResult<T> success(String msg)
    {
        return new RestResult<>(ResultStatus.SUCCESS.code(), msg);
    }

    public static <T> RestResult<T> success(ResultStatus resultStatus)
    {
        return new RestResult<>(resultStatus);
    }

    public static <T> RestResult<T> success(ResultStatus resultStatus,
        String msg)
    {
        return new RestResult<>(resultStatus.code(), msg);
    }

    public static <T> RestResult<T> successData(T data)
    {
        return new RestResult<>(ResultStatus.SUCCESS, data);
    }

    public static <T> RestResult<T> successData(String msg, T data)
    {
        return new RestResult<>(ResultStatus.SUCCESS.code(), msg, data);
    }

    public static <T> RestResult<T> successData(ResultStatus resultStatus,
        T data)
    {
        return new RestResult<>(resultStatus, data);
    }

    public static <T> RestResult<T> successData(ResultStatus resultStatus,
        String msg,
        T data)
    {
        return new RestResult<>(resultStatus.code(), msg, data);
    }

    public static <T> RestResult<T> fail()
    {
        return new RestResult<>(ResultStatus.FAIL);
    }

    public static <T> RestResult<T> fail(String msg)
    {
        return new RestResult<>(ResultStatus.FAIL.code(), msg);
    }

    public static <T> RestResult<T> fail(ResultStatus resultStatus)
    {
        return new RestResult<>(resultStatus);
    }

    public static <T> RestResult<T> fail(ResultStatus resultStatus, String msg)
    {
        return new RestResult<>(resultStatus.code(), msg);
    }

    public static <T> RestResult<T> failData(T data)
    {
        return new RestResult<>(ResultStatus.FAIL, data);
    }

    public static <T> RestResult<T> failData(String msg, T data)
    {
        return new RestResult<>(ResultStatus.FAIL.code(), msg, data);
    }

    public static <T> RestResult<T> failData(ResultStatus resultStatus, T data)
    {
        return new RestResult<>(resultStatus, data);
    }

    public static <T> RestResult<T> failData(ResultStatus resultStatus,
        String msg,
        T data)
    {
        return new RestResult<>(resultStatus.code(), msg, data);
    }

    public static <T> RestResult<T> error()
    {
        return new RestResult<>(ResultStatus.ERROR);
    }

    public static <T> RestResult<T> error(String msg)
    {
        return new RestResult<>(ResultStatus.ERROR.code(), msg);
    }

    public static <T> RestResult<T> error(ResultStatus resultStatus)
    {
        return new RestResult<>(resultStatus);
    }

    public static <T> RestResult<T> error(ResultStatus resultStatus, String msg)
    {
        return new RestResult<>(resultStatus.code(), msg);
    }

    public static <T> RestResult<T> errorData(T data)
    {
        return new RestResult<>(ResultStatus.ERROR, data);
    }

    public static <T> RestResult<T> errorData(String msg, T data)
    {
        return new RestResult<>(ResultStatus.ERROR.code(), msg, data);
    }

    public static <T> RestResult<T> errorData(ResultStatus resultStatus, T data)
    {
        return new RestResult<>(resultStatus, data);
    }

    public static <T> RestResult<T> errorData(ResultStatus resultStatus,
        String msg,
        T data)
    {
        return new RestResult<>(resultStatus.code(), msg, data);
    }

}
