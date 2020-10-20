package org.foi.nwtis.igradiski.zadaca_1;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.igradiski.konfiguracije.Konfiguracija;
import org.foi.nwtis.igradiski.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.igradiski.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.igradiski.konfiguracije.NemaKonfiguracije;
import static org.foi.nwtis.igradiski.zadaca_1.ServerSimulatoraLeta.kolekcijaLetovaAviona;

public class ServerSimulatoraLeta {

    public static  ArrayList<Avion> kolekcijaLetovaAviona;
    Socket klijent;
    Konfiguracija konfig;
    /**
     * Konstruktor klase simulatorLeta
     * @param klijent socket klijenta
     * @param konfig konfiguracija
     */
    public ServerSimulatoraLeta(Socket klijent, Konfiguracija konfig) {
        this.klijent = klijent;
        this.konfig = konfig;
        this.kolekcijaLetovaAviona = null;
    }
    
    /**
     * Main metoda za pokretanje koja prima konf datoteku
     * @param args argumenti kroz konzolu
     * @throws NemaKonfiguracije
     * @throws NeispravnaKonfiguracija 
     */
    public static void main(String[] args) throws NemaKonfiguracije, NeispravnaKonfiguracija {
        String sintaksa = "([a-z|A-Z|0-9|!|#|-|_]{0,1000}.((?:txt|xml|bin|json)))";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String p = sb.toString().trim();
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(p);
        boolean status = m.matches();
        if (!status) {
            System.out.println("Ne odgovara!");
            return;
        }
        File f = new File(m.group(1));
        if (f.exists()) {
            CitanjeKonfiguracije(f);
        } 
        else {
            return;
        }
    }
    /**
     * Cita konfiguraciju iz primljene datoteke
     * @param f datoteka koja je dana kao parametar kod pokretanja
     * @throws NemaKonfiguracije
     * @throws NeispravnaKonfiguracija 
     */
    public static void CitanjeKonfiguracije(File f) throws NemaKonfiguracije, NeispravnaKonfiguracija {
        Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(f.getName());
        int port = Integer.parseInt(konfig.dajPostavku("port.simulator"));
        int maksCekaca = Integer.parseInt(konfig.dajPostavku("maks.cekaca"));
        boolean portSlobodan = ProvjeraZauzetostiPorta(port);
        if (portSlobodan) {
            kolekcijaLetovaAviona = new ArrayList<>();
            KreirajServerSocket(port, maksCekaca, konfig);
        } else {
            System.out.println("Port je zauzet !");
            return;
        }
    }
    /**
     * Kreiranje server socketa na odredenom portu i s brojem cekaca
     * @param port - port za server
     * @param brojCekaca - max broj cekaca
     * @param konfig -konfiguracija za server
     */
    public static void KreirajServerSocket(int port, int brojCekaca, Konfiguracija konfig) {
        try {
            ServerSocket server = new ServerSocket(port, brojCekaca);
            System.out.println("Čekam vezu na vratima: " + port);
            System.out.println("Maks broj čekača: " + brojCekaca);
            boolean slijedno = true;
            while (true) {
                Socket veza = server.accept();
                ServerSimulatorLetaDretva dretva = new ServerSimulatorLetaDretva(veza);
                dretva.start();
                if (slijedno) {
                    dretva.join();
                }
            }
        } catch (IOException | InterruptedException e) {
            System.err.println(e);
        }
    }

    /**
     * Funckija koja provjerava zauzetost porta
     * @param port port koji sluzi za provjeru
     * @return 
     */
    public static boolean ProvjeraZauzetostiPorta(int port) {
        boolean portFree;
        try (var ignored = new ServerSocket(port)) {
            portFree = true;
            return portFree;
        } catch (IOException e) {
            portFree = false;
            return portFree;
        }
    }
}

 class ServerSimulatorLetaDretva extends Thread {
     
    String odgovorServera="";
    Socket veza;
    boolean tocnostKomande = false;
    static int BrojDretve=0;
    
    /**
     * Konstruktor dretve
     * @param veza  veza za komunikaciju s korisnikom
     */
    public ServerSimulatorLetaDretva(Socket veza) {
         super("Dretva" + BrojDretve);
         BrojDretve++;
         this.veza = veza;
     }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    @Override
     public void run() {
         try {
             InputStream inps = veza.getInputStream();
             OutputStream outs = veza.getOutputStream();
             StringBuilder tekst = new StringBuilder();
             while (true) {
                 int i = inps.read();
                 if (i == -1) {
                     break;
                 }
                 tekst.append((char) i);
             }
             ProvjeriPristigluKomandu(tekst);
             if(tocnostKomande)
                 outs.write(odgovorServera.getBytes());
             else 
                 outs.write("ERROR 20; Komanda nije tocno napisana!".getBytes());
             outs.flush();
             veza.shutdownOutput();
             veza.shutdownInput();
             veza.close();
         } catch (IOException ex) {
             Logger.getLogger(ServerSimulatorLetaDretva.class.getName()).log(Level.SEVERE, null, ex);
         } catch (ParseException ex) {
            Logger.getLogger(ServerSimulatorLetaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
     }
         

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Funkcija provjerava pristiglu komandu 
     * @param tekst pristigla komanda
     * @throws ParseException 
     */
     public synchronized void ProvjeriPristigluKomandu(StringBuilder tekst) throws ParseException {
         String komanda = tekst.toString();
         if (komanda.contains("POLIJETANJE") || komanda.contains("SLIJETANJE")) {
             ObradiKomanduPoleti(komanda);
         }
         if (komanda.contains("POZICIJA")) {
             ObradiKomanduPozicija(komanda);
         }
     }
     /**
      * Ako je komanda poleti obraduje i kontrolira ispravnost
      * @param komanda komanda koja je stigla
      * @throws ParseException 
      */
     public synchronized void ObradiKomanduPoleti(String komanda) throws ParseException {
         String sintaksa = "(LET [a-z|A-Z|0-9|_|-]{3,50}; )(POLIJETANJE ([0-9]{4}.)"
                 + "([0-1][0-9].)([0-3][0-9] )([0-1][0-9]|[2][0-3]):([0-5][0-9]):"
                 + "([0-5][0-9]); )(SLIJETANJE ([0-9]{4}.)([0-1][0-9].)([0-3][0-9] )"
                 + "([0-1][0-9]|[2][0-3]):([0-5][0-9]):([0-5][0-9]);)";
         Pattern pattern = Pattern.compile(sintaksa);
         Matcher m = pattern.matcher(komanda);
         boolean status = m.matches();
         tocnostKomande = status;
         if (status) {
             DohvatiPodatkeKomandePoleti(komanda, m);
         } else {
             System.out.println("Ne odgovara!");
         }
     }
     /**
      * Ako je komanda pozicija obraduje i kontrolira ispravnost
      * @param komanda komanda koja je pristigla
      * @throws ParseException 
      */
     public synchronized void ObradiKomanduPozicija(String komanda) throws ParseException {
         String sintaksa = "(POZICIJA: [a-z|A-Z|0-9|_|-]{3,50};)";
         Pattern pattern = Pattern.compile(sintaksa);
         Matcher m = pattern.matcher(komanda);
         boolean status = m.matches();
         tocnostKomande = status;
         if (status) {
             DohvatiPodatkeKomandePozicija(komanda, m);
         } else {
             System.out.println("Ne odgovara!");
         }
     }
     /**
      * Funkcija preuzima podatke koji su stigli s komandom
      * @param komanda pristigla komanda
      * @param m parametri u komandi
      * @throws ParseException 
      */
     public synchronized void DohvatiPodatkeKomandePoleti(String komanda, Matcher m) throws ParseException {
         String let = m.group(1).substring(4, m.group(1).length() - 2);
         String polijetanje = m.group(2).substring(12, m.group(2).length() - 2);
         String slijetanje = m.group(9).substring(11, m.group(9).length() - 1);
         boolean avionPostoji = ProvjeriPostojanjeAviona(let);
         if (avionPostoji) {
             boolean avionLeti = ProvjeriLetiLiAvion(let);
             if (avionLeti) {
                 odgovorServera = "ERROR 21; Avion je jos u letu!";
             } else {
                 ObrisiAvion(let);
             }
         } else {
             Avion a = new Avion(let, polijetanje, slijetanje, "", "");
             ServerSimulatoraLeta.kolekcijaLetovaAviona.add(a);
             odgovorServera = "OK;";
         }
     }
     /**
      * Komanda koja brise avion iz liste kada je potrebno
      * @param let avion koji se provjerava
      */
     public synchronized void ObrisiAvion(String let) {
         Avion a = null;
         if (!ServerSimulatoraLeta.kolekcijaLetovaAviona.isEmpty()) {
             for (Avion avion : ServerSimulatoraLeta.kolekcijaLetovaAviona) {
                 if (avion.let.equals(let)) {
                     a = avion;
                 }
             }
             ServerSimulatoraLeta.kolekcijaLetovaAviona.remove(a);
             odgovorServera = "OK;";
         } else {
             odgovorServera = "ERROR 22; Ne moze se brisati jer je lista prazna!;";
         }
     }
     /**
      * Funckija provjerava da li je avion jos uvijek u letu
      * @param let
      * @return true ako leti, false ako ne leti
      * @throws ParseException 
      */
     public synchronized boolean ProvjeriLetiLiAvion(String let) throws ParseException {
         boolean leti = false;
         Date trenutniDatum = new Date(System.currentTimeMillis());
         for (Avion avion : ServerSimulatoraLeta.kolekcijaLetovaAviona) {
             if (avion.let.equals(let)) {
                 if (trenutniDatum.compareTo(avion.slijetanje) < 0) {
                     leti = true;
                 }
                 if (trenutniDatum.compareTo(avion.slijetanje) > 0) {
                     leti = false;
                 }
             }
         }
         return leti;
     }
     /**
      * Funkcija za kontrolu putem ispisa liste aviona
      */
     public synchronized static void IspisTest() {
         for (Avion avion : ServerSimulatoraLeta.kolekcijaLetovaAviona) {
             System.out.println(avion.let + " " + avion.polijetanje + " " + avion.slijetanje);
         }
     }
     /**
      * Funkcija provjerava postoji li avion u listi
      * @param let avion koji se provjerava
      * @return  true postoji, false ne postoji
      */
     public synchronized boolean ProvjeriPostojanjeAviona(String let) {
         boolean postojiAvion = false;
         for (Avion avion : ServerSimulatoraLeta.kolekcijaLetovaAviona) {
             if (avion.let.equals(let)) {
                 postojiAvion = true;
             }
         }
         return postojiAvion;
     }
     
     /**
      * Dohvaća i izvrsava komandu  pozicija te vraća odgovor ovisno o podacima
      * ok leti ok sletio ili avion ne postoji
      * @param komanda
      * @param m
      * @throws ParseException 
      */
     public synchronized void DohvatiPodatkeKomandePozicija(String komanda, Matcher m) throws ParseException {
         String let = m.group(1).substring(10, m.group(1).length() - 1);
         boolean postoji = ProvjeriPostojanjeAviona(let);
         if (postoji) {
             boolean avionLeti = ProvjeriLetiLiAvion(let);
             if (avionLeti) {
                 odgovorServera = "OK; LETI;";
             } else {
                 odgovorServera = "OK; SLETIO;";
             }
         } else {
             odgovorServera = "ERROR 23; Avion ne postoji!";
         }
     }

 }
