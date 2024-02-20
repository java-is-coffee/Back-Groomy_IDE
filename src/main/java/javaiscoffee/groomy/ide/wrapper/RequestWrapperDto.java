package javaiscoffee.groomy.ide.wrapper;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class RequestWrapperDto<T> {
    @Valid
    private T data;
}
