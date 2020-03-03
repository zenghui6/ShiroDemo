package com.how2java;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 这个用了取出数据库中的数据,如果不使用数据库,直接写在shiro.ini中也行
 */
public class Dao {
    public Dao(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/shiro?characterEncoding=UTF-8","root","a1210128434");
    }

    /**
     * 根据用户名获取数据库中的密码
     * @param userName
     * @return
     */
    public String getPassword(String userName) {
        String sql = "select password from user where name = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {

            ps.setString(1, userName);

            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return rs.getString("password");

        } catch (SQLException e) {

            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据用户名获取该用户的角色
     * @param userName
     * @return
     */
    public Set<String> listRoles(String userName) {

        Set<String> roles = new HashSet<>();
        String sql = "select r.name from user u "
                + "left join user_role ur on u.id = ur.uid "
                + "left join role r on r.id = ur.rid "
                + "where u.name = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                roles.add(rs.getString(1));
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
        return roles;
    }

    /**
     * 查询用户权限, user 与 user_role左连接 => 用户的role_id  , 用户的role_id 与 role左连接 => 用户的角色
     *              用户的角色 与 role_permission 左连接 => 该用户对应的权限id  , 权限id 与 权限 左连接 =>该用户对应的权限
     * @param userName
     * @return
     */
    public Set<String> listPermissions(String userName) {
        Set<String> permissions = new HashSet<>();
        String sql =
                "select p.name from user u "+
                        "left join user_role ru on u.id = ru.uid "+
                        "left join role r on r.id = ru.rid "+
                        "left join role_permission rp on r.id = rp.rid "+
                        "left join permission p on p.id = rp.pid "+
                        "where u.name =?";

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql);) {

            ps.setString(1, userName);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                permissions.add(rs.getString(1));
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
        return permissions;
    }
    public static void main(String[] args) {
        System.out.println(new Dao().listRoles("zhang3"));      //查看张三的角色 -> admin
        System.out.println(new Dao().listRoles("li4"));         //查看李四的角色 -> productManager
        System.out.println(new Dao().listPermissions("zhang3"));    //查看张三的权限
        System.out.println(new Dao().listPermissions("li4"));       //查看李四的权限
    }
}

