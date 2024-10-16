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
import rs.etf.sab.operations.DistrictOperations;

/**
 *
 * @author Matija
 */
public class km200432_District implements DistrictOperations{

    @Override
    public int insertDistrict(String name, int cityId, int xCord, int yCord) {
        int result = -1;
        Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement stm = conn.prepareStatement("insert into Opstina(Naziv,X,Y,SifG) values (?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
                PreparedStatement stm1 = conn.prepareStatement("select * from Grad where SifG = ?")) {
           
            stm1.setInt(1,cityId);
            ResultSet rs1 = stm1.executeQuery();
            if(!rs1.next()) {
                return result;
            }
            
            stm.setString(1,name);
            stm.setInt(2, xCord);
            stm.setInt(3, yCord);
            stm.setInt(4,cityId);
            
           stm.executeUpdate();
           ResultSet rs = stm.getGeneratedKeys();
           if (rs.next()) {
               return rs.getInt(1);
           }
            
           
        } catch (SQLException ex) {
            Logger.getLogger(km200432_District.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }

    @Override
    public int deleteDistricts(String... names) {
        int result = 0;
        if (names == null) {
            return result;
        }
        Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement stm = conn.prepareStatement("delete from Opstina where Naziv = ?")) {
            for (String name : names) {
                 stm.setString(1, name);
                 result += stm.executeUpdate();
            }
        } catch (SQLException ex) {
           
        }
        return result;
    }

    @Override
    public boolean deleteDistrict(int i) {
        boolean result = false;
        Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement stm = conn.prepareStatement("delete from Opstina where SifO = ?")) {
              stm.setInt(1, i);
              if (stm.executeUpdate() > 0) {
                  result = true;
              }
          }
        catch (SQLException ex) {
            
        }
        return result;
    }

    @Override
    public int deleteAllDistrictsFromCity(String nameOfTheCity) {
        int result = 0;
        Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement stm = conn.prepareStatement(" select SifO from Grad g join Opstina o on g.SifG = o. SifG where g.Naziv = ?")) {
              stm.setString(1, nameOfTheCity);
              ResultSet rs = stm.executeQuery();
              while(rs.next()) {
              
                  if (deleteDistrict(rs.getInt(1))) {
                      result++;
                  }
              }
          }
        catch (SQLException ex) {
            
        }
        return result;
    }

    @Override
    public List<Integer> getAllDistrictsFromCity(int idCity) {
        List<Integer> lista = new ArrayList<>();
        Connection conn = DB.getInstance().getConnection();
         try(PreparedStatement stm = conn.prepareStatement(" select * from Grad where SifG = ?");
             PreparedStatement stm1 = conn.prepareStatement(" select SifO from Opstina where SifG = ?");) {
              stm.setInt(1,idCity);
              ResultSet rs = stm.executeQuery();
              if (!rs.next()) {
                  return null;
              }
              stm1.setInt(1,idCity);
              ResultSet rs1 = stm1.executeQuery();
              while(rs1.next()) {
                  lista.add(rs1.getInt(1));
              }

          }
        catch (SQLException ex) {
            
        }
         
         return lista;
    }

    @Override
    public List<Integer> getAllDistricts() {
        List<Integer> lista = new ArrayList<>();
        Connection conn = DB.getInstance().getConnection();
         try(PreparedStatement stm1 = conn.prepareStatement("select SifO from Opstina");) {

              ResultSet rs1 = stm1.executeQuery();
              while(rs1.next()) {
                  lista.add(rs1.getInt(1));
              }

          }
        catch (SQLException ex) {
            
        }
         
         return lista;
    }
    
}
