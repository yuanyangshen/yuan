package com.shenyy.yuan.service;

import com.github.pagehelper.PageInfo;
import com.shenyy.yuan.common.PageData;
import com.shenyy.yuan.model.User;

import java.util.List;

public interface UserService {

    public PageData<User> getUsers(Integer pageIndex, Integer pageSize);

    User getUserByIdOrName(User user);
}
