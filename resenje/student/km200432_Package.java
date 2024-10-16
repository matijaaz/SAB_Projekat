/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student;

import connection.DB;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.PackageOperations;

/**
 *
 * @author Matija
 */
public class km200432_Package implements PackageOperations{
    
    
   
   static double euclidean(int x1, int y1, int x2, int y2) {
    return Math.sqrt(((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)));
  }
    @Override
    public int insertPackage(int districtFrom,int districtTo,String userName,int packageType,BigDecimal weight) {
        int result = -1;
        if (packageType < 0 || packageType > 2) return -1;
        Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement stm1 = conn.prepareStatement("select * from Opstina where SifO = ?");
            PreparedStatement stm2 = conn.prepareStatement("select SifK from Korisnik where Kor_ime = ?");
             PreparedStatement stm3 = conn.prepareStatement("insert into ZahtevZaPrevoz values(?,?,?,?,?)",PreparedStatement.RETURN_GENERATED_KEYS);
                PreparedStatement stm4 = conn.prepareStatement("insert into Paket(SifZ,Status) values(?,0)")
               ) {
           
            stm1.setInt(1, districtFrom);
            ResultSet rs1 = stm1.executeQuery();
            if(!rs1.next()) {
                return -1;
            }
            stm1.setInt(1, districtTo);
            ResultSet rs11 = stm1.executeQuery();
            if(!rs11.next()) {
            return -1;
            }
            
            int sifK = 0;
            stm2.setString(1, userName);
            ResultSet rs2 = stm2.executeQuery();
            if(rs2.next()) {
                sifK = rs2.getInt(1);
            } else  {
                return - 1;
            }
            
            stm3.setInt(1, sifK);
            stm3.setInt(2, districtFrom);
            stm3.setInt(3, districtTo);
            stm3.setInt(4,packageType);
            stm3.setBigDecimal(5, weight);
            
            stm3.executeUpdate();
            ResultSet r = stm3.getGeneratedKeys();
            if (r.next()) {
                result = r.getInt(1);
            }else {
                return -1;
            }
            
            stm4.setInt(1, result);
            stm4.executeUpdate();
            
            
            
            return result;
            
            
        } catch (SQLException ex) {
           
        }
        return result;
    }

    @Override
    public int insertTransportOffer(String couriersUserName,int packageId, BigDecimal pricePercentage) {
        
        Connection conn = DB.getInstance().getConnection();
        int result = -1;
        
        if (pricePercentage == null) {
             double randomFactor = (new Random().nextDouble() * 20) - 10;
             pricePercentage = BigDecimal.valueOf(randomFactor).setScale(3, RoundingMode.HALF_UP);
        }
       try(PreparedStatement stm1 = conn.prepareStatement("select k.sifK from Kurir k join Korisnik ko on k.SifK = ko.SifK where ko.Kor_ime = ?\n" +
                                                            "and k.Status = 0");
           PreparedStatement stm2 = conn.prepareStatement("select SifP from ZahtevZaPrevoz Z join Paket p on z.SifZ = p.SifZ where z.SifZ = ? and p.Status = 0");
           PreparedStatement stm3 = conn.prepareStatement("insert into Ponuda(SifK,SifPaket,ProcenatCeneIsporuke) values(?,?,?)",PreparedStatement.RETURN_GENERATED_KEYS)
               )
        {
            int sifK = 0;
            int sifP = 0;
            
            stm1.setString(1,couriersUserName);
            ResultSet rs1 = stm1.executeQuery();
            if(rs1.next()) {
                sifK = rs1.getInt(1);
            }else {
                return result;
            }
            
            stm2.setInt(1,packageId);
            ResultSet rs2 = stm2.executeQuery();
            if(rs2.next()) {
                sifP = rs2.getInt(1);
            } else {
                return result;
            }
            
            stm3.setInt(1,sifK);
            stm3.setInt(2,sifP);
            stm3.setBigDecimal(3,pricePercentage);
            
            stm3.executeUpdate();
            ResultSet rs3 = stm3.getGeneratedKeys();
            if (rs3.next()) {
                return rs3.getInt(1);
            }
            
            
          
       } catch (SQLException ex) {
           Logger.getLogger(km200432_Package.class.getName()).log(Level.SEVERE, null, ex);
       }
        
        return result;
    }

    @Override
    public boolean acceptAnOffer(int offerId) {
        
        Connection conn = DB.getInstance().getConnection();
        
       try(PreparedStatement stm1 = conn.prepareStatement("select * from Ponuda where SifP = ?");
               PreparedStatement stmo = conn.prepareStatement("Select Status from Kurir where SifK = ?");
           PreparedStatement stm2 = conn.prepareStatement("select z.OpstinaOd,z.OpstinaDo,z.TipPaketa,z.TezinaPaketa from Paket p join ZahtevZaPrevoz z on (p.SifZ = z.SifZ) where p.SifP = ?");
            PreparedStatement stm3 = conn.prepareStatement("select X, Y from Opstina where SifO = ?");
               PreparedStatement stm4 = conn.prepareStatement("Update paket set SifK = ?, Cena = ?, Status = 1, VremePrihvatanjaZahteva = convert(date,GETDATE()) where SifP = ?");
               PreparedStatement stm5 = conn.prepareStatement("insert into Voznja(SifP) values(?)")
               ) {
          stm1.setInt(1,offerId);
          ResultSet rs1 = stm1.executeQuery();
          int sifraPaketa = 0;
          BigDecimal tezina = new BigDecimal(1);
          int tip = 0;
          BigDecimal procenat;
          int sifraKurira = 0;
          if(rs1.next()) {
            sifraPaketa = rs1.getInt("SifPaket");
            procenat = rs1.getBigDecimal("ProcenatCeneIsporuke");
            sifraKurira = rs1.getInt("SifK");
          } else {
              return false;
          }
          
          stmo.setInt(1,sifraKurira);
          ResultSet rs0 = stmo.executeQuery();
          if (rs0.next()) {
              if (rs0.getInt(1) == 1) {
                  return false;
              }
          }else {return false;}
          
          int opstinaOd = 0;
          int opstinaDo = 0;
          stm2.setInt(1, sifraPaketa);
          ResultSet rs2 = stm2.executeQuery();
          if(rs2.next()) {
              opstinaOd = rs2.getInt(1);
              opstinaDo = rs2.getInt(2);
              tip = rs2.getInt(3);
              tezina = rs2.getBigDecimal(4);
          }
          
          stm3.setInt(1, opstinaOd);
          
          int Xod = 0;
          int Yod = 0;
          int Xdo = 0;
          int Ydo = 0;
          
          ResultSet rs3 = stm3.executeQuery();
          if (rs3.next()) {
              Xod = rs3.getInt(1);
              Yod = rs3.getInt(2);
          }
         
          stm3.setInt(1,opstinaDo);
          ResultSet rs4 = stm3.executeQuery();
          if (rs4.next()) {
              Xdo = rs4.getInt(1);
              Ydo = rs4.getInt(2);
          }
          
          double distance = euclidean(Xod, Yod, Xdo, Ydo);
          procenat = procenat.divide(new BigDecimal(100));
          BigDecimal cena = new BigDecimal(1);
           switch (tip) {
                case 0:
                    cena = (new BigDecimal(10.0D * distance)).multiply(procenat.add(new BigDecimal(1)));
                    break;
                case 1:
                    cena = (new BigDecimal((25.0D + tezina.doubleValue() * 100.0D) * distance)).multiply(procenat.add(new BigDecimal(1)));
                    break;
                case 2:
                    cena = (new BigDecimal((75.0D + tezina.doubleValue() * 300.0D * 2.0D) * distance)).multiply(procenat.add(new BigDecimal(1)));
            } 
           
           stm4.setInt(1, sifraKurira);
           stm4.setBigDecimal(2, cena.setScale(3, RoundingMode.HALF_UP));
           stm4.setInt(3, sifraPaketa);
           
           stm4.executeUpdate();
           
           stm5.setInt(1, sifraPaketa);
           stm5.executeUpdate();
           
           return true;
           
           
       } catch (SQLException ex) {
           Logger.getLogger(km200432_Package.class.getName()).log(Level.SEVERE, null, ex);
       }
         return true;
    }

    @Override
    public List<Integer> getAllOffers() {
       List<Integer> lista = new ArrayList<>();
        Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement stm = conn.prepareStatement("select SifP from Ponuda");) {
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                lista.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            
        }
        return lista;
    }

    @Override
    public List<Pair<Integer, BigDecimal>> getAllOffersForPackage(int packageId) {
        List<Pair<Integer,BigDecimal>> lista = new ArrayList<>();
        Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement stm = conn.prepareStatement("select po.SifP,po.ProcenatCeneIsporuke from ZahtevZaPrevoz z join Paket p on (z.SifZ = p.SifZ) join Ponuda po on(p.SifP = po.SifPaket)\n" +
"where z.SifZ = ?");) {
            stm.setInt(1,packageId);
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                Pair<Integer,BigDecimal> par = new km200432_Pair<>(rs.getInt(1),rs.getBigDecimal(2));
                lista.add(par);
            }
        } catch (SQLException ex) {
            
        }
        return lista;
    }

    @Override
    public boolean deletePackage(int i) {
        Connection conn = DB.getInstance().getConnection();
        boolean result = false;
       try(PreparedStatement stm = conn.prepareStatement("delete from ZahtevZaPrevoz where SifZ = ?");
               PreparedStatement stm1 = conn.prepareStatement("select * from ZahtevZaPrevoz z join Paket p on z.SifZ = p.SifZ\n" +
"where z.SifZ = ? and (p.Status = 2 or p.Status = 3)")) {
           
           stm1.setInt(1,i);
           ResultSet rs1 = stm1.executeQuery();
           if(rs1.next()) {
               return false;
           }
           
           stm.setInt(1,i);
           if (stm.executeUpdate() > 0) {
               return true;
           }
       } catch (SQLException ex) {
           Logger.getLogger(km200432_Package.class.getName()).log(Level.SEVERE, null, ex);
       }
       
       return result;
    }

    @Override
    public boolean changeWeight(int i, BigDecimal bd) {
         Connection conn = DB.getInstance().getConnection();
         
       try(PreparedStatement stm = conn.prepareStatement("update ZahtevZaPrevoz set TezinaPaketa = ? where SifZ = ?");) {
           stm.setBigDecimal(1, bd);
           stm.setInt(2,i);
           if (stm.executeUpdate() > 0) {
               return true;
           }
       } catch (SQLException ex) {
           Logger.getLogger(km200432_Package.class.getName()).log(Level.SEVERE, null, ex);
       }
       
       
       return false;
         
    }

    @Override
    public boolean changeType(int packageId,int newType) {
         Connection conn = DB.getInstance().getConnection();
         if (newType < 0 || newType > 2) {
             return false;
         }
       try(PreparedStatement stm = conn.prepareStatement("update ZahtevZaPrevoz set TipPaketa = ? where SifZ = ?");) {
           stm.setInt(1,newType);
           stm.setInt(2,packageId);
           if (stm.executeUpdate() > 0) {
               return true;
           }
       } catch (SQLException ex) {
           Logger.getLogger(km200432_Package.class.getName()).log(Level.SEVERE, null, ex);
       }
       
       
       return false;
    }

    @Override
    public Integer getDeliveryStatus(int packageId) {
        Connection conn = DB.getInstance().getConnection();
        int status = 0;
       try(PreparedStatement stm = conn.prepareStatement("select p.Status from ZahtevZaPrevoz z join Paket p on (p.SifZ = z.SifZ) where z.SifZ = ?");) {
           stm.setInt(1,packageId);
           ResultSet rs = stm.executeQuery();
           if (rs.next()) {
               status = rs.getInt(1);
           }else {
               return null;
           }
       } catch (SQLException ex) {
           Logger.getLogger(km200432_Package.class.getName()).log(Level.SEVERE, null, ex);
       }
       
       return status;
       
    }

    @Override
    public BigDecimal getPriceOfDelivery(int i) {
       Connection conn = DB.getInstance().getConnection();
       BigDecimal bd = null;
       try(PreparedStatement stm = conn.prepareStatement("select p.Cena from ZahtevZaPrevoz z join Paket p on (p.SifZ = z.SifZ) where z.SifZ = ?");) {
           stm.setInt(1,i);
           ResultSet rs = stm.executeQuery();
           if (rs.next()) {
               bd = rs.getBigDecimal(1);
           }
          
       } catch (SQLException ex) {
           Logger.getLogger(km200432_Package.class.getName()).log(Level.SEVERE, null, ex);
       }
       
       return bd;
    }

    @Override
    public Date getAcceptanceTime(int packageId) {
       Connection conn = DB.getInstance().getConnection();
       Date datum = null;
       try(PreparedStatement stm = conn.prepareStatement("select p.VremePrihvatanjaZahteva from ZahtevZaPrevoz z join Paket p on (p.SifZ = z.SifZ) where z.SifZ = ?");) {
           stm.setInt(1,packageId);
           ResultSet rs = stm.executeQuery();
           if (rs.next()) {
               datum = rs.getDate(1);
           }
          
       } catch (SQLException ex) {
           Logger.getLogger(km200432_Package.class.getName()).log(Level.SEVERE, null, ex);
       }
       
       return datum;
    }

    @Override
    public List<Integer> getAllPackagesWithSpecificType(int type) {
       List<Integer> lista = new ArrayList<>();
        Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement stm = conn.prepareStatement("select SifZ from ZahtevZaPrevoz where TipPaketa = ?");) {
            stm.setInt(1, type);
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                lista.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            
        }
        return lista;
    }

    @Override
    public List<Integer> getAllPackages() {
        List<Integer> lista = new ArrayList<>();
        Connection conn = DB.getInstance().getConnection();
        try(PreparedStatement stm = conn.prepareStatement("select SifZ from ZahtevZaPrevoz");) {
            ResultSet rs = stm.executeQuery();
            while(rs.next()) {
                lista.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            
        }
        return lista;
    }

    @Override
    public List<Integer> getDrive(String courierUsername) {
        Connection conn = DB.getInstance().getConnection();
        List<Integer> lista = new ArrayList<>();
        int sifraKurira = 0;
       try(PreparedStatement stm = conn.prepareStatement("select k.sifK from Korisnik k join Kurir ku on (k.SifK = ku.SifK) where k.Kor_ime = ? and ku.Status = 1");
              PreparedStatement stm1 = conn.prepareStatement("select z.SifZ from Voznja v join Paket p on(v.SifP = p.SifP) join ZahtevZaPrevoz z on (p.SifZ = z.SifZ) \n" +
"where p.SifK = ? and (p.Status = 1 or p.Status = 2) order by p.VremePrihvatanjaZahteva,p.SifP")) {
           
           stm.setString(1,courierUsername);
           ResultSet rs = stm.executeQuery();
           if(rs.next()) {
               sifraKurira = rs.getInt(1);
               
               
           }else {
               return null;
           }
           
           stm1.setInt(1,sifraKurira);
           ResultSet rs1 = stm1.executeQuery();
           
           while(rs1.next()) {
               lista.add(rs1.getInt(1)); // osiguran poredak id-ova po FCFS principu
           }
           
           
           
       } catch (SQLException ex) {
           Logger.getLogger(km200432_Package.class.getName()).log(Level.SEVERE, null, ex);
       }
        
        
        
        
        
        return lista;
        
    }

    @Override
    public int driveNextPackage(String courierUserName) {
        
        
        Connection conn = DB.getInstance().getConnection();
        int sifraKurira = 0;
        int status = 0;
        
       try(PreparedStatement stm = conn.prepareStatement("select k.sifK,ku.Status from Korisnik k join Kurir ku on (k.SifK = ku.SifK) where k.Kor_ime = ?");
               PreparedStatement stm1 = conn.prepareStatement("Update Kurir set Status = ? where SifK = ?"); PreparedStatement stm2 = conn.prepareStatement("select SifP from Paket where SifZ = ?");
               PreparedStatement stm3 = conn.prepareStatement("update Paket set Status = ? where SifP = ?");
               PreparedStatement stm4 = conn.prepareStatement("update Voznja set PotrosenoGorivo = ? where SifP = ?");
               PreparedStatement stm5 = conn.prepareStatement("delete from Voznja where SifP = ?");
               PreparedStatement stm6 = conn.prepareStatement("update Kurir set Profit = Profit + (select (sum(p.Cena)-sum(v.PotrosenoGorivo)) from Voznja v join Paket p on(v.SifP = p.SifP) where p.SifK = ?) where SifK = ?");
           PreparedStatement stm7 = conn.prepareStatement("select z.SifZ from Voznja v join Paket p on (v.SifP = p.SifP) join ZahtevZaPrevoz z on(p.SifZ = z.SifZ)\n" +
"where p.Status = 3 and p.SifK = ? order by p.VremePrihvatanjaZahteva,p.SifP");
               PreparedStatement stm8 = conn.prepareStatement("select X,Y from Opstina where SifO = ?");
               PreparedStatement stm9 = conn.prepareStatement("Select OpstinaOd,OpstinaDo from ZahtevZaPrevoz where SifZ = ?");
               PreparedStatement stm10 = conn.prepareStatement("select v.Potrosnja,v.Tip from Vozilo v join Kurir k on(v.SifV = k.SifV)\n" +
"where SifK = ?");
               PreparedStatement stm11 = conn.prepareStatement("select p.SifP from Voznja v join Paket p on(p.SifP=v.SifP) where p.SifK = ?")
               
               
               ) {
           stm.setString(1,courierUserName);
           ResultSet rs = stm.executeQuery();
           if(rs.next()) {
               sifraKurira = rs.getInt(1);
               status = rs.getInt(2);
           }else {return -2;}
           
           List<Integer> SifZs;
           
           if (status == 0) { //ne vozi,nije pocela voznja,zapocni voznju prvog paketa ako ima sta da vozi 
               
               
               stm1.setInt(1, 1);
               stm1.setInt(2, sifraKurira);
               stm1.executeUpdate();
               SifZs = this.getDrive(courierUserName);
               
               if (SifZs.isEmpty()) {
                   stm1.setInt(1,0);
                   stm1.setInt(2, sifraKurira);
                   stm1.executeUpdate();
                   return -1;
               }
               
               
               stm2.setInt(1,SifZs.get(0));
               ResultSet rs2 = stm2.executeQuery();
               int sifP = 0;
               if (rs2.next()) {
                   sifP = rs2.getInt(1);
               }
               
               for (int i = 0; i < SifZs.size(); i++) {
                   
                  //promeni status paketa u 2
                  
                  stm2.setInt(1,SifZs.get(i));
                  ResultSet rs22 = stm2.executeQuery();
                  if(rs22.next()) {
                      
                      stm3.setInt(1, 2);//status - 2
                      stm3.setInt(2,rs22.getInt(1));
                      stm3.executeUpdate();
                  
                  }
                   
               }
               
              BigDecimal d = (this.racunaj(SifZs.get(0),sifraKurira)).setScale(3,RoundingMode.HALF_UP);
              
              //stavi paket da je isporucen , status = 3
              stm3.setInt(1,3);
              stm3.setInt(2,sifP);
              stm3.executeUpdate();
              
              //insertuj u voznju potroseno gorivo
              stm4.setBigDecimal(1,d);
              stm4.setInt(2,sifP);
              
              
              stm4.executeUpdate();
              
              if (SifZs.size() == 1) { // ako je postojao samo jedan paket da preveze
              
                  stm6.setInt(1,sifraKurira);
                  stm6.setInt(1, sifraKurira);
                  stm6.executeUpdate();//ubaci profit
                  
                  stm1.setInt(1,0);
                  stm1.setInt(2,sifraKurira);// kurir - ne vozi
                  stm1.executeUpdate();
                  
                  stm5.setInt(1,sifP);
                  stm5.executeUpdate();
              }
              
              return SifZs.get(0);
              
           }
           
           else { // vec vozi, vozi sledeci paket ako postoji
           
                // prvo da se vidi u kom mestu se trenutno nalazi
                
               
               SifZs = this.getDrive(courierUserName);
               
               stm2.setInt(1,SifZs.get(0));
               ResultSet rs2 = stm2.executeQuery();
               int sifP = 0;
               if (rs2.next()) {
                   sifP = rs2.getInt(1);
               }
               
               stm7.setInt(1,sifraKurira);
               ResultSet rs7 = stm7.executeQuery();
               int sifZ = 0;
                
               while(rs7.next()) {
                  sifZ = rs7.getInt(1); // od kog je krenuo po drugi paket
               }
               
               stm9.setInt(1,SifZs.get(0));
               ResultSet rs9 = stm9.executeQuery();
               
               int Do = 0;
               int Od = 0;
               
               if (rs9.next()) {
                   Od = rs9.getInt(1); // opstina od koje krece da se vozi novi paket
               }
               
               stm9.setInt(1,sifZ); // opstina od koje je krenuo po novi paket
               ResultSet rs99 = stm9.executeQuery();
               if (rs99.next()) {
                   
                   Do = rs99.getInt(2);
               
               }
               int Xod = 0;
               int Yod = 0;
               int Xdo = 0;
               int Ydo = 0;
               
               
               stm8.setInt(1, Od);
               ResultSet rs8 = stm8.executeQuery();
               if(rs8.next()) {
                   Xod = rs8.getInt(1);
                   Yod = rs8.getInt(2);
               }
               
               stm8.setInt(1, Do);
               ResultSet rs88 = stm8.executeQuery();
               if(rs88.next()) {
                   Xdo = rs88.getInt(1);
                   Ydo = rs88.getInt(2);
               }
               
               int tip = 0;
               BigDecimal potrosnja = new BigDecimal(1);
               stm10.setInt(1,sifraKurira);
               ResultSet rs10 = stm10.executeQuery();
               if(rs10.next()) {
                   potrosnja = rs10.getBigDecimal(1);
                   tip = rs10.getInt(2);
               }
                
               double distance = euclidean(Xod, Yod, Xdo, Ydo); 
                
               BigDecimal cena = new BigDecimal(1);
           
               switch(tip) {
               case 0 : cena = new BigDecimal(15);break;
               case 1 : cena = new BigDecimal(32);break;
               case 2 : cena = new BigDecimal(36);
           }
               BigDecimal potrosnjaGoriva  = (BigDecimal.valueOf(distance).multiply(potrosnja).multiply(cena));
               
               BigDecimal potrosnja2 = racunaj(SifZs.get(0), sifraKurira);
               
               BigDecimal ukupno = (potrosnja2.add(potrosnjaGoriva)).setScale(3,RoundingMode.HALF_UP);
               
              //stavi paket da je isporucen , status = 3
              stm3.setInt(1,3);
              stm3.setInt(2,sifP);
              stm3.executeUpdate();
              
              //inseruj u voznju potroseno gorivo
              stm4.setBigDecimal(1,ukupno);
              stm4.setInt(2,sifP);
              
              
              stm4.executeUpdate();
               
              //ako je to bio poslednji paket u voznji,izracunaj profit i obrisi sve iz voznje sa tog kurira
              if (SifZs.size() == 1) {
                  stm6.setInt(1,sifraKurira);
                  stm6.setInt(2, sifraKurira);
                  stm6.executeUpdate();//ubaci profit
                  
                  stm1.setInt(1,0);
                  stm1.setInt(2,sifraKurira);// kurir - ne vozi
                  stm1.executeUpdate();
                  
                  
                  List<Integer> lista = new ArrayList<>();
                  stm11.setInt(1,sifraKurira);
                  ResultSet rs11 = stm11.executeQuery();
                  while(rs11.next()) {
                      lista.add(rs11.getInt(1));
                  }
                  
                  for(int i = 0 ; i < lista.size();i++) {
                      stm5.setInt(1,lista.get(i));
                      stm5.executeUpdate();
                  }
              }
              
              return SifZs.get(0);
                
           }
           
           
           
       } catch (SQLException ex) {
           Logger.getLogger(km200432_Package.class.getName()).log(Level.SEVERE, null, ex);
       }
        
        
      return -2;
      
    }

    
    
    private BigDecimal racunaj(Integer sifZ,int sifraKurira) { //vrati potrosnju goriva i povecaj br poslatih paketa korisnika, povecj brIsporucenih kod Kurira
        
        Connection conn = DB.getInstance().getConnection();
        int sifraKorisnika = 0;
        int Od = 0;
        int Do = 0;
        int Xod = 0;
        int Yod = 0;
        int Xdo = 0;
        int Ydo = 0;
        int tip = 0;
        BigDecimal potrosnja = new BigDecimal(1);
        
        
       try(PreparedStatement stm = conn.prepareStatement("Select SifK,OpstinaOd,OpstinaDo from ZahtevZaPrevoz where SifZ = ?")
           ;PreparedStatement stm1 = conn.prepareStatement("Update Korisnik set BrPoslatihPaketa = BrPoslatihPaketa + 1 where SifK  = ?");
            PreparedStatement stm2 = conn.prepareStatement("select X,Y from Opstina where SifO = ?");
               PreparedStatement stm3 = conn.prepareStatement("select v.Potrosnja,v.Tip from Vozilo v join Kurir k on(v.SifV = k.SifV)\n" +
"where SifK = ?"); PreparedStatement stm4 = conn.prepareStatement("update Kurir set BrIsporucenihPaketa = BrIsporucenihPaketa + 1 where SifK = ?")
               
              ) {
           
           stm.setInt(1,sifZ);
           ResultSet rs = stm.executeQuery();
           
           if (rs.next()) {
               sifraKorisnika = rs.getInt(1);
               Od = rs.getInt(2);
               Do = rs.getInt(3);
           }
           
           stm1.setInt(1,sifraKorisnika);
           stm1.executeUpdate();
           
           stm2.setInt(1, Od);
           ResultSet rs2 = stm2.executeQuery();
           if (rs2.next()) {
               Xod = rs2.getInt(1);
               Yod = rs2.getInt(2);
           }
           
           stm2.setInt(1,Do);
           ResultSet rs22 = stm2.executeQuery();
           if (rs22.next()) {
               Xdo = rs22.getInt(1);
               Ydo = rs22.getInt(2);
           }
           
           stm3.setInt(1,sifraKurira);
           ResultSet rs3 = stm3.executeQuery();
           if (rs3.next()) {
               potrosnja = rs3.getBigDecimal(1);
               tip = rs3.getInt(2);
               
           }
           
           stm4.setInt(1,sifraKurira);
           stm4.executeUpdate();
           
           BigDecimal cena = new BigDecimal(1);
           
           switch(tip) {
               case 0 : cena = new BigDecimal(15);break;
               case 1 : cena = new BigDecimal(32);break;
               case 2 : cena = new BigDecimal(36);
           }
           
           double distance = euclidean(Xod, Yod, Xdo, Ydo);
           
           BigDecimal potrosnjaGoriva  = BigDecimal.valueOf(distance).multiply(potrosnja).multiply(cena);
           
           return potrosnjaGoriva;
           
           
           
       } catch (SQLException ex) {
           Logger.getLogger(km200432_Package.class.getName()).log(Level.SEVERE, null, ex);
       }
        
        
        
        return new BigDecimal(1);
    }
    
}
