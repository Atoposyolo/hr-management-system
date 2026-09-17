<template>
  <div class="app-container p-2" v-loading="loading">
    <el-card shadow="never" class="mb-2">
      <div style="display:flex;justify-content:space-between;align-items:center;">
        <span style="font-size:16px;font-weight:600;">员工数据统计看板</span>
        <el-button type="primary" icon="Refresh" @click="getStat">刷新统计</el-button>
      </div>
    </el-card>

    <el-row :gutter="16">
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="mb-2">
          <div style="color:#909399;font-size:13px;">员工总数</div>
          <div style="font-size:28px;font-weight:700;color:#303133;">{{ stat.total ?? 0 }}<span style="font-size:13px;font-weight:400;color:#909399;"> 人</span></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="mb-2">
          <div style="color:#909399;font-size:13px;">在职人数</div>
          <div style="font-size:28px;font-weight:700;color:#67c23a;">{{ stat.onJobCount ?? 0 }}<span style="font-size:13px;font-weight:400;color:#909399;"> 人</span></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="mb-2">
          <div style="color:#909399;font-size:13px;">离职人数</div>
          <div style="font-size:28px;font-weight:700;color:#f56c6c;">{{ stat.leaveCount ?? 0 }}<span style="font-size:13px;font-weight:400;color:#909399;"> 人</span></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :sm="12">
        <el-card shadow="never" class="mb-2">
          <template #header><span>按在职状态分布</span></template>
          <div v-for="item in stat.statusStats" :key="item.label" style="margin-bottom:14px;">
            <div style="display:flex;justify-content:space-between;margin-bottom:4px;font-size:13px;">
              <dict-tag :options="hr_employee_status" :value="item.label" />
              <span>{{ item.value }} 人（{{ percent(item.value) }}%）</span>
            </div>
            <el-progress :percentage="percent(item.value)" :stroke-width="14" />
          </div>
          <el-empty v-if="!stat.statusStats || stat.statusStats.length===0" description="暂无数据" :image-size="60" />
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12">
        <el-card shadow="never" class="mb-2">
          <template #header><span>按学历分布</span></template>
          <div v-for="item in stat.educationStats" :key="item.label" style="margin-bottom:14px;">
            <div style="display:flex;justify-content:space-between;margin-bottom:4px;font-size:13px;">
              <dict-tag :options="hr_education" :value="item.label" />
              <span>{{ item.value }} 人（{{ percent(item.value) }}%）</span>
            </div>
            <el-progress :percentage="percent(item.value)" :stroke-width="14" />
          </div>
          <el-empty v-if="!stat.educationStats || stat.educationStats.length===0" description="暂无数据" :image-size="60" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="EmployeeStatistics" lang="ts">
import { getEmployeeStatistics } from '@/api/system/employee';
import { useDict } from '@/utils/dict';

const { hr_employee_status, hr_education } = toRefs<any>(useDict('hr_employee_status', 'hr_education'));

const loading = ref(false);
const stat = ref<any>({
  total: 0,
  onJobCount: 0,
  leaveCount: 0,
  statusStats: [],
  educationStats: []
});

/** 拉取统计数据 */
const getStat = async () => {
  loading.value = true;
  try {
    const res = await getEmployeeStatistics();
    stat.value = res.data;
  } finally {
    loading.value = false;
  }
};

/** 计算某项占总数的百分比 */
const percent = (value: number) => {
  const total = Number(stat.value.total) || 0;
  if (!total || !value) return 0;
  return Math.round((Number(value) / total) * 100);
};

onMounted(() => {
  getStat();
});
</script>
