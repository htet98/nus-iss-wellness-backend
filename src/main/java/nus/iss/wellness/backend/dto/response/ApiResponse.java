package nus.iss.wellness.backend.dto.response;

//author: Junior

public class ApiResponse {

    private boolean success;

    private String message;

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

    public ApiResponse() {
        super();
    }

    public ApiResponse(boolean success, String message) {
        super();
        this.success = success;
        this.message = message;
    }
}
