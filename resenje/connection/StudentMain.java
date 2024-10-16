/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package connection;
import rs.etf.sab.operations.CityOperations;
import rs.etf.sab.operations.CourierOperations;
import rs.etf.sab.operations.CourierRequestOperation;
import rs.etf.sab.operations.DistrictOperations;
import rs.etf.sab.operations.GeneralOperations;
import rs.etf.sab.operations.PackageOperations;
import rs.etf.sab.operations.UserOperations;
import rs.etf.sab.operations.VehicleOperations;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;
import student.km200432_City;
import student.km200432_Courier;
import student.km200432_CourierRequest;
import student.km200432_District;
import student.km200432_General;
import student.km200432_Package;
import student.km200432_User;
import student.km200432_Vehicle;
/**
 *
 * @author Matija
 */



public class StudentMain {

    public static void main(String[] args) {
        CityOperations cityOperations = new km200432_City(); // Change this to your implementation.
        DistrictOperations districtOperations = new km200432_District(); // Do it for all classes.
        CourierOperations courierOperations = new km200432_Courier(); // e.g. = new MyDistrictOperations();
        CourierRequestOperation courierRequestOperation = new km200432_CourierRequest();
        GeneralOperations generalOperations = new km200432_General();
        UserOperations userOperations = new km200432_User();
        VehicleOperations vehicleOperations = new km200432_Vehicle();
        PackageOperations packageOperations = new km200432_Package();

        TestHandler.createInstance(
                cityOperations,
                courierOperations,
                courierRequestOperation,
                districtOperations,
                generalOperations,
                userOperations,
                vehicleOperations,
                packageOperations);

        TestRunner.runTests();
    }
}
