package com.stocktide.stocktideserver.member.controller;

import com.stocktide.stocktideserver.util.CustomJWTException;
import com.stocktide.stocktideserver.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
public class APIRefreshController {

    //accessToken 만료 : accessToken, refreshToken ->  새로운 accessToken
    //refreshToken 만료 : refreshToken -> 새로운 refreshToken

    @RequestMapping("/api/member/refresh")
    public Map<String, Object> refresh(@RequestHeader("Authorization") String authHeader, String refreshToken) {
        //해더에서 Authorization 값인 accessToken 을 얻고, refreshToken 은 매개변수로 받는다.
        log.info("refresh refreshToken: " + refreshToken);
        log.info("refresh authHeader: " + authHeader);

        if (refreshToken == null) {
            throw new CustomJWTException("NULL_REFRASH");
        }

        if (authHeader == null || authHeader.length() < 7) {
            throw new CustomJWTException("INVALID_STRING");
        }

        String accessToken = authHeader.substring(7);

        //Access 토큰이 만료되지 않았다면 그대로 반환
        if (!checkExpiredToken(accessToken)) {
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        }

        Map<String, Object> claims = JWTUtil.validateToken(refreshToken);

        //refreshToken 의 claims 으로써 accessToken 생성
        String newAccessToken = JWTUtil.generateToken(claims, 10);

        //refreshToken 만료에 가까워지면 새로운 refreshToken 생성
        String newRefreshToken = checkTime((Integer) claims.get("exp")) ? JWTUtil.generateToken(claims, 60 * 24) : refreshToken;

        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
    }

    //시간이 1시간 미만으로 남았다면
    private boolean checkTime(Integer exp) {

        //JWT exp를 날짜로 변환
        java.util.Date expDate = new java.util.Date((long) exp * (1000));

        //현재 시간과의 차이 계산 - 밀리세컨즈
        long gap = expDate.getTime() - System.currentTimeMillis();

        //분단위 계산
        long leftMin = gap / (1000 * 60);

        //1시간도 안남았는지..
        return leftMin < 60;
    }

    private boolean checkExpiredToken(String token) {

        try {
            JWTUtil.validateToken(token);
            //오류가 안뜨면 false, Expired 오류메세지의 오류 -> true
        } catch (CustomJWTException ex) {
            if (ex.getMessage().equals("Expired")) {
                return true;
            }
        }
        return false;
    }

}
