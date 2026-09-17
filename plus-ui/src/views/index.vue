<template>
  <div class="home">
    <section class="hero-panel">
      <div class="hero-copy">
        <h1>欢迎使用企业人事管理系统</h1>
        <p>
          面向企业内部的人事信息管理平台，覆盖员工档案、组织架构、岗位配置与数据字典等模块，
          基于 Spring Boot、MyBatis-Plus、Sa-Token、Redis 与 Vue 3 构建，支持细粒度权限控制与数据导出。
        </p>
      </div>
    </section>
    <div class="card-grid">
      <section class="module-card" v-for="m in modules" :key="m.title">
        <div class="module-icon">{{ m.icon }}</div>
        <h3>{{ m.title }}</h3>
        <p>{{ m.desc }}</p>
      </section>
    </div>
  </div>
</template>

<script setup name="Index" lang="ts">
const modules = [
  { icon: '👤', title: '员工档案', desc: '维护员工工号、姓名、性别、部门、岗位、联系方式、学历与入职日期等档案信息，支持条件查询与 Excel 导出。' },
  { icon: '🏢', title: '组织架构', desc: '以树形结构管理公司多级部门层级，员工归属到具体部门，组织关系清晰可查。' },
  { icon: '🔐', title: '岗位与权限', desc: '基于 RBAC 模型配置角色、菜单与按钮级权限，员工关联岗位，实现细粒度访问控制。' },
  { icon: '📖', title: '数据字典', desc: '统一维护员工状态、学历、性别等枚举项，下拉选项与列表标签由字典动态渲染。' }
];
</script>

<style lang="scss" scoped>
.home {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.hero-panel {
  border-radius: 28px;
  border: 1px solid var(--app-surface-border);
  background: radial-gradient(circle at top left, rgba(53, 109, 255, 0.16), transparent 30%), var(--app-surface-bg);
  box-shadow: var(--app-shadow-sm);
  padding: 34px 30px;
}
.hero-copy {
  h1 {
    margin: 0 0 14px;
    font-size: clamp(26px, 3.4vw, 40px);
    line-height: 1.1;
    letter-spacing: -0.03em;
    color: var(--app-text-title);
  }
  p {
    margin: 0;
    max-width: 820px;
    color: var(--app-text-muted);
    font-size: 15px;
    line-height: 1.9;
  }
}
.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 20px;
}
.module-card {
  border-radius: 24px;
  border: 1px solid var(--app-surface-border);
  background: var(--app-surface-bg);
  box-shadow: var(--app-shadow-sm);
  padding: 24px;
  transition:
    transform 0.25s ease,
    border-color 0.25s ease;
  &:hover {
    transform: translateY(-3px);
    border-color: rgba(53, 109, 255, 0.25);
  }
  .module-icon {
    font-size: 30px;
    margin-bottom: 14px;
  }
  h3 {
    margin: 0 0 10px;
    font-size: 19px;
    color: var(--app-text-title);
  }
  p {
    margin: 0;
    color: var(--app-text-muted);
    font-size: 14px;
    line-height: 1.8;
  }
}
@media (max-width: 640px) {
  .hero-panel {
    padding: 22px;
    border-radius: 22px;
  }
  .module-card {
    padding: 20px;
  }
}
</style>
