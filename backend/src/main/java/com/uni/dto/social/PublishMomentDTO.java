package com.uni.dto.social;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class PublishMomentDTO {
    @NotBlank(message = "内容不能为空")
    private String content;
    private String images;
    private String type;
}
