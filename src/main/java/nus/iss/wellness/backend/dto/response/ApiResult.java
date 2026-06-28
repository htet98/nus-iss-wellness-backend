package nus.iss.wellness.backend.dto.response;

//author: Junior

public class ApiResult<T> {
    private boolean success;
    private String message;
    private T data;

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
    public ApiResult() {
        super();
    }
    public ApiResult(boolean success, String message, T data) {
        super();
        this.success = success;
        this.message = message;
        this.data = data;
    }
    @Override
    public String toString() {
        return "ApiResult [success=" + success + ", message=" + message + ", data=" + data + "]";
    }
}
