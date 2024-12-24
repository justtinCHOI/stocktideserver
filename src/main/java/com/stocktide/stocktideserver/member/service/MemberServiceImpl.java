package com.stocktide.stocktideserver.member.service;

import com.stocktide.stocktideserver.cash.entity.Cash;
import com.stocktide.stocktideserver.cash.service.CashService;
import com.stocktide.stocktideserver.member.dto.MemberDTO;
import com.stocktide.stocktideserver.member.dto.MemberModifyDTO;
import com.stocktide.stocktideserver.member.entity.Member;
import com.stocktide.stocktideserver.member.entity.MemberRole;
import com.stocktide.stocktideserver.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
//@Transactional(readOnly = true)
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CashService cashService;
//    private final CustomAuthorityUtils authorityUtils;
//    private final StockOrderRepository stockOrderRepository;

    @Override //accessToken -> 사용자 정보 -> 새로운 사용자 DTO || 기존 사용자 DTO
    public MemberDTO getKakaoMember(String accessToken) {

        String nickname = getNicknameFromKakaoAccessToken(accessToken);

        List<Member> result = memberRepository.findByNickname(nickname);

        //기존의 회원
        if (!result.isEmpty()) {
            return entityToDTO(result.get(0));
        }
        //새로운 회원이라면
        //닉네임: '소셜회원' 패스워드는 임의로 생성 -> 저장
        Member socialMember = makeSocialMember(nickname);
        log.info("socialMember: {}", socialMember);
        memberRepository.save(socialMember);

        return entityToDTO(socialMember);
    }

    // 2차 kakao accessToken -> 사용자 정보 가져오기
    private String getNicknameFromKakaoAccessToken(String accessToken) {

        String kakaoGetUserURL = "https://kapi.kakao.com/v2/user/me";

        if (accessToken == null) {
            throw new RuntimeException("Access Token is null");
        }
        //header 추가
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoGetUserURL).build();

        ResponseEntity<LinkedHashMap> response =
                restTemplate.exchange(
                        uriBuilder.toString(),
                        HttpMethod.GET,
                        entity,
                        LinkedHashMap.class);

        LinkedHashMap<String, LinkedHashMap> bodyMap = response.getBody();

        //response body 에 properties/nickname 또는 kakao_account/profile/nickname 으로 접근할 수 있다.
        LinkedHashMap<String, String> kakaoAccount = bodyMap.get("properties");

        return kakaoAccount.get("nickname");
    }

    //social 로그인시 nickname ( email) 로  db에 저장할 새로운 member 생성
    private Member makeSocialMember(String nickname) {
        Member member = Member.builder()
                .password(passwordEncoder.encode(makeTempPassword()))
                .nickname(nickname)
                .email("nickname@aaa.com")
                .name(nickname)
                .social(true)
                .build();
        member.addRole(MemberRole.USER);

        Cash cash = Cash.builder()
                .accountNumber(cashService.generateUniqueAccountNumber())
                .member(member)
                .build();

        List<Cash> cashList = new ArrayList<>();
        cashList.add(cash);
        member.setCashList(cashList);

        return member;
    }

    //10자리의 password
    private String makeTempPassword() {

        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < 10; i++) {
            buffer.append((char) ((int) (Math.random() * 55) + 65));
        }
        return buffer.toString();
    }

    @Override
    public void modifyMember(MemberModifyDTO memberModifyDTO) {

        Optional<Member> result = memberRepository.findById(memberModifyDTO.getMemberId());

        Member member = result.orElseThrow();

        member.setName(memberModifyDTO.getName());
        member.setEmail(memberModifyDTO.getEmail());
        member.setPassword(passwordEncoder.encode(memberModifyDTO.getPassword()));
        member.setSocial(false);

        memberRepository.save(member);
    }

    @Override
    public MemberDTO entityToDTO(Member member) {
        return new MemberDTO(
                member.getMemberId(),
                member.getEmail(),
                member.getName(),
                member.getNickname(),
                member.getPassword(),
                member.getCashList(),
                member.isSocial(),
                member.getMemberRoleList().stream()
                        .map(Enum::name)
                        .collect(Collectors.toList()),
                member.getMemberStatus().name()

        );
    }

    @Override
    public boolean checkEmail(String email) {
        Optional<Member> byEmail = memberRepository.findByEmail(email);
        return byEmail.isPresent();
    }


}
//    public Member createMember(Member member) {
//
//        verifyExistsEmail(member.getEmail());
//
//        String password = member.getPassword();
//        String confirmPassword = member.getConfirmPassword();
//
//        if (!password.equals(confirmPassword)) {
//            throw new BusinessLogicException(ExceptionCode.INVALID_PASSWORD);
//        }// 암호 재확인 기능
//
////        String encryptedPassword = passwordEncoder.encode(member.getPassword());
////        member.setPassword(encryptedPassword);
////
////        List<String> roles = authorityUtils.createRoles(member.getEmail());
////        member.setRoles(roles);
//
//        member.setMemberStatus(Member.MemberStatus.MEMBER_ACTIVE);
//
//        return memberRepository.save(member);
//    }

//    public Member updateMember(Member member) {
//
//
//        Member findMember = findVerifiedMember(member.getMemberId());
//
//        Optional.ofNullable(member.getName())
//                .ifPresent(name -> findMember.setName(name));
//
//        return memberRepository.save(findMember);
//    }

//    public  Member findMember(long memberId) {
//
//        Member findMember = findVerifiedMember(memberId);
//
//        if (findMember.getMemberId() != memberId) {
//            throw new BusinessLogicException(ExceptionCode.INVALID_FAILED);
//        }
//
//        return findVerifiedMember(findMember.getMemberId());
//    }

//    public void deleteMember(long memberId) {
//
//        memberRepository.deleteById(memberId);
//
//        List<StockOrder> orders = stockOrderRepository.findByMemberMemberId(memberId);
//        stockOrderRepository.deleteAll(orders);
//    }

//    public Member findVerifiedMember(long memberId) {
//
//        Optional<Member> optionalMember =
//                memberRepository.findByMemberId(memberId);
//        Member findMember =
//                optionalMember.orElseThrow(() ->
//                        new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
//        return findMember;
//    }

//    public void verifyExistsEmail(String email) {
//        Optional<Member> member = memberRepository.findByEmail(email);
//        if (member.isPresent())
//            throw new BusinessLogicException(ExceptionCode.EMAIL_DUPLICATION);
//    }

//    public Member findMemberByEmail(String email) {
//        Optional<Member> optionalUser = memberRepository.findByEmail(email);
//
//        return optionalUser.orElse(null);
//    }

//    public int findMemberIdByEmail(String email) {
//        Optional<Member> optionalMember = memberRepository.findByEmail(email);
//
//        if (optionalMember.isPresent()) {
//            Member member = optionalMember.get();
//            return (int) member.getMemberId();
//        } else {
//
//            throw new  BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
//        }
//    }

//    public void deleteStockOrdersByMemberId(Long memberId) {
//        stockOrderRepository.deleteAllByMemberId(memberId);
//    }