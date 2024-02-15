package javaiscoffee.groomy.ide.codeeditor;

import lombok.Data;

@Data
public class CodeEditorDto {
    private CodeEditorAction action;
    private String fileId;
    private Long memberId;
    private CodeEdit codeEdit;
    private CursorMove cursorMove;

    @Data
    public static class CodeEdit {
        private Range range;
        private String text;
        private int rangeOffset;
        private int rangeLength;
    }
    @Data
    public static class CursorMove {
        private String memberName;
        private Position position;
        @Data
        public static class Position {
            private int lineNumber;
            private int columnNumber;
        }
    }
}
