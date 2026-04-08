<script setup lang="ts">
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'
import { useUserStore } from './store/user'
import { useThemeStore } from './stores/theme'

const userStore = useUserStore()
const themeStore = useThemeStore()

onLaunch(() => {
  // 应用启动，检查登录状态
  userStore.initUserState()
  // 加载主题设置
  themeStore.loadTheme()
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
@import './styles/theme.scss';
</style>
