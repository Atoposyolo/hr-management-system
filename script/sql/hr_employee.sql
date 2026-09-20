-- ----------------------------
-- 员工表 hr_employee
-- 人事模块业务表，在导入 ry_vue.sql 之后执行
-- ----------------------------
drop table if exists hr_employee;
create table hr_employee (
  id            bigint(20)   not null                   comment '主键（雪花ID）',
  emp_no        varchar(30)  not null                   comment '工号',
  emp_name      varchar(30)  not null                   comment '员工姓名',
  gender        char(1)      default '2'                comment '性别（0男 1女 2未知）',
  dept_id       bigint(20)   default null               comment '部门id',
  post_id       bigint(20)   default null               comment '岗位id',
  phone         varchar(20)  default ''                 comment '手机号码',
  email         varchar(50)  default ''                 comment '邮箱',
  id_card       varchar(20)  default ''                 comment '身份证号',
  education     char(1)      default null               comment '学历（1大专 2本科 3硕士 4博士 5其他）',
  entry_date    datetime     default null               comment '入职日期',
  status        char(1)      default '0'                comment '在职状态（0在职 1离职）',
  remark        varchar(500) default null               comment '备注',
  create_dept   bigint(20)   default null               comment '创建部门',
  create_by     bigint(20)   default null               comment '创建者',
  create_time   datetime     default null               comment '创建时间',
  update_by     bigint(20)   default null               comment '更新者',
  update_time   datetime     default null               comment '更新时间',
  del_flag      int(1)       default 0                  comment '删除标志（0代表存在，其他值代表删除）',
  primary key (id) using btree,
  -- 工号全局唯一：工号是员工在企业内的终身标识，员工离职（逻辑删除）后工号也不复用
  -- Service 层校验之外由数据库唯一索引兜底，防止并发“先查后插”产生重复工号
  unique key uk_emp_no (emp_no),
  key idx_hr_employee_dept_id (dept_id),
  key idx_hr_employee_post_id (post_id)
) engine=innodb comment='员工表';
