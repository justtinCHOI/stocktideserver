package com.stocktide.stocktideserver.member.controller;

import com.stocktide.stocktideserver.member.dto.MemberDTO;
import com.stocktide.stocktideserver.member.dto.MemberModifyDTO;
import com.stocktide.stocktideserver.member.service.MemberService;
import com.stocktide.stocktideserver.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Log4j2
@RequiredArgsConstructor
public class SocialController {

    private final MemberService memberService;

    @GetMapping("/api/member/kakao")
    public Map<String, Object> getMemberFromKakao(String accessToken) {

        // 1. api 요청 accessToken -> nickname -> 새로운 MemberDTO
        // 2. 새로운 MemberDTO -> claims -> accessToken, refreshToken -> claims

        //1.
        MemberDTO memberDTO = memberService.getKakaoMember(accessToken);

        //2.
        Map<String, Object> claims = memberDTO.getClaims();

        String jwtAccessToken = JWTUtil.generateToken(claims, 10);
        String jwtRefreshToken = JWTUtil.generateToken(claims, 60 * 24);

        //spring security 는 JWT 를 gson 으로 변환해서 response 에 넣어준다.
        claims.put("accessToken", jwtAccessToken);
        claims.put("refreshToken", jwtRefreshToken);


        return claims;
    }

    @PutMapping("/api/member/modify")
    public Map<String, String> modify(@RequestBody MemberModifyDTO memberModifyDTO) {

        memberService.modifyMember(memberModifyDTO);

        return Map.of("result", "modified");
    }

    @GetMapping("/api/member/checkEmail")
    public boolean checkEmail(@RequestParam String email) {
        return  memberService.checkEmail(email);
    }

}
