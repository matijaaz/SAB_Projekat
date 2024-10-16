/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import connection.DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import rs.etf.sab.operations.UserOperations;

/**
 *
 * @author Matija
 */
public class km200432_User implements UserOperations{

    @Override
    public boolean insertUser(String userName,String firstName, String lastName,String password) {
        boolean result = true;
        Connection conn = DB.getInstance().getConnection();
         try(PreparedStatement stm = conn.prepareStatement("select * from Korisnik where Kor_ime = ?");
                 PreparedStatement stm1 = conn.prepareStatement("insert into Korisnik(Ime,Prezime,Kor_ime,Sifra) values(?,?,?,?)")
               )
       {    
           stm.setString(1,userName);
           ResultSet rs = stm.executeQuery();
           if(rs.next()) {
               return false;
           }
           
           if(!Character.isUpperCase( firstName.codePointAt(0) )) return false;
           if(!Character.isUpperCase( lastName.codePointAt(0) )) return false;
           if (password.length() < 8)  return false;
           boolean containsLetter = Pattern.compile("[a-zA-Z]").matcher(password).find();
           boolean containsDigit = Pattern.compile("[0-9]").matcher(password).find();
           if (!containsLetter) return false;
           if(!containsDigit) return false;
           
           stm1.setString(1, firstName);
           stm1.setString(2, lastName);
           stm1.setString(3, userName);
           stm1.setString(4,password);
           
           stm1.executeUpdate();
            
           
        }
        catch (SQLException ex) {
            
        }
         
        return result;
    }

    @Override
    public int declareAdmin(String userName) {
        int result = 0;
        Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement stm = conn.prepareStatement("select SifK from Korisnik where Kor_ime = ?");
                PreparedStatement stm1 = conn.prepareStatement("select * from Administrator where SifK = ?");
                PreparedStatement stm2 = conn.prepareStatement("insert into Administrator(SifK) values(?)");
                
                ) {
            int userId;
            stm.setString(1, userName);
            ResultSet rs = stm.executeQuery();
            if(rs.next()) {
                userId = rs.getInt(1);
            }else {
                return 2;
            }
            stm1.setInt(1,userId);
            ResultSet rs1 = stm1.executeQuery();
            if(rs1.next()) {
                return 1;
            }
            
            stm2.setInt(1,userId);
            stm2.executeUpdate();
        }
        catch (SQLException ex) {
            
        }
        
        return result;
    }

    @Override
    public Integer getSentPackages(String... userNames) {
       Integer sum = null;
       boolean flag = true;
       Connection conn = DB.getInstance().getConnection();
       try(PreparedStatement stm = conn.prepareStatement("select BrPoslatihPaketa from Korisnik where Kor_ime = ?")) {
            for (String name : userNames) {
                 stm.setString(1, name);
                 ResultSet rs = stm.executeQuery();
                 if(rs.next()) {
                      if (flag) {sum = 0 ; flag = false;}
                      sum += rs.getInt(1);
                 }
                 
            }
        } catch (SQLException ex) {
           
        }
       
       return sum;
    }

    @Override
    public int deleteUsers(String... userNames) {
        int result = 0;
        if (userNames == null) {
            return result;
        }
        Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement stm = conn.prepareStatement("delete from Korisnik where Kor_ime = ?")) {
            for (String name : userNames) {
                 stm.setString(1, name);
                 result += stm.executeUpdate();
            }
        } catch (SQLException ex) {
           
        }
        return result;
    }

    @Override
    public List<String> getAllUsers() {
       List<String> lista = new ArrayList<>();
       Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement stm = conn.prepareStatement("select Kor_ime from Korisnik");) {
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                lista.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            
        }
        return lista;
    }
    

    
    
}
