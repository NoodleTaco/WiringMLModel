import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

public class TaskOneModel { 

    static final int FEATURE_SPACE_LENGTH = 1601; //400 * (4 + 4! /2)

    static final double GRADIENT_LOSS_THRESHOLD = 0.05; //Determines where SGD can stop once the gradient of its loss goes below this threshold

    static final int MAX_ITERATIONS = 1000; //max number of iterations of SGD in case it dosen't converge

    static final int SGD_SAMPLE_SIZE = 100; //The size of the input set an iteration of SGD uses

    static final int NUM_TESTING_INPUTS = 100; //Size of the testing set 

    static final double ASSIGNMENT_THRESHOLD = 0.5; //Decision threshold for assigning a label 

    private int numSamples;
    private double alphha;
    private double lambda; 
    private double assignmentThreshold;

    private double[] weights;


    private HashMap<Integer[], Integer> diagramMap; //Holds all the Samples that were provided

    private HashMap<Integer[], Integer> testingInputMap; //Holds all the Samples that were provided

    private ArrayList<Double> trainingLossList;

    private ArrayList<Double> testingLossList;

    private ArrayList<Double> trainingAccuracy;

    private ArrayList<Double> testingAccuracy;

    /**
     * Constructor without assignmentThreshold specified
     * assignmentThreshold set to constant of 0.5 
     * @param numSamples
     * @param alpha
     * @param lambda
     */
    public TaskOneModel(int numSamples, double alpha, double lambda){
        this.numSamples = numSamples;
        this.alphha = alpha;
        this.lambda = lambda;

        diagramMap = new HashMap<>();

        testingInputMap = new HashMap<>();

        weights = new double[FEATURE_SPACE_LENGTH];

        trainingLossList = new ArrayList<>();
        testingLossList = new ArrayList<>();

        trainingAccuracy = new ArrayList<>();
        testingAccuracy = new ArrayList<>();

        assignmentThreshold = ASSIGNMENT_THRESHOLD;
    }
    
    /**
     * Constructor with assignmentThreshold specified
     * @param numSamples
     * @param alpha
     * @param lambda
     * @param assignmentThreshold
     */
    public TaskOneModel(int numSamples, double alpha, double lambda, double assignmentThreshold){
        this.numSamples = numSamples;
        this.alphha = alpha;
        this.lambda = lambda;
        this.assignmentThreshold = assignmentThreshold;

        diagramMap = new HashMap<>();

        testingInputMap = new HashMap<>();

        weights = new double[FEATURE_SPACE_LENGTH];

        trainingLossList = new ArrayList<>();
        testingLossList = new ArrayList<>();
    }

    public void readTrainingDiagrams() {
        try (BufferedReader reader = new BufferedReader(new FileReader(GenerateWireDiagrams.taskOneTrainingData))) {
            
            for(int i = 0; i < numSamples; i ++){
                String diagram = reader.readLine(); //Read first line
                int label = Integer.parseInt(reader.readLine()); //Read second line 
                diagramMap.put(getFeatureSpace(diagram), label);

                reader.readLine(); //Skip the 2nd line which represents the wire to cut label 
                reader.readLine(); //Skip the blank line 
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readTestingDiagrams() {
        try (BufferedReader reader = new BufferedReader(new FileReader(GenerateWireDiagrams.taskOneTestingData))) {
            
            for(int i = 0; i < NUM_TESTING_INPUTS; i ++){
                String diagram = reader.readLine(); //Read first line
                int label = Integer.parseInt(reader.readLine()); //Read second line 
                testingInputMap.put(getFeatureSpace(diagram), label);

                reader.readLine(); //Skip the 2nd line which represents the wire to cut label 
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
    private Integer[] getFeatureSpace(String diagram){
        if(diagram.length() != 400){
            System.out.println("Error: Diagram String representation legnth != 400");
            return null;
        }

        //System.out.println(diagram);

        Integer[] featureSpace = new Integer[FEATURE_SPACE_LENGTH];

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

        //STOP UNDO HERE

        featureSpace[0] = 1; //Add baseline feature 
        int addToIndex = 1;

        matrixManager.addArraysAtIndex(featureSpace, matrixManager.flattenArray(redWireMatrix), addToIndex);

        //matrixManager.addArraysAtIndex(featureSpace, matrixManager.flattenArray(blueWireMatrix), addToIndex+=400);

        matrixManager.addArraysAtIndex(featureSpace, matrixManager.flattenArray(yellowWireMatrix),addToIndex+=400);

        //matrixManager.addArraysAtIndex(featureSpace, matrixManager.flattenArray(greenWireMatrix), addToIndex+=400);
        
        matrixManager.addArraysAtIndex(featureSpace, matrixManager.flattenArray(matrixManager.multiplyMatrices(redWireMatrix, yellowWireMatrix)), addToIndex+= 400);

        matrixManager.addArraysAtIndex(featureSpace, matrixManager.flattenArray(matrixManager.multiplyMatrices(yellowWireMatrix, redWireMatrix)), addToIndex+= 400);

        /* 
        ArrayList<int[][]> colorMatrixList = new ArrayList<>();

        colorMatrixList.add(redWireMatrix); colorMatrixList.add(blueWireMatrix); colorMatrixList.add(yellowWireMatrix); colorMatrixList.add(greenWireMatrix);

        for(int[][] matrixOne: colorMatrixList){
            for(int[][] matrixTwo: colorMatrixList){
                if(matrixOne != matrixTwo){
                    matrixManager.addArraysAtIndex(featureSpace, matrixManager.flattenArray(matrixManager.multiplyMatrices(matrixOne, matrixTwo)), addToIndex += 400);
                }
            }
        }
        
        */

        return featureSpace;
    }

    /**
     * Performs stochastic gradient descent to update the weights vector 
     * Requires diagramMap to be initialized 
     */
    public void SGD(){

        readTrainingDiagrams();
        readTestingDiagrams();


        //First pick a random selection of weights
        Random random = new Random();

        
        double scaleFactor = 0.2; // Adjust the scale factor based on the desired range of random values
        for (int i = 0; i < weights.length; i++) {
            double randomValue = -scaleFactor + 2 * scaleFactor * random.nextDouble();
            weights[i] = randomValue;
        }

        
        //SGD will stop when the gradient of the loss function goes below a certain threshold 
        int iterations = 0;

        while(iterations < MAX_ITERATIONS){
            
            //System.out.println("Training Data Success Rate: " + testSuccessRate(diagramMap));
            //System.out.println("Testing Data Success Rate: " + testSuccessRate(testingInputMap));

            List<Entry<Integer[], Integer>> randomEntries = getRandomEntriesFromMap(diagramMap, SGD_SAMPLE_SIZE); //Select random entries in the input sample space
            

            ArrayList<Double[]> gradientList = new ArrayList<>();

            for (Map.Entry<Integer[], Integer> entry : randomEntries) {
            
                gradientList.add(computeLossGradient(entry.getKey(), entry.getValue()));
            }

            //Get the average of the gradients 
            Double[] averageLossGradient = new Double[FEATURE_SPACE_LENGTH];
            for(int i = 0; i < gradientList.get(0).length; i ++){
                double total = 0;
                for(Double[] gradient: gradientList){
                    total += gradient[i];
                }
                averageLossGradient[i] = total/gradientList.size();
            }


            //Update each weight: w_k+1 = w_k - alpha * gradient Loss
            for(int i = 0; i < weights.length; i ++){
                weights[i] -= alphha * averageLossGradient[i];
            }

            

            trainingLossList.add(calculateLoss(diagramMap));
            testingLossList.add(calculateLoss(testingInputMap));

            trainingAccuracy.add(testSuccessRate(diagramMap));
            testingAccuracy.add(testSuccessRate(testingInputMap));


            iterations ++;

        }


    }



    /**
     * Calculates the log loss with l2 regression over a set of inputs 
     * @param inputMap  the set of inputs
     * @return the loss over the set of inputs
     */
    private double calculateLoss(HashMap<Integer[], Integer> inputMap){
        double totalLoss = 0;
        for (Map.Entry<Integer[], Integer> entry : inputMap.entrySet()) {
           totalLoss += -((entry.getValue()*Math.log(calculateSigmoid(entry.getKey(), weights))) + ((1 - entry.getValue()) * Math.log(1 - calculateSigmoid(entry.getKey(), weights))));
        }
        double l2RegressionFactor = 0;
        for(double weight: weights){
            l2RegressionFactor += Math.pow(weight, 2);
        }
        l2RegressionFactor *= lambda;

        return ((totalLoss + l2RegressionFactor)/inputMap.size()) ;
    }

    /**
     * Calculates thes gradient of the loss over the input 
     * Derivative of loss with respect to weight j = -(y - f(x))xj + 2 * lambda * weight j
     * Details in Report
     * @param input Input considered
     * @param label label of the Input
     */
    private Double[] computeLossGradient(Integer[] input, Integer label) {
        Double[] lossGradient = new Double[input.length];
        double sigmoidMinusLabel = calculateSigmoid(input, weights) - label;

    
        for (int i = 0; i < input.length; i++) {
            lossGradient[i] = (input[i] * sigmoidMinusLabel) + (2 * lambda * weights[i]);
        }
    
        return lossGradient;
    }


    /**
     * Tests the model's prediction of a random input from the testing data
     * @return true if the model correctly predicted the input's actual lable, false otherwise
     */
    public boolean testPrediction(){

        List<Entry<Integer[], Integer>> randomEntry = getRandomEntriesFromMap(diagramMap, 1); //Select a random input

        return(predictLabel(randomEntry.get(0).getKey()) == randomEntry.get(0).getValue());

    }

    /**
     * Predicts the label of a given input based on the calculated weights
     * @param input
     * @return 0: safe, 1: dangerous
     */
    public int predictLabel(Integer[] input){
        //If the probability is above the threshold, assign label
        double probability = calculateSigmoid(input, weights);
        if(probability >= assignmentThreshold){
            return 1;
        }
        else{
            return 0;
        }
    }



     /**
     * Calculates the sigmoid function given an input vector and a weight vector.
     * @param inputVector  The input vector (array of ints).
     * @param weightVector The weight vector (array of doubles).
     * @return The result of the sigmoid function.
     */
    public static double calculateSigmoid(Integer[] inputVector, double[] weightVector) {
        if (inputVector.length != weightVector.length) {
            System.out.println("Input vector and weight vector must have the same length.");
            return -1;
        }
    
        double weightedSum = 0.0;
    
        // Calculate the weighted sum
        for (int i = 0; i < inputVector.length; i++) {
            weightedSum += (inputVector[i] * weightVector[i]);
        }
    
        // Calculate the sigmoid function
        double sigmoidResult = 1.0 / (1.0 + Math.exp(-weightedSum));
    
        return sigmoidResult;
    }

    public static double calculateL2Norm(Double[] vector) {
        double sumOfSquares = 0.0;
    
        // Calculate the sum of squares of each element
        for (Double element : vector) {
            sumOfSquares += element * element;
        }
    
        // Calculate the square root of the sum of squares
        double l2Norm = Math.sqrt(sumOfSquares);
    
        return l2Norm;
    }


    public static List<Map.Entry<Integer[], Integer>> getRandomEntriesFromMap(Map<Integer[], Integer> map, int count) {
        // Convert the key set to a list
        List<Map.Entry<Integer[], Integer>> entryList = new ArrayList<>(map.entrySet());

        // Shuffle the list
        Collections.shuffle(entryList);

        // Get the first 'count' elements
        return entryList.subList(0, Math.min(count, entryList.size()));
    }


    public HashMap<Integer[], Integer> getDiagramMap(){
        return diagramMap;
    }

    // Getter for trainingLossList
    public ArrayList<Double> getTrainingLossList() {
        return trainingLossList;
    }

    // Getter for testingLossList
    public ArrayList<Double> getTestingLossList() {
        return testingLossList;
    }

    public ArrayList<Double> getTrainingAccuracyList() {
        return trainingAccuracy;
    }

    // Getter for testingLossList
    public ArrayList<Double> getTestingAccuracyList() {
        return testingAccuracy;
    }


    private double testSuccessRate(HashMap<Integer[], Integer> map){
        int[] predictions = new int[map.size()];

        int count = 0;
        for(Integer[] input: map.keySet()){
            if(predictLabel(input) == map.get(input)){
                predictions[count] = 1;
            }
            else{
                predictions[count] = 0;
            }
            count ++;
        }

        double averagePredictionSuccessRate = ModelTesting.calculateArrayAverage(predictions);

        return averagePredictionSuccessRate;

    }
    
    public static void main(String[] args) {

        TaskOneModel taskOneModel = new TaskOneModel(2000, 0.01, 0.1);
        taskOneModel.readTrainingDiagrams();

        taskOneModel.SGD();

        System.out.println(taskOneModel.testPrediction());
        System.out.println(taskOneModel.testPrediction());
        System.out.println(taskOneModel.testPrediction());
        System.out.println(taskOneModel.testPrediction());

    }




}
