package javaiscoffee.groomy.ide.chat;

import javaiscoffee.groomy.ide.member.JpaMemberRepository;
import javaiscoffee.groomy.ide.member.Member;
import javaiscoffee.groomy.ide.project.JpaProjectRepository;
import javaiscoffee.groomy.ide.project.Project;
import javaiscoffee.groomy.ide.project.ProjectMemberId;
import javaiscoffee.groomy.ide.security.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatService {
    private final JpaChatRepository chatRepository;
    private final JpaProjectRepository projectRepository;
    private final JpaMemberRepository memberRepository;

    public List<ChatMessageDto> getChatLogs(Long memberId, Long projectId, int paging, int pagingNumber) {
        Member findMember = memberRepository.findByMemberId(memberId).orElseThrow(() -> new BaseException("멤버를 찾을 수 없습니다."));
        Project findProject = projectRepository.getProjectByProjectId(projectId);
        if (findProject == null) {
            throw new BaseException("프로젝트를 찾을 수 없습니다.");
        }
        ProjectMemberId projectMemberId = new ProjectMemberId(findProject.getProjectId(), memberId);
        if(!projectRepository.isParticipated(projectMemberId)) {
            throw new BaseException("프로젝트에 참여하고 있지 않습니다.");
        }
        List<ProjectChat> projectChatLogs = chatRepository.getProjectChatLogs(projectId, paging, pagingNumber);
        return convertToChatLogResponseDtoList(projectChatLogs);
    }

    // ProjectChat 리스트를 ChatLogResponseDto List로 변환
    public List<ChatMessageDto> convertToChatLogResponseDtoList(List<ProjectChat> projectChats) {
        return projectChats.stream().map(chat -> {
            ChatMessageDto dto = new ChatMessageDto();
            Member member = chat.getMember();

            // Member 정보가 있는 경우
            if (member != null) {
                dto.setName(member.getName());
                dto.setEmail(member.getEmail());
            }
            // Member 정보가 없는 경우 ex) 회원 탈퇴
            else {
                dto.setName("탈퇴한 사용자");
                dto.setEmail("");
            }
            dto.setMessage(chat.getMessage()); // 메시지 내용 설정
            dto.setCreatedTime(chat.getCreatedTime()); // 메시지 생성 시간 설정

            return dto; // 변환된 dto 반환
        }).collect(Collectors.toList()); // 결과를 List로 수집
    }

    /**
     * 프로젝트 채팅 검증하고 로그 저장하는 메서드
     * 요구 데이터 : 토큰 값에서 뽑은 memberId, projectId, ChatMessageRequestDto
     * 반환 데이터 : ChatMessageDto
     */
    @Transactional
    public ChatMessageDto sendProjectChat(Long memberId, Long projectId, ChatMessageRequestDto requestDto) {
        //멤버Id가 잘못 입력된 경우
        if(memberId != requestDto.getData().getMemberId()) {
            throw new BaseException("입력 값이 잘못되었습니다.");
        }
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new BaseException("멤버를 찾을 수 없습니다."));

        //프로젝트가 존재하지 않는 경우
        Project project = projectRepository.getProjectByProjectId(projectId);
        if (project == null) {
            throw new BaseException("입력 값이 잘못되었습니다.");
        }

        ProjectMemberId isProjectMember = new ProjectMemberId(projectId, memberId);
        //요청한 멤버가 프로젝트에 참여하지 않은 경우
        if(!projectRepository.isParticipated(isProjectMember)) {
            throw new BaseException("참여하고 있지 않은 프로젝트입니다.");
        }

        //채팅 로그 저장
        ProjectChat projectChat = new ProjectChat();
        projectChat.setMember(member);
        projectChat.setProject(project);
        projectChat.setMessage(requestDto.getData().getMessage());
        ProjectChat savedChat = chatRepository.writeProjectChat(projectChat);

        //응답값 생성
        ChatMessageDto responseDto = new ChatMessageDto();
        responseDto.setName(savedChat.getMember().getName());
        responseDto.setEmail(savedChat.getMember().getEmail());
        BeanUtils.copyProperties(savedChat, responseDto);
        return responseDto;
    }


    /**
     * 채팅 추가 테스트
     */
    @Transactional
    public ChatMessageDto writeChatTest(Long projectId, Long memberId) {
        Project findProject = projectRepository.getProjectByProjectId(projectId);
        Member findMember = memberRepository.findByMemberId(memberId).get();
        ProjectChat projectChat = new ProjectChat();
        projectChat.setProject(findProject);
        projectChat.setMember(findMember);
        projectChat.setMessage("테스트");
        ProjectChat savedChat = chatRepository.writeProjectChat(projectChat);
        ChatMessageDto responseDto = new ChatMessageDto();
        responseDto.setName(savedChat.getMember().getName());
        responseDto.setEmail(savedChat.getMember().getEmail());
        BeanUtils.copyProperties(savedChat, responseDto);
        return responseDto;
    }

}
