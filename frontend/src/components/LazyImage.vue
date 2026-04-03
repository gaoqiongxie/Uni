<template>
  <image
    ref="imageRef"
    class="lazy-image"
    :class="{ loaded: isLoaded }"
    :src="currentSrc"
    :mode="mode"
    :style="imageStyle"
    @load="onLoad"
    @error="onError"
  />
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { getLazyLoader } from '@/utils/lazy-load'

const props = defineProps<{
  src: string
  placeholder?: string
  mode?: string
  width?: string
  height?: string
}>()

const emit = defineEmits<{
  load: []
  error: []
}>()

const imageRef = ref<HTMLElement>()
const isLoaded = ref(false)
const isError = ref(false)

const currentSrc = computed(() => {
  if (isError.value) {
    return '/static/default-image.png'
  }
  // 先显示占位图，懒加载后替换
  return props.placeholder || '/static/default-image.png'
})

const imageStyle = computed(() => {
  return {
    width: props.width || '100%',
    height: props.height || 'auto'
  }
})

function onLoad() {
  isLoaded.value = true
  emit('load')
}

function onError() {
  isError.value = true
  emit('error')
}

onMounted(() => {
  if (imageRef.value && props.src) {
    getLazyLoader().observe(imageRef.value, props.src)
  }
})

onUnmounted(() => {
  if (imageRef.value) {
    getLazyLoader().unobserve(imageRef.value)
  }
})

// 监听 src 变化
watch(() => props.src, (newSrc) => {
  if (imageRef.value && newSrc) {
    isLoaded.value = false
    getLazyLoader().observe(imageRef.value, newSrc)
  }
})
</script>

<style scoped>
.lazy-image {
  opacity: 0;
  transition: opacity 0.3s ease;
}

.lazy-image.loaded {
  opacity: 1;
}
</style>
