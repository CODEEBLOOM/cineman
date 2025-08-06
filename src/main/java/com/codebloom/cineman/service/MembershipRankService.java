package com.codebloom.cineman.service;


import com.codebloom.cineman.controller.request.MembershipRankRequest;
import com.codebloom.cineman.controller.response.MembershipRankResponse;

import java.util.List;

public interface MembershipRankService {

    MembershipRankResponse create(MembershipRankRequest request);

    MembershipRankResponse update(Integer id, MembershipRankRequest request);

    void delete(Integer id);

    MembershipRankResponse findById(Integer id);

    List<MembershipRankResponse> findAll();
    

}
