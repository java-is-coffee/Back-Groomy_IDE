package Javaiscoffee.Groomy.IDE.login;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class EmailCheckResultDto {
    private boolean duplicated;
}
