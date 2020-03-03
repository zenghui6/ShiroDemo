package com.how2java;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.Arrays;
import java.util.Set;

public class DatabaseRealm extends AuthorizingRealm {
    //调用角色和权限的时会调用这个
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //能进入到这里,表示账户已经通过验证了
        //获取验证后的主体用户名,然后根据用户名获取角色和权限
        String userName  = (String) principalCollection.getPrimaryPrincipal();
        //通过Dao获取角色和权限
        Set<String> roles = new Dao().listRoles(userName);
        Set<String> permissions = new Dao().listPermissions(userName);

//        System.out.println("roles: " +roles + ", permission: "+ permissions);

        //授权信息
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        //把角色和权限放入授权信息中
        authorizationInfo.setRoles(roles);
        authorizationInfo.setStringPermissions(permissions);

        return authorizationInfo;
    }

    //在登录验证这中身份验证会调用,每次角色验证和权限验证前都会验证身份, 只有身份验证成功后才会调用上面的doGetAuthorizationInfo
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
       //根据身份证令牌获取账号密码
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        //获取主体的账号和密码
        String userName = token.getPrincipal().toString();
        //这里要将char[]里的元素转化为String
        String password = String.valueOf(token.getPassword());
//        System.out.println("userName: "+userName+", password: "+password);

        //获取数据库中的密码
        String passwordInDB = new Dao().getPassword(userName);

        //如果为空,就是账户不存在,如果不相同就是密码错误
        //但是都抛出AuthenticationException，而不是抛出具体错误原因，免得给破解者提供帮助信息
        if (null == passwordInDB || !passwordInDB.equals(password))
            throw new AuthenticationException();

        //身份验证信息里存放账号密码, getName() 是当前Realm的继承方法,通常返回当前类名: databaseRealm
        SimpleAuthenticationInfo authenticationInfo  = new SimpleAuthenticationInfo(userName,password,getName());

        return authenticationInfo;
    }
}
