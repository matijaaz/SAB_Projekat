/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import connection.DB;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import rs.etf.sab.operations.CourierOperations;

/**
 *
 * @author Matija
 */
public class km200432_Courier implements CourierOperations{

    @Override
    public boolean insertCourier(String courierUserName, String licencePlateNumbe) {
        boolean result = true;
        Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement stm = conn.prepareStatement("select SifK from Korisnik where Kor_ime = ?");
                PreparedStatement stm1 = conn.prepareStatement("select * from Kurir where SifK = ?");
                PreparedStatement stm2 = conn.prepareStatement("insert into Kurir(SifK,SifV) values(?,?)");
                PreparedStatement stm3 = conn.prepareStatement("Select SifV from Vozilo where RegBr = ?")
                
                ) {
            int userId;
            stm.setString(1, courierUserName);
            ResultSet rs = stm.executeQuery();
            if(rs.next()) {
                userId = rs.getInt(1);
            }else {
                return false;
            }
            stm1.setInt(1,userId);
            ResultSet rs1 = stm1.executeQuery();
            if(rs1.next()) {
                return false;
            }
            
            stm3.setString(1, licencePlateNumbe);
            int id;
            ResultSet rs3 = stm3.executeQuery();
            if(rs3.next()) {
                id = rs.getInt(1);
            } else {
                return false;
            }
            
            stm2.setInt(1,userId);
            stm2.setInt(2, id);
            stm2.executeUpdate();
        }
        catch (SQLException ex) {
            System.out.println("Greska");
        }
        
        return result;
    }

    @Override
    public boolean deleteCourier(String courierUserName) {
        Connection conn = DB.getInstance().getConnection();
       try(PreparedStatement stm = conn.prepareStatement("select SifK from Korisnik where Kor_ime = ?");
             PreparedStatement stm1 = conn.prepareStatement("delete from Kurir where SifK = ?")) {
           
           stm.setString(1, courierUserName);
           ResultSet rs = stm.executeQuery();
           int sifK;
           if(rs.next()) {
               sifK = rs.getInt(1);
           }else {
               return false;
           }
           
           stm1.setInt(1, sifK);
           stm1.executeUpdate();
           
        } catch (SQLException ex) {
           System.out.println("Greskaa");
        }
       
       return true;
    }

    @Override
    public List<String> getCouriersWithStatus(int i) {
       List<String> lista = new ArrayList<>();
       Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement stm = conn.prepareStatement("select Kor_ime from Korisnik ko join Kurir ku on ko.SifK = ku.SifK\n" +
"where status = ?");) {
            stm.setInt(1, i);
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                lista.add(rs.getString(1));
            }
            
        } catch (SQLException ex) {
            System.out.println("greskaaa");
        }
        return lista;
    }

    @Override
    public List<String> getAllCouriers() {
         List<String> lista = new ArrayList<>();
       Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement stm = conn.prepareStatement("select Kor_ime from Korisnik ko join Kurir ku on ko.SifK = ku.SifK\n" +
"order by Profit DESC");) {
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                lista.add(rs.getString(1));
            }
            
        } catch (SQLException ex) {
            System.out.println("greskaaa");
        }
        return lista;
    }

    @Override
    public BigDecimal getAverageCourierProfit(int numberOfDeliveries) {
       BigDecimal bd = new BigDecimal(0);
       Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement stm = conn.prepareStatement("select coalesce(avg(Profit),0)\n" +
"from Kurir\n" +
"where BrIsporucenihPaketa > ?");)
 {          
            stm.setInt(1, numberOfDeliveries);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
            
                bd = rs.getBigDecimal(1);
            }
            
        } catch (SQLException ex) {
            System.out.println("greskaaa");
        }
       
        if (bd == new BigDecimal(0)) {
            return null;
        }else {
            return bd;
        }
    }
    
}
