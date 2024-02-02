package Javaiscoffee.Groomy.IDE.response;

import lombok.Getter;
import lombok.Setter;

public class Status {
    private ResponseStatus responseStatus;

    public Status(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getCode() {
        return responseStatus.getCode();
    }
    public String getMessage() {
        return responseStatus.getMessage();
    }
}
