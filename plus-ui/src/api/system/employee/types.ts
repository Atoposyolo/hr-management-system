export interface EmployeeVO {
  /**
   * 工号
   */
  empNo: string;
  /**
   * 员工姓名
   */
  empName: string;
  /**
   * 性别（0男 1女 2未知）
   */
  gender: string;
  /**
   * 部门id
   */
  deptId: string | number;
  /**
   * 手机号码
   */
  phone: string;
  /**
   * 学历（1大专 2本科 3硕士 4博士 5其他）
   */
  education: string;
  /**
   * 入职日期
   */
  entryDate: string;
  /**
   * 状态（0在职 1离职）
   */
  status: boolean;
  /**
   * 创建时间
   */
  createTime: string;
}

export interface EmployeeForm extends BaseEntity {
  /**
   * 工号
   */
  empNo?: string;
  /**
   * 员工姓名
   */
  empName?: string;
  /**
   * 性别（0男 1女 2未知）
   */
  gender?: string;
  /**
   * 部门id
   */
  deptId?: string | number;
  /**
   * 岗位id
   */
  postId?: string | number;
  /**
   * 手机号码
   */
  phone?: string;
  /**
   * 邮箱
   */
  email?: string;
  /**
   * 身份证号
   */
  idCard?: string | number;
  /**
   * 学历（1大专 2本科 3硕士 4博士 5其他）
   */
  education?: string;
  /**
   * 入职日期
   */
  entryDate?: string;
  /**
   * 状态（0在职 1离职）
   */
  status?: boolean;
  /**
   * 备注
   */
  remark?: string;
}

export interface EmployeeQuery extends PageQuery {
  /**
   * 工号
   */
  empNo?: string;
  /**
   * 员工姓名
   */
  empName?: string;
  /**
   * 手机号码
   */
  phone?: string;
  /**
   * 部门id
   */
  deptId?: string | number;
  /**
   * 状态（0在职 1离职）
   */
  status?: string;
  /**
   * 入职日期
   */
  entryDate?: string;
  /**
   * 日期范围参数
   */
  params?: any;
}
