package com.uni.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uni.common.BizCode;
import com.uni.common.BizException;
import com.uni.dto.meal.MealRecordDTO;
import com.uni.entity.MealRecordEntity;
import com.uni.mapper.MealRecordMapper;
import com.uni.service.MealRecordService;
import com.uni.vo.meal.MealCalendarVO;
import com.uni.vo.meal.MealRecordVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 餐食打卡服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MealRecordServiceImpl extends ServiceImpl<MealRecordMapper, MealRecordEntity> implements MealRecordService {

    private static final String[] MEAL_TYPE_NAMES = {"", "早餐", "午餐", "晚餐", "加餐"};

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MealRecordVO createMealRecord(MealRecordDTO dto) {
        MealRecordEntity entity = new MealRecordEntity();
        BeanUtils.copyProperties(dto, entity);
        entity.setCreateUser(String.valueOf(dto.getUserId()));
        entity.setUpdateUser(String.valueOf(dto.getUserId()));
        entity.setDeleteFlag(0);

        save(entity);

        MealRecordVO vo = new MealRecordVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setMealTypeName(getMealTypeName(entity.getMealType()));
        return vo;
    }

    @Override
    public List<MealRecordVO> getUserMealRecordsByDate(Long userId, LocalDate recordDate) {
        List<MealRecordEntity> entities = baseMapper.selectByUserIdAndDate(userId, recordDate);
        return entities.stream().map(entity -> {
            MealRecordVO vo = new MealRecordVO();
            BeanUtils.copyProperties(entity, vo);
            vo.setMealTypeName(getMealTypeName(entity.getMealType()));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public MealCalendarVO getMealCalendar(Long userId, int year, int month) {
        List<LocalDate> checkInDates = baseMapper.selectCheckInDates(userId, year, month);

        // 每天的打卡次数
        Map<LocalDate, Long> dateMealCountMap = list(new LambdaQueryWrapper<MealRecordEntity>()
                .eq(MealRecordEntity::getUserId, userId)
                .in(MealRecordEntity::getRecordDate, checkInDates)
                .eq(MealRecordEntity::getDeleteFlag, 0))
                .stream()
                .collect(Collectors.groupingBy(MealRecordEntity::getRecordDate, Collectors.counting()));

        Set<LocalDate> checkInSet = checkInDates.stream().collect(Collectors.toSet());

        YearMonth ym = YearMonth.of(year, month);
        int totalDays = ym.lengthOfMonth();
        List<MealCalendarVO.DayVO> calendar = new ArrayList<>();

        for (int d = 1; d <= totalDays; d++) {
            LocalDate date = LocalDate.of(year, month, d);
            MealCalendarVO.DayVO dayVO = new MealCalendarVO.DayVO();
            dayVO.setDate(date);
            dayVO.setCheckIn(checkInSet.contains(date));
            dayVO.setMealCount(dateMealCountMap.getOrDefault(date, 0L).intValue());
            calendar.add(dayVO);
        }

        // 计算连续打卡天数
        int currentStreak = calculateCurrentStreak(checkInDates);
        int maxStreak = calculateMaxStreak(checkInDates);

        MealCalendarVO vo = new MealCalendarVO();
        vo.setYear(year);
        vo.setMonth(month);
        vo.setTotalDays(totalDays);
        vo.setCheckInDays(checkInDates.size());
        vo.setCheckInRate(checkInDates.size() * 100.0 / totalDays);
        vo.setCurrentStreak(currentStreak);
        vo.setMaxStreak(maxStreak);
        vo.setCalendar(calendar);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMealRecord(Long id, Long userId) {
        MealRecordEntity record = getById(id);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new BizException(BizCode.WEIGHT_RECORD_NOT_FOUND, "记录不存在");
        }
        return removeById(id);
    }

    private String getMealTypeName(Integer mealType) {
        if (mealType == null || mealType < 1 || mealType >= MEAL_TYPE_NAMES.length) {
            return "未知";
        }
        return MEAL_TYPE_NAMES[mealType];
    }

    private int calculateCurrentStreak(List<LocalDate> dates) {
        if (dates.isEmpty()) return 0;
        List<LocalDate> sorted = dates.stream().sorted().distinct().collect(Collectors.toList());
        LocalDate today = LocalDate.now();
        int streak = 0;
        LocalDate check = today;
        for (int i = sorted.size() - 1; i >= 0; i--) {
            if (sorted.get(i).equals(check)) {
                streak++;
                check = check.minusDays(1);
            } else if (sorted.get(i).isBefore(check)) {
                break;
            }
        }
        return streak;
    }

    private int calculateMaxStreak(List<LocalDate> dates) {
        if (dates.isEmpty()) return 0;
        List<LocalDate> sorted = dates.stream().sorted().distinct().collect(Collectors.toList());
        int max = 1, cur = 1;
        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i).equals(sorted.get(i - 1).plusDays(1))) {
                cur++;
                max = Math.max(max, cur);
            } else {
                cur = 1;
            }
        }
        return max;
    }
}
