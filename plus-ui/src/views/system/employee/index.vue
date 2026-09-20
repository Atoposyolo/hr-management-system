<template>
  <div class="p-2 page-shell system-employee-page">
    <div class="search-wrap">
      <el-card shadow="hover" class="search-panel" :class="{ 'is-collapsed': !showSearch }">
        <template #header>
          <div class="panel-heading search-panel-toggle" @click.stop="showSearch = !showSearch">
            <div><h3>筛选条件</h3></div>
          </div>

        </template>
        <el-form ref="queryFormRef" :model="queryParams" :inline="true" class="query-form">
            <el-form-item label="工号" prop="empNo">
              <el-input v-model="queryParams.empNo" placeholder="请输入工号" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="员工姓名" prop="empName">
              <el-input v-model="queryParams.empName" placeholder="请输入员工姓名" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="手机号码" prop="phone">
              <el-input v-model="queryParams.phone" placeholder="请输入手机号码" clearable @keyup.enter="handleQuery" />
            </el-form-item>
          <el-form-item label="部门" prop="deptId">
            <el-tree-select
              v-model="queryParams.deptId"
              :data="deptOptions"
              :props="{ value: 'id', label: 'label', children: 'children' }"
              value-key="id"
              placeholder="请选择部门"
              clearable
              check-strictly
            />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
              <el-option
                v-for="dict in hr_employee_status"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="入职日期" style="width: 308px">
              <el-date-picker
                v-model="dateRangeEntryDate"
                value-format="YYYY-MM-DD HH:mm:ss"
                type="daterange"
                range-separator="-"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 1, 1, 23, 59, 59)]"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
              <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
        </el-form>
      </el-card>
    </div>

    <el-card shadow="hover" class="table-panel">
      <template #header>
        <div class="toolbar-shell">
          <div class="table-heading">
            <h3>员工管理列表</h3>
          </div>
          <div class="toolbar-actions">
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['system:employee:add']">新增</el-button>
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['system:employee:edit']">修改</el-button>
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['system:employee:remove']">删除</el-button>
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['system:employee:export']">导出</el-button>
            <el-button type="info" plain icon="Document" :disabled="single" @click="handleAiGen">AI生成文档</el-button>
            <right-toolbar v-model:show-search="showSearch" :search="false" @query-table="getList"></right-toolbar>
          </div>
        </div>
      </template>

      <el-table v-loading="loading" border class="data-table" :data="employeeList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="工号" align="center" prop="empNo" />
        <el-table-column label="员工姓名" align="center" prop="empName" />
        <el-table-column label="性别" align="center" prop="gender">
          <template #default="scope">
            <dict-tag :options="sys_user_gender" :value="scope.row.gender"/>
          </template>
        </el-table-column>
        <el-table-column label="部门" align="center" prop="deptName" />
        <el-table-column label="手机号码" align="center" prop="phone" />
        <el-table-column label="学历" align="center" prop="education">
          <template #default="scope">
            <dict-tag :options="hr_education" :value="scope.row.education" />
          </template>
        </el-table-column>
        <el-table-column label="入职日期" align="center" prop="entryDate" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.entryDate, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="hr_employee_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:employee:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:employee:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改员工管理对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="employeeFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="工号" prop="empNo">
          <el-input v-model="form.empNo" placeholder="请输入工号" />
        </el-form-item>
        <el-form-item label="员工姓名" prop="empName">
          <el-input v-model="form.empName" placeholder="请输入员工姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio
              v-for="dict in sys_user_gender"
              :key="dict.value"
              :value="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="部门" prop="deptId">
          <el-tree-select
            v-model="form.deptId"
            :data="deptOptions"
            :props="{ value: 'id', label: 'label', children: 'children' }"
            value-key="id"
            placeholder="请选择部门"
            check-strictly
            @change="handleDeptChange"
          />
        </el-form-item>
        <el-form-item label="岗位" prop="postId">
          <el-select v-model="form.postId" placeholder="请选择岗位" clearable style="width: 100%">
            <el-option
              v-for="item in postOptions"
              :key="item.postId"
              :label="item.postName"
              :value="item.postId"
              :disabled="item.status === '1'"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号码" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="form.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="学历" prop="education">
          <el-select v-model="form.education" placeholder="请选择学历">
            <el-option v-for="dict in hr_education" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="入职日期" prop="entryDate">
          <el-date-picker clearable
            v-model="form.entryDate"
                          type="date"
                          value-format="YYYY-MM-DD"
                          placeholder="请选择入职日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option v-for="dict in hr_employee_status" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- AI生成文档弹窗 -->
    <el-dialog v-model="aiDialogVisible" title="AI 生成人事文档" width="640px" append-to-body>
      <el-form label-width="90px">
        <el-form-item label="文档类型">
          <el-radio-group v-model="aiDocType">
            <el-radio value="cert">在职证明</el-radio>
            <el-radio value="comment">转正评语</el-radio>
            <el-radio value="jd">招聘JD</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div v-loading="aiLoading" style="min-height:160px;background:#f8f8f8;border-radius:6px;padding:12px;white-space:pre-wrap;line-height:1.8;">
        {{ aiResult || '点击下方按钮，AI 将根据该员工信息自动生成文档。' }}
      </div>
      <template #footer>
        <el-button @click="aiDialogVisible = false">关 闭</el-button>
        <el-button type="primary" :loading="aiLoading" @click="handleAiSubmit">生成文档</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Employee" lang="ts">
import {
  addEmployee,
  delEmployee,
  getEmployee,
  listEmployee,
  updateEmployee,
  genHrAiDoc
} from '@/api/system/employee';
import { EmployeeForm, EmployeeQuery, EmployeeVO } from '@/api/system/employee/types';
import { useLoading } from '@/hooks/async/useLoading';
import { useFormDialog } from '@/hooks/dialog/useFormDialog';
import { useDateRangeQuery } from '@/hooks/form/useDateRangeQuery';
import { useSearchReset } from '@/hooks/form/useSearchReset';
import { useSearchToggle } from '@/hooks/form/useSearchToggle';
import { useTableSelection } from '@/hooks/table/useTableSelection';
import { useDict } from '@/utils/dict';
import { deptTreeSelect } from '@/api/system/user';
import { listPost, optionselect } from '@/api/system/post';
import type { PostVO } from '@/api/system/post/types';
import { parseTime } from '@/utils/ruoyi';
import modal from '@/plugins/modal';
import { download as requestDownload } from '@/utils/request';

const { sys_user_gender, hr_education, hr_employee_status } = toRefs<any>(useDict('sys_user_gender', 'hr_education', 'hr_employee_status'));

const deptOptions = ref<any[]>([]);
/** 查询部门下拉树 */
const getDeptTree = async () => {
  const res = await deptTreeSelect();
  deptOptions.value = res.data;
};

const employeeList = ref<EmployeeVO[]>([]);
const buttonLoading = ref(false);
const { loading, withLoading } = useLoading(true);
const { showSearch } = useSearchToggle();
const total = ref(0);
const {
  dateRange: dateRangeEntryDate,
  applyDateRange: applyEntryDateDateRange,
  resetDateRange: resetEntryDateDateRange
} = useDateRangeQuery('EntryDate');

const queryFormRef = ref<ElFormInstance>();
const employeeFormRef = ref<ElFormInstance>();

const initFormData: EmployeeForm = {
  empNo: undefined,
  empName: undefined,
  gender: undefined,
  deptId: undefined,
  postId: undefined,
  phone: undefined,
  email: undefined,
  idCard: undefined,
  education: undefined,
  entryDate: undefined,
  status: undefined,
  remark: undefined,
}
const data = reactive<PageData<EmployeeForm, EmployeeQuery>>({
  form: { ...initFormData },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    empNo: undefined,
    empName: undefined,
    phone: undefined,
    deptId: undefined,
    status: undefined,
    params: {
      entryDate: undefined,
    }
  },
  rules: {
empNo: [
      { required: true, message: "工号不能为空", trigger: "blur" }
    ],
empName: [
      { required: true, message: "员工姓名不能为空", trigger: "blur" }
    ],
deptId: [
      { required: true, message: "部门id不能为空", trigger: "change" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);
const { ids, single, multiple, handleSelectionChange } = useTableSelection<EmployeeVO>(item => item.id);
const { dialog, resetForm: reset, openDialog, showDialog, closeDialog } = useFormDialog({
  form,
  formRef: employeeFormRef,
  initialFormData: initFormData
});

/** 查询员工管理列表 */
const getList = async () => {
  await withLoading(async () => {
    let params = queryParams.value;
params = applyEntryDateDateRange(params);
    const res = await listEmployee(params);
    employeeList.value = res.data?.rows;
    total.value = res.data?.total;
  });
};

/** 取消按钮 */
const cancel = () => {
  reset();
  closeDialog();
};

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
};

const { resetQuery } = useSearchReset({
  queryFormRef,
  queryParams,
  pageNumKey: 'pageNum',
  pageSizeKey: 'pageSize',
  initialPageSize: 10,
  resetExtras: () => {
resetEntryDateDateRange();
  },
  afterReset: () => {
    handleQuery();
  }
});

/** 新增按钮操作 */
const handleAdd = () => {
  openDialog('添加员工管理');
};

/** 修改按钮操作 */
const handleUpdate = async (row?: Partial<EmployeeVO>) => {
  reset();
  const _id = row?.id || ids.value[0];
  const res = await getEmployee(_id);
  Object.assign(form.value, res.data);
  showDialog('修改员工管理');
};

/** 提交按钮 */
const submitForm = () => {
  employeeFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateEmployee(form.value).finally(() => (buttonLoading.value = false));
      } else {
        await addEmployee(form.value).finally(() => (buttonLoading.value = false));
      }
      modal.msgSuccess('操作成功');
      closeDialog();
      await getList();
    }
  });
};

/** 删除按钮操作 */
const handleDelete = async (row?: Partial<EmployeeVO>) => {
  const _ids = row?.id || ids.value;
  await modal.confirm('是否确认删除员工管理编号为"' + _ids + '"的数据项？');
  await delEmployee(_ids);
  modal.msgSuccess('删除成功');
  await getList();
};

/** 导出按钮操作 */
const handleExport = () => {
  requestDownload(
    'system/employee/export',
    {
      ...queryParams.value
    },
    `employee_${new Date().getTime()}.xlsx`
  );
};


const postOptions = ref<PostVO[]>([]);
/** 选择部门后联动加载该部门岗位；清空部门则恢复全部岗位 */
const handleDeptChange = async (deptId: string | number) => {
  if (!deptId) {
    const res = await listPost({ pageNum: 1, pageSize: 100 } as any);
    postOptions.value = res.data.rows;
  } else {
    const res = await optionselect(deptId);
    postOptions.value = res.data;
  }
  form.value.postId = undefined;
};
onMounted(() => {
  getList();
  getDeptTree();
  listPost({ pageNum: 1, pageSize: 100 } as any).then((res) => (postOptions.value = res.data.rows));
});
// AI生成文档
const aiDialogVisible = ref(false);
const aiDocType = ref('cert');
const aiLoading = ref(false);
const aiResult = ref('');
const aiEmpId = ref<any>(null);

const handleAiGen = () => {
  if (ids.value.length === 0) {
    modal.msgWarning('请先在列表中勾选一名员工');
    return;
  }
  aiEmpId.value = ids.value[0];
  aiResult.value = '';
  aiDialogVisible.value = true;
};

const handleAiSubmit = async () => {
  aiLoading.value = true;
  try {
    const res: any = await genHrAiDoc(aiEmpId.value, aiDocType.value);
    aiResult.value = res.data || 'AI返回为空，请稍后重试';
  } catch (e: any) {
    aiResult.value = 'AI生成失败：' + (e?.message || '未知错误');
  } finally {
    aiLoading.value = false;
  }
};
</script>
