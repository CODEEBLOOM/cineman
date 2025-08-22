package com.codebloom.cineman.controller.response;



import com.codebloom.cineman.model.SnackEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailBookingSnackResponse {

    private Long id;
    private Integer totalSnack;
    private Integer snackId;
    private Long invoiceId;
    private Double totalMoney;
    private SnackEntity snack;

}
