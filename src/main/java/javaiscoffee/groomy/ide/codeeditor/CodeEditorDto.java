package javaiscoffee.groomy.ide.codeeditor;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class CodeEditorDto {
    private CodeEditorAction action;
    private String fileId;
    private Long memberId;
    private CodeEdit codeEdit;

    @Data
    public static class CodeEdit {
        private String memberName;
        private Range range;
        private String text;
    }
}
