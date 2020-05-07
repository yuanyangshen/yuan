package com.shenyy.yuan.service.serviceImpl;

import com.github.pagehelper.Page;
import com.shenyy.yuan.config.PageData;
import com.shenyy.yuan.dao.UserDao;
import com.shenyy.yuan.model.SysPermission;
import com.shenyy.yuan.model.SysRole;
import com.shenyy.yuan.model.User;
import com.shenyy.yuan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public PageData<User> getUsers(Integer pageIndex,Integer pageSize) {
        Page<User> page = (Page<User>) userDao.getUserList(pageIndex,pageSize);
        long total = page.getTotal();
        PageData<User> users = new PageData<>();
        users.setPageIndex(pageIndex);
        users.setPageSize(pageSize);
        users.setTotal(page.getTotal());
        users.setData(page);
        return users;
    }

    @Override
    public User getUserByIdOrName(User user) {
        return userDao.getUserByIdOrName(user);
    }

    @Override
    public List<SysRole> getRoleList(User user) {
        return userDao.getRoleList(user);
    }

    @Override
    public List<SysPermission> getPermissionList(SysRole sysRole) {
        return userDao.getPermissionList(sysRole);
    }

    @Override
    public List<SysPermission> getPermissionListByUser(List<SysRole> sysRoleList) {
        return userDao.getPermissionByUser(sysRoleList);
    }
}
