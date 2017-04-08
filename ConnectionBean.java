package Connection;

import java.io.Serializable;
import java.sql.*;
import java.util.*;

import org.json.JSONObject;
import org.json.JSONArray;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;

@Named(value = "dbconnectionBean")
@SessionScoped
public class ConnectionBean implements Serializable {

    private Connection conn;
    private JSONArray elementList = new JSONArray();
    private ArrayList<Element> elements = new ArrayList<Element>();
    private ArrayList<Isotope> isotopes = new ArrayList<Isotope>();
    private int currRow = 0;

    public ConnectionBean() {
        connect();
        initializeData();
    }

    //Establishes Connection
    public void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection();
            System.out.println("Database connection established...");
        } catch (Exception e) {
            System.err.println("Connection Error: " + e);
        }
    }

    //Initializes element and isotope data, construct main table. ISOTOPE DISPLAY NOT INCLUDED.
    public void initializeData() {
        String elemName = "";
        String atomSymb = "";
        String atomNum = "";
        String mass = "";
        String groupNum = "";
        String period = "";
        String state25 = "";
        String valences = "";
        String elecConf = "";
        String density = "";
        String series = "";
        String xCor = "";
        String yCor = "";
        try {

            //Elements
            Statement select = conn.createStatement();
            ResultSet rs = select.executeQuery("call sp_getallelements()");

            while (rs.next()) {
                elemName = rs.getString("Elementname");
                atomSymb = rs.getString("AtomicSymbol");
                atomNum = rs.getString("AtomicNumber");
                mass = rs.getString("atomicmass");
                groupNum = rs.getString("groupnumber");
                period = rs.getString("Period");
                state25 = rs.getString("stateofmatter25");
                valences = rs.getString("valences");
                elecConf = rs.getString("configuration");
                density = rs.getString("Density");
                series = rs.getString("series");
                xCor = rs.getString("xCoord");
                yCor = rs.getString("yCoord");

                JSONObject element = new JSONObject();
                element.put("elemName", elemName);
                element.put("atomSymb", atomSymb);
                element.put("atomNum", atomNum);
                element.put("mass", mass);
                element.put("groupNum", groupNum);
                element.put("period", period);
                element.put("state25", state25);
                element.put("valences", valences);
                element.put("elecConf", elecConf);
                element.put("density", density);
                element.put("series", series);
                element.put("xCor", xCor);
                element.put("yCor", yCor);

                elementList.put(element);
            }

            for (int i = 1; i <= getLargestY(); i++) {
                for (int j = 1; j <= getLargestX(); j++) {
                    JSONObject json = getElementAt(j, i);
                    if (json != null) {
                        Element el = new Element("visible", json.getString("elemName"), json.getString("atomSymb"),
                                json.getString("atomNum"), json.getString("xCor"), json.getString("yCor"),
                                json.getString("mass"), json.getString("groupNum"), json.getString("period"),
                                json.getString("state25"), json.getString("valences"), json.getString("elecConf"),
                                json.getString("density"), json.getString("series"));
                        elements.add(el);
                    } else {
                        elements.add(new Element("invisible", "", "", "", "", "", "", "", "", "", "", "", "", ""));
                    }
                }
            }

            //Isotopes
            Float isoAtomNum = 0f;
            String isoName = "";
            Float isoNum = 0f;
            String isoSymb = "";
            String isoMass = "";
            String isoComp = "";
            String isoWeight = "";
            String isoAbundance = "";

            rs = select.executeQuery("call sp_getallisotopes()");
            while (rs.next()) {
                isoAtomNum = rs.getFloat("Atomic Number");
                isoName = rs.getString("IsoName");
                isoNum = rs.getFloat("IsotopeNum");
                isoSymb = rs.getString("Symbol");
                isoMass = rs.getString("RelativeAtomicMass");
                isoComp = rs.getString("Isotopic Composition");
                isoWeight = rs.getString("Standard Atomic Weight");
                isoAbundance = rs.getString("Abundance");

                isotopes.add(new Isotope(isoAtomNum, isoName, isoNum, isoSymb, isoMass, isoComp, isoWeight, isoAbundance));
            }
        } catch (Exception e) {

        }
        System.out.println("\nData has been initialized...\n");
    }

    //Returns the elements (and invisible elements) as an arraylist. 
    public ArrayList<Element> getElements() {
        return elements;
    }

    //Returns connection, used for initializing the FunctionalityBean
    public Connection getCon() {
        return conn;
    }

    //Gets an element at the specified coordinates as a JSONObject. Only used in InitializeData.
    private JSONObject getElementAt(int x, int y) {
        try {
            for (int i = 0; i < elementList.length(); i++) {
                JSONObject element = elementList.getJSONObject(i);
                if ((getX(element) == x) && (getY(element) == y)) {
                    return element;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    //Gets the largest X coordinate of any element. Only used in InitializeData and getNextRow.
    private int getLargestX() {
        int largest = 0;
        try {
            for (int i = 0; i < elementList.length(); i++) {
                JSONObject element = elementList.getJSONObject(i);
                if ((getX(element) > largest)) {
                    largest = getX(element);
                }
            }
        } catch (Exception e) {
        }
        return largest;
    }

    //Gets the largest Y coordinate of any element. Only used in InitializeData and getNextRow.
    private int getLargestY() {
        int largest = 0;
        try {
            for (int i = 0; i < elementList.length(); i++) {
                JSONObject element = elementList.getJSONObject(i);
                if ((getY(element) > largest)) {
                    largest = getY(element);
                }
            }
        } catch (Exception e) {
        }
        return largest;
    }

    //Returns the x coordinate of a JSON element. Only used in InitializeData.
    public int getX(JSONObject jo) {
        String value = "";
        int cor = 0;
        try {
            value = jo.getString("xCor");
        } catch (Exception e) {
        }
        cor = Integer.parseInt(value);
        return cor;
    }

    //Returns the y coordinate of a JSON element. Only used in InitializeData.
    public int getY(JSONObject jo) {
        String value = "";
        int cor = 0;
        try {
            value = jo.getString("yCor");
        } catch (Exception e) {
        }
        cor = Integer.parseInt(value);
        return cor;
    }

    //Creates the next row of elements in the table display and returns it as an arraylist. 
    public ArrayList<Element> getNextRow() {
        if (currRow == getLargestY()) {
            currRow = 0;
        }
        ArrayList<Element> el = new ArrayList<Element>();
        for (int i = currRow * getLargestX(); i < currRow * getLargestX() + getLargestX(); i++) {
            el.add(elements.get(i));
        }
        currRow++;
        return el;
    }

    //Prints out elements to console.
    public void displayElements() {
        try {
            for (int i = 0; i < elementList.length(); i++) {
                JSONObject element = elementList.getJSONObject(i);
                System.out.println("Element " + element.getString("elemName"));
            }
        } catch (Exception e) {
        }
    }

    //Prints out isotopes to console.
    public void displayIsotopes() {
        for (Isotope iso : isotopes) {
            System.out.println(iso.getSymbol());
        }
    }

    //Returns an arraylist of isotopes of the given atomic number. If no isotopes, returns null.
    public ArrayList<Isotope> getIsotopesOfAtom(int atomNum) {
        ArrayList<Isotope> isotopesOfAtom = new ArrayList<Isotope>();
        for (Isotope i : isotopes) {
            if (i.getAtomNum() == atomNum) {
                isotopesOfAtom.add(i);
            }
        }
        if (isotopesOfAtom.size() > 0) {
            return isotopesOfAtom;
        } else {
            return null;
        }
    }
    //Example of how to instantiate the Functionality Bean
    public static void main(String args[]) throws Exception {
        ConnectionBean cb = new ConnectionBean();
        FunctionalityBean fn = new FunctionalityBean(cb.getCon());

        fn.insertIsotope(160, "Isotope Test", 50, "TEST", 50, "IsoComp", "180", "");
        
    }
}

