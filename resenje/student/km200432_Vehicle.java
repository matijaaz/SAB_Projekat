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
import rs.etf.sab.operations.VehicleOperations;

/**
 *
 * @author Matija
 */
public class km200432_Vehicle implements VehicleOperations{

    @Override
    public boolean insertVehicle(String licencePlateNumber,int fuelType,BigDecimal fuelConsumtion) {
       
        boolean result = false;
        Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement stm = conn.prepareStatement("select * from Vozilo where RegBr = ?");
            PreparedStatement stm1 = conn.prepareStatement("insert into Vozilo(RegBr,Tip,Potrosnja) values(?,?,?)")
                ) {
           
            stm.setString(1,licencePlateNumber);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return result;
            }
            
            stm1.setString(1, licencePlateNumber);
            stm1.setInt(2, fuelType);
            stm1.setBigDecimal(3, fuelConsumtion);
            
            stm1.executeUpdate();
            result = true;
            
        } catch (SQLException ex) {
            System.out.println("Greska");
        }
        
        return result;
    }

    @Override
    public int deleteVehicles(String... licencePlateNumbers) {
        int result = 0;
        if (licencePlateNumbers == null) {
            return result;
        }
        Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement stm = conn.prepareStatement("delete from Vozilo where RegBr = ?")) {
            for (String name : licencePlateNumbers) {
                 stm.setString(1, name);
                 result += stm.executeUpdate();
            }
        } catch (SQLException ex) {
            System.out.println("Greska");
        }
        return result;
    }

    @Override
    public List<String> getAllVehichles() {
       List<String> lista = new ArrayList<>();
       Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement stm = conn.prepareStatement("select RegBr from Vozilo");) {
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                lista.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            
        }
        return lista;
    }

    @Override
    public boolean changeFuelType(String licencePlateNumber,int fuelType) {
       boolean result = true;
       Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement stm1 = conn.prepareStatement("update vozilo set Tip = ? where RegBr = ? ")
             ) {
           
            stm1.setInt(1, fuelType);
            stm1.setString(2, licencePlateNumber);
            
            if (stm1.executeLargeUpdate() > 0) {
                return true;
            }else {
               result = false;
            }
            
        } catch (SQLException ex) {
            System.out.println("Greska");
        }
       
        return result;
    }

    @Override
    public boolean changeConsumption(String string, BigDecimal bd) {
       boolean result = true;
       Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement stm1 = conn.prepareStatement("update vozilo set Potrosnja = ? where RegBr = ? ")
             ) {
           
            stm1.setBigDecimal(1, bd);
            stm1.setString(2, string);
            
            if (stm1.executeLargeUpdate() > 0) {
                return true;
            }else {
               result = false;
            }
            
        } catch (SQLException ex) {
            System.out.println("Greska");
        }
       
        return result;
    }
    
}
