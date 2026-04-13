/**
 * 身体成分类型定义
 * Sprint 11 - 周一体脂率 & 身体成分追踪
 */

/** 身体成分记录 */
export interface BodyComposition {
  id?: number;
  userId?: number;
  recordDate: string;
  height: number;
  weight: number;
  bmi: number;
  bodyFatRate: number;
  fatLevel: 'lean' | 'normal' | 'high' | 'obese';
  fatLevelText: string;
  muscleMass: number;
  boneMass: number;
  waterRate: number;
  visceralFat: number;
  waist: number;
  neck: number;
  hip: number;
  gender: 'male' | 'female';
  createTime?: string;
}

/** 身体成分录入请求 */
export interface BodyCompositionDTO {
  recordDate?: string;
  height: number;
  weight: number;
  waist?: number;
  neck?: number;
  hip?: number;
  gender: 'male' | 'female';
}

/** 体脂等级配置 */
export const FAT_LEVEL_CONFIG = {
  lean: {
    label: '偏瘦',
    color: '#3498db',
    bgColor: '#e8f4fc'
  },
  normal: {
    label: '正常',
    color: '#27ae60',
    bgColor: '#e8f6ed'
  },
  high: {
    label: '偏高',
    color: '#f39c12',
    bgColor: '#fef9e7'
  },
  obese: {
    label: '肥胖',
    color: '#e74c3c',
    bgColor: '#fdeaea'
  }
};

/** 身体成分雷达图数据 */
export interface BodyRadarData {
  label: string;
  value: number;
  max: number;
  unit: string;
}
