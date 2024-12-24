package com.stocktide.stocktideserver.cash.service;

import com.stocktide.stocktideserver.cash.entity.Cash;
import com.stocktide.stocktideserver.cash.repository.CashRepository;
import com.stocktide.stocktideserver.exception.BusinessLogicException;
import com.stocktide.stocktideserver.exception.ExceptionCode;
import com.stocktide.stocktideserver.member.entity.Member;
import com.stocktide.stocktideserver.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class CashService {

    private final CashRepository cashRepository;
    private final MemberRepository memberRepository;

    public Cash createCash(long memberId) {

        Member member = memberRepository.findById(memberId).get();

        if (validateCash(member)) {
            throw new BusinessLogicException(ExceptionCode.CASH_DUPLICATION);
        }

        Cash cash = Cash.builder()
                .accountNumber(generateUniqueAccountNumber())
                .member(member).build();
        Cash createdCash = cashRepository.save(cash);

        List<Cash> cashList = member.getCashList();
        cashList.add(createdCash);
        member.setCashList(cashList);

        return createdCash;
    }

    private boolean validateCash(Member member) {

        List<Cash> existingCashList = cashRepository.findByMember(member);

        return existingCashList.size() > 9;
    }

    public String generateUniqueAccountNumber() {
        Random random = new Random();
        String accountNumber;
        do {
            accountNumber = String.format("%05d-%02d-%03d", random.nextInt(100000), random.nextInt(100), random.nextInt(1000));
        } while (accountNumberExists(accountNumber));
        return accountNumber;
    }

    private boolean accountNumberExists(String accountNumber) {
        return cashRepository.existsByAccountNumber(accountNumber);
    }

    public void remove(Long cashId) {
        cashRepository.deleteById(cashId);
    }


    public Cash updateCash(Long cashId, long money, long dollar){


        Cash cash = cashRepository.findByCashId(cashId);

        cash.setMoney(money);
        cash.setDollar(dollar);

        return cashRepository.save(cash);

    }

    public Cash findCash(Long memberId) {

        Optional<Member> member = memberRepository.findByMemberId(memberId);

        if (member.isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        }

        List<Cash> cashList = member.get().getCashList();

        if (cashList == null) {
            throw new BusinessLogicException(ExceptionCode.INVALID_CASH);
        }

        return cashList.get(0);
    }

    public void checkCash(long price, Member member) {
        if(price > member.getCashList().get(0).getMoney())
            throw new BusinessLogicException(ExceptionCode.NOT_ENOUGH_MONEY);
        else
            return;
    }

}


//    private void validateAuthor(Cash cash, Member member) {
//
//        if (!cash.getMember().equals(member)) {
//            throw new BusinessLogicException(ExceptionCode.INVALID_CASH);
//        }
//    }

