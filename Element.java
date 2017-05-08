
import java.util.ArrayList;

    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class Element {
    
    private String style;
    private String name;
    private String symbol;
    private String atomNum;
    private String mass;
    private String groupNumber;
    private String period;
    private String stateOfMatter25;
    private String valences;
    private String configuration;
    private String density;
    private String series;
    private int xCoord;
    private int yCoord;
    private ArrayList<Isotope> isotopes;

    public ArrayList<Isotope> getIsotopes() {
        return isotopes;
    }
    
    public void displayElement() {
        System.out.println(style + name + symbol + atomNum + mass + groupNumber + period + stateOfMatter25 + valences + configuration + density + series + xCoord + yCoord);
    }

    public void setIsotopes(ArrayList<Isotope> isotopes) {
        this.isotopes = isotopes;
    }

    public String getMass() {
        return mass;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public String getPeriod() {
        return period;
    }

    public String getStateOfMatter25() {
        return stateOfMatter25;
    }

    public String getValences() {
        return valences;
    }

    public String getConfiguration() {
        return configuration;
    }

    public String getDensity() {
        return density;
    }

    public String getSeries() {
        return series;
    }

    public int getxCoord() {
        return xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }
    

    public Element(String style, String name, String symbol, String atomNum, String x, String y, String mass, String groupNumber,
            String period, String stateOfMatter25, String valences, String configuration, String density, String series) {
        this.style = style;
        this.name = name;
        this.symbol = symbol;
        this.atomNum = atomNum;
        if(x.equals("")) {
            xCoord = -1;
            yCoord = -1;
        } else {
            xCoord = Integer.parseInt(x);
            yCoord = Integer.parseInt(y);
        }
        
        this.mass = mass;
        this.groupNumber = groupNumber;
        this.period = period;
        this.stateOfMatter25 = stateOfMatter25;
        this.valences = valences;
        this.configuration = configuration;
        this.density = density;
        this.series = series;
    }

    public String getStyle() {
        return style;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }
    
    public String getAtomNum() {
        return atomNum;
    }
    
    public String getId() {
        if(xCoord == -1) {
            return "ab";
        }
        int prefix = 10 + (yCoord - 1) * 8 ;
        int suffix = xCoord - 1;
        return "#j_idt" + prefix + "\\:" + suffix + "\\:" + "ab";
    }
    
}
