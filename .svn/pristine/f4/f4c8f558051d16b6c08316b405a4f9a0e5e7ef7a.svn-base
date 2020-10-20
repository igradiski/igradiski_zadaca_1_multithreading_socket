/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.igradiski.zadaca_1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.igradiski.konfiguracije.Konfiguracija;
import org.foi.nwtis.igradiski.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.igradiski.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.igradiski.konfiguracije.NemaKonfiguracije;
import static org.foi.nwtis.igradiski.zadaca_1.ServerAviona.PronadiSlobodnuDretvu;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Korisnik
 */
public class ServerAvionaTest {
    ServerAviona instance = null;
    Konfiguracija testKonfig =null;
    File testFile=null;
    
    public ServerAvionaTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new ServerAviona(); 
        testFile = new File("NWTiS_igradiski_zadaca_1.txt");
        try {
            testKonfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(testFile.getName());
        } catch (NemaKonfiguracije ex) {
            Logger.getLogger(ServerAvionaTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NeispravnaKonfiguracija ex) {
            Logger.getLogger(ServerAvionaTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class ServerAviona.
     */
    @Test
    public void testMain() throws Exception {
        System.out.println("main");
        String[] args = new String[5];
        args[0]="NWTiS_igradiski_zadaca_1.txt --brojDretvi 10";
        instance.main(args);
        
    }

    /**
     * Test of setPrimajKlijente method, of class ServerAviona.
     */
    @Test
    public void testSetPrimajKlijente() {
        System.out.println("setPrimajKlijente");
        boolean primajKlijente = true;
        instance.setPrimajKlijente(primajKlijente);
    }

    /**
     * Test of getServisneDretve method, of class ServerAviona.
     */
    @Test
    public void testGetServisneDretve() {
        System.out.println("getServisneDretve");
        ArrayList<ServisAviona> expResult = instance.servisneDretve;
        ArrayList<ServisAviona> result = ServerAviona.getServisneDretve();
        assertEquals(expResult, result);
    }

    /**
     * Test of getKorisnickeDretve method, of class ServerAviona.
     */
    @Test
    public void testGetKorisnickeDretve() {
        System.out.println("getKorisnickeDretve");
        ArrayList<ZahtjevAviona> expResult = instance.korisnickeDretve;
        ArrayList<ZahtjevAviona> result = ServerAviona.getKorisnickeDretve();
        assertEquals(expResult, result);
    }

    /**
     * Test of pozoviPrekid method, of class ServerAviona.
     */
    @Test
    public void testPozoviPrekid() {
        System.out.println("pozoviPrekid");
        instance.pozoviPrekid();
    }

   

    /**
     * Test of Ugasi method, of class ServerAviona.
     */
   // @Test
    public void testUgasi() {
        System.out.println("Ugasi");
        instance.Ugasi();
    }

    /**
     * Test of DohvatiServisnuDretvu method, of class ServerAviona.
     */
    @Test
    public void testDohvatiServisnuDretvu() {
        System.out.println("DohvatiServisnuDretvu");
        instance.DohvatiServisnuDretvu();
    }

    /**
     * Test of getDatoteka method, of class ServerAviona.
     */
    @Test
    public void testGetDatoteka() {
        System.out.println("getDatoteka");
        File expResult = instance.getDatoteka();
        File result = ServerAviona.getDatoteka();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of setDatoteka method, of class ServerAviona.
     */
    @Test
    public void testSetDatoteka() {
        System.out.println("setDatoteka");
        File datoteka = testFile;
        instance.setDatoteka(datoteka);
        
        
    }

    /**
     * Test of PreuzimanjeKonfiguracije method, of class ServerAviona.
     */
    @Test
    public void testPreuzimanjeKonfiguracije() throws Exception {
        System.out.println("PreuzimanjeKonfiguracije");
        String sintaksa = "([a-z|A-Z|0-9|!|#|-|_]{0,1000}.((?:txt|xml|bin|json)))( --brojDretvi ([0-9]*$))";
        String p ="NWTiS_igradiski_zadaca_1.txt --brojDretvi 9";
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(p);
        instance.PreuzimanjeKonfiguracije(m);
    }

    /**
     * Test of ProvjeraZauzetostiPorta method, of class ServerAviona.
     */
    @Test
    public void testProvjeraZauzetostiPorta() {
        System.out.println("ProvjeraZauzetostiPorta");
        int port = Integer.parseInt(testKonfig.dajPostavku("port.avioni"));
        boolean expResult = instance.ProvjeraZauzetostiPorta(port);
        boolean result = ServerAviona.ProvjeraZauzetostiPorta(port);
        assertEquals(expResult, result);
    }

    /**
     * Test of ProvjeriPostojanjeDatoteka method, of class ServerAviona.
     */
    @Test
    public void testProvjeriPostojanjeDatoteka() throws Exception {
        System.out.println("ProvjeriPostojanjeDatoteka");
        Konfiguracija konfig = testKonfig;
        instance.ProvjeriPostojanjeDatoteka(konfig);
    }

    /**
     * Test of UspostaviPocetnoStanje method, of class ServerAviona.
     */
    @Test
    public void testUspostaviPocetnoStanje() throws Exception {
        System.out.println("UspostaviPocetnoStanje");
        String intervalPohrane = testKonfig.dajPostavku("interval.pohrane.aviona");
        String datotekaAvioni = testKonfig.dajPostavku("datoteka.avioni");
        Konfiguracija konfig = testKonfig;
        instance.UspostaviPocetnoStanje(intervalPohrane, datotekaAvioni, konfig);
    }

    /**
     * Test of KreirajDretvuZaKorisnike method, of class ServerAviona.
     */
    @Test
    public void testKreirajDretvuZaKorisnike() {
        System.out.println("KreirajDretvuZaKorisnike");
        instance.KreirajDretvuZaKorisnike();
    }

    /**
     * Test of ProvjeriKolicinuDretvi method, of class ServerAviona.
     */
    @Test
    public void testProvjeriKolicinuDretvi() throws Exception {
        System.out.println("ProvjeriKolicinuDretvi");
        int port = Integer.parseInt(testKonfig.dajPostavku("port.avioni"));
        int brojCekaca = Integer.parseInt(testKonfig.dajPostavku("maks.cekaca"));
        ServerSocket server = new ServerSocket(port, brojCekaca);
        Socket veza = server.accept();
        instance.ProvjeriKolicinuDretvi(veza);
    }

    /**
     * Test of ProvjeriGasenjeSustava method, of class ServerAviona.
     */
    @Test
    public void testProvjeriGasenjeSustava() {
        System.out.println("ProvjeriGasenjeSustava");
        instance.ProvjeriGasenjeSustava();
        
    }

    /**
     * Test of PokreniServerZaZahtjeveKorisnika method, of class ServerAviona.
     */
    @Test
    public void testPokreniServerZaZahtjeveKorisnika() throws Exception {
        System.out.println("PokreniServerZaZahtjeveKorisnika");
        Konfiguracija konfig = testKonfig;
        int port = Integer.parseInt(konfig.dajPostavku("port.avioni"));
        int cekaci = Integer.parseInt(konfig.dajPostavku("maks.cekaca"));
        ServerSocket server = new ServerSocket(port, cekaci);
        instance.PokreniServerZaZahtjeveKorisnika(konfig, server);
    }

    /**
     * Test of ObrisiDretveKojeNisuKoristene method, of class ServerAviona.
     */
    @Test
    public void testObrisiDretveKojeNisuKoristene() {
        System.out.println("ObrisiDretveKojeNisuKoristene");
        instance.ObrisiDretveKojeNisuKoristene();
    }
       
    /**
     * Test of AktivirajDretvu method, of class ServerAviona.
     */
    @Test
    public void testAktivirajDretvu() throws IOException {
        System.out.println("AktivirajDretvu");
        int brojCekaca = Integer.parseInt(testKonfig.dajPostavku("maks.cekaca"));
        int port = Integer.parseInt(testKonfig.dajPostavku("port.avioni"));
        ServerSocket server = new ServerSocket(port, brojCekaca);
        Socket veza = server.accept();
        ZahtjevAviona slobodna = ServerAviona.PronadiSlobodnuDretvu(veza);
        ZahtjevAviona rezultat = instance.PronadiSlobodnuDretvu(veza);
        assertEquals(slobodna, rezultat);
        instance.AktivirajDretvu(veza, slobodna);
        
    }

    /**
     * Test of PronadiSlobodnuDretvu method, of class ServerAviona.
     */
    @Test
    public void testPronadiSlobodnuDretvu() throws IOException {
        System.out.println("PronadiSlobodnuDretvu");
        int brojCekaca = Integer.parseInt(testKonfig.dajPostavku("maks.cekaca"));
        int port = Integer.parseInt(testKonfig.dajPostavku("port.avioni"));
        ServerSocket server = new ServerSocket(port, brojCekaca);
        Socket veza = server.accept();
        ZahtjevAviona expResult = instance.PronadiSlobodnuDretvu(veza);
        ZahtjevAviona result = ServerAviona.PronadiSlobodnuDretvu(veza);
        assertEquals(expResult, result);
    }

    /**
     * Test of ProvjeriRaspolozivostDretvi method, of class ServerAviona.
     */
    @Test
    public void testProvjeriRaspolozivostDretvi() {
        System.out.println("ProvjeriRaspolozivostDretvi");
        boolean expResult = instance.ProvjeriRaspolozivostDretvi();
        boolean result = ServerAviona.ProvjeriRaspolozivostDretvi();
        assertEquals(expResult, result);
    }

    /**
     * Test of UcitajAvioneUMemoriju method, of class ServerAviona.
     */
    @Test
    public void testUcitajAvioneUMemoriju() throws Exception {
        System.out.println("UcitajAvioneUMemoriju");
        ObjectInputStream ois;
        ArrayList<String> poljeAviona = new ArrayList<String>();
        ois = new ObjectInputStream(new FileInputStream("avioni.bin"));
        poljeAviona = (ArrayList<String>) (ois.readObject());
        ArrayList<String> avioniSvi = poljeAviona;
        instance.UcitajAvioneUMemoriju(avioniSvi);
    }

    /**
     * Test of IspraviFormatDatuma method, of class ServerAviona.
     */
    @Test
    public void testIspraviFormatDatuma() throws Exception {
        System.out.println("IspraviFormatDatuma");
        String datum = "1997.03.14 22:22:22";
        String expResult = instance.IspraviFormatDatuma(datum);
        String result = ServerAviona.IspraviFormatDatuma(datum);
        assertEquals(expResult, result);
    }

    /**
     * Test of IspisiAvioneIzDatoteke method, of class ServerAviona.
     */
    @Test
    public void testIspisiAvioneIzDatoteke() throws Exception {
        System.out.println("IspisiAvioneIzDatoteke");
        instance.IspisiAvioneIzDatoteke();
        
    }

    /**
     * Test of UcitajAerodromeIzDatoteke method, of class ServerAviona.
     */
    @Test
    public void testUcitajAerodromeIzDatoteke() throws Exception {
        System.out.println("UcitajAerodromeIzDatoteke");
        String datotekaAerodrom = testKonfig.dajPostavku("datoteka.aerodromi");
        File aerodromi = new File(datotekaAerodrom);
        instance.UcitajAerodromeIzDatoteke(aerodromi);
        
    }

    /**
     * Test of UcitajKorisnikeIzDatoteke method, of class ServerAviona.
     */
    @Test
    public void testUcitajKorisnikeIzDatoteke() throws Exception {
        System.out.println("UcitajKorisnikeIzDatoteke");
        String datotekaKorisnika = testKonfig.dajPostavku("datoteka.korisnika");
        File result = new File(datotekaKorisnika);
        instance.UcitajKorisnikeIzDatoteke(result);
    }

    /**
     * Test of DohvatiUsernamePassword method, of class ServerAviona.
     */
    @Test
    public void testDohvatiUsernamePassword() throws IOException {
        FileReader fr = null;
        try {
            System.out.println("DohvatiUsernamePassword");
            ArrayList<String> korisniciString = new ArrayList<>();
            String datotekaKorisnika = testKonfig.dajPostavku("datoteka.korisnika");
            File korisnici = new File(datotekaKorisnika);
            fr = new FileReader(korisnici);
            BufferedReader bf = new BufferedReader(fr);
            StringBuilder sb = new StringBuilder();
            String line = bf.readLine();
            while (line != null) {
                if (line.contains("key=")) {
                    korisniciString.add(line);
                }
                line = bf.readLine();
            }   instance.DohvatiUsernamePassword(korisniciString);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServerAvionaTest.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(ServerAvionaTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Test of ProvjeriPostojanjeDatoteke method, of class ServerAviona.
     * DONE
     */
    @Test
    public void testProvjeriPostojanjeDatoteke() {
        System.out.println("ProvjeriPostojanjeDatoteke");
        String datotekaAvioni = "avioni.bin";
        boolean expResult = instance.ProvjeriPostojanjeDatoteke(datotekaAvioni);
        boolean result = ServerAviona.ProvjeriPostojanjeDatoteke(datotekaAvioni);
        assertEquals(expResult, result);
    }
    
     /**
     * Test of PrekiniRadSvihDretvaSustava method, of class ServerAviona.
     */
    @Test
    public void testPrekiniRadSvihDretvaSustava() {
        System.out.println("PrekiniRadSvihDretvaSustava");
        instance.PrekiniRadSvihDretvaSustava();
    }
    
}
