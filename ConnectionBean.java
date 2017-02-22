package Connection;

import java.io.Serializable;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.sql.*;
import java.util.*;
import org.json.JSONObject;
import org.json.JSONArray;

import javax.inject.Named;
import javax.enterprise.context.Dependent;

@Named(value = "dbconnectionBean")
@Dependent
public class ConnectionBean implements Serializable {

    private static Connection conn;
    private JSONArray everything = new JSONArray();
    private ArrayList<String> elements = new ArrayList<>();

    public ConnectionBean() {

    }

    public void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://periodictable.cb2vg6rxwemx.us-west-2.rds.amazonaws.com:3306/cecs493ptable", "root", "Redbull11!");
            System.out.println("Database connection established...");
        } catch (Exception e) {
            System.err.println("Connection Error: " + e);
        }
    }

    //Puts all of the elements into JSON objects and places them in a JSON array
    public void JSONify() {
        long before = 0;
        long after = 0;
        long total = 0;
        try {
            Statement select = conn.createStatement();
            ResultSet rs = select.executeQuery("call sp_getallelements()");

            before = System.currentTimeMillis();
            while (rs.next()) {

                String elemName = rs.getString("Elementname");
                String atomSymb = rs.getString("AtomicSymbol");
                String atomNum = rs.getString("AtomicNumber");
                String mass = rs.getString("atomicmass");
                String groupNum = rs.getString("groupnumber");
                String period = rs.getString("Period");
                String state25 = rs.getString("stateofmatter25");
                String valences = rs.getString("valences");
                String elecConf = rs.getString("configuration");
                String density = rs.getString("Density");
                String series = rs.getString("series");
                String xCor = rs.getString("xCoord");
                String yCor = rs.getString("yCoord");

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

                everything.put(element);

            }
            after = System.currentTimeMillis();
            total = after - before;
            System.out.println("Time: " + total);
        } catch (Exception e) {

        }
        System.out.println("\nData has been JSONified...\n");
    }

    public JSONArray getEverything() {
        return everything;
    }

    public JSONObject getElement(int i) {
        JSONObject elem = new JSONObject();
        try {
            elem = everything.getJSONObject(i);
        } catch (Exception e) {
        }
        return elem;
    }

    public String getName(JSONObject jo) {
        String value = "";
        try {
            value = jo.getString("elemName");
        } catch (Exception e) {
        }
        return value;
    }

    public String getSymbol(JSONObject jo) {
        String value = "";
        try {
            value = jo.getString("atomSymb");
        } catch (Exception e) {
        }
        return value;
    }

    public String getAtomNum(JSONObject jo) {
        String value = "";
        try {
            value = jo.getString("atomNum");
        } catch (Exception e) {
        }
        return value;
    }

    public String getMass(JSONObject jo) {
        String value = "";
        try {
            value = jo.getString("mass");
        } catch (Exception e) {
        }
        return value;
    }

    public String getGroup(JSONObject jo) {
        String value = "";
        try {
            value = jo.getString("groupNum");
        } catch (Exception e) {
        }
        return value;
    }

    public String getPeriod(JSONObject jo) {
        String value = "";
        try {
            value = jo.getString("period");
        } catch (Exception e) {
        }
        return value;
    }

    public String getDensity(JSONObject jo) {
        String value = "";
        try {
            value = jo.getString("density");
        } catch (Exception e) {
        }
        return value;
    }

    public String getSeries(JSONObject jo) {
        String value = "";
        try {
            value = jo.getString("series");
        } catch (Exception e) {
        }
        return value;
    }

    public String getState25(JSONObject jo) {
        String value = "";
        try {
            value = jo.getString("state25");
        } catch (Exception e) {
        }
        return value;
    }

    /*
    public String getValences(JSONObject jo) {
        String value = "";
        try {
            value = jo.getString("valences");
        } catch (Exception e) {
        }
        return value;
    }

    public String getElecConf(JSONObject jo) {
        String value = "";
        try {
            value = jo.getString("elecConf");
        } catch (Exception e) {
        }
        return value;
    }

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
     */
    public boolean LSI(JSONObject jo) {
        boolean indicator = false;
        try {
            String series = jo.getString("series");
            if (series.equalsIgnoreCase("lanthanide")) {
                indicator = true;
            }
        } catch (Exception e) {
        }
        return indicator;
    }

    public boolean ASI(JSONObject jo) {
        boolean indicator = false;
        try {
            String series = jo.getString("series");
            if (series.equalsIgnoreCase("actinide")) {
                indicator = true;
            }
        } catch (Exception e) {
        }
        return indicator;
    }

    //Tester
    public static void main(String[] args) {
        ConnectionBean con = new ConnectionBean();
        con.connect();
        con.JSONify();

        //Returns an element as a JSON Object at JSONArray[0]
        System.out.println(con.getElement(0));

        //Returns the element name of the JSON Object at JSONArray[0]
        System.out.println(con.getName(con.getElement(0)));
    }
}
