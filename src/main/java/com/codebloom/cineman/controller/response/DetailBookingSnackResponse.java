package com.codebloom.cineman.controller.response;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetailBookingSnackResponse {

    private Long id;
    private Integer totalSnack;
    private Integer snackId;
    private Long invoiceId;

}
