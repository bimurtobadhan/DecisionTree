/**
 * Created by bimurto on 27-Nov-15.
 */
public class DataUnit {
    int attr[] , result, weight;

    public DataUnit(int attr[], int result){
        this.attr = new int[9];
        for(int i=0;i<9;i++){
            this.attr[i] = attr[i];
        }
        this.result = result;
    }

    @Override
    public String toString() {
        String s = "";
        for(int i=0;i<9;i++){
            s += i + " : " + attr[i] + ", ";
        }
        s += " :: " + result +" weight: "+ weight;
        return s;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
