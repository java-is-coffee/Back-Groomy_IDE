package javaiscoffee.groomy.ide.member;

import javaiscoffee.groomy.ide.response.MyResponse;
import javaiscoffee.groomy.ide.response.ResponseStatus;
import javaiscoffee.groomy.ide.response.Status;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final JpaMemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 현재 사용 용도 : 자기 이메일을 통해 자기 정보를 반환
     * 요구 데이터 : 정보를 조회할 이메일
     * 반환 데이터 : 이메일에 해당하는 MemberInformationDto
     */
    public MyResponse<MemberInformationDto> getMemberInformation(String email) {
        Optional<Member> memberOptional = memberRepository.findByEmail(email);
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            log.info("정보 조회하려고 찾은 멤버 = {}", member);

            // MemberInformationDto의 내부 Data 객체를 생성하고 정보 복사
            MemberInformationDto.Data memberData = new MemberInformationDto.Data();
            BeanUtils.copyProperties(member, memberData);

            // MemberInformationDto 객체 생성 및 내부 Data 객체 설정
            MemberInformationDto memberInformationDto = new MemberInformationDto();
            memberInformationDto.setData(memberData);
            return new MyResponse<>(new Status(ResponseStatus.SUCCESS), memberInformationDto);
        } else {
            log.error("해당 이메일을 가진 멤버가 없습니다: {}", email);
            return new MyResponse<>(new Status(ResponseStatus.NOT_FOUND));
        }
    }

    /**
     * 사용 용도 : 마이페이지에서 정보 수정
     * 요구 데이터 : MemberInformationDto
     * 반환 데이터 : 받은 정보로 수정된 MemberInformationDto
     */
    @Transactional
    public MyResponse<MemberInformationDto> updateMemberInformation(String email, MemberInformationDto memberInformationDto) {
        Member member = memberRepository.findByEmail(email).get();

        //멤버 조회 실패했을 경우 (포스트맨으로 다르게 보내는 경우 등)
        if(member == null || member.getMemberId() != memberInformationDto.getData().getMemberId()
        || !member.getEmail().equals(memberInformationDto.getData().getEmail())) {
            return new MyResponse<>(new Status(ResponseStatus.NOT_FOUND));
        }

        //멤버 정보 수정
        member.setName(memberInformationDto.getData().getName());
        member.setNickname(memberInformationDto.getData().getNickname());

        //새로운 멤버로 response 생성
        MemberInformationDto updatedDto = new MemberInformationDto();
        MemberInformationDto.Data updatedData = new MemberInformationDto.Data();
        BeanUtils.copyProperties(member, updatedData); // 멤버 정보를 Data 객체에 복사
        updatedDto.setData(updatedData); // Data 객체를 updatedDto에 설정

        log.info("수정된 멤버 정보 = {}",updatedDto);

        return new MyResponse<>(new Status(ResponseStatus.SUCCESS), updatedDto);
    }

    /**
     * 용도 : 마이페이지에서 비밀번호 재설정
     * 요구 데이터 : 패스워드와 토큰에 있던 이메일
     * 반환 데이터 : 성공했다는 status만 가지고 있는 MyResponse
     */
    @Transactional
    public MyResponse<Null> resetPassword(String email, String password) {
        Member member = memberRepository.findByEmail(email).get();

        //멤버 조회 실패했을 경우 (포스트맨으로 다르게 보내는 경우 등)
        if(member == null) {
            return new MyResponse<>(new Status(ResponseStatus.NOT_FOUND));
        }

        log.info("입력받은 비밀번호 = {}",password);

        member.setPassword(password);
        member.hashPassword(passwordEncoder);

        log.info("변경된 비밀번호를 담은 멤버 = {}",member);

        return new MyResponse<>(new Status(ResponseStatus.SUCCESS));
    }
}