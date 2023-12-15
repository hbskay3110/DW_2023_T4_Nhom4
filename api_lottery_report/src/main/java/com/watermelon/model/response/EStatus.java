package com.watermelon.model.response;

public enum EStatus {
    SUCCESS(200, "success", "The operation was successful."),
    CREATED(201, "created", "The resource was successfully created."),
    ACCEPTED(202, "accepted", "The request has been accepted for processing."),
    NO_CONTENT(204, "no content", "The server successfully processed the request but there is no content to return."),
    
    BAD_REQUEST(400, "bad request", "The request was invalid or cannot be served."),
    UNAUTHORIZED(401, "unauthorized", "The request requires user authentication."),
    FORBIDDEN(403, "forbidden", "The server understood the request, but it refuses to authorize it."),
    NOT_FOUND(404, "not found", "The requested resource could not be found."),
    METHOD_NOT_ALLOWED(405, "method not allowed", "The request method is not supported for the requested resource."),
    
    INTERNAL_SERVER_ERROR(500, "internal server error", "The server encountered an unexpected condition that prevented it from fulfilling the request."),
    NOT_IMPLEMENTED(501, "not implemented", "The server does not support the functionality required to fulfill the request."),
    SERVICE_UNAVAILABLE(503, "service unavailable", "The server is currently unable to handle the request due to temporary overloading or maintenance of the server.");

    private final int status;
    private final String title;
    private final String description;

    EStatus(int status, String title, String description) {
        this.status = status;
        this.title = title;
        this.description = description;
    }

    public int getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
