import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

public class TaskTwoModel {
    static final int FEATURE_SPACE_LENGTH = 1601; //400 * (4 + 4! /2)

    static final int NUM_ITERATIONS = 100;

    static final int SGD_SAMPLE_SIZE = 10; //The size of the input set an iteration of SGD uses

    static final int NUM_TESTING_INPUTS = 1000; //Size of the testing set 

    static final double ASSIGNMENT_THRESHOLD = 0.5; //Decision threshold for assigning a label 

    private int numSamples;
    private double alpha;
    private double lambda;

    private ArrayList<Double> trainingLossList;

    private ArrayList<Double> testingLossList;

    ArrayList<double[]> weightsList; //List that will hold the different weight vectors 



    private double assignmentThreshold;

    private HashMap<Double[], Integer> diagramMap; //Holds all the Samples that were provided

    private HashMap<Double[], Integer> testingInputMap; //Holds all the Samples that were provided

    public TaskTwoModel(int numSamples, double alpha, double lambda){
        this.numSamples = numSamples;
        this.alpha = alpha;
        this.lambda = lambda;

        diagramMap = new HashMap<>();

        testingInputMap = new HashMap<>();

        weightsList = new ArrayList<>();
        //initialize four weight vectors for each color 
        for(int i = 0; i < 4; i ++){
            weightsList.add(new double[FEATURE_SPACE_LENGTH]);
        }

        assignmentThreshold = ASSIGNMENT_THRESHOLD;

        trainingLossList = new ArrayList<>();
        testingLossList = new ArrayList<>();
    }

    /**
     * Reads over the training text file to add it to diagramMap
     */
    public void readTrainingDiagrams(){
        try (BufferedReader reader = new BufferedReader(new FileReader(GenerateWireDiagrams.taskTwoTrainingData))) {
            
            for(int i = 0; i < numSamples; i ++){
                String diagram = reader.readLine(); //Read first line
                reader.readLine(); //Skip 2nd line which contains safe/dangerous label
                int label = Integer.parseInt(reader.readLine()); //Read third line which represents the wire to be cut
                diagramMap.put(getFeatureSpace(diagram), label);
        
                reader.readLine(); //Skip the blank line 
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads over the Testing text file to add it to diagramMap
     */
    public void readTestingDiagrams(){
        try (BufferedReader reader = new BufferedReader(new FileReader(GenerateWireDiagrams.taskTwoTestingData))) {
            
            for(int i = 0; i < NUM_TESTING_INPUTS; i ++){
                String diagram = reader.readLine(); //Read first line
                reader.readLine(); //Skip 2nd line which contains safe/dangerous label
                int label = Integer.parseInt(reader.readLine()); //Read third line which represents the wire to be cut
                testingInputMap.put(getFeatureSpace(diagram), label);
        
                reader.readLine(); //Skip the blank line 
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function will take a diagram and return a vector representing the model
     * Notes on what dictates the model space in report 
     */
    private Double[] getFeatureSpace(String diagram){
        if(diagram.length() != 400){
            System.out.println("Error: Diagram String representation legnth != 400");
            return null;
        }


        Double[] featureSpace = new Double[FEATURE_SPACE_LENGTH];

        int[][] redWireMatrix = new int[WireDiagram.WIRE_DIAGRAM_SIZE][WireDiagram.WIRE_DIAGRAM_SIZE];
        int[][] blueWireMatrix = new int[WireDiagram.WIRE_DIAGRAM_SIZE][WireDiagram.WIRE_DIAGRAM_SIZE];
        int[][] yellowWireMatrix = new int[WireDiagram.WIRE_DIAGRAM_SIZE][WireDiagram.WIRE_DIAGRAM_SIZE];
        int[][] greenWireMatrix = new int[WireDiagram.WIRE_DIAGRAM_SIZE][WireDiagram.WIRE_DIAGRAM_SIZE];

        //Construct the matrices for each wire
        int diagramPointer = 0;
        for(int x = 0; x < 20; x ++){
            for(int y = 0; y < 20; y ++){
                int currentWire = Character.getNumericValue(diagram.charAt(diagramPointer));

                if(currentWire == Wire.RED_WIRE){
                    redWireMatrix[x][y] = 1;
                }
                else{
                    redWireMatrix[x][y] = 0;
                }

                if(currentWire == Wire.BLUE_WIRE){
                    blueWireMatrix[x][y] = 1;
                }
                else{
                    blueWireMatrix[x][y] = 0;
                }

                if(currentWire == Wire.YELLOW_WIRE){
                    yellowWireMatrix[x][y] = 1;
                }
                else{
                    yellowWireMatrix[x][y] = 0;
                }

                if(currentWire == Wire.GREEN_WIRE){
                    greenWireMatrix[x][y] = 1;
                }
                else{
                    greenWireMatrix[x][y] = 0;
                }

                diagramPointer ++;
            }
        }
        featureSpace[0] = 1.0; //Add baseline feature 
        int addToIndex = 1;

        matrixManager.addArraysAtIndex(featureSpace, matrixManager.flattenArray(redWireMatrix), addToIndex);

        matrixManager.addArraysAtIndex(featureSpace, matrixManager.flattenArray(blueWireMatrix), addToIndex+=400);

        matrixManager.addArraysAtIndex(featureSpace, matrixManager.flattenArray(yellowWireMatrix),addToIndex+=400);

        matrixManager.addArraysAtIndex(featureSpace, matrixManager.flattenArray(greenWireMatrix), addToIndex+=400);
        

        return featureSpace;
    }

    public void SGD(){
        readTrainingDiagrams();
        readTestingDiagrams();

        Random random = new Random();

        double scaleFactor = 0.2; // Adjust the scale factor based on the desired range of random values
        for(double[] weights: weightsList){
            //Set a random weight value in scale range for every weight in each set of weights
            for (int i = 0; i < weights.length; i++) {
            double randomValue = -scaleFactor + 2 * scaleFactor * random.nextDouble();
            weights[i] = randomValue;
            }
        }

        int iterations = 0;

        while(iterations < NUM_ITERATIONS){
            List<Entry<Double[], Integer>> randomEntries = getRandomEntriesFromMap(diagramMap, SGD_SAMPLE_SIZE); //Select random entries in the input sample space

            ArrayList<ArrayList<Double[]>> classGradients = new ArrayList<>(); //Create a list that holds lists for each weight
            for(int i = 0 ; i <4; i ++){
                classGradients.add(new ArrayList<Double[]>());
            }

            for (Map.Entry<Double[], Integer> entry : randomEntries) {
                for(int i = 0 ; i < weightsList.size(); i ++){
                    classGradients.get(i).add(computeLossGradient(entry.getKey(), weightsList.get(i) ,entry.getValue() )); //Compute the loss gradient for each weight set
                }
            }

            //Now take each list and combine them via averaging 
            ArrayList<Double[]> weightSetGradients = new ArrayList<>(); //Will hold the averaged gradient for each weight set
            for(ArrayList<Double[]> colorWeightList: classGradients){
                Double[] averageLossGradient = new Double[FEATURE_SPACE_LENGTH];
                for(int i = 0; i < colorWeightList.get(0).length; i ++){
                    double total = 0;
                    for(Double[] gradient: colorWeightList){
                        total += gradient[i];
                    }
                    averageLossGradient[i] = total/colorWeightList.size();
                }
                weightSetGradients.add(averageLossGradient);
            }

            //Update weights from average loss gradient
            for(int i = 0; i < weightsList.size(); i ++){
                for(int j = 0 ; j < weightsList.get(i).length; j ++){
                    weightsList.get(i)[j] -= alpha * weightSetGradients.get(i)[j];
                }
            }

            trainingLossList.add(calculateLoss(diagramMap));
            testingLossList.add(calculateLoss(testingInputMap));
            iterations++;
        }

        
        
    }

     /**
      * Returns the probability of a classification's label being assigned to an input
      * @param input The input being tested
      * @param weights The weights associated with the classification
      * @return The probability
      */
    public double probabilityOfLabel(Double[] input, double[] weights){
        //Get the sum of the weights to normalize 
        double totalWeights = 0;
        for(double[] weightVector: weightsList){
            totalWeights += eRaisedToDotProd(input, weightVector);
        }

        return eRaisedToDotProd(input, weights) / totalWeights;
    }

    /**
     * Return e ^ (dot producto of paramters)
     * Parameters are the weights and input
     * @return
     */
    private double eRaisedToDotProd(Double[] input, double[] weights){
        double sum = 0;

        for(int i = 0; i < input.length; i ++){
            sum += input[i] * weights[i];
        }

        return Math.exp(sum);
    }

    /**
     * Calculates the gradient of loss over a set of weights
     * @param input Input considered
     * @param weights The weights of the classifier
     * @param label Whether the label applies to the classifier, 1 if it is the wire to be cut, 0 otherwise 
     * @return An array where each element represents the derivative of loss with respect to a single weight
     */
    private Double[] computeLossGradient(Double[] input, double[] weights, int label) {
        Double[] lossGradient = new Double[input.length];
        double probabilityMinusLabel = probabilityOfLabel(input, weights) - label;
        for(int i = 0; i < lossGradient.length; i ++){
            lossGradient[i] = (input[i] * probabilityMinusLabel) +  (2 * lambda * weights[i]);
        }
        return lossGradient;
    }

    /**
     * Calculates the Cross Entropy Loss of a set of inputs 
     * @param inputMap
     * @return
     */
    private double calculateLoss(HashMap<Double[], Integer> inputMap){
        double totalLoss = 0;
        
        for(Map.Entry<Double[], Integer> entry : inputMap.entrySet()){
            int[] encodedLabel = encodeLabel(entry.getValue());
            for(int i = 0 ;i < weightsList.size(); i ++){
                totalLoss -= encodedLabel[i] * Math.log(probabilityOfLabel(entry.getKey(), weightsList.get(i)));
            }
        }

        //Add penalizing factor for l2 regression
        double l2RegressionFactor = 0;
        for(double[] weightsVector: weightsList){
            for(double weight: weightsVector){
                l2RegressionFactor += Math.pow(weight, 2);
            }
        }
        l2RegressionFactor *= lambda;

        return ((totalLoss + l2RegressionFactor)/inputMap.size()) ;
    }

    /**
     * Encode the label to a 4 length array 
     * @param label Numerical representation of the wire to be cut
     * @return Array representation of the wire to be cut
     */
    private int[] encodeLabel(int label){
        
        if(label == Wire.RED_WIRE){
            int[] encodedLabel = {1, 0, 0, 0}; 
            return encodedLabel;
        }
        else if(label == Wire.BLUE_WIRE){
            int[] encodedLabel = {0, 1, 0, 0}; 
            return encodedLabel;
        }
        else if(label == Wire.YELLOW_WIRE){
            int[] encodedLabel = {0, 0, 1, 0}; 
            return encodedLabel;
        }
        else{
            int[] encodedLabel = {0, 0, 0, 1}; 
            return encodedLabel;
        }

    }

    public static List<Map.Entry<Double[], Integer>> getRandomEntriesFromMap(Map<Double[], Integer> map, int count) {
        // Convert the key set to a list
        List<Map.Entry<Double[], Integer>> entryList = new ArrayList<>(map.entrySet());

        // Shuffle the list
        Collections.shuffle(entryList);

        // Get the first 'count' elements
        return entryList.subList(0, Math.min(count, entryList.size()));
    }

    // Getter for trainingLossList
    public ArrayList<Double> getTrainingLossList() {
        return trainingLossList;
    }

    // Getter for testingLossList
    public ArrayList<Double> getTestingLossList() {
        return testingLossList;
    }

    public static void main(String[] args) {

        TaskTwoModel taskTwoModel = new TaskTwoModel(2000, 0.01, 0.1);
        taskTwoModel.SGD();

    }


}
