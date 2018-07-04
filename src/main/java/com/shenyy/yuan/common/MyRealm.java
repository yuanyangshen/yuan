package com.shenyy.yuan.common;

import com.shenyy.yuan.model.SysPermission;
import com.shenyy.yuan.model.SysRole;
import com.shenyy.yuan.model.User;
import com.shenyy.yuan.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by yuanyang on 2018/7/1.
 */
public class MyRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        User user = (User) principalCollection.getPrimaryPrincipal();
        for(SysRole role : user.getRoleList()){
            info.addRole(role.getRole());
            for (SysPermission permission : role.getPermissions()){
                info.addStringPermission(permission.getPermission());
            }
        }
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.print("登录操作进行登录验证-----");
        String username = (String) authenticationToken.getPrincipal();
        System.out.println(authenticationToken.getCredentials());
        User user = new User();
        user.setUsername(username);
        User userInfo = userService.getUserByIdOrName(user);
        if (userInfo == null){
            return null;
        }
        List<SysRole> roleList = userService.getRoleList(userInfo);
        if (roleList != null && roleList.size() > 0){
            for (SysRole sysRole : roleList){
                sysRole.setPermissions(userService.getPermissionList(sysRole));
            }
        }
        userInfo.setRoleList(roleList);
        List<SysPermission> permissionList = userService.getPermissionListByUser(roleList);
        userInfo.setPermissionList(permissionList);
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(userInfo.getUsername(),userInfo.getPassword(), this.getName());
        return authenticationInfo;
    }
}
