package hexlet.code.model;

import java.time.LocalDateTime;

public final class Url {

    private Long id;

    private String name;

    private LocalDateTime createdAt;

    public Url() {
    }

    public Url(Long id, String name, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long idValue) {
        this.id = idValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String nameValue) {
        this.name = nameValue;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAtValue) {
        this.createdAt = createdAtValue;
    }
}
