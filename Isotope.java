public class Isotope {
    
    private float atomNum;
    private String isoName;
    private float isoNum;
    private String symbol;
    private String isoMass;
    private String isoComp;
    private String isoWeight;
    private String abundance;
    
    public Isotope (float atomNum , String isoName, float isoNum, String symbol, String isoMass, String isoComp, String isoWeight, String abundance) {
       this.atomNum = atomNum;
       this.isoName = isoName;
       this.isoNum = isoNum;
       this.symbol = symbol;
       this.isoMass = isoMass;
       this.isoComp = isoComp;
       this.isoWeight = isoWeight;
       this.abundance = abundance;
    }
    
    public int getAtomNum(){
        return Math.round(atomNum);
    }
   
    public String getIsoName(){
        return isoName;
    }

    public String getIsoNum() {
        return Float.toString(isoNum);
    }
    
    public String getMass() {
        return "" + isoMass;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getIsoComp() {
        return isoComp;
    }

    public String getIsoWeight() {
        return isoWeight;
    }

    public String getAbundance() {
        return abundance;
    }
}