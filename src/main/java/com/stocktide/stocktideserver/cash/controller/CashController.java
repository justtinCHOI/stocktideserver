package com.stocktide.stocktideserver.cash.controller;

import com.stocktide.stocktideserver.cash.dto.CashResponseDto;
import com.stocktide.stocktideserver.cash.entity.Cash;
import com.stocktide.stocktideserver.cash.mapper.CashMapper;
import com.stocktide.stocktideserver.cash.service.CashService;
import com.stocktide.stocktideserver.member.entity.Member;
import com.stocktide.stocktideserver.member.repository.MemberRepository;
import com.stocktide.stocktideserver.member.service.MemberServiceImpl;
import com.stocktide.stocktideserver.stock.service.StockHoldService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/cash")
@RequiredArgsConstructor
public class CashController {

    private final CashMapper mapper;
    private final CashService cashService;
    private final MemberServiceImpl memberService;
    private final StockHoldService stockHoldService;
//    private final StockOrderService stockOrderService;
    private final MemberRepository memberRepository;

    @GetMapping
    private ResponseEntity getCashList(@RequestParam long memberId){

        Member member = memberRepository.findById(memberId).orElse(null);

        if (member == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Cash> cashList = member.getCashList();
        List<CashResponseDto> cashResponseDtoList= new ArrayList<>();
        for (Cash cash : cashList) {
            CashResponseDto responseDto = mapper.cashToCashResponseDto(cash);
            cashResponseDtoList.add(responseDto);
        }

        return new ResponseEntity<>(cashResponseDtoList, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity createCash(@RequestParam long memberId){

        Cash createdCash = cashService.createCash(memberId);

        CashResponseDto responseDto = mapper.cashToCashResponseDto(createdCash);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{cashId}")
    public ResponseEntity deleteCash(@PathVariable("cashId") Long cashId) {

        cashService.remove(cashId);

        return new ResponseEntity<>(cashId, HttpStatus.OK);
    }


    @PutMapping("/{cashId}")
    public ResponseEntity updateCash(@PathVariable("cashId") Long cashId
            , @RequestParam long money
            , @RequestParam long dollar) {
        log.info("---PutMapping-----------cashId money : {} {} {}", cashId, money, dollar);

        Cash cash = cashService.updateCash(cashId, money, dollar);

        return new ResponseEntity<>(cash, HttpStatus.OK);
    }

    @GetMapping("/one/{memberId}")
    private ResponseEntity getOneCash(@PathVariable("memberId") Long memberId){

        Cash cash = cashService.findCash(memberId);

        return new ResponseEntity<>(mapper.cashToCashResponseDto(cash), HttpStatus.OK);
    }

}



//    @PostMapping
//    public ResponseEntity postCash(@Valid @RequestBody CashPostDto cashPostDto,
//                                   Member member){
//        Cash cashToCreate = mapper.cashPostToCash(cashPostDto);
//
//        cashToCreate.setMember(member);
//
//        Cash createdCash = cashService.createCash(cashToCreate);
//        CashResponseDto responseDto = mapper.cashToCashResponseDto(createdCash);
//
//        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
//    }
//
//    @PatchMapping("{cashId}")
//    public ResponseEntity patchCash(@PathVariable long cashId, @Valid @RequestBody CashPatchDto requestBody,
//                                    Member member){
//
//        Cash cashToUpdate = mapper.cashPatchToCash(requestBody);
//
//        cashToUpdate.setMember(member);
//
//        requestBody.setCashId(cashId);
//
//        Cash cash = cashService.updateCash(cashId, member, requestBody);
//        stockHoldService.deleteStockHolds(member.getMemberId());
//        stockOrderService.deleteStockOrders(member);
//
//        return new ResponseEntity<>(mapper.cashToCashResponseDto(cash), HttpStatus.OK);
//    }
//
//    @GetMapping
//    private ResponseEntity getCash( Member member){
//        Cash response = cashService.findCash(member);
//
//        return new ResponseEntity<>(mapper.cashToCashResponseDto(response), HttpStatus.OK);
//    }
//
////    @Operation(summary = "현금 정보 생성", description = "새로운 현금 정보를 생성합니다.", tags = { "Cash" })
////    @ApiResponse(responseCode = "201", description = "Created",
////            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CashResponseDto.class)))
////    @ApiResponse(responseCode = "400", description = "이미 보유한 현금이 있습니다.")
////    @ApiResponse(responseCode = "401", description = "Not Enough Money")
//    @PostMapping
//    public ResponseEntity postCash(@Valid @RequestBody CashPostDto cashPostDto,
//                                  Member member){
////    public ResponseEntity postCash(@Schema(implementation = CashPostDto.class)@Valid @RequestBody CashPostDto cashPostDto,
////                                   @AuthenticationPrincipal Member member){
//
//        Cash cashToCreate = mapper.cashPostToCash(cashPostDto);
//
//        cashToCreate.setMember(member);
//
//        Cash createdCash = cashService.createCash(cashToCreate);
//        CashResponseDto responseDto = mapper.cashToCashResponseDto(createdCash);
//
//        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
//    }




//
////    @Operation(summary = "현금 정보 업데이트", description = "현금 정보를 업데이트합니다.", tags = { "Cash" })
////    @ApiResponse(responseCode = "200", description = "OK",
////            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CashResponseDto.class)))
////    @ApiResponse(responseCode = "400", description = "Bad Request")
////    @ApiResponse(responseCode = "401", description = "Invalid Cash")
////    @ApiResponse(responseCode = "404", description = "Not Found")
//    @PatchMapping("{cashId}")
//    public ResponseEntity patchCash(@PathVariable long cashId, @Valid @RequestBody CashPatchDto requestBody,
//                                     Member member){
////    public ResponseEntity patchCash(@Schema(implementation = CashPatchDto.class)@PathVariable long cashId, @Valid @RequestBody CashPatchDto requestBody,
////                                    @AuthenticationPrincipal Member member){
//
//        Cash cashToUpdate = mapper.cashPatchToCash(requestBody);
//
//        cashToUpdate.setMember(member);
//
//        requestBody.setCashId(cashId);
//
//        Cash cash = cashService.updateCash(cashId, member, requestBody);
//        stockHoldService.deleteStockHolds(member.getMemberId());
//        stockOrderService.deleteStockOrders(member);
//
//        return new ResponseEntity<>(mapper.cashToCashResponseDto(cash), HttpStatus.OK);
//    }
//
////    @Operation(summary = "현금 정보 조회", description = "현금 정보를 조회합니다.", tags = { "Cash" })
////    @ApiResponse(responseCode = "200", description = "OK",
////            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CashResponseDto.class)))
////    @ApiResponse(responseCode = "401", description = "Invalid Cash")
////    @ApiResponse(responseCode = "404", description = "Not Found")
//    @GetMapping
////    private ResponseEntity getCash(@AuthenticationPrincipal Member member){
//    private ResponseEntity getCash( Member member){
//        Cash response = cashService.findCash(member);
//
//        return new ResponseEntity<>(mapper.cashToCashResponseDto(response), HttpStatus.OK);
//    }