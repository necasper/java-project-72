package hexlet.code.model;

import java.time.LocalDateTime;

public final class UrlCheck {

    private Long id;

    private int statusCode;

    private String title;

    private String h1;

    private String description;

    private Long urlId;

    private LocalDateTime createdAt;

    public UrlCheck() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long idValue) {
        this.id = idValue;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCodeValue) {
        this.statusCode = statusCodeValue;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String titleValue) {
        this.title = titleValue;
    }

    public String getH1() {
        return h1;
    }

    public void setH1(String h1Value) {
        this.h1 = h1Value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String descriptionValue) {
        this.description = descriptionValue;
    }

    public Long getUrlId() {
        return urlId;
    }

    public void setUrlId(Long urlIdValue) {
        this.urlId = urlIdValue;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAtValue) {
        this.createdAt = createdAtValue;
    }
}
