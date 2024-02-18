package javaiscoffee.groomy.ide.file;

import javaiscoffee.groomy.ide.member.Member;
import javaiscoffee.groomy.ide.member.MemberRepository;
import javaiscoffee.groomy.ide.project.JpaProjectRepository;
import javaiscoffee.groomy.ide.project.Project;
import javaiscoffee.groomy.ide.project.ProjectMemberId;
import javaiscoffee.groomy.ide.response.ResponseStatus;
import javaiscoffee.groomy.ide.security.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
    private final MemberRepository memberRepository;
    private final JpaProjectRepository projectRepository;
    private final String projectBasePath = "C:\\Users\\tkrhk\\Desktop\\api테스트\\";
    //private final String projectBasePath = "/home/projects/";

    /**
     * 파일 및 폴더 생성
     * 파일 내용 수정까지 포함
     */
    public void createAndSave (FileRequestDto requestDto, Long memberId) {
        FileRequestDto.RequestData data = requestDto.getData();
        //멤버가 존재하는지, 프로젝트가 존재하는지, 멤버가 프로젝트에 참가하는지 검증
        isParticipated(data.getProjectId(), memberId);

        Path fullPath = getFileFullPath(memberId, data.getProjectId(), data.getFilePath());
        try {
            //파일 생성 API
            if(data.getType() == FileType.FILE) {
                //디렉토리가 없으면 생성
                Files.createDirectories(fullPath.getParent());
                //파일에 내용쓰기
                Files.writeString(fullPath, data.getContent(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
            //폴더 생성 API
            else if (data.getType() == FileType.FOLDER) {
                //디렉토리 생성
                Files.createDirectories(fullPath);
            }
            log.info("파일 생성 성공 = {}",fullPath);
        } catch (IOException e) {
            log.error("파일 생성 예외 발생 = {}",data.getFileName());
            throw new BaseException(ResponseStatus.SAVE_FAILED.getMessage());
        }
    }

    /**
     * 파일 및 폴더 이름 변경
     */
    public void renameFileOrFolder(Long memberId, FileRenameRequestDto requestDto) {
        FileRenameRequestDto.RequestData data = requestDto.getData();
        //권한이 있는지 검사
        isParticipated(data.getProjectId(), memberId);

        try{
            Path oldFullPath = Paths.get(projectBasePath + memberId + "/" + data.getProjectId() + "/" + data.getOldPath());
            Path newFullPath = oldFullPath.resolveSibling(data.getNewName()); // 같은 부모 디렉토리 내에서 새 이름으로 경로 생성
            Files.move(oldFullPath, newFullPath, StandardCopyOption.REPLACE_EXISTING); // 기존 파일/폴더를 새 경로(이름)로 이동
        } catch (IOException e) {
            log.error("파일 수정 예외 발생 = {}",data.getOldPath());
            throw new BaseException(ResponseStatus.SAVE_FAILED.getMessage());
        }
    }
    /**
     * 프로젝트 폴더 내용 목록 조회
     * 반환 데이터 = 전체 파일들의 절대 경로를 String으로 반환
     */
    public List<String> getAllFiles(Long memberId, FileRenameRequestDto requestDto) {
        FileRenameRequestDto.RequestData data = requestDto.getData();
        //권한이 있는지 검사
        isParticipated(data.getProjectId(), memberId);

        Path projectPath = Paths.get(projectBasePath + memberId + "/" + data.getProjectId());
        List<String> fileList = new ArrayList<>();
        List<String> directoryList = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(projectPath)) {
            paths.forEach(path -> {
                if (Files.isRegularFile(path)) {
                    // 파일인 경우
                    fileList.add(projectPath.relativize(path).toString());
                } else if (Files.isDirectory(path) && !path.equals(projectPath)) {
                    // 디렉토리인 경우, 최상위 디렉토리는 제외
                    directoryList.add(projectPath.relativize(path).toString());
                }
            });
        } catch (IOException e) {
            log.error("프로젝트 파일 목록 조회 예외 발생 = {}", projectPath);
            throw new BaseException(ResponseStatus.READ_FAILED.getMessage());
        }

        // 파일과 디렉토리 리스트를 합쳐서 반환
        directoryList.addAll(fileList);
        return directoryList;
    }

    /**
     * 파일 내용 조회
     * oldPath = 전체 경로
     * newName = null 사용 안함
     * 반환 데이터 = 전체 코드를 String으로 반환
     */
    public String readFileContent(Long memberId, FileRenameRequestDto requestDto) {
        FileRenameRequestDto.RequestData data = requestDto.getData();
        //권한이 있는지 검사
        isParticipated(data.getProjectId(), memberId);
        try {
            Path fullPath = getFileFullPath(memberId, data.getProjectId(), data.getOldPath());
            String content = Files.readString(fullPath);
            return content;
        } catch (IOException e) {
            log.error("파일 읽기 예외 발생 = {}",data.getOldPath());
            throw new BaseException(ResponseStatus.NOT_FOUND.getMessage());
        }
    }

    /**
     * 파일 및 폴더 제거
     * oldPath = 전체 경로
     * newName = null 사용 안함
     */
    public void deleteFileOrFolder(Long memberId, FileRenameRequestDto requestDto) {
        FileRenameRequestDto.RequestData data = requestDto.getData();
        //권한이 있는지 검사
        isParticipated(data.getProjectId(), memberId);
        Path fullPath = getFileFullPath(memberId, data.getProjectId(), data.getOldPath());
        try {
            Files.deleteIfExists(fullPath);
        } catch (IOException e) {
            log.error("파일 삭제 예외 발생 = {}",data.getOldPath());
            throw new BaseException(ResponseStatus.DELETE_FAILED.getMessage());
        }
    }

    /**
     * 멤버가 프로젝트에 참가하고 있는지 검사
     */
    private void isParticipated(Long projectId, Long memberId) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new BaseException(ResponseStatus.NOT_FOUND.getMessage()));
        Project project = projectRepository.getProjectByProjectId(projectId);
        //멤버가 없으면, 프로젝트가 없으면, 프로젝트가 삭제되면 예외처리
        if(member == null || project == null || project.getDeleted()) {
            throw new BaseException(ResponseStatus.NOT_FOUND.getMessage());
        }
        ProjectMemberId projectMemberId = new ProjectMemberId(project.getProjectId(), member.getMemberId());
        if(!projectRepository.isParticipated(projectMemberId)) {
            throw new BaseException(ResponseStatus.FORBIDDEN.getMessage());
        }
    }


    public Path getFileFullPath (Long memberId, Long projectId, String filePath) {
        return Paths.get(projectBasePath + memberId + "\\" + projectId + "\\" + filePath);
//        return Paths.get(projectBasePath + memberId + "/" + projectId + "/" + filePath + "/" + fileName);
    }
}
