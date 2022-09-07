package com.xxxx.seckill.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.vo.RespBean;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @version v1.0
 * @ClassName: UserUtil
 * @Description: 生成用户工具
 * @Author: ChenQ
 * @Date: 2022/9/7 on 20:29
 */
public class UserUtil {
    private static void createUser(int count) throws Exception {
        List<User> list = new ArrayList<>();
        for (int i=0;i<count;i++){
            User user = new User();
            user.setId(13000000000L+i);
            user.setNickname("user"+i);
            user.setSalt("1a2b3c");
            user.setPassword(MD5Util.inputPassToDBPass("123456",user.getSalt()));
            user.setLoginCount(1);
            user.setRegisterDate(new Date());
            list.add(user);
        }
        System.out.println("create user");

        //插入数据库
//        Connection conn = getConn();
//        String sql = "insert into t_user(login_count,id,nickname,register_date,password,salt) values(?,?,?,?,?,?)";
//        PreparedStatement pstmt = conn.prepareStatement(sql);
//        for (int i = 0; i < list.size(); i++) {
//            User user = list.get(i);
//            pstmt.setInt(1,user.getLoginCount());
//            pstmt.setLong(2,user.getId());
//            pstmt.setString(3,user.getNickname());
//            pstmt.setTimestamp(4,new Timestamp(user.getRegisterDate().getTime()));
//            pstmt.setString(5,user.getPassword());
//            pstmt.setString(6,user.getSalt());
//            pstmt.addBatch();
//        }
//        pstmt.executeBatch();
//        pstmt.clearParameters();
//        conn.close();
        System.out.println("insert to db");

        //登录，生成UserTicket
        String urlString = "http://localhost:80/login/doLogin";
        File file = new File("E:\\LinuxShare\\config.txt");
        if (file.exists()){
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file,"rw");
        raf.seek(0);
        for (int i = 0; i < list.size(); i++) {
            User user = list.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            String params = "mobile="+user.getId()+"&password="+MD5Util.inputPassToFromPass("123456");

            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len=inputStream.read(buff))>=0){
                bout.write(buff,0,len);
            }
            inputStream.close();
            bout.close();
            String response = new String(bout.toByteArray());
            System.out.println(response);
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
            String userTicket = (String) respBean.getObj();
            String row = user.getId()+","+userTicket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file:"+user.getId());
        }
        raf.close();
        System.out.println("finish");
    }

    private static Connection getConn() throws ClassNotFoundException, SQLException {
        String url = "jdbc:mysql://localhost:3306/seckill?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
        String password = "root";
        String username = "root";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url,username,password);
    }

    public static void main(String[] args) throws Exception {
        createUser(5000);
    }
}
