<template>
  <view class="publish-page">
    <!-- 内容输入 -->
    <textarea
      v-model="content"
      class="content-input"
      placeholder="分享你的减脂心得、运动打卡、健康饮食..."
      maxlength="500"
    />
    <text class="word-count">{{ content.length }}/500</text>

    <!-- 图片上传 -->
    <view class="image-section">
      <view
        v-for="(img, index) in images"
        :key="index"
        class="image-item"
      >
        <image class="preview-img" :src="img" mode="aspectFill" />
        <text class="delete-btn" @click="removeImage(index)">×</text>
      </view>
      <view v-if="images.length < 9" class="upload-btn" @click="chooseImage">
        <text class="upload-icon">+</text>
        <text class="upload-text">{{ images.length }}/9</text>
      </view>
    </view>

    <!-- 分享数据 -->
    <view class="share-section">
      <view class="share-title">分享数据</view>
      <view class="share-options">
        <view class="share-option" @click="shareWeight = !shareWeight">
          <text class="checkbox" :class="{ checked: shareWeight }">✓</text>
          <text class="option-text">分享今日体重</text>
        </view>
        <view class="share-option" @click="shareExercise = !shareExercise">
          <text class="checkbox" :class="{ checked: shareExercise }">✓</text>
          <text class="option-text">分享今日运动</text>
        </view>
      </view>
    </view>

    <!-- 发布按钮 -->
    <button
      class="publish-btn"
      :class="{ disabled: !canPublish }"
      :disabled="!canPublish"
      @click="publish"
    >
      发布
    </button>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { publishMoment } from '@/api/social.api'
import { uploadFile } from '@/api/file.api'

const content = ref('')
const images = ref<string[]>([])
const shareWeight = ref(0)
const shareExercise = ref(0)
const isPublishing = ref(false)

const canPublish = computed(() => {
  return content.value.trim().length > 0 && !isPublishing.value
})

function chooseImage() {
  const remainCount = 9 - images.value.length
  uni.chooseImage({
    count: remainCount,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async (res) => {
      const tempFiles = res.tempFiles as { path: string; size: number }[]
      for (const file of tempFiles) {
        try {
          const uploadRes = await uploadFile(file.path, 'moment')
          if (uploadRes.code === 200 && uploadRes.data) {
            images.value.push(uploadRes.data.url)
          }
        } catch (error) {
          uni.showToast({ title: '图片上传失败', icon: 'none' })
        }
      }
    }
  })
}

function removeImage(index: number) {
  images.value.splice(index, 1)
}

async function publish() {
  if (!canPublish.value) return

  isPublishing.value = true
  uni.showLoading({ title: '发布中...' })

  try {
    const res = await publishMoment({
      content: content.value.trim(),
      images: images.value.join(','),
      shareWeight: shareWeight.value,
      shareExercise: shareExercise.value
    })

    if (res.code === 200) {
      uni.showToast({ title: '发布成功', icon: 'success' })
      setTimeout(() => {
        uni.navigateBack()
      }, 1500)
    } else {
      uni.showToast({ title: res.message || '发布失败', icon: 'none' })
    }
  } catch (error) {
    uni.showToast({ title: '发布失败', icon: 'none' })
  } finally {
    isPublishing.value = false
    uni.hideLoading()
  }
}
</script>

<style scoped>
.publish-page {
  min-height: 100vh;
  background: #fff;
  padding: 30rpx;
}

.content-input {
  width: 100%;
  height: 300rpx;
  font-size: 30rpx;
  line-height: 1.6;
  padding: 20rpx 0;
}

.word-count {
  display: block;
  text-align: right;
  font-size: 24rpx;
  color: #999;
  margin-bottom: 30rpx;
}

.image-section {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
  margin-bottom: 40rpx;
}

.image-item {
  position: relative;
  width: 200rpx;
  height: 200rpx;
}

.preview-img {
  width: 100%;
  height: 100%;
  border-radius: 12rpx;
}

.delete-btn {
  position: absolute;
  top: -10rpx;
  right: -10rpx;
  width: 40rpx;
  height: 40rpx;
  background: #ff4757;
  color: #fff;
  border-radius: 50%;
  text-align: center;
  line-height: 40rpx;
  font-size: 28rpx;
}

.upload-btn {
  width: 200rpx;
  height: 200rpx;
  border: 2rpx dashed #ddd;
  border-radius: 12rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.upload-icon {
  font-size: 60rpx;
  color: #999;
  margin-bottom: 10rpx;
}

.upload-text {
  font-size: 24rpx;
  color: #999;
}

.share-section {
  margin-bottom: 60rpx;
}

.share-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 20rpx;
}

.share-options {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.share-option {
  display: flex;
  align-items: center;
  padding: 20rpx;
  background: #f8f9fa;
  border-radius: 12rpx;
}

.checkbox {
  width: 40rpx;
  height: 40rpx;
  border: 2rpx solid #ddd;
  border-radius: 50%;
  margin-right: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  color: transparent;
}

.checkbox.checked {
  background: #667eea;
  border-color: #667eea;
  color: #fff;
}

.option-text {
  font-size: 28rpx;
  color: #333;
}

.publish-btn {
  width: 100%;
  height: 90rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 32rpx;
  font-weight: 600;
  border-radius: 45rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.publish-btn.disabled {
  background: #ccc;
}
</style>
