package com.stocktide.stocktideserver.member.repository;

import com.stocktide.stocktideserver.member.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
    Optional<Member> findByMemberId(Long memberId);

    @EntityGraph(attributePaths = {"memberRoleList"})
    @Query("select m from Member m where m.email = :email")
    Member getWithRoles(@Param("email") String email);

    @EntityGraph(attributePaths = {"memberRoleList"})
    @Query("select m from Member m where m.nickname = :nickname")
    List<Member> findByNickname(@Param("nickname") String nickname);

}
