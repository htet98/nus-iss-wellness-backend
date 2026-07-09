package nus.iss.wellness.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;
/**
 *  Author: Htet Nandar
 */
public class ChatRequest {

    @NotBlank(message = "Message is required")
    private String message;

    private Map<String, String> userContext;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Map<String, String> getUserContext() { return userContext; }
    public void setUserContext(Map<String, String> userContext) { this.userContext = userContext; }
}
