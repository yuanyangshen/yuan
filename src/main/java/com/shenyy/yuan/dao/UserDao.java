package com.shenyy.yuan.dao;

import com.shenyy.yuan.model.SysPermission;
import com.shenyy.yuan.model.SysRole;
import com.shenyy.yuan.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDao {

    List<User> getUserList(@Param("pageNum")int pageNum, @Param("pageSize")int pageSize);

    User getUserByIdOrName(User user);

    List<SysRole> getRoleList(User user);

    List<SysPermission> getPermissionList(SysRole sysRole);

    List<SysPermission> getPermissionByUser(List<SysRole> sysRoles);
}
