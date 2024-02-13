package javaiscoffee.groomy.ide.board;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ScrapId implements Serializable {
    private Long memberId;
    private Long boardId;
}
