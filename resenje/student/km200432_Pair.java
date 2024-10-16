/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;
import rs.etf.sab.operations.PackageOperations.Pair;

/**
 *
 * @author Matija
 */
public class km200432_Pair<A,B> implements Pair<A,B> {
    
    private A first;
    private B second;
    
    public km200432_Pair(A first,B second) {
        this.first = first;
        this.second = second;
    }
    
    @Override
    public A getFirstParam() {
       return first;
    }

    @Override
    public B getSecondParam() {
       return second;
    }
    
}
