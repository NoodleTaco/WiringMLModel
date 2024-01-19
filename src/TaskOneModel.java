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

    static final int FEATURE_SPACE_LENGTH = 1613; 

    static final int NUM_ITERATIONS = 1000;

    static final int SGD_SAMPLE_SIZE = 100; //The size of the input set an iteration of SGD uses

    static final int NUM_TESTING_INPUTS = 1000; //Size of the testing set 

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


        int[][] diagramLiteralMatrix = new int[20][20]; //Representation of matrix without one hot encoding, used for overlap features


        int diagramPointer = 0;
        for(int x = 0; x< 20; x++){
            for(int y = 0; y < 20; y++){
                diagramLiteralMatrix[x][y] = Character.getNumericValue(diagram.charAt(diagramPointer));
                diagramPointer++;
            }
        }




        Integer[] featureSpace = new Integer[FEATURE_SPACE_LENGTH];

        //Features that represent if a given color overlaps another
        int[] redOverlaps = {0,0,0}; //Ex: redOverlaps[0] = 1 means the red wire overlaps blue 
        int[] blueOverlaps = {0,0,0};
        int[] yellowOverlaps = {0,0,0};
        int[] greenOverlaps= {0,0,0};


        featureSpace[0] = 1; //Add baseline feature 
        int addToIndex = -3; //Start at -3 so i can increment with addToIndex += 4, -3 + 4 = index 1


        for(int i = 0 ; i < diagram.length(); i ++){
            //Create one hot encoded diagram 
            int currentWire = Character.getNumericValue(diagram.charAt(i));
            matrixManager.addArraysAtIndex(featureSpace,Wire.encodeWireColor(currentWire), addToIndex+= 4);
        }

        //Populate the overlap arrarys 
        for(int x = 0 ; x < diagramLiteralMatrix.length; x ++){
            for(int y = 0; y < diagramLiteralMatrix[0].length; y ++){
                if(diagramLiteralMatrix[x][y] == Wire.RED_WIRE){
                    int neighborResults = neighborColorChecker(diagramLiteralMatrix, x, y, Wire.RED_WIRE);
                    if(neighborResults !=0){
                        redOverlaps[neighborResults - 2] = 1;
                    }
                }
                else if(diagramLiteralMatrix[x][y] == Wire.BLUE_WIRE){
                    int neighborResults = neighborColorChecker(diagramLiteralMatrix, x, y, Wire.BLUE_WIRE);
                    if(neighborResults ==1){
                        blueOverlaps[neighborResults - 1] = 1;
                    }
                    else if(neighborResults == 3 || neighborResults == 4){
                        blueOverlaps[neighborResults - 2] = 1;
                    }
                }
                else if(diagramLiteralMatrix[x][y] == Wire.YELLOW_WIRE){
                    int neighborResults = neighborColorChecker(diagramLiteralMatrix, x, y, Wire.YELLOW_WIRE);
                    if(neighborResults == 1 || neighborResults == 2){
                        yellowOverlaps[neighborResults - 1] = 1;
                    }
                    else if(neighborResults == 4){
                        yellowOverlaps[neighborResults - 2] = 1;
                    }
                }
                else if(diagramLiteralMatrix[x][y] == Wire.GREEN_WIRE){
                    int neighborResults = neighborColorChecker(diagramLiteralMatrix, x, y, Wire.GREEN_WIRE);
                    if(neighborResults !=0){
                        greenOverlaps[neighborResults - 1] = 1;
                    }
                }
            }
        }


        //Add the overlap features to the feature space

        matrixManager.addArraysAtIndex(featureSpace, redOverlaps, addToIndex += 4);
        matrixManager.addArraysAtIndex(featureSpace, blueOverlaps, addToIndex += 3);
        matrixManager.addArraysAtIndex(featureSpace, yellowOverlaps, addToIndex += 3);
        matrixManager.addArraysAtIndex(featureSpace, greenOverlaps, addToIndex += 3);




        return featureSpace;
    }


    /**
     * Helper method that indicates if the passed in wire overrlaps another
     * @param diagram Wire diagram 
     * @param xCoord x location of the wire in the diagram 
     * @param yCoord y location of the wire in the diagram
     * @param wireColor What wire is being passed in
     * @return
     */
    private int neighborColorChecker(int[][] diagram, int xCoord, int yCoord, int wireColor){
        //Loop through each color
        for(int color = Wire.RED_WIRE; color <= Wire.GREEN_WIRE; color++){
            //Don't check the color's own wire
            if(color != wireColor){
                int neighborCount = 0; //Will hold how many neighbors of a single color there are
                //A wire Overlaps another if at any point, one of its cells has two neighbors of the same color wire
                //If the cell has one neighbor but that colored neighbor is perpendicular to it, its also an overlap
                if(xCoord + 1 < diagram.length && diagram[xCoord +1][yCoord] == color){
                    neighborCount ++;
                    if(xCoord +2 < diagram.length && diagram[xCoord +2][yCoord] == color ){
                        return color;
                    }
                }
                if(xCoord - 1 > -1 && diagram[xCoord -1][yCoord] == color){
                    neighborCount ++;
                    if(xCoord - 2 > -1 && diagram[xCoord -2][yCoord] == color){
                        return color;
                    }
                }
                if(yCoord + 1 < diagram.length && diagram[xCoord][yCoord +1] == color){
                    neighborCount ++;
                    if(yCoord + 2 < diagram.length && diagram[xCoord][yCoord +2] == color){
                        return color;
                    }
                }
                if(yCoord - 1 > -1 && diagram[xCoord][yCoord  -1] == color){
                    neighborCount ++;
                    if(yCoord - 2 > -1 && diagram[xCoord][yCoord  -2] == color){
                        return color;
                    }
                }

                if(neighborCount == 2){
                    return color;
                }
            }
        }

        return 0;  //Dosen't overlap, return 0
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

    
        int iterations = 0;

        while(iterations < NUM_ITERATIONS){
            

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
        //Add penalizing factor for l2 regression
        double l2RegressionFactor = 0;
        for(double weight: weights){
            l2RegressionFactor += Math.pow(weight, 2);
        }
        l2RegressionFactor *= lambda;

        return ((totalLoss)/inputMap.size()) + l2RegressionFactor ;
    }

    /**
     * Calculates the gradient of the loss over the input 
     * Derivative of loss with respect to weight j = -(y - f(x))xj + 2 * lambda * weight j
     * Details in Report
     * @param input Input considered
     * @param label label of the Input
     */
    private Double[] computeLossGradient(Integer[] input, Integer label) {
        Double[] lossGradient = new Double[input.length];
        //Calculate once 
        double sigmoidMinusLabel = calculateSigmoid(input, weights) - label;

    
        for (int i = 0; i < input.length; i++) {
            lossGradient[i] = (input[i] * sigmoidMinusLabel) + (2 * lambda * weights[i]);
        }
    
        return lossGradient;
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



    /**
     * Returns a list of random entries from the given map
     * @param map The map being looked at
     * @param count Number of entries to return
     * @return a list of random entries from the given map
     */
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


    /**
     * Test the success rate of model by comparing its predicted labels to the actual labels in a map
     * @param map Data set looked overr
     * @return The averrage success rate over the map
     */
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

        TaskOneModel taskOneModel = new TaskOneModel(3000, 0.05, 0.1);
        taskOneModel.readTrainingDiagrams();

        taskOneModel.SGD();


    }




}
