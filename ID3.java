import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

    public TreeNode buildDecisionTree(List<Map<String, String>> data, List<String> attributes) {
        TreeNode root = buildDecisionTreeRecursive(data, attributes);
        return root;
    }

    private TreeNode buildDecisionTreeRecursive(List<Map<String, String>> data, List<String> attributes) {
        TreeNode node = new TreeNode();

        // Check if all instances belong to the same class
        String classAttr = "class";
        boolean sameClass = areAllInstancesSameClass(data, classAttr);

        if (sameClass) {
            node.setDecision(data.get(0).get(classAttr));
        } else if (attributes.isEmpty()) {
            node.setDecision(getMajorityClass(data, classAttr));
        } else {
            // Choose the best attribute to split on
            String bestAttribute = chooseBestAttribute(data, attributes, classAttr);
            node.setAttribute(bestAttribute);

            // Recursively build subtrees for each attribute value
            for (String value : getAttributeValues(data, bestAttribute)) {
                List<Map<String, String>> subset = getSubset(data, bestAttribute, value);
                List<String> remainingAttributes = new ArrayList<>(attributes);
                remainingAttributes.remove(bestAttribute);

                TreeNode child = buildDecisionTreeRecursive(subset, remainingAttributes);
                node.addChild(value, child);
            }
        }

        return node;
    }

    private boolean areAllInstancesSameClass(List<Map<String, String>> data, String classAttr) {
        String firstClass = data.get(0).get(classAttr);
        for (Map<String, String> entry : data) {
            if (!entry.get(classAttr).equals(firstClass)) {
                return false;
            }
        }
        return true;
    }

    private String getMajorityClass(List<Map<String, String>> data, String classAttr) {
        // Count the occurrences of each class
        Map<String, Integer> classCounts = new HashMap<>();
        for (Map<String, String> entry : data) {
            String classValue = entry.get(classAttr);
            classCounts.put(classValue, classCounts.getOrDefault(classValue, 0) + 1);
        }

        // Find the class with the highest count
        int maxCount = 0;
        String majorityClass = null;
        for (Map.Entry<String, Integer> entry : classCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                majorityClass = entry.getKey();
            }
        }

        return majorityClass;
    }

    private List<String> getAttributeValues(List<Map<String, String>> data, String attribute) {
        // Extract unique values for a given attribute from the dataset
        List<String> values = new ArrayList<>();
        for (Map<String, String> entry : data) {
            String value = entry.get(attribute);
            if (!values.contains(value)) {
                values.add(value);
            }
        }
        return values;
    }

    private List<Map<String, String>> getSubset(List<Map<String, String>> data, String attribute, String value) {
        // Get a subset of data where the specified attribute has a specific value
        List<Map<String, String>> subset = new ArrayList<>();
        for (Map<String, String> entry : data) {
            if (entry.get(attribute).equals(value)) {
                subset.add(entry);
            }
        }
        return subset;
    }

    private double calculateInformationGain(List<Map<String, String>> data, String attribute, String classAttr) {
        double entropyBeforeSplit = calculateEntropy(data, classAttr);
        List<String> attributeValues = getAttributeValues(data, attribute);
        double entropyAfterSplit = 0.0;

        for (String value : attributeValues) {
            List<Map<String, String>> subset = getSubset(data, attribute, value);
            double weight = (double) subset.size() / data.size();
            entropyAfterSplit += weight * calculateEntropy(subset, classAttr);
        }

        return entropyBeforeSplit - entropyAfterSplit;
    }

    private String chooseBestAttribute(List<Map<String, String>> data, List<String> attributes, String classAttr) {

        String bestAttribute = null;
        double maxInformationGain = -1.0;

        for (String attribute : attributes) {
            double informationGain = calculateInformationGain(data, attribute, classAttr);

            if (informationGain > maxInformationGain) {
                maxInformationGain = informationGain;
                bestAttribute = attribute;
            }
        }

        return bestAttribute;
    }

    private static List<Map<String, String>> readCSV(String filePath) throws IOException {
        List<Map<String, String>> data = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        String[] headers = reader.readLine().split(",");

        while ((line = reader.readLine()) != null) {
            String[] values = line.split(",");
            Map<String, String> entry = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                entry.put(headers[i], values[i]);
            }
            data.add(entry);
        }
        reader.close();
        return data;
    }
    public void printDecisionTree(TreeNode node, String indent) {
        if (node == null) {
            return;
        }
    
        // Print the current node's attribute and value (if available)
        if (node.getAttribute() != null) {
            System.out.println(indent + "|---" + node.getAttribute());
        }
    
        // Print the decision (if available)
        if (node.getDecision() != null) {
            System.out.println(indent + "|---" + String.format("%2s", node.getValue()) + " Decision: " + node.getDecision());
            return;
        }
    
        // Recursively print child nodes with adjusted indentation
        for (int i = 0; i < node.getChildren().size(); i++) {
            String childIndent = indent + "|   ";
            printDecisionTree(node.getChildren().get(i), childIndent);
        }
    }
 public static void main(String[] args) throws IOException {
        // Read CSV data into a list of maps
        List<Map<String, String>> data = readCSV("./data.csv");

        // List of attribute names (excluding the class attribute)
        List<String> attributes = new ArrayList<>();
        attributes.add("Math");
        attributes.add("Statistics");
        attributes.add("Science");
        attributes.add("English");
        // Add more attributes as needed

        // Create an instance of ID3 and build the decision tree
        ID3 id3 = new ID3();
        TreeNode decisionTree = id3.buildDecisionTree(data, attributes);
        id3.printDecisionTree(decisionTree, "");
    }

 class TreeNode {
    String value;
    String attribute;
    List<TreeNode> children;
    String decision;

    public TreeNode() {
        this.children = new ArrayList<>();
    }

    public String getValue() {
        return this.value;
    }

    public List<TreeNode> getChildren() {
        return this.children;
    }

    public String getDecision() {
        return this.decision;
    }

    public String getAttribute() {
        return this.attribute;
    }

    public TreeNode(String attribute) {
        this.attribute = attribute;
        this.children = new ArrayList<>();
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public void addChild(String value, TreeNode node) {
        node.setValue(value);
        this.children.add(node);
    }

    public void setValue(String value) {
        this.value = value;
    }
}
}