package com.uni.service;

import com.uni.dto.goal.UserGoalDTO;
import com.uni.vo.goal.UserGoalVO;

import java.util.List;

/**
 * 用户目标服务
 */
public interface UserGoalService {

    /** 创建目标（自动将旧进行中目标置为放弃） */
    UserGoalVO createGoal(Long userId, UserGoalDTO dto);

    /** 查询当前进行中目标（含进度统计） */
    UserGoalVO getActiveGoal(Long userId);

    /** 查询目标历史列表 */
    List<UserGoalVO> listGoals(Long userId);

    /** 放弃当前目标 */
    boolean abandonGoal(Long goalId, Long userId);

    /** 标记目标达成 */
    boolean achieveGoal(Long goalId, Long userId);
}
