package javaiscoffee.groomy.ide.response;

public class Status {
    private ResponseStatus responseStatus;

    public Status(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public Integer getCode() {
        return responseStatus.getCode();
    }
    public String getMessage() {
        return responseStatus.getMessage();
    }
}
