package com.stocktide.stocktideserver.member.dto;

import com.stocktide.stocktideserver.cash.entity.Cash;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
public class MemberDTO extends User {
    private long memberId;

    private String email;

    private String name;

    private String nickname;

    private String password;

    private List<Cash> cashList = new ArrayList<>();

    private boolean social;

    private List<String> roleNames = new ArrayList<>();

    private String status;

    public MemberDTO(long memberId, String email, String name, String nickname,
                     String password, List<Cash> cashList, boolean social, List<String> roleNames,
                     String status) {
        super(
                email, // username
                password, // password
                roleNames.stream().map(str -> new SimpleGrantedAuthority("ROLE_"+str)).collect(Collectors.toList()));
        this.memberId = memberId;
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.cashList = cashList;
        this.social = social;
        this.roleNames = roleNames;
        this.status = status;
    }

    public Map<String, Object> getClaims() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("memberId", memberId);
        dataMap.put("email", email);
        dataMap.put("name", name);
        dataMap.put("nickname", nickname);
        dataMap.put("password", password);
        dataMap.put("cashList", cashList);
        dataMap.put("social", social);
        dataMap.put("roleNames", roleNames);
        dataMap.put("status", status);
        return dataMap;
    }

}
