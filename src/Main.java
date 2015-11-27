import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Created by Bimurto on 30-Sep-15.
 */
public class Main {



    public static Vector <DataUnit> trainset, testset;
    Random random;// = new Random();
    int positivecounter[][];
    int negetivecounter[][];
    int allcounter[][];
    Vector attrset = new Vector();
    Vector resultset = new Vector();
    double  totalAccuracy = 0;
    double  totalPrecision = 0;
    double  totalRecall = 0;
    double  totalFmeasure = 0;
    double  totalGmeasure = 0;
    double  totalGmean = 0;

    public static void main(String args[]){
        new Main();
    }

    public Main(){
        int attr = 9;
        int values = 11;

        for(int k=0;k<100;k++){
            for(int i=0;i<attr;i++)
                attrset.add(i);

            trainset = new Vector<>();
            testset = new Vector<>();
            random = new Random();

            positivecounter = new int [attr][values];
            negetivecounter = new int [attr][values];
            allcounter = new int [attr][values];

            readdata();

            double percentage = (double) testset.size();
            percentage = percentage / 683.0 ;
            System.out.println("\n" + trainset.size() + " " + testset.size() + " " + percentage);

            Node root = id3(trainset, attrset, resultset);

            traverse(root);

            test(testset, root);
        }
        System.out.println();
        System.out.println("Accuracy :" + totalAccuracy/100);
        System.out.println("Precision :" + totalPrecision/100);
        System.out.println("Recall :" + totalRecall/100);
        System.out.println("F-measure :" + totalFmeasure/100);
        System.out.println("G-measure :" + totalGmeasure/100);
        System.out.println("G-mean :" + totalGmean/100);

        System.out.println("End");
    }

    private void test(Vector<DataUnit> testset, Node root) {
        int TP=0,TN=0,FP=0,FN=0;
        for(int i=0;i<testset.size();i++){
            DataUnit unit = testset.elementAt(i);
            int t = testUnit(unit,root);
            if(t==1) TP++;
            else if(t==2) TN++;
            else if(t==3) FP++;
            else if(t==4) FN++;
//            Node node = root;
//            while(root.label == -1){
//                int attr = root.attrType;
//                root = root.child[unit.attr[attr]];
//            }

        }

        totalAccuracy += (double) (TN + TP )/ (double) (FN + FP + TP + TN);
        double precision = (double) (TP) / (double) (TP + FP);
        totalPrecision += precision;
        double recall = (double) (TP) / (double) (TP + FN);
        totalRecall += recall;
        totalFmeasure += 2*recall*precision/(recall + precision);
        totalGmeasure += Math.sqrt(precision*recall);
        double negAccuracy = (double) (TN) / (double) (TN + FP);
        totalGmean += Math.sqrt(precision*negAccuracy);

    }

    private int testUnit(DataUnit unit, Node root) {
        while(root.label == -1){
            int attr = root.attrType;
            root = root.child[unit.attr[attr]];
        }
        //System.out.println(root.label + " " + unit.result);
        if(root.label == unit.result){
            if(root.label == 1)
                return 1;
            else return 2;
        }else{
            if(root.label == 1)
                return 4;
            else return 3;
        }

    }

    private void traverse(Node root) {
        for(int i=1;i<=10;i++){
            if(root.label == -1)
                System.out.print("\n" + root.attrType + " " + i + " ");
            else if(root.label == 0)
                System.out.print(" Negative");
            else if(root.label == 1)
                System.out.print(" Positive");
            Node child = root.child[i];
            if (child == null) return;
            traverse(child);
        }
    }


    private Node id3(Vector <DataUnit> exampleset, Vector attrset, Vector resultset) {

        Node root = new Node();

       // System.out.println(attrset.size());
//        for(int i=0;i<attrset.size();i++){
//            System.out.println(attrset.);
//        }

        if(checkAllPositive(exampleset)) {
            root.label = 1;
            return root;
        }
        if(checkAllNegative(exampleset)) {
            root.label = 0;
            return root;
        }
        if(attrset.isEmpty()){
            root.label = mostCommonAttribute(exampleset);
            return root;
        }
        else {
            int chosenAttr = chooseAttribute(exampleset,attrset,resultset);
            root.attrType = chosenAttr;
            for(int i=1;i<=10;i++){
                Vector <DataUnit> examplesetVi = new Vector<>();
                for(int j=0;j< exampleset.size();j++){
                    DataUnit unit = exampleset.elementAt(j);
                    if(exampleset.elementAt(j).attr[chosenAttr] == i)
                        examplesetVi.add(exampleset.elementAt(j));
                }
                attrset.remove((Integer)chosenAttr);


                if(examplesetVi.isEmpty()){
                    //----------------
                    Node node = new Node();
                    node.label = mostCommonAttribute(exampleset);
                    //root.child.add(i,node);
                    root.child[i] =  node;
                }else{
                    Node node = id3(examplesetVi,attrset,resultset);
                    root.child[i] =  node;
                }
            }
        }

        return root;
//        for(int i=0;i<9;i++){
//            System.out.println(calculateInformationGain(i));
//        }
    }

    private int mostCommonAttribute(Vector<DataUnit> exampleset) {
        int positives=0;
        int negatives =0;
        for(int i=0;i<exampleset.size();i++){
            DataUnit unit = exampleset.elementAt(i);
            if(unit.result == 0)
                negatives++;
            else positives++;
        }

        if(negatives > positives)
            return 0;
        else return 1;
    }

    private int chooseAttribute(Vector <DataUnit> exampleset, Vector attrset, Vector resultset) {
        positivecounter = new int[9][11];
        negetivecounter = new int[9][11];
        for(int i=0;i<exampleset.size();i++){
            fillCounters(exampleset.elementAt(i));
        }

        int chosenAttr = -1;
        double gain = -1;
        for(int i=0;i<attrset.size();i++){

            int attribute = (int) attrset.elementAt(i);
            double infogain = calculateInformationGain(attribute);
            if(infogain > gain ){
                chosenAttr = attribute;
                gain = infogain;
            }
        }
        return chosenAttr;
    }

    private boolean checkAllNegative(Vector<DataUnit> exampleset) {
        for(int i=0;i<exampleset.size();i++){
            if(exampleset.elementAt(i).result == 1) {
                //System.out.println(exampleset.elementAt(i));
                return false;
            }
        }
        return true;
    }

    private boolean checkAllPositive(Vector<DataUnit> exampleset) {
        for(int i=0;i<exampleset.size();i++){
            if(exampleset.elementAt(i).result == 0) {
                //System.out.println(exampleset.elementAt(i));
                return false;
            }
        }
        return true;
    }

    private double calculateInformationGain(int attrType){ //0 to 8


        int totalpositive=0;
        for(int i=0;i<11;i++){
            totalpositive += positivecounter[attrType][i];
        }

        int totalnegative=0;
        for(int i=0;i<11;i++){
            totalnegative += negetivecounter[attrType][i];
        }

        int total = totalnegative + totalpositive;
        double h_s = calculateEntrody(totalpositive, totalnegative);

        double gain = h_s;
        for(int i=0;i<11;i++){
            int positives = positivecounter[attrType][i];
            int negatives = negetivecounter[attrType][i];
            double weight = (double) (positives + negatives) /(double) total;
            double value_entropy = 0;
            if(weight > 0)
                value_entropy = weight * calculateEntrody(positives, negatives);
            else value_entropy = 0;
            gain -= value_entropy;
        }

        return gain;
    }

    private double calculateEntrody(int positives, int negatives){
        int total = positives + negatives;

        if(positives == 0 || negatives == 0)
            return 0;

        double p_plus = (double)positives / (double) total;
        double p_minus = (double)negatives / (double) total;


        double result = - p_plus * log(p_plus) - p_minus * log(p_minus);
        return result;
    }

    private double log(double val){
        return  (Math.log(val) / Math.log(2));
    }


    public void readdata(){
        String filename = "data.csv";
        String line ;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while(( line = bufferedReader.readLine() )!= null){
                int attr [] = new int [9];
                StringTokenizer tokenizer = new StringTokenizer(line, ",");
                for(int i=0;i<9;i++){
                    attr[i] = Integer.parseInt(tokenizer.nextToken());
                }
                int result = Integer.parseInt(tokenizer.nextToken());

                DataUnit unit = new DataUnit(attr,result);
                int rand = random.nextInt(100);
                if(rand >= 20) {
                    trainset.add(unit);
                    fillCounters(unit);
                }
                else testset.add(unit);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    private void countersCheck() {
        for(int i=0;i<9;i++){
            int sum =0, sum1 = 0, sum2 = 0 ;
            for(int j=0;j<11;j++){
                sum = sum + allcounter[i][j];
                sum1 = sum1 + positivecounter[i][j];
                sum2 = sum2 + negetivecounter[i][j];
            }
            System.out.println(  sum + " " + sum1 + " " + sum2);
        }
    }

    private void fillCounters(DataUnit unit) {
        if(unit.result == 1){
            resultset.add(1);
            for(int i=0;i<unit.attr.length;i++){
                positivecounter[i][unit.attr[i]]++;
            }
        }
        else{
            resultset.add(0);
            for(int i=0;i<unit.attr.length;i++){
                negetivecounter[i][unit.attr[i]]++;
            }
        }

        for(int i=0;i<unit.attr.length;i++){
            allcounter[i][unit.attr[i]]++;
        }
    }

}
