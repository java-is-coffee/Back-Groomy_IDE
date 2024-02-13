package javaiscoffee.groomy.ide.codeeditor;

import lombok.Data;

@Data
public class CodeEditorResponseDto {
    private Long memberId;
    private String action;
    private Range range;
    private String text;
    private int rangeOffset;
    private int rangeLength;
}
