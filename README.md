#### RBAC 是当下权限系统的设计基础，同时有两种解释：
一： Role-Based Access Control，基于角色的访问控制
即，你要能够删除产品，那么当前用户就必须拥有产品经理这个角色

二：Resource-Based Access Control，基于资源的访问控制
即，你要能够删除产品，那么当前用户就必须拥有删除产品这样的权限


1. 用户和角色是多对多: 一个用户可以有多种角色,一个角色也可以赋予多个用户
2. 角色和权限是多对多: 一个角色可以有多个权限,一个权限也可以赋予给多个角色

> 多对多关系,需要一张中间表,使用联合主键

基于RBAC: 就会存在3张基础表: 用户,角色,权限, 以及两张中间表来建立

#### Realm 域,其实更像一个中介
那么Realm 在 Shiro里到底扮演什么角色呢?
当应用程序向 Shiro 提供了 账号和密码后, Shiro 就会问 Realm这个账号密码是否对,如果对的话,其所对应的用户拥有哪些角色,哪些权限.

Realm 得到 Shiro 给的账号和密码后,有可能会去找 ini 文件,也可以去找数据库.

使用数据库则要使用类继承AuthorizingRealm,实现它的两个方法:
```java
//调用角色和权限的时会调用这个
protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection)
    
//在登录验证这中身份验证会调用,每次角色验证和权限验证前都会验证身份, 只有身份验证成功后才会调用上面的doGetAuthorizationInfo
protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException 
```


