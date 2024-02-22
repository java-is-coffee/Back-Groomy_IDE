package javaiscoffee.groomy.ide.codeeditor;

import jakarta.annotation.Nullable;
import lombok.Data;

/**
 * 코드 변경 웹소켓에서 사용하는 메세지 전달 DTO
 */
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
