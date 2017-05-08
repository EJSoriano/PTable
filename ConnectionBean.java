
import com.sun.org.apache.xml.internal.serializer.ToHTMLStream;
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
    private List<Element> elements = new ArrayList<Element>();
    private List<Isotope> isotopes = new ArrayList<Isotope>();
    private int currRow = 0;
    Element currElement;
    private int elementReportNum = -1;
    private int currElementNum;
    private List<Isotope> isotopesOfAtomList;
    private List<Isotope> isotopesOfAtomListReport;

    public List<Isotope> getIsotopesOfAtomListReport(String atomNum) {
        int num = Integer.parseInt(atomNum);
        isotopesOfAtomListReport = new ArrayList<Isotope>();
        for(Isotope i : isotopes) {
            if(i.getAtomNum() == num)
                isotopesOfAtomListReport.add(i);
        }
        return isotopesOfAtomListReport;
    }

    public void setIsotopesOfAtomListReport(List<Isotope> isotopesOfAtomListReport) {
        this.isotopesOfAtomListReport = isotopesOfAtomListReport;
    }

    public List<Isotope> getIsotopesOfAtomList() {
        isotopesOfAtomList = new ArrayList<Isotope>();
        if(currElement.getAtomNum().isEmpty()) {
            currElementNum++;
            if(currElementNum >= elements.size()) {
                currElementNum = 0;
            }
            currElement = elements.get(currElementNum);
            return isotopesOfAtomList;
        }
        int atomNum = Integer.parseInt(currElement.getAtomNum());
        for (Isotope i : isotopes) {
            if (i.getAtomNum() == atomNum) {
                isotopesOfAtomList.add(i);
            }
        }
        currElementNum++;
        if(currElementNum >= elements.size()) {
            currElementNum = 0;
        }
        currElement = elements.get(currElementNum);
        return isotopesOfAtomList;
    }

    public void setIsotopesOfAtomList(List<Isotope> isotopesOfAtomList) {
        this.isotopesOfAtomList = isotopesOfAtomList;
    }
    

    public ConnectionBean() {
        connect();
        initializeData();
    }
    
    public ConnectionBean(Connection conn) {
        this.conn = conn;
        initializeData();
    }

    //Establishes Connection
    public void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://periodictable.cb2vg6rxwemx.us-west-2.rds.amazonaws.com:3306/cecs493ptable", "root", "z_?PvVIr,HcuK_2vU9ldbE_i0");
            System.out.println("Database connection established...");
        } catch (Exception e) {
            System.err.println("Connection Error: " + e);
        }
    }
    
    public void displayElementsArrayList() {
        for(Element e : elements) {
            System.out.println(e.getName() + ", " + e.getxCoord() + ", " + e.getyCoord());
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

            System.out.println(getLargestX() +"," + getLargestY());
            for (int i = 1; i <= getLargestY(); i++) {
                for (int j = 1; j <= getLargestX(); j++) {
                    JSONObject json = getElementAt(j, i);
                    System.out.println("SOMETHING");
                    if(json!=null)System.out.println(json.getString("elemName") + ", " + json.getString("xCor") + ", " + json.getString("yCor"));
                    if (json != null) {
                        Element el = new Element("visible", json.getString("elemName"), json.getString("atomSymb"),
                                json.getString("atomNum"), json.getString("xCor"), json.getString("yCor"),
                                json.getString("mass"), json.getString("groupNum"), json.getString("period"),
                                json.getString("state25"), json.getString("valences"), json.getString("elecConf"),
                                json.getString("density"), json.getString("series"));
                        elements.add(el);
                        //System.out.println(el.getName() + ",x: " + el.getxCoord() + ",y:" + el.getyCoord() + ",j:" + j +",i:" + i);
                    } else {
                        elements.add(new Element("invisible", "", "", "", "", "", "", "", "", "", "", "", "", ""));
                       // System.out.println("blank");
                    }
                }
                
            }
            currElement = elements.get(0);

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
                isoMass = rs.getString("MassOfAtom");
                isoComp = rs.getString("Isotopic Composition");
                isoWeight = rs.getString("Standard Atomic Weight");
                isoAbundance = rs.getString("Abundance");

                isotopes.add(new Isotope(isoAtomNum, isoName, isoNum, isoSymb, isoMass, isoComp, isoWeight, isoAbundance));
                //Element e = elements
            }
        } catch (Exception e) {

        }
        System.out.println("\nData has been initialized...\n");
    }

    //Returns the elements (and invisible elements) as an arraylist. 
    public List<Element> getElements() {
        return elements;
    }

    //Returns connection, used for initializing the FunctionalityBean
    public Connection getCon() {
        return conn;
    }

    //Gets an element at the specified coordinates as a JSONObject. Only used in InitializeData.
    public JSONObject getElementAt(int x, int y) {
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
    
    public Element getElementByName(String name) {
        for(Element e : elements) {
            if (e.getName().equals(name)) {
                return e;
            }
        } return null;
    }
    
    public ArrayList<String> getElementNames() {
        ArrayList<String> names = new ArrayList<String>();
        for(Element e : elements) {
            if(!e.getName().isEmpty())
                names.add(e.getName());
        }
        return names;
    }

    //Gets the largest X coordinate of any element. Only used in InitializeData and getNextRow.
    public int getLargestX() {
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
    public int getLargestY() {
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
    public ArrayList<Isotope> getIsotopesOfAtom() {
        
        ArrayList<Isotope> isotopesOfAtom = new ArrayList<Isotope>();
        if(currElement.getAtomNum().isEmpty()) {
            isotopesOfAtom.add(new Isotope(0, "", 0, "", "", "", "", ""));
            return isotopesOfAtom;
        }
        int atomNum = Integer.parseInt(currElement.getAtomNum());
        for (Isotope i : isotopes) {
            if (i.getAtomNum() == atomNum) {
                isotopesOfAtom.add(i);
            }
        }
        currElementNum++;
        if(currElementNum >= elements.size()) {
            currElementNum = 0;
        }
        currElement = elements.get(currElementNum);
        return isotopesOfAtom;
        
    }
    
    public ArrayList<Element> getAllElements() {
        ArrayList<Element> all = new ArrayList<Element>();
        for(Element e: elements) {
            if(!e.getName().equals("")) {
                all.add(e);
            }
        }
        return all;
    }
    
    //Example of how to instantiate the Functionality Bean
    public static void main(String args[]) throws Exception {
        ConnectionBean cb = new ConnectionBean();
        JSONObject e = cb.getElementAt(18, 10);
        
//        System.out.println(cb.getElementAt(2, 1).get("elemName"));
        cb.displayElementsArrayList();
        System.out.println(e);
        
        
    }
}
