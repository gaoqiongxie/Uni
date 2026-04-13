<template>
  <view class="food-record-page">
    <!-- 顶部日期选择 -->
    <view class="date-bar">
      <text class="date-text">{{ formatDate(currentDate) }}</text>
      <text class="date-sub">{{ getWeekDay(currentDate) }}</text>
    </view>

    <!-- 热量总览卡片 -->
    <view class="calorie-card" v-if="dailySummary">
      <view class="calorie-header">
        <view class="calorie-total">
          <text class="total-num">{{ dailySummary.totalCalories }}</text>
          <text class="total-unit">千卡</text>
        </view>
        <view class="nutrition-bars" v-if="dailySummary.nutritionRatio">
          <view class="nutrition-item">
            <text class="nutrition-label">蛋白质</text>
            <view class="nutrition-bar">
              <view class="bar protein" :style="{ width: dailySummary.nutritionRatio.proteinRatio + '%' }"></view>
            </view>
            <text class="nutrition-value">{{ dailySummary.nutritionRatio.proteinRatio }}%</text>
          </view>
          <view class="nutrition-item">
            <text class="nutrition-label">脂肪</text>
            <view class="nutrition-bar">
              <view class="bar fat" :style="{ width: dailySummary.nutritionRatio.fatRatio + '%' }"></view>
            </view>
            <text class="nutrition-value">{{ dailySummary.nutritionRatio.fatRatio }}%</text>
          </view>
          <view class="nutrition-item">
            <text class="nutrition-label">碳水</text>
            <view class="nutrition-bar">
              <view class="bar carbs" :style="{ width: dailySummary.nutritionRatio.carbsRatio + '%' }"></view>
            </view>
            <text class="nutrition-value">{{ dailySummary.nutritionRatio.carbsRatio }}%</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 餐次列表 -->
    <view class="meal-list">
      <view 
        class="meal-section" 
        v-for="meal in mealList" 
        :key="meal.type"
        @click="showAddFood(meal.type)"
      >
        <view class="meal-header">
          <view class="meal-info">
            <text class="meal-icon">{{ meal.icon }}</text>
            <text class="meal-name">{{ meal.name }}</text>
            <text class="meal-calorie">{{ getMealCalories(meal.type) }} 千卡</text>
          </view>
          <text class="add-btn">+ 添加</text>
        </view>
        
        <!-- 该餐食物列表 -->
        <view class="food-list" v-if="getMealFoods(meal.type).length > 0">
          <view 
            class="food-item" 
            v-for="food in getMealFoods(meal.type)" 
            :key="food.id"
            @click.stop="showFoodDetail(food)"
          >
            <view class="food-info">
              <text class="food-name">{{ food.foodName }}</text>
              <text class="food-amount">{{ food.amount }}{{ food.unit }}</text>
            </view>
            <view class="food-calorie">
              <text class="calorie-num">{{ food.calories }}</text>
              <text class="calorie-unit">千卡</text>
            </view>
            <text class="delete-btn" @click.stop="deleteFood(food.id)">×</text>
          </view>
        </view>
        
        <view class="empty-tip" v-else>
          <text>点击添加{{ meal.name }}记录</text>
        </view>
      </view>
    </view>

    <!-- 添加食物弹窗 -->
    <uni-popup ref="addPopup" type="bottom">
      <view class="add-popup">
        <view class="popup-header">
          <text class="popup-title">添加{{ currentMealName }}</text>
          <text class="popup-close" @click="closeAddPopup">×</text>
        </view>
        
        <!-- 搜索栏 -->
        <view class="search-bar">
          <input 
            class="search-input" 
            v-model="searchKeyword"
            placeholder="搜索食物..."
            @input="onSearch"
          />
          <text class="search-icon">🔍</text>
        </view>

        <!-- 分类标签 -->
        <scroll-view class="category-tabs" scroll-x>
          <view 
            class="category-tab" 
            :class="{ active: currentCategory === '' }"
            @click="selectCategory('')"
          >
            <text>全部</text>
          </view>
          <view 
            class="category-tab" 
            v-for="cat in categories" 
            :key="cat.code"
            :class="{ active: currentCategory === cat.code }"
            @click="selectCategory(cat.code)"
          >
            <text>{{ cat.icon }} {{ cat.name }}</text>
          </view>
        </scroll-view>

        <!-- 食物列表 -->
        <scroll-view class="food-select-list" scroll-y>
          <view 
            class="food-select-item" 
            v-for="food in filteredFoods" 
            :key="food.id"
            @click="selectFood(food)"
          >
            <view class="food-select-info">
              <text class="food-select-name">{{ food.foodName }}</text>
              <text class="food-select-calorie">{{ food.caloriesPer100g }}千卡/100g</text>
            </view>
            <text class="food-select-portion">{{ food.commonPortion }}</text>
          </view>
        </scroll-view>

        <!-- 自定义添加 -->
        <view class="custom-add" @click="showCustomAdd">
          <text>+ 自定义添加食物</text>
        </view>
      </view>
    </uni-popup>

    <!-- 食物详情/编辑弹窗 -->
    <uni-popup ref="detailPopup" type="center">
      <view class="detail-popup" v-if="selectedFood">
        <view class="detail-header">
          <text class="detail-name">{{ selectedFood.foodName }}</text>
          <text class="detail-close" @click="closeDetailPopup">×</text>
        </view>
        
        <view class="detail-content">
          <view class="amount-input-row">
            <text class="input-label">摄入量</text>
            <input 
              class="amount-input" 
              type="digit"
              v-model="inputAmount"
              placeholder="请输入"
            />
            <text class="input-unit">{{ selectedFood.unit }}</text>
          </view>
          
          <view class="calorie-preview">
            <text class="preview-label">预计热量</text>
            <text class="preview-value">{{ calculatedCalories }} 千卡</text>
          </view>

          <view class="nutrition-detail">
            <view class="nutrition-row">
              <text>蛋白质: {{ calculatedProtein }}g</text>
              <text>脂肪: {{ calculatedFat }}g</text>
              <text>碳水: {{ calculatedCarbs }}g</text>
            </view>
          </view>
        </view>

        <view class="detail-actions">
          <button class="btn-cancel" @click="closeDetailPopup">取消</button>
          <button class="btn-confirm" @click="confirmAddFood">确认添加</button>
        </view>
      </view>
    </uni-popup>

    <!-- 自定义添加弹窗 -->
    <uni-popup ref="customPopup" type="center">
      <view class="custom-popup">
        <view class="popup-header">
          <text class="popup-title">自定义添加</text>
          <text class="popup-close" @click="closeCustomPopup">×</text>
        </view>
        
        <view class="custom-form">
          <view class="form-item">
            <text class="form-label">食物名称</text>
            <input class="form-input" v-model="customForm.foodName" placeholder="请输入食物名称" />
          </view>
          
          <view class="form-item">
            <text class="form-label">热量(千卡)</text>
            <input class="form-input" type="digit" v-model="customForm.calories" placeholder="请输入热量" />
          </view>
          
          <view class="form-item">
            <text class="form-label">摄入量</text>
            <view class="amount-row">
              <input class="form-input" type="digit" v-model="customForm.amount" placeholder="数量" />
              <input class="form-input unit-input" v-model="customForm.unit" placeholder="单位(如:克、个)" />
            </view>
          </view>
        </view>

        <view class="detail-actions">
          <button class="btn-cancel" @click="closeCustomPopup">取消</button>
          <button class="btn-confirm" @click="confirmCustomAdd">确认添加</button>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import {
  getFoodCategories,
  getFoodsByCategory,
  searchFoods,
  getCommonFoods,
  addFoodLog,
  deleteFoodLog,
  getDailySummary
} from '@/api/food.api';
import type { FoodLibrary, FoodDailyLog, DailyCalorieSummary, MealType } from '@/types/food';
import { MEAL_CONFIG, FOOD_CATEGORIES } from '@/types/food';

// 当前日期
const currentDate = ref(new Date().toISOString().split('T')[0]);

// 每日汇总数据
const dailySummary = ref<DailyCalorieSummary | null>(null);

// 弹窗引用
const addPopup = ref<any>(null);
const detailPopup = ref<any>(null);
const customPopup = ref<any>(null);

// 当前选择的餐次
const currentMealType = ref<MealType>('breakfast');
const currentMealName = computed(() => MEAL_CONFIG[currentMealType.value].name);

// 搜索和筛选
const searchKeyword = ref('');
const currentCategory = ref('');
const categories = ref(FOOD_CATEGORIES);

// 食物列表
const foodLibrary = ref<FoodLibrary[]>([]);
const filteredFoods = ref<FoodLibrary[]>([]);

// 选中的食物
const selectedFood = ref<FoodLibrary | null>(null);
const inputAmount = ref('');

// 自定义表单
const customForm = ref({
  foodName: '',
  calories: '',
  amount: '',
  unit: '克'
});

// 餐次列表
const mealList = computed(() => [
  { type: 'breakfast' as MealType, ...MEAL_CONFIG.breakfast },
  { type: 'lunch' as MealType, ...MEAL_CONFIG.lunch },
  { type: 'dinner' as MealType, ...MEAL_CONFIG.dinner },
  { type: 'snack' as MealType, ...MEAL_CONFIG.snack }
]);

// 计算热量
const calculatedCalories = computed(() => {
  if (!selectedFood.value || !inputAmount.value) return 0;
  const amount = parseFloat(inputAmount.value);
  if (isNaN(amount)) return 0;
  return Math.round(selectedFood.value.caloriesPer100g * amount / 100);
});

const calculatedProtein = computed(() => {
  if (!selectedFood.value || !inputAmount.value) return 0;
  const amount = parseFloat(inputAmount.value);
  if (isNaN(amount)) return 0;
  return (selectedFood.value.proteinPer100g * amount / 100).toFixed(1);
});

const calculatedFat = computed(() => {
  if (!selectedFood.value || !inputAmount.value) return 0;
  const amount = parseFloat(inputAmount.value);
  if (isNaN(amount)) return 0;
  return (selectedFood.value.fatPer100g * amount / 100).toFixed(1);
});

const calculatedCarbs = computed(() => {
  if (!selectedFood.value || !inputAmount.value) return 0;
  const amount = parseFloat(inputAmount.value);
  if (isNaN(amount)) return 0;
  return (selectedFood.value.carbsPer100g * amount / 100).toFixed(1);
});

// 获取每日汇总
const loadDailySummary = async () => {
  try {
    const res = await getDailySummary(currentDate.value);
    dailySummary.value = res;
  } catch (error) {
    console.error('获取每日汇总失败', error);
  }
};

// 获取某餐热量
const getMealCalories = (mealType: MealType) => {
  if (!dailySummary.value) return 0;
  switch (mealType) {
    case 'breakfast': return dailySummary.value.breakfastCalories;
    case 'lunch': return dailySummary.value.lunchCalories;
    case 'dinner': return dailySummary.value.dinnerCalories;
    case 'snack': return dailySummary.value.snackCalories;
    default: return 0;
  }
};

// 获取某餐食物列表
const getMealFoods = (mealType: MealType): FoodDailyLog[] => {
  if (!dailySummary.value || !dailySummary.value.mealRecords) return [];
  return dailySummary.value.mealRecords[mealType] || [];
};

// 显示添加食物弹窗
const showAddFood = (mealType: MealType) => {
  currentMealType.value = mealType;
  searchKeyword.value = '';
  currentCategory.value = '';
  loadFoodLibrary();
  addPopup.value?.open();
};

// 关闭添加弹窗
const closeAddPopup = () => {
  addPopup.value?.close();
};

// 加载食物库
const loadFoodLibrary = async () => {
  try {
    const res = await getCommonFoods();
    foodLibrary.value = res;
    filteredFoods.value = res;
  } catch (error) {
    console.error('获取食物库失败', error);
  }
};

// 搜索食物
const onSearch = async () => {
  if (!searchKeyword.value) {
    filteredFoods.value = foodLibrary.value;
    return;
  }
  try {
    const res = await searchFoods({ keyword: searchKeyword.value });
    filteredFoods.value = res;
  } catch (error) {
    console.error('搜索食物失败', error);
  }
};

// 选择分类
const selectCategory = async (category: string) => {
  currentCategory.value = category;
  try {
    if (category) {
      const res = await getFoodsByCategory(category);
      filteredFoods.value = res;
    } else {
      const res = await getCommonFoods();
      filteredFoods.value = res;
    }
  } catch (error) {
    console.error('获取分类食物失败', error);
  }
};

// 选择食物
const selectFood = (food: FoodLibrary) => {
  selectedFood.value = food;
  inputAmount.value = String(food.unitWeight);
  closeAddPopup();
  setTimeout(() => {
    detailPopup.value?.open();
  }, 300);
};

// 显示食物详情
const showFoodDetail = (food: FoodDailyLog) => {
  // 可以扩展查看详情功能
  uni.showModal({
    title: food.foodName,
    content: `热量: ${food.calories}千卡\n蛋白质: ${food.protein}g\n脂肪: ${food.fat}g\n碳水: ${food.carbs}g`,
    showCancel: false
  });
};

// 关闭详情弹窗
const closeDetailPopup = () => {
  detailPopup.value?.close();
  selectedFood.value = null;
  inputAmount.value = '';
};

// 确认添加食物
const confirmAddFood = async () => {
  if (!selectedFood.value || !inputAmount.value) {
    uni.showToast({ title: '请输入摄入量', icon: 'none' });
    return;
  }
  
  try {
    await addFoodLog({
      recordDate: currentDate.value,
      mealType: currentMealType.value,
      foodId: selectedFood.value.id,
      foodName: selectedFood.value.foodName,
      calories: calculatedCalories.value,
      amount: parseFloat(inputAmount.value),
      unit: selectedFood.value.unit,
      protein: parseFloat(calculatedProtein.value),
      fat: parseFloat(calculatedFat.value),
      carbs: parseFloat(calculatedCarbs.value)
    });
    
    uni.showToast({ title: '添加成功', icon: 'success' });
    closeDetailPopup();
    loadDailySummary();
  } catch (error) {
    uni.showToast({ title: '添加失败', icon: 'none' });
  }
};

// 显示自定义添加
const showCustomAdd = () => {
  closeAddPopup();
  customForm.value = { foodName: '', calories: '', amount: '', unit: '克' };
  setTimeout(() => {
    customPopup.value?.open();
  }, 300);
};

// 关闭自定义弹窗
const closeCustomPopup = () => {
  customPopup.value?.close();
};

// 确认自定义添加
const confirmCustomAdd = async () => {
  if (!customForm.value.foodName || !customForm.value.calories) {
    uni.showToast({ title: '请填写完整信息', icon: 'none' });
    return;
  }
  
  try {
    await addFoodLog({
      recordDate: currentDate.value,
      mealType: currentMealType.value,
      foodName: customForm.value.foodName,
      calories: parseFloat(customForm.value.calories),
      amount: parseFloat(customForm.value.amount) || 1,
      unit: customForm.value.unit
    });
    
    uni.showToast({ title: '添加成功', icon: 'success' });
    closeCustomPopup();
    loadDailySummary();
  } catch (error) {
    uni.showToast({ title: '添加失败', icon: 'none' });
  }
};

// 删除食物
const deleteFood = async (logId: number) => {
  uni.showModal({
    title: '确认删除',
    content: '确定要删除这条记录吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await deleteFoodLog(logId);
          uni.showToast({ title: '删除成功', icon: 'success' });
          loadDailySummary();
        } catch (error) {
          uni.showToast({ title: '删除失败', icon: 'none' });
        }
      }
    }
  });
};

// 格式化日期
const formatDate = (dateStr: string) => {
  const date = new Date(dateStr);
  return `${date.getMonth() + 1}月${date.getDate()}日`;
};

// 获取星期
const getWeekDay = (dateStr: string) => {
  const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
  return days[new Date(dateStr).getDay()];
};

onShow(() => {
  loadDailySummary();
});
</script>

<style scoped lang="scss">
.food-record-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 40rpx;
}

.date-bar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40rpx 30rpx;
  color: #fff;
  
  .date-text {
    font-size: 48rpx;
    font-weight: bold;
  }
  
  .date-sub {
    font-size: 28rpx;
    opacity: 0.8;
    margin-left: 20rpx;
  }
}

.calorie-card {
  margin: -30rpx 30rpx 30rpx;
  background: #fff;
  border-radius: 20rpx;
  padding: 40rpx;
  box-shadow: 0 4rpx 20rpx rgba(0,0,0,0.08);
  
  .calorie-header {
    display: flex;
    align-items: center;
    gap: 40rpx;
  }
  
  .calorie-total {
    text-align: center;
    
    .total-num {
      font-size: 72rpx;
      font-weight: bold;
      color: #333;
    }
    
    .total-unit {
      font-size: 28rpx;
      color: #999;
      margin-left: 10rpx;
    }
  }
  
  .nutrition-bars {
    flex: 1;
  }
  
  .nutrition-item {
    display: flex;
    align-items: center;
    margin-bottom: 16rpx;
    
    &:last-child {
      margin-bottom: 0;
    }
    
    .nutrition-label {
      width: 80rpx;
      font-size: 24rpx;
      color: #666;
    }
    
    .nutrition-bar {
      flex: 1;
      height: 16rpx;
      background: #f0f0f0;
      border-radius: 8rpx;
      overflow: hidden;
      margin: 0 20rpx;
      
      .bar {
        height: 100%;
        border-radius: 8rpx;
        transition: width 0.3s ease;
        
        &.protein { background: #FF9500; }
        &.fat { background: #FF2D55; }
        &.carbs { background: #34C759; }
      }
    }
    
    .nutrition-value {
      width: 80rpx;
      font-size: 24rpx;
      color: #333;
      text-align: right;
    }
  }
}

.meal-list {
  padding: 0 30rpx;
}

.meal-section {
  background: #fff;
  border-radius: 20rpx;
  margin-bottom: 30rpx;
  overflow: hidden;
  
  .meal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 30rpx;
    border-bottom: 1rpx solid #f0f0f0;
    
    .meal-info {
      display: flex;
      align-items: center;
      gap: 16rpx;
      
      .meal-icon {
        font-size: 40rpx;
      }
      
      .meal-name {
        font-size: 32rpx;
        font-weight: 600;
        color: #333;
      }
      
      .meal-calorie {
        font-size: 28rpx;
        color: #667eea;
        font-weight: 500;
      }
    }
    
    .add-btn {
      font-size: 28rpx;
      color: #667eea;
      padding: 10rpx 24rpx;
      background: rgba(102, 126, 234, 0.1);
      border-radius: 30rpx;
    }
  }
}

.food-list {
  padding: 20rpx;
}

.food-item {
  display: flex;
  align-items: center;
  padding: 24rpx;
  background: #f8f9fa;
  border-radius: 16rpx;
  margin-bottom: 16rpx;
  
  &:last-child {
    margin-bottom: 0;
  }
  
  .food-info {
    flex: 1;
    
    .food-name {
      font-size: 30rpx;
      color: #333;
      margin-bottom: 8rpx;
    }
    
    .food-amount {
      font-size: 24rpx;
      color: #999;
    }
  }
  
  .food-calorie {
    text-align: right;
    margin-right: 20rpx;
    
    .calorie-num {
      font-size: 32rpx;
      font-weight: 600;
      color: #FF9500;
    }
    
    .calorie-unit {
      font-size: 24rpx;
      color: #999;
      margin-left: 4rpx;
    }
  }
  
  .delete-btn {
    width: 48rpx;
    height: 48rpx;
    line-height: 48rpx;
    text-align: center;
    font-size: 40rpx;
    color: #999;
    background: #e0e0e0;
    border-radius: 50%;
  }
}

.empty-tip {
  padding: 60rpx;
  text-align: center;
  
  text {
    font-size: 28rpx;
    color: #999;
  }
}

// 弹窗样式
.add-popup {
  background: #fff;
  border-radius: 40rpx 40rpx 0 0;
  max-height: 80vh;
  
  .popup-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 30rpx;
    border-bottom: 1rpx solid #f0f0f0;
    
    .popup-title {
      font-size: 32rpx;
      font-weight: 600;
      color: #333;
    }
    
    .popup-close {
      font-size: 48rpx;
      color: #999;
      padding: 0 20rpx;
    }
  }
}

.search-bar {
  position: relative;
  padding: 20rpx 30rpx;
  
  .search-input {
    height: 80rpx;
    background: #f5f5f5;
    border-radius: 40rpx;
    padding: 0 80rpx 0 30rpx;
    font-size: 28rpx;
  }
  
  .search-icon {
    position: absolute;
    right: 50rpx;
    top: 50%;
    transform: translateY(-50%);
    font-size: 32rpx;
  }
}

.category-tabs {
  white-space: nowrap;
  padding: 20rpx 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
  
  .category-tab {
    display: inline-block;
    padding: 16rpx 32rpx;
    margin-right: 20rpx;
    background: #f5f5f5;
    border-radius: 32rpx;
    font-size: 26rpx;
    color: #666;
    
    &.active {
      background: #667eea;
      color: #fff;
    }
  }
}

.food-select-list {
  max-height: 600rpx;
  padding: 20rpx 30rpx;
}

.food-select-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
  
  .food-select-info {
    .food-select-name {
      font-size: 30rpx;
      color: #333;
      margin-bottom: 8rpx;
    }
    
    .food-select-calorie {
      font-size: 24rpx;
      color: #999;
    }
  }
  
  .food-select-portion {
    font-size: 24rpx;
    color: #667eea;
    background: rgba(102, 126, 234, 0.1);
    padding: 8rpx 20rpx;
    border-radius: 20rpx;
  }
}

.custom-add {
  padding: 30rpx;
  text-align: center;
  border-top: 1rpx solid #f0f0f0;
  
  text {
    font-size: 28rpx;
    color: #667eea;
  }
}

// 详情弹窗
.detail-popup, .custom-popup {
  background: #fff;
  border-radius: 24rpx;
  width: 600rpx;
  padding: 40rpx;
  
  .detail-header, .popup-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 40rpx;
    
    .detail-name, .popup-title {
      font-size: 36rpx;
      font-weight: 600;
      color: #333;
    }
    
    .detail-close, .popup-close {
      font-size: 48rpx;
      color: #999;
    }
  }
}

.detail-content {
  .amount-input-row {
    display: flex;
    align-items: center;
    margin-bottom: 40rpx;
    
    .input-label {
      width: 120rpx;
      font-size: 28rpx;
      color: #666;
    }
    
    .amount-input {
      flex: 1;
      height: 80rpx;
      background: #f5f5f5;
      border-radius: 12rpx;
      padding: 0 24rpx;
      font-size: 32rpx;
      text-align: center;
    }
    
    .input-unit {
      width: 80rpx;
      font-size: 28rpx;
      color: #666;
      text-align: right;
    }
  }
  
  .calorie-preview {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 16rpx;
    padding: 40rpx;
    text-align: center;
    margin-bottom: 30rpx;
    
    .preview-label {
      font-size: 24rpx;
      color: rgba(255,255,255,0.8);
      margin-bottom: 16rpx;
    }
    
    .preview-value {
      font-size: 56rpx;
      font-weight: bold;
      color: #fff;
    }
  }
  
  .nutrition-detail {
    .nutrition-row {
      display: flex;
      justify-content: space-around;
      
      text {
        font-size: 26rpx;
        color: #666;
      }
    }
  }
}

// 自定义表单
.custom-form {
  .form-item {
    margin-bottom: 30rpx;
    
    .form-label {
      display: block;
      font-size: 28rpx;
      color: #666;
      margin-bottom: 16rpx;
    }
    
    .form-input {
      height: 80rpx;
      background: #f5f5f5;
      border-radius: 12rpx;
      padding: 0 24rpx;
      font-size: 28rpx;
    }
    
    .amount-row {
      display: flex;
      gap: 20rpx;
      
      .unit-input {
        width: 200rpx;
      }
    }
  }
}

.detail-actions {
  display: flex;
  gap: 20rpx;
  margin-top: 40rpx;
  
  button {
    flex: 1;
    height: 88rpx;
    line-height: 88rpx;
    border-radius: 44rpx;
    font-size: 30rpx;
    
    &.btn-cancel {
      background: #f5f5f5;
      color: #666;
    }
    
    &.btn-confirm {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: #fff;
    }
  }
}
</style>
