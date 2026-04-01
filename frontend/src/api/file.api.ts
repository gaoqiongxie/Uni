import http from '../utils/request'

export interface FileVO {
  id: number
  fileName: string
  fileSize: number
  fileType: string
  url: string
  thumbnailUrl?: string
}

export const fileApi = {
  /**
   * 上传单张图片
   * 注意：这里不能用默认的 JSON Content-Type，需要用 multipart/form-data
   * 所以手动实现 uni.uploadFile
   */
  uploadImage(filePath: string, attachmentType = 1, uploadSource = 2): Promise<FileVO> {
    return new Promise((resolve, reject) => {
      const token = uni.getStorageSync('uni_token') || ''
      const userId = uni.getStorageSync('uni_user_id') || ''

      uni.uploadFile({
        url: 'http://localhost:8080/api/file/upload',
        filePath: filePath,
        name: 'file',
        formData: {
          attachmentType: String(attachmentType),
          uploadSource: String(uploadSource)
        },
        header: {
          'Authorization': `Bearer ${token}`,
          'X-User-Id': String(userId)
        },
        success: (res) => {
          try {
            const response = JSON.parse(res.data)
            if (response.code === 200) {
              resolve(response.data)
            } else {
              uni.showToast({ title: response.message || '上传失败', icon: 'none' })
              reject(new Error(response.message))
            }
          } catch (e) {
            reject(new Error('上传响应解析失败'))
          }
        },
        fail: (err) => {
          uni.showToast({ title: '图片上传失败', icon: 'none' })
          reject(err)
        }
      })
    })
  },

  /**
   * 获取文件URL列表
   */
  getFileUrls(attachmentIds: string): Promise<string[]> {
    return http.get<string[]>('/api/file/urls', { attachmentIds })
  }
}
