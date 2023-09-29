import java.util.List;
import java.util.Map;

 public class ID3{
    
    private static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

    public double calculateEntropy(List<Map<String, String>> data, String classAttr){
        if (data.size() == 0){
            return 0.0;
        }
        //Init class count
        int classA = 0;
        int classB = 0;

        for (Map<String,String> entry: data){
            if (entry.get(classAttr).equals("Yes")){
                classA++;
            }
            else if (entry.get(classAttr).equals("No")){
                classB++;
            }
        }

        if (classA == 0 || classB == 0){return 0.0;}

        if(classA == classB){return 1.0;}

        double total = classA + classB;
        double fracA = classA/total;
        double fracB = classB/total;

        return -fracA * log2(fracA) - fracB * log2(fracB);

    }

    //TODO: 



 }