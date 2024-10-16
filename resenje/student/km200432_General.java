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
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.GeneralOperations;

/**
 *
 * @author Matija
 */
public class km200432_General implements GeneralOperations{

    @Override
    public void eraseAll() {
       Connection conn = DB.getInstance().getConnection();
      
       
        try(PreparedStatement stm1 = conn.prepareStatement("delete from Grad");
            PreparedStatement stm2 = conn.prepareStatement("delete from Korisnik");
            PreparedStatement stm3 = conn.prepareStatement("delete from Vozilo");) {
            
            stm1.executeUpdate();
            stm2.executeUpdate();
            stm3.executeUpdate();
            
            
        } catch (SQLException ex) {
            Logger.getLogger(km200432_General.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
