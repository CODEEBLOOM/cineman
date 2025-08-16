package com.codebloom.cineman.controller;


import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.MembershipRankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.path}/membership-rank")
@Validated
@RequiredArgsConstructor
@Tag(name = "Membership Rank Controller", description = "Controller quản lý membership rank")
public class MembershipRankController {

    private final MembershipRankService membershipRankService;

    @Operation(summary = "Api Lấy danh sách membership rank", description = "Api Lấy Lấy danh sách membership rank")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .message("Success")
                        .status(HttpStatus.OK.value())
                        .data(membershipRankService.findAll())
                        .build()
        );
    }

    @Operation(summary = "Api nâng cấp hạng thẻ thành viên", description = "Api Lấy Lấy danh sách membership rank")
    @PutMapping("/upgrade/user/{userId}/membership-rank/{membershipRankId}")
    public ResponseEntity<ApiResponse> upgradeMembershipRank(
            @PathVariable("userId") @Min(value = 1, message = "Id của người dùng phải lớn hơn 0 !") Long userId,
            @PathVariable("membershipRankId") @Min(value = 1, message = "Id của membership rank phải lớn hơn 0 !") Integer membershipRankId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .message("Success")
                        .status(HttpStatus.OK.value())
                        .data(membershipRankService.upgradeMembershipRank(userId, membershipRankId))
                        .build()
        );
    }

}
