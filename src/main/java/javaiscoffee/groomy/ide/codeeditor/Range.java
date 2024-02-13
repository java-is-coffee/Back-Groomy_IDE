package javaiscoffee.groomy.ide.codeeditor;

import lombok.Data;

@Data
public class Range {
    private int startLineNumber;
    private int startColumn;
    private int endLineNumber;
    private int endColumn;
}
