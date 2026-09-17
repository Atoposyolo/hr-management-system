import type { EmployeeForm, EmployeeQuery, EmployeeVO } from '@/api/system/employee/types';
import type { PageResult } from '@/api/types';
import type { AxiosPromise } from '@/utils/api-types';
import request from '@/utils/request';

/**
 * 查询员工管理列表
 * @param query
 * @returns {*}
 */
export const listEmployee = (query?: EmployeeQuery): AxiosPromise<PageResult<EmployeeVO>> => {
  return request({
    url: '/system/employee/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询员工管理详细
 * @param id
 */
export const getEmployee = (id: string | number): AxiosPromise<EmployeeVO> => {
  return request({
    url: '/system/employee/' + id,
    method: 'get'
  });
};

/**
 * 新增员工管理
 * @param data
 */
export const addEmployee = (data: EmployeeForm) => {
  return request({
    url: '/system/employee',
    method: 'post',
    data: data
  });
};

/**
 * 修改员工管理
 * @param data
 */
export const updateEmployee = (data: EmployeeForm) => {
  return request({
    url: '/system/employee',
    method: 'put',
    data: data
  });
};



/**
 * 删除员工管理
 * @param id
 */
export const delEmployee = (id: string | number | Array<string | number>) => {
  return request({
    url: '/system/employee/' + id,
    method: 'delete'
  });

};
// 查询员工统计看板数据
export function getEmployeeStatistics(): AxiosPromise {
  return request({
    url: '/system/employee/statistics',
    method: 'get'
  });
}
// AI生成人事文档（在职证明/转正评语/招聘JD）
export function genHrAiDoc(empId: string | number, docType: string) {
  return request({
    url: '/system/hr/ai/genDoc',
    method: 'get',
    params: { empId, docType },
    timeout: 120000
  });
}

