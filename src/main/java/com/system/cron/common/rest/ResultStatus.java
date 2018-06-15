package com.system.cron.common.rest;

/**
 * 应用自定义 REST 状态码
 * Created by ctianlong on 2018/5/31.
 */
public enum ResultStatus
{
    // 应用自定义status，每个模块可以自定义状态码，这里暂时只是提供一些比较通用的
    SUCCESS(200, "Success"),
    SUCCESS_ADD(201, "Add Success"),
    SUCCESS_UPDATE(202, "Update Success"),
    SUCCESS_DELETE(203, "Delete Success"),

    FAIL(300, "Fail"),
    FAIL_ADD(301, "Add Fail"),
    FAIL_UPDATE(302, "Update Fail"),
    FAIL_DELETE(303, "Delete Fail"),

    ERROR(400, "Error"),
    INVALID_PARAM(401, "Invalid Param"),
    SYSTEM_ERROR(402, "System Error");

    private final int code;

    private final String msg;

    ResultStatus(int code, String msg)
    {
        this.code = code;
        this.msg = msg;
    }

    public int code()
    {
        return this.code;
    }

    public String getMsg()
    {
        return this.msg;
    }

    public static ResultStatus valueOf(int code)
    {
        ResultStatus[] var1 = values();
        int var2 = var1.length;
        for (int var3 = 0; var3 < var2; ++var3)
        {
            ResultStatus status = var1[var3];
            if (status.code == code)
            {
                return status;
            }
        }
        throw new IllegalArgumentException(
            "No matching constant for [" + code + "]");
    }

    @Override
    public String toString()
    {
        return Integer.toString(this.code);
    }
}