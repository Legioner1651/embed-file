package ru.ruslan.model;

public class CreateFileVKResponse {
    private String status;
    private String message;
    private String htmlContent;
    private String fileName;

    public CreateFileVKResponse() {
    }

    public CreateFileVKResponse(String status, String message, String htmlContent, String fileName) {
        this.status = status;
        this.message = message;
        this.htmlContent = htmlContent;
        this.fileName = fileName;
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}