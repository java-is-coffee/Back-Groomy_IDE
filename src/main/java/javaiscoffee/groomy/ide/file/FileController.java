package javaiscoffee.groomy.ide.file;

import javaiscoffee.groomy.ide.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 웹소켓 만들기 전에 테스트용으로 사용한 API들
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file")
public class FileController {
    private final FileService fileService;
    @PostMapping("/create")
    public ResponseEntity<?> createFile(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody FileRequestDto requestDto) {
        Long memberId = userDetails.getMemberId();
        fileService.createAndSave(requestDto, memberId);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/rename")
    public ResponseEntity<?> renameFile(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody FileRenameRequestDto requestDto) {
        Long memberId = userDetails.getMemberId();
        fileService.renameFileOrFolder(memberId, requestDto);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/list")
    public ResponseEntity<?> getFileList(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody FileRenameRequestDto requestDto) {
        Long memberId = userDetails.getMemberId();
        return ResponseEntity.ok(fileService.getProjectFilesStructure(memberId, requestDto.getData().getProjectId()));
    }

    @PostMapping("/content")
    public ResponseEntity<?> getFileContent(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody FileRenameRequestDto requestDto) {
        Long memberId = userDetails.getMemberId();
        return ResponseEntity.ok(fileService.readFileContent(memberId, requestDto));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteFile(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody FileRenameRequestDto requestDto) {
        Long memberId = userDetails.getMemberId();
        fileService.deleteFileOrFolder(memberId, requestDto);
        return ResponseEntity.ok(null);
    }
}
