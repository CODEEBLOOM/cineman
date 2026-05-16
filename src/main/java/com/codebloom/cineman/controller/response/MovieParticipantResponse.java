package com.codebloom.cineman.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieParticipantResponse {

    private Integer id;
    private Integer movieId;
    private String movieTitle;
    private Integer participantId;
    private String participantBirthName;
    private String participantNickname;
    private Integer movieRoleId;
    private String movieRoleName;
}
