/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import connection.DB;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.CourierRequestOperation;

/**
 *
 * @author Matija
 */
public class km200432_CourierRequest implements CourierRequestOperation{

    @Override
    public boolean insertCourierRequest(String userName, String licencePlateNumber) {
        Connection conn = DB.getInstance().getConnection();
        try( PreparedStatement stm = conn.prepareStatement("select SifK from Korisnik where Kor_ime = ?");
                PreparedStatement stm1 = conn.prepareStatement("select SifV from Vozilo where RegBr = ?");
                PreparedStatement stm2 = conn.prepareStatement("select * from Kurir where SifV = ?");
                PreparedStatement stm3 = conn.prepareStatement("insert into ZahtevZaKurira(SifK,SifV) values(?,?)");
                PreparedStatement stm4 = conn.prepareStatement("select * from ZahtevZaKurira where SifK = ?")) {
            stm.setString(1, userName);
            stm1.setString(1, licencePlateNumber);
            
            int sifK = 0;
            int sifV = 0;
            
            ResultSet rs = stm.executeQuery();
            if(rs.next()) {
                  sifK = rs.getInt(1);
            } else {
                return false;
            }
            ResultSet rs1 = stm1.executeQuery();
            if(rs1.next()) {
                sifV = rs1.getInt(1);
             }else {
                return false;
            }
            
            stm2.setInt(1, sifV);
            
            ResultSet rs3 = stm2.executeQuery();
            if (rs3.next()) {
                return false;
            }
            
            
            stm4.setInt(1, sifK);
            ResultSet rs4 = stm4.executeQuery();
            if (rs4.next()) {
                return false;
            }
            
            stm3.setInt(1, sifK);
            stm3.setInt(2,sifV);
            
            
            if (stm3.executeUpdate() > 0) {
                return true;
            }
            
            
            
            
            
        } catch (SQLException ex) {
            Logger.getLogger(km200432_CourierRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return true;
    }

    @Override
    public boolean deleteCourierRequest(String userName) {
        Connection conn = DB.getInstance().getConnection();
        
        
        try(PreparedStatement ps = conn.prepareStatement("select SifK from Korisnik Kor_ime = ?");
                PreparedStatement ps1 = conn.prepareStatement("delete from ZahtevZaKurira where SifK = ?")) {
            
            ps.setString(1, userName);
            
            int sifK = 0;
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                sifK = rs.getInt(1);
            } else {
                return false;
            }
            
            ps1.setInt(1, sifK);
            if (ps1.executeUpdate() > 0 ) {
                return true;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(km200432_CourierRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return true;
    }

    @Override
    public boolean changeVehicleInCourierRequest(String userName, String licencePlateNumber) {
      Connection conn = DB.getInstance().getConnection();
        try( PreparedStatement stm = conn.prepareStatement("select SifK from Korisnik Kor_ime = ?");
                PreparedStatement stm1 = conn.prepareStatement("select SifV from Vozilo where RegBr = ?");
                PreparedStatement stm2 = conn.prepareStatement("select * from Kurir where SifV = ?");
                PreparedStatement stm3 = conn.prepareStatement("update ZahtevZaKurira set SifV = ? where SifK = ?");
                PreparedStatement stm4 = conn.prepareStatement("select * from ZahtevZaKurira where SifK = ?")) {
            stm.setString(1, userName);
            stm1.setString(1, licencePlateNumber);
            
            int sifK = 0;
            int sifV = 0;
            
            ResultSet rs = stm.executeQuery();
            if(rs.next()) {
                  sifK = rs.getInt(1);
            } else {
                return false;
            }
            ResultSet rs1 = stm1.executeQuery();
            if(rs1.next()) {
                sifV = rs.getInt(1);
             }else {
                return false;
            }
            
            stm2.setInt(1, sifV);
            
            ResultSet rs3 = stm2.executeQuery();
            if (rs3.next()) {
                return false;
            }
            
            
            stm4.setInt(1, sifK);
            ResultSet rs4 = stm4.executeQuery();
            if (rs4.next()) {
                return false;
            }
            
            stm3.setInt(1, sifV);
            stm3.setInt(2,sifK);
            
            
            if (stm3.executeUpdate() > 0) {
                return true;
            }
            
            
            
            
            
        } catch (SQLException ex) {
            Logger.getLogger(km200432_CourierRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return true;
    }

    @Override
    public List<String> getAllCourierRequests() {
        List<String> lista = new ArrayList<>();
        Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement stm = conn.prepareStatement("select * from ZahtevZaKurira");
                PreparedStatement stm1 = conn.prepareStatement("select Kor_ime from Korisnik where SifK = ?")) {
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                int sifK = rs.getInt(1);
                stm1.setInt(1, sifK);
                ResultSet rs1 = stm1.executeQuery();
                if (rs1.next()) {
                     lista.add(rs1.getString(1));
                }
               
            }
        } catch (SQLException ex) {
            
        }
        return lista;
    }

    @Override
    public boolean grantRequest(String userName) {
        Connection conn = DB.getInstance().getConnection();
        int success = 0;
        String query = "{ call SPGrant_Request (?,?) }";
        try(CallableStatement cs = conn.prepareCall(query);) {
            cs.setString(1, userName);
            cs.registerOutParameter(2, java.sql.Types.INTEGER);
            cs.execute();
            success = cs.getInt(2);
        } catch (SQLException ex) {
            Logger.getLogger(km200432_CourierRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return  (success == 1);
    }
    
}
