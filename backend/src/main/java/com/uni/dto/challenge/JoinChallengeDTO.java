package com.uni.dto.challenge;

import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class JoinChallengeDTO {
    @NotNull(message = "挑战ID不能为空")
    private Long challengeId;
}
