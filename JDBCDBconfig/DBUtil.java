package com.weteam.util;

import org.apache.commons.dbcp.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 连接数据库工具类
 * Created by Joker on 2017/5/26.
 */
public class DBUtil {
    private static BasicDataSource basicDataSource;
    private static Connection connection;
    static{
        try{
            /*
            * 解析databaseConfig.properties文件
            *
            * java.util.Properties
            *     该类可解析.properties文件
            */
            Properties databaseConfig=new Properties();
//            System.out.println(databaseConfig);
            databaseConfig.load(DBUtil.class.getClassLoader().getResourceAsStream("com/weteam/util/databaseConfig.properties"));
            String driverClassName=databaseConfig.getProperty("jdbc.driverClassName");
            String url=databaseConfig.getProperty("jdbc.url");
            String username=databaseConfig.getProperty("jdbc.username");
            String password=databaseConfig.getProperty("jdbc.password");

            String initialSize=databaseConfig.getProperty("dataSource.initialSize");
            String maxIdle=databaseConfig.getProperty("dataSource.maxIdle");
            String minIdle=databaseConfig.getProperty("dataSource.minIdle");
            String maxActive=databaseConfig.getProperty("dataSource.maxActive");
            String maxWait=databaseConfig.getProperty("dataSource.maxWait");

            /*
            * 初始化数据库连接池
            */
            basicDataSource=new BasicDataSource();
            basicDataSource.setDriverClassName(driverClassName);
            basicDataSource.setUrl(url);
            basicDataSource.setUsername(username);
            basicDataSource.setPassword(password);
            if(initialSize!=null){
                basicDataSource.setInitialSize(Integer.parseInt(initialSize));
            }
            if(maxIdle!=null){
                basicDataSource.setMaxIdle(Integer.parseInt(maxIdle));
            }
            if(minIdle!=null){
                basicDataSource.setMinIdle(Integer.parseInt(minIdle));
            }
            if(maxActive!=null&&!maxActive.trim().equals("0")){
                basicDataSource.setMaxActive(Integer.parseInt(maxActive));
            }
            if(maxWait!=null){
                basicDataSource.setMaxWait(Long.parseLong(maxWait));
            }
        }catch(Exception e){
            throw new RuntimeException("创建并初始化连接池异常！",e);
        }
    }

    public static synchronized Connection getConnection() throws SQLException {
        if(basicDataSource!=null){
            /*
            *     连接池的Connection getConnection()是一个阻塞方法，通常会立刻返回连
            * 接池中的可用连接。当连接池中没有可用连接时，该方法会阻塞，阻塞时间由创建
            * 连接池时setMaxWait()设置的最大等待时间决定。当阻塞期内有可用连接时，该方
            * 法会立刻结束阻塞并将可用连接返回，若阻塞超时仍无可用连接，该方法会抛出超时
            * 异常。
            */
            connection=basicDataSource.getConnection();
        }
        return connection;
    }

    public static void closeConnection(Connection connection){
        try{
            if(connection!=null){
                connection.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            connection=DBUtil.getConnection();
            System.out.println("数据库连接初始化完毕："+connection);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            DBUtil.closeConnection(connection);
        }
    }

}
