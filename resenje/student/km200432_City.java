/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import connection.DB;
import java.util.List;
import rs.etf.sab.operations.CityOperations;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Matija
 */
public class km200432_City implements CityOperations{

    @Override
    public int insertCity(String name, String postalCode) {
        int result = -1;
        if (name == null || postalCode == null) {
            return result;
        }
        Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement stm1 = conn.prepareStatement("select * from Grad where PostanskiBroj = ?");
                PreparedStatement stm2 = conn.prepareStatement("select * from Grad where Naziv = ?");
                PreparedStatement stm = conn.prepareStatement("Insert into Grad(PostanskiBroj,Naziv) values(?,?)", PreparedStatement.RETURN_GENERATED_KEYS);) {
            stm1.setString(1, postalCode);
            ResultSet rs1 = stm1.executeQuery();
            if(rs1.next()) {
                return result;
            }
            stm2.setString(1, name);
            ResultSet rs2 = stm2.executeQuery();
            if(rs2.next()) {
                return result;
            }
            stm.setString(1, postalCode);
            stm.setString(2, name);
            stm.executeUpdate();
            ResultSet rs = stm.getGeneratedKeys();
            if(rs.next()) {
                result = rs.getInt(1);
            }
            
        } catch (SQLException ex) {
           
        }
        return result;
    }

    @Override
    public int deleteCity(String... names) {
        int result = 0;
        if (names == null) {
            return result;
        }
        Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement stm = conn.prepareStatement("delete from Grad where Naziv = ?")) {
            for (String name : names) {
                 stm.setString(1, name);
                 result += stm.executeUpdate();
            }
        } catch (SQLException ex) {
           
        }
        return result;
    }

    @Override
    public boolean deleteCity(int i) {
        boolean result = false;
        Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement stm = conn.prepareStatement("delete from Grad where SifG = ?")) {
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
    public List<Integer> getAllCities() {
        List<Integer> lista = new ArrayList<>();
        Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement stm = conn.prepareStatement("select * from Grad");) {
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                lista.add(rs.getInt("SifG"));
            }
        } catch (SQLException ex) {
            
        }
        return lista;
    }
    
    
   
}
