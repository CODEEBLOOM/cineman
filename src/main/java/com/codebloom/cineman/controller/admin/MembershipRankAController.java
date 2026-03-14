package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.MembershipRankRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.MembershipRankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.path}/admin/membership-rank")
@Validated
@RequiredArgsConstructor
@Tag(name = "Membership Rank Controller Admin", description = "Quan ly membership rank")
public class MembershipRankAController {

    private final MembershipRankService membershipRankService;

    @Operation(
            summary = "Tao moi membership rank",
            description = "API dung de tao moi mot membership rank trong he thong."
    )
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createMembershipRank(@RequestBody @Valid MembershipRankRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .message("Create membership rank successfully")
                        .status(HttpStatus.CREATED.value())
                        .data(membershipRankService.create(request))
                        .build()
        );
    }
}
