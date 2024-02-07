package javaiscoffee.groomy.ide.chat;

import javaiscoffee.groomy.ide.member.JpaMemberRepository;
import javaiscoffee.groomy.ide.member.Member;
import javaiscoffee.groomy.ide.project.JpaProjectRepository;
import javaiscoffee.groomy.ide.project.Project;
import javaiscoffee.groomy.ide.project.ProjectMemberId;
import javaiscoffee.groomy.ide.security.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final JpaChatRepository chatRepository;
    private final JpaProjectRepository projectRepository;
    private final JpaMemberRepository memberRepository;

    public List<ChatLogResponseDto> getChatLogs(Long memberId, Long projectId, int paging, int pagingNumber) {
        Member findMember = memberRepository.findByMemberId(memberId).orElseThrow(() -> new MemberNotFoundException("멤버를 찾을 수 없습니다."));
        Project findProject = projectRepository.getProjectByProjectId(projectId);
        if (findProject == null) {
            throw new MemberNotFoundException("프로젝트를 찾을 수 없습니다.");
        }
        ProjectMemberId projectMemberId = new ProjectMemberId(findProject.getProjectId(), memberId);
        if(!projectRepository.isParticipated(projectMemberId)) {
            throw new MemberNotFoundException("프로젝트에 참여하고 있지 않습니다.");
        }
        List<ProjectChat> projectChatLogs = chatRepository.getProjectChatLogs(projectId, paging, pagingNumber);
        return convertToChatLogResponseDtoList(projectChatLogs);
    }

    // ProjectChat 리스트를 ChatLogResponseDto List로 변환
    public List<ChatLogResponseDto> convertToChatLogResponseDtoList(List<ProjectChat> projectChats) {
        return projectChats.stream().map(chat -> {
            ChatLogResponseDto dto = new ChatLogResponseDto();
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

    public ChatLogResponseDto writeChat(Long projectId, Long memberId) {
        Project findProject = projectRepository.getProjectByProjectId(projectId);
        Member findMember = memberRepository.findByMemberId(memberId).get();
        ProjectChat projectChat = new ProjectChat();
        projectChat.setProject(findProject);
        projectChat.setMember(findMember);
        projectChat.setMessage("테스트");
        ProjectChat savedChat = chatRepository.writeProjectChat(projectChat);
        ChatLogResponseDto responseDto = new ChatLogResponseDto();
        responseDto.setName(savedChat.getMember().getName());
        responseDto.setEmail(savedChat.getMember().getEmail());
        BeanUtils.copyProperties(savedChat, responseDto);
        return responseDto;
    }

}
