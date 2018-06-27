package com.shenyy.yuan.dao;

import com.shenyy.yuan.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDao {

    List<User> getUserList(@Param("pageNum")int pageNum, @Param("pageSize")int pageSize);
}
