package com.uni.dto.challenge;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class CheckinDTO {
    @NotNull(message = "挑战ID不能为空")
    private Long challengeId;

    private BigDecimal actualValue;
    private String note;
    private String photoUrl;
}
