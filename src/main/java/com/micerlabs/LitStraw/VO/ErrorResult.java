package com.micerlabs.LitStraw.VO;

public class ErrorResult
{
    private int status; // 状态码
    private String error;  // 错误消息（简述）
    private String message; // 消息日志（详细）

    public ErrorResult(int status, String error)
    {
        this.status = status;
        this.error = error;
    }

    public ErrorResult(int status, String error, String message)
    {
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}

