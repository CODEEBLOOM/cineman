package com.codebloom.cineman.component;

import com.codebloom.cineman.common.enums.UserStatus;
import com.codebloom.cineman.common.enums.UserType;
import com.codebloom.cineman.controller.response.PromotionResponse;
import com.codebloom.cineman.message.PromotionActivatedMessage;
import com.codebloom.cineman.model.MembershipRankEntity;
import com.codebloom.cineman.model.PromotionEntity;
import com.codebloom.cineman.model.UserEntity;
import com.codebloom.cineman.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
    @Slf4j(topic = "PROMOTION_ACTIVATION_NOTIFIER")
public class PromotionActivationNotifier {

    public static final String USER_PROMOTION_QUEUE = "/queue/promotions";
    private static final String PROMOTION_ACTIVATED = "PROMOTION_ACTIVATED";

    private final SimpMessageSendingOperations messagingTemplate;
    private final UserRepository userRepository;

    public void notifyEligibleUsers(PromotionEntity promotionEntity, PromotionResponse promotionResponse) {
        PromotionActivatedMessage payload = PromotionActivatedMessage.builder()
                .type(PROMOTION_ACTIVATED)
                .promotion(promotionResponse)
                .build();

        List<UserEntity> recipients = userRepository.findAllByStatus(UserStatus.ACTIVE)
                .stream()
                .filter(this::isCustomerUser)
                .filter(user -> isEligibleForPromotion(user, promotionEntity))
                .toList();

        recipients.forEach(user -> messagingTemplate.convertAndSendToUser(
                user.getEmail(),
                USER_PROMOTION_QUEUE,
                payload
        ));

        log.info("Sent promotion activation notification for promotion {} to {} users", promotionEntity.getId(), recipients.size());
    }

    private boolean isCustomerUser(UserEntity userEntity) {
        return userEntity.getUserRoles() != null && userEntity.getUserRoles()
                .stream()
                .map(userRoleEntity -> userRoleEntity.getRole())
                .filter(Objects::nonNull)
                .anyMatch(roleEntity -> Objects.equals(roleEntity.getRoleId(), UserType.USER.name()));
    }

    private boolean isEligibleForPromotion(UserEntity userEntity, PromotionEntity promotionEntity) {
        List<MembershipRankEntity> membershipRanks = promotionEntity.getMembershipRanks();
        if (membershipRanks == null || membershipRanks.isEmpty()) {
            return true;
        }

        if (userEntity.getMembershipRank() == null) {
            return false;
        }

        Integer userRankId = userEntity.getMembershipRank().getId();
        return membershipRanks.stream()
                .map(MembershipRankEntity::getId)
                .anyMatch(rankId -> Objects.equals(rankId, userRankId));
    }
}
