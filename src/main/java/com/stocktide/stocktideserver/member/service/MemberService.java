package com.stocktide.stocktideserver.member.service;

import com.stocktide.stocktideserver.member.dto.MemberDTO;
import com.stocktide.stocktideserver.member.dto.MemberModifyDTO;
import com.stocktide.stocktideserver.member.entity.Member;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface MemberService {

    MemberDTO getKakaoMember(String accessToken);

    void modifyMember(MemberModifyDTO memberModifyDTO);

    MemberDTO entityToDTO(Member member);

    boolean checkEmail(String email);

}
