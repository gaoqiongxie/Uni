<template>
  <view class="meal-record-container">
    <!-- 日期选择栏 -->
    <view class="date-bar">
      <text class="date-arrow" @click="prevDay">‹</text>
      <picker mode="date" :value="selectedDate" @change="onDateChange">
        <view class="date-display">
          <text>{{ selectedDate }}</text>
          <text v-if="selectedDate === todayStr" class="today-badge">今天</text>
        </view>
      </picker>
      <text
        class="date-arrow"
        :class="{ disabled: selectedDate >= todayStr }"
        @click="nextDay"
      >›</text>
    </view>

    <!-- 热量进度条 -->
    <view class="calorie-card card">
      <view class="calorie-header">
        <text class="calorie-title">今日热量</text>
        <text class="calorie-goal">目标 {{ calorieGoal }} kcal</text>
      </view>
      <view class="calorie-bar-wrap">
        <view class="calorie-bar">
          <view class="calorie-fill" :style="{ width: caloriePercent + '%' }"></view>
        </view>
        <text class="calorie-num">{{ totalCalorie }} <text class="kcal">kcal</text></text>
      </view>
      <view class="calorie-breakdown">
        <view class="breakdown-item" v-for="m in mealTypeSummary" :key="m.type">
          <text class="b-icon">{{ m.icon }}</text>
          <text class="b-name">{{ m.name }}</text>
          <text class="b-cal">{{ m.calorie }} kcal</text>
        </view>
      </view>
    </view>

    <!-- 分餐打卡 -->
    <view class="meal-section" v-for="mealType in mealTypes" :key="mealType.type">
      <view class="meal-header">
        <view class="meal-left">
          <text class="meal-type-icon">{{ mealType.icon }}</text>
          <text class="meal-type-name">{{ mealType.name }}</text>
          <text class="meal-type-cal">{{ getMealCalorie(mealType.type) }} kcal</text>
        </view>
        <view class="add-btn" @click="openAddModal(mealType.type)">
          <text>+ 添加</text>
        </view>
      </view>

      <view class="meal-items" v-if="getMealItems(mealType.type).length > 0">
        <view
          class="meal-food-item"
          v-for="item in getMealItems(mealType.type)"
          :key="item.id"
        >
          <view class="food-left">
            <text class="food-name">{{ item.foodName }}</text>
            <text class="food-amount">{{ item.amount }}</text>
          </view>
          <view class="food-right">
            <text class="food-cal">{{ item.calorieEstimate }} kcal</text>
            <text class="food-del" @click="deleteItem(item.id)">×</text>
          </view>
        </view>
      </view>
      <view v-else class="meal-empty">
        <text>点击 + 添加{{ mealType.name }}食物</text>
      </view>
    </view>

    <!-- 添加食物弹窗 -->
    <view class="modal-mask" v-if="showAddModal" @click.self="closeModal">
      <view class="modal-panel">
        <text class="modal-title">添加食物 - {{ currentMealTypeName }}</text>
        <view class="modal-form">
          <view class="modal-item">
            <text class="modal-label">食物名称</text>
            <input class="modal-input" v-model="addForm.foodName" placeholder="如：白米饭、鸡胸肉" />
          </view>
          <view class="modal-item">
            <text class="modal-label">数量/分量</text>
            <input class="modal-input" v-model="addForm.amount" placeholder="如：200g、1碗" />
          </view>
          <view class="modal-item">
            <text class="modal-label">热量估算(kcal)</text>
            <input class="modal-input" type="number" v-model="addForm.calorieEstimate" placeholder="可选，输入估算热量" />
          </view>
          <view class="modal-item">
            <text class="modal-label">图片（可选）</text>
            <view class="img-picker" @click="chooseImage">
              <image v-if="addForm.imageUrl" :src="addForm.imageUrl" mode="aspectFill" class="preview-img" />
              <text v-else class="img-plus">📷 拍照/选图</text>
            </view>
          </view>
        </view>
        <view class="modal-actions">
          <button class="modal-cancel" @click="closeModal">取消</button>
          <button class="modal-confirm gradient-bg" @click="handleAddFood" :loading="addLoading">
            确认添加
          </button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted } from 'vue'
import { useMealStore } from '../../store/meal'
import { useUserStore } from '../../store/user'
import { today } from '../../utils/date'

const mealStore = useMealStore()
const userStore = useUserStore()

const todayStr = today()
const selectedDate = ref(todayStr)
const showAddModal = ref(false)
const addLoading = ref(false)
const currentMealType = ref(1)

const addForm = reactive({
  foodName: '',
  amount: '',
  calorieEstimate: '',
  imageUrl: ''
})

const calorieGoal = computed(() => userStore.userInfo?.calorieGoal || 1400)

const mealTypes = [
  { type: 1, name: '早餐', icon: '🌅' },
  { type: 2, name: '午餐', icon: '☀️' },
  { type: 3, name: '晚餐', icon: '🌙' },
  { type: 4, name: '加餐', icon: '🍎' }
]

const currentMealTypeName = computed(
  () => mealTypes.find(m => m.type === currentMealType.value)?.name || ''
)

const todayMeals = computed(() => mealStore.todayMeals)

const totalCalorie = computed(() =>
  todayMeals.value.reduce((s, m) => s + (m.calorieEstimate || 0), 0)
)

const caloriePercent = computed(() =>
  Math.min((totalCalorie.value / calorieGoal.value) * 100, 100)
)

const mealTypeSummary = computed(() =>
  mealTypes.map(mt => ({
    ...mt,
    calorie: getMealCalorie(mt.type)
  }))
)

function getMealItems(type: number) {
  return todayMeals.value.filter(m => m.mealType === type)
}

function getMealCalorie(type: number) {
  return getMealItems(type).reduce((s, m) => s + (m.calorieEstimate || 0), 0)
}

function prevDay() {
  const d = new Date(selectedDate.value)
  d.setDate(d.getDate() - 1)
  selectedDate.value = d.toISOString().substring(0, 10)
  reload()
}

function nextDay() {
  if (selectedDate.value >= todayStr) return
  const d = new Date(selectedDate.value)
  d.setDate(d.getDate() + 1)
  selectedDate.value = d.toISOString().substring(0, 10)
  reload()
}

function onDateChange(e: any) {
  selectedDate.value = e.detail.value
  reload()
}

function reload() {
  mealStore.loadTodayMeals(selectedDate.value)
}

function openAddModal(type: number) {
  currentMealType.value = type
  addForm.foodName = ''
  addForm.amount = ''
  addForm.calorieEstimate = ''
  addForm.imageUrl = ''
  showAddModal.value = true
}

function closeModal() {
  showAddModal.value = false
}

function chooseImage() {
  uni.chooseImage({
    count: 1,
    success: (res) => {
      addForm.imageUrl = res.tempFilePaths[0]
    }
  })
}

async function handleAddFood() {
  if (!addForm.foodName) {
    uni.showToast({ title: '请输入食物名称', icon: 'none' })
    return
  }
  addLoading.value = true
  try {
    await mealStore.addMealRecord({
      mealDate: selectedDate.value,
      mealType: currentMealType.value,
      foodName: addForm.foodName,
      amount: addForm.amount,
      calorieEstimate: addForm.calorieEstimate ? Number(addForm.calorieEstimate) : 0,
      imageUrl: addForm.imageUrl || undefined
    })
    uni.showToast({ title: '添加成功', icon: 'success' })
    closeModal()
  } catch (e: any) {
    uni.showToast({ title: e.message || '添加失败', icon: 'none' })
  } finally {
    addLoading.value = false
  }
}

async function deleteItem(id: number) {
  uni.showModal({
    title: '提示',
    content: '确定删除该条记录？',
    success: async (res) => {
      if (res.confirm) {
        await mealStore.deleteMealRecord(id)
        uni.showToast({ title: '已删除', icon: 'success' })
      }
    }
  })
}

onMounted(async () => {
  await mealStore.loadTodayMeals(selectedDate.value)
})
</script>

<style lang="scss" scoped>
@import '../../styles/variables.scss';

.meal-record-container {
  min-height: 100vh;
  background: $bg-color;
  padding-bottom: 120rpx;

  .date-bar {
    display: flex;
    align-items: center;
    justify-content: center;
    background: #fff;
    padding: 24rpx 40rpx;
    gap: 40rpx;
    border-bottom: 1rpx solid $border-color;

    .date-arrow {
      font-size: 48rpx;
      color: $primary-color;
      padding: 0 10rpx;

      &.disabled { color: $text-placeholder; }
    }

    .date-display {
      display: flex;
      align-items: center;
      gap: 16rpx;
      font-size: $font-size-md;
      font-weight: 500;
      color: $text-primary;

      .today-badge {
        background: $primary-color;
        color: #fff;
        font-size: 22rpx;
        padding: 4rpx 12rpx;
        border-radius: 20rpx;
      }
    }
  }

  .calorie-card {
    margin: 24rpx 30rpx;
    padding: 36rpx;

    .calorie-header {
      display: flex;
      justify-content: space-between;
      margin-bottom: 20rpx;

      .calorie-title {
        font-size: $font-size-md;
        font-weight: 600;
        color: $text-primary;
      }

      .calorie-goal {
        font-size: $font-size-sm;
        color: $text-secondary;
      }
    }

    .calorie-bar-wrap {
      display: flex;
      align-items: center;
      gap: 20rpx;
      margin-bottom: 24rpx;

      .calorie-bar {
        flex: 1;
        height: 16rpx;
        background: $border-color;
        border-radius: 8rpx;
        overflow: hidden;

        .calorie-fill {
          height: 100%;
          background: linear-gradient(90deg, #667eea, #764ba2);
          border-radius: 8rpx;
          transition: width 0.3s ease;
        }
      }

      .calorie-num {
        font-size: $font-size-md;
        font-weight: bold;
        color: $primary-color;
        white-space: nowrap;

        .kcal {
          font-size: $font-size-sm;
          font-weight: normal;
          color: $text-secondary;
        }
      }
    }

    .calorie-breakdown {
      display: flex;
      justify-content: space-around;

      .breakdown-item {
        text-align: center;

        .b-icon {
          display: block;
          font-size: 32rpx;
          margin-bottom: 6rpx;
        }

        .b-name {
          display: block;
          font-size: $font-size-sm;
          color: $text-secondary;
          margin-bottom: 4rpx;
        }

        .b-cal {
          font-size: $font-size-sm;
          font-weight: 500;
          color: $text-primary;
        }
      }
    }
  }

  .meal-section {
    margin: 0 30rpx 24rpx;
    background: #fff;
    border-radius: $border-radius-lg;
    overflow: hidden;
    box-shadow: $shadow-sm;

    .meal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 28rpx 30rpx;
      border-bottom: 1rpx solid $border-color;

      .meal-left {
        display: flex;
        align-items: center;
        gap: 16rpx;

        .meal-type-icon { font-size: 36rpx; }

        .meal-type-name {
          font-size: $font-size-md;
          font-weight: 600;
          color: $text-primary;
        }

        .meal-type-cal {
          font-size: $font-size-sm;
          color: $text-secondary;
        }
      }

      .add-btn {
        background: rgba(102, 126, 234, 0.1);
        color: $primary-color;
        font-size: $font-size-sm;
        padding: 12rpx 28rpx;
        border-radius: 30rpx;
        font-weight: 500;
      }
    }

    .meal-items {
      padding: 0 30rpx;

      .meal-food-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 20rpx 0;
        border-bottom: 1rpx solid $border-color;

        &:last-child { border-bottom: none; }

        .food-left {
          .food-name {
            display: block;
            font-size: $font-size-md;
            color: $text-primary;
            margin-bottom: 4rpx;
          }

          .food-amount {
            font-size: $font-size-sm;
            color: $text-placeholder;
          }
        }

        .food-right {
          display: flex;
          align-items: center;
          gap: 20rpx;

          .food-cal {
            font-size: $font-size-sm;
            color: $primary-color;
            font-weight: 500;
          }

          .food-del {
            color: #ff4d4f;
            font-size: 36rpx;
            line-height: 1;
          }
        }
      }
    }

    .meal-empty {
      padding: 30rpx;
      text-align: center;
      font-size: $font-size-sm;
      color: $text-placeholder;
    }
  }

  /* 弹窗 */
  .modal-mask {
    position: fixed;
    inset: 0;
    background: rgba(0,0,0,0.5);
    z-index: 999;
    display: flex;
    align-items: flex-end;

    .modal-panel {
      width: 100%;
      background: #fff;
      border-radius: $border-radius-lg $border-radius-lg 0 0;
      padding: 40rpx 40rpx 80rpx;

      .modal-title {
        display: block;
        font-size: $font-size-md;
        font-weight: 600;
        color: $text-primary;
        text-align: center;
        margin-bottom: 36rpx;
      }

      .modal-form {
        .modal-item {
          margin-bottom: 28rpx;

          .modal-label {
            display: block;
            font-size: $font-size-sm;
            color: $text-secondary;
            margin-bottom: 10rpx;
          }

          .modal-input {
            width: 100%;
            height: 86rpx;
            border: 2rpx solid $border-color;
            border-radius: $border-radius;
            padding: 0 24rpx;
            font-size: $font-size-md;
            color: $text-primary;
            background: $bg-color;
            box-sizing: border-box;
          }

          .img-picker {
            width: 160rpx;
            height: 160rpx;
            border: 2rpx dashed $border-color;
            border-radius: $border-radius;
            display: flex;
            align-items: center;
            justify-content: center;
            overflow: hidden;

            .preview-img { width: 100%; height: 100%; }

            .img-plus {
              font-size: $font-size-sm;
              color: $text-placeholder;
              text-align: center;
            }
          }
        }
      }

      .modal-actions {
        display: flex;
        gap: 20rpx;
        margin-top: 10rpx;

        .modal-cancel, .modal-confirm {
          flex: 1;
          height: 96rpx;
          border-radius: $border-radius;
          border: none;
          font-size: $font-size-md;
          font-weight: 500;
        }

        .modal-cancel {
          background: $bg-color;
          color: $text-secondary;
        }

        .modal-confirm {
          color: #fff;
        }
      }
    }
  }
}
</style>
