package com.stocktide.stocktideserver.star.controller;


import com.stocktide.stocktideserver.member.entity.Member;
import com.stocktide.stocktideserver.star.dto.StarResponseDto;
import com.stocktide.stocktideserver.star.sevice.StarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stars")
public class StarController {
    private final StarService starService;

    public StarController(StarService starService) {
        this.starService = starService;
    }

    @PostMapping
    public ResponseEntity setStar(@RequestParam long companyId,
                                  @AuthenticationPrincipal Member member) {
        starService.saveStar(member, companyId);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity getStarList(@AuthenticationPrincipal Member member) {
        List<StarResponseDto> starResponseDtos = starService.getStarResponseDtoList(member);

        return new ResponseEntity<>(starResponseDtos, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity deleteStar(@RequestParam long companyId,
                                     @AuthenticationPrincipal Member member) {
        starService.deleteStar(member, companyId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
