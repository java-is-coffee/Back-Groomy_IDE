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
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
    private final MemberRepository memberRepository;
    private final JpaProjectRepository projectRepository;
    private final String projectBasePath = "/home/projects/";

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
     * 웹소켓 통신 메세지를 받고 파일 및 폴더 생성하고 나서 응답 메세지를 반환
     * 파일 내용 수정까지 포함
     */
    public FileWebsocketResponseDto websocketSave (FileWebsocketRequestDto.RequestData data, Long memberId, Long projectId) {
        Path fullPath = getFileFullPath(memberId, projectId, data.getPath());
        FileWebsocketResponseDto responseDto = new FileWebsocketResponseDto();
        log.info("웹소켓 파일 및 폴더 저장 = {}",fullPath);
        try {
            //파일 생성
            if(data.getType() == FileType.FILE) {
                //디렉토리가 없으면 생성
                Files.createDirectories(fullPath.getParent());
                //파일에 내용쓰기
                Files.writeString(fullPath, data.getContent(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
            //폴더 생성
            else if (data.getType() == FileType.FOLDER) {
                //디렉토리 생성
                Files.createDirectories(fullPath);
            }
            log.info("파일 생성 성공 = {}",fullPath);

            //응답DTO 생성
            BasicFileAttributes attrs = Files.readAttributes(fullPath, BasicFileAttributes.class);
            responseDto.setItemId(attrs.creationTime().toString() + data.getName());
            BeanUtils.copyProperties(data,responseDto);
            return responseDto;

        } catch (IOException e) {
            log.error("웹소켓 파일 생성 예외 발생 = {}",fullPath);
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
     * 웹소켓 통신 메시지를 받고 파일 및 폴더 이름을 변경하고 메세지 전송
     */
    public FileWebsocketResponseDto websocketRename(FileWebsocketRequestDto.RequestData data, Long memberId, Long projectId) {
        FileWebsocketResponseDto responseDto = new FileWebsocketResponseDto();
        try{
            Path oldFullPath = Paths.get(projectBasePath + memberId + "/" + projectId + "/" + data.getPath());
            Path newFullPath = oldFullPath.resolveSibling(data.getName()); // 같은 부모 디렉토리 내에서 새 이름으로 경로 생성
            Files.move(oldFullPath, newFullPath, StandardCopyOption.REPLACE_EXISTING); // 기존 파일/폴더를 새 경로(이름)로 이동

            //응답DTO 생성
            BasicFileAttributes attrs = Files.readAttributes(newFullPath, BasicFileAttributes.class);
            responseDto.setItemId(attrs.creationTime().toString() + data.getName());
            BeanUtils.copyProperties(data,responseDto);
            return responseDto;
        } catch (IOException e) {
            log.error("웹소켓 파일 수정 예외 발생 = {}",data.getPath());
            throw new BaseException(ResponseStatus.SAVE_FAILED.getMessage());
        }
    }

    /**
     * 프로젝트 폴더 내용 목록 조회
     * 반환 데이터 = 파일 및 폴더 구조를 탐색하고 FileResponseDto 리스트로 반환
     */
    public List<FileResponseDto> getProjectFilesStructure(Long memberId, Long projectId) {
        Project project = projectRepository.getProjectByProjectId(projectId);
        Path rootPath = Paths.get(projectBasePath + project.getMemberId().getMemberId() + "/" + projectId);
        log.info("파일 목록 조회 경로 = {}",rootPath);
        //권한이 있는지 검사
        isParticipated(projectId, memberId);

        List<FileResponseDto> rootList = new ArrayList<>();
        traverseFolder(rootPath, rootList, rootPath);
        return rootList;
    }

    /**
     * dfs 방식으로 폴더 탐색
     * 반환 리스트
     */
    private void traverseFolder(Path directory, List<FileResponseDto> fileList, Path rootPath) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path path : stream) {
                BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                FileResponseDto fileDto = new FileResponseDto();
                fileDto.setId(attrs.creationTime().toString() + path.getFileName());
                fileDto.setName(path.getFileName().toString());
                fileDto.setPath(rootPath.relativize(path).toString());
                fileDto.setLastUpdatedTime(attrs.lastModifiedTime().toString());

                if (attrs.isDirectory()) {
                    fileDto.setType(FileType.FOLDER);
                    fileDto.setChildren(new ArrayList<>());
                    traverseFolder(path, fileDto.getChildren(), rootPath);
                } else {
                    fileDto.setType(FileType.FILE);
                    // 파일의 경우 children을 설정하지 않거나 빈 리스트를 할당
                }

                fileList.add(fileDto);
            }
        }   catch (IOException e) {
            throw new BaseException("파일 목록 조회에 실패했습니다.");
        }
    }


    /**
     * 파일 내용 조회
     * oldPath = 전체 상대 경로 => /home/projects/1/9/ 제외
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
     * 웹소켓 메세지를 전달 받고 파일 및 폴더를 삭제하고 메세지 전달
     * path만 사용
     */
    public FileWebsocketResponseDto websocketDelete(FileWebsocketRequestDto.RequestData data, Long memberId, Long projectId) {
        FileWebsocketResponseDto responseDto = new FileWebsocketResponseDto();
        Path fullPath = getFileFullPath(memberId, projectId, data.getPath());
        try {
            //응답 DTO 내용 초기화
            BasicFileAttributes attrs = Files.readAttributes(fullPath, BasicFileAttributes.class);
            responseDto.setItemId(attrs.creationTime().toString() + data.getName());
            BeanUtils.copyProperties(data,responseDto);
            //파일 및 폴더 삭제
            Files.deleteIfExists(fullPath);

            return responseDto;
        } catch (IOException e) {
            log.error("파일 삭제 예외 발생 = {}",fullPath);
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
        //프로젝트에 참가하고 있지 않으면 예외처리
        if(!projectRepository.isParticipated(projectMemberId)) {
            throw new BaseException(ResponseStatus.FORBIDDEN.getMessage());
        }
    }


    public Path getFileFullPath (Long memberId, Long projectId, String filePath) {
        return Paths.get(projectBasePath + memberId + "/" + projectId + "/" + filePath);
    }

    public String getFileId (String filePath,Long memberId, Long projectId) {
        try {
            Path fileFullPath = getFileFullPath(memberId, projectId, filePath);
            BasicFileAttributes attrs = Files.readAttributes(fileFullPath, BasicFileAttributes.class);
            return attrs.creationTime().toString() + fileFullPath.getFileName();
        } catch (IOException e) {
            throw new BaseException(ResponseStatus.NOT_FOUND.getMessage());
        }
    }
}
