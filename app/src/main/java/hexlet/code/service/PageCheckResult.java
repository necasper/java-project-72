package hexlet.code.service;

public final class PageCheckResult {

    private final boolean success;

    private final int statusCode;

    private final String h1;

    private final String title;

    private final String description;

    private PageCheckResult(boolean successValue, int statusCodeValue, String h1Value,
                            String titleValue, String descriptionValue) {
        this.success = successValue;
        this.statusCode = statusCodeValue;
        this.h1 = h1Value;
        this.title = titleValue;
        this.description = descriptionValue;
    }

    public static PageCheckResult success(int statusCodeValue, String h1Value,
                                          String titleValue, String descriptionValue) {
        return new PageCheckResult(true, statusCodeValue, h1Value, titleValue, descriptionValue);
    }

    public static PageCheckResult failure() {
        return new PageCheckResult(false, 0, "", "", "");
    }

    public boolean isSuccess() {
        return success;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getH1() {
        return h1;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
