<script setup lang="ts">
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'
import { useUserStore } from './store/user'

const userStore = useUserStore()

onLaunch(() => {
  // 应用启动，检查登录状态
  userStore.initUserState()
})

onShow(() => {
  console.log('App Show')
})

onHide(() => {
  console.log('App Hide')
})
</script>

<style lang="scss">
@import './styles/index.scss';
</style>
