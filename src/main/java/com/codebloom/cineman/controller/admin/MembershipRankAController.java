package com.codebloom.cineman.controller.admin;

import com.codebloom.cineman.controller.request.MembershipRankRequest;
import com.codebloom.cineman.controller.response.ApiResponse;
import com.codebloom.cineman.service.MembershipRankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
            summary = "Lay danh sach membership rank",
            description = "API dung de lay danh sach membership rank dang hoat dong."
    )
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> findAllMembershipRanks() {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Get membership rank list successfully")
                        .status(HttpStatus.OK.value())
                        .data(membershipRankService.findAll())
                        .build()
        );
    }

    @Operation(
            summary = "Lay chi tiet membership rank",
            description = "API dung de lay chi tiet membership rank theo id."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> findMembershipRankById(
            @PathVariable("id") @Min(value = 1, message = "Id cua membership rank phai lon hon 0 !") Integer id
    ) {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Get membership rank successfully")
                        .status(HttpStatus.OK.value())
                        .data(membershipRankService.findById(id))
                        .build()
        );
    }

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

    @Operation(
            summary = "Cap nhat membership rank",
            description = "API dung de cap nhat membership rank trong he thong."
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateMembershipRank(
            @PathVariable("id") @Min(value = 1, message = "Id cua membership rank phai lon hon 0 !") Integer id,
            @RequestBody @Valid MembershipRankRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Update membership rank successfully")
                        .status(HttpStatus.OK.value())
                        .data(membershipRankService.update(id, request))
                        .build()
        );
    }

    @Operation(
            summary = "Xoa mem membership rank",
            description = "API dung de xoa mem membership rank neu chua co user dang su dung."
    )
    @DeleteMapping({"/{id}", "/{id}/delete"})
    public ResponseEntity<ApiResponse> deleteMembershipRank(
            @PathVariable("id") @Min(value = 1, message = "Id cua membership rank phai lon hon 0 !") Integer id
    ) {
        membershipRankService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .message("Delete membership rank successfully")
                        .status(HttpStatus.OK.value())
                        .data(id)
                        .build()
        );
    }
}
