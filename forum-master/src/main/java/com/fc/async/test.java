package com.fc.async;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class test {
    private Connection conn;
    private PreparedStatement pstmt;
    private PreparedStatement pstmt2;
    private ResultSet rs;
    private String user = "bass";
    private String password = "bass123";
    private String dbUrl = "jdbc:mysql://192.168.37.234:3306/bass_db?useUnicode=true&characterEncoding=utf8&autoReconnect=true&?rewriteBatchedStatements=true";
    private int limitNum = 1;

    public void changeData() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(dbUrl, user, password);
            
//            //既不用batch,也不用事务
//            testBatch(false,false);
//            //只用batch, 不用事务
//            testBatch(false,true);
//            //只用事务，不用batch
//            testBatch(true,false);
            //不仅用事务，还用batch
            testBatch(true,true);
            
            pstmt.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void testBatch(Boolean openTransaction, Boolean useBatch) throws SQLException{
        if(openTransaction)
            conn.setAutoCommit(false);
        
        if(pstmt!=null){
            pstmt.clearParameters();
            pstmt.clearBatch();
        }
        
        pstmt = conn.prepareStatement("UPDATE t_o2o_work k,areane f\n" + 
        		"SET k.AREA=f.area_code\n" + 
        		"WHERE k.proname = ? and f.city like concat (k.city,'%')   \n" + 
        		"");
        long start = System.currentTimeMillis();
        for(int a = 0;a<limitNum;a++){
        	pstmt.setString(1, "宁夏");
            if(useBatch)
                pstmt.addBatch();
            else
                pstmt.executeUpdate();
        }
        
        if(useBatch)
           pstmt.executeBatch();
         
        if(openTransaction){
            conn.commit();
            conn.setAutoCommit(true);
        }
        long end = System.currentTimeMillis();
        System.out.println("use time:"+(end-start)+" ms");
        
    }
    
    //main method
    public static void main(String[] args){
        test ebt = new test();
        ebt.changeData();
    }
       
}