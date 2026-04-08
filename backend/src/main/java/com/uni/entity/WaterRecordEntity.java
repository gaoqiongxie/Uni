package com.uni.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 饮水记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_water_record")
public class WaterRecordEntity extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 记录日期
     */
    private LocalDate recordDate;

    /**
     * 饮水量(ml)
     */
    private Integer amount;

    /**
     * 饮水时间
     */
    private LocalTime drinkTime;

    /**
     * 饮品类型: water-水, tea-茶, coffee-咖啡, juice-果汁, milk-牛奶, other-其他
     */
    private String drinkType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
