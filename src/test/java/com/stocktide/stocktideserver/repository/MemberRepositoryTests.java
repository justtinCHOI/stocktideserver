package com.stocktide.stocktideserver.repository;

import com.stocktide.stocktideserver.cash.entity.Cash;
import com.stocktide.stocktideserver.member.entity.Member;
import com.stocktide.stocktideserver.member.entity.MemberRole;
import com.stocktide.stocktideserver.member.repository.MemberRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

//@Disabled
@SpringBootTest
@Log4j2
@ActiveProfiles("test")
public class MemberRepositoryTests {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // If you finished with updating, remove comment on @Transactional
    @Test
    @Transactional
    public void testInsertMemberWithCash(){

        for (int i = 0; i < 10 ; i++) {

            Member member = Member.builder()
                    .email("newuser"+i+"@aaa.com")
                    .name("NewUser"+i)
                    .nickname("NEW_USER"+i)
                    .password(passwordEncoder.encode("1234"))
                    .build();

            member.addRole(MemberRole.USER);

            if(i >= 5){ // 권한 2개
                member.addRole(MemberRole.MANAGER);
            }

            if(i >= 8){ // 권한 3개
                member.addRole(MemberRole.ADMIN);
            }

            // Create Cash entity and set relationship
            Cash cash = new Cash();
            cash.setMoney(1000 * (i + 1));
            cash.setDollar(1000 * (i + 1));
            cash.setDollar(1000 * (i + 1));
            cash.setMember(member);

            memberRepository.save(member);
        }
    }

    @Test
    public void testRead() {

        String email = "newuser9@aaa.com";

        Member member = memberRepository.getWithRoles(email);

        log.info("-----------------");
        log.info(member);
//        log.info(member.getMemberRoleList());
//        log.info(member.getCash());

    }

}