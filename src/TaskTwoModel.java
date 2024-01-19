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
    static final int FEATURE_SPACE_LENGTH = 1613; 

    static final int NUM_ITERATIONS = 1000;

    static final int SGD_SAMPLE_SIZE = 100; //The size of the input set an iteration of SGD uses

    static final int NUM_TESTING_INPUTS = 1000; //Size of the testing set 


    private int numSamples;
    private double alpha;
    private double lambda;

    private ArrayList<Double> trainingLossList;

    private ArrayList<Double> testingLossList;


    private ArrayList<Double> trainingAccuracy;

    private ArrayList<Double> testingAccuracy;

    ArrayList<double[]> weightsList; //List that will hold the different weight vectors 



    private HashMap<Integer[], Integer> diagramMap; //Holds all the Samples that were provided

    private HashMap<Integer[], Integer> testingInputMap; //Holds all the Samples that were provided

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


        trainingLossList = new ArrayList<>();
        testingLossList = new ArrayList<>();

        trainingAccuracy = new ArrayList<>();
        testingAccuracy = new ArrayList<>();
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


        /*
        matrixManager.addArraysAtIndex(featureSpace, matrixManager.flattenArray(redWireMatrix), addToIndex);

        matrixManager.addArraysAtIndex(featureSpace, matrixManager.flattenArray(blueWireMatrix), addToIndex+=400);

        matrixManager.addArraysAtIndex(featureSpace, matrixManager.flattenArray(yellowWireMatrix),addToIndex+=400);

        matrixManager.addArraysAtIndex(featureSpace, matrixManager.flattenArray(greenWireMatrix), addToIndex+=400);
        
        matrixManager.addArraysAtIndex(featureSpace, matrixManager.flattenArray(matrixManager.multiplyMatrices(redWireMatrix, yellowWireMatrix)), addToIndex+= 400);

        matrixManager.addArraysAtIndex(featureSpace, matrixManager.flattenArray(matrixManager.multiplyMatrices(yellowWireMatrix, redWireMatrix)), addToIndex+= 400);

        */

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

    public void SGD(){
        readTrainingDiagrams();
        readTestingDiagrams();

        Random random = new Random();

        double scaleFactor = 0.1; // Adjust the scale factor based on the desired range of random values
        for(double[] weights: weightsList){
            //Set a random weight value in scale range for every weight in each set of weights
            for (int i = 0; i < weights.length; i++) {
            double randomValue = -scaleFactor + 2 * scaleFactor * random.nextDouble();
            weights[i] = randomValue;
            }
        }

        int iterations = 0;

        while(iterations < NUM_ITERATIONS){
            List<Entry<Integer[], Integer>> randomEntries = getRandomEntriesFromMap(diagramMap, SGD_SAMPLE_SIZE); //Select random entries in the input sample space

            ArrayList<ArrayList<Double[]>> classGradients = new ArrayList<>(); //Create a list that holds lists for each weight
            for(int i = 0 ; i <4; i ++){
                classGradients.add(new ArrayList<Double[]>());
            }

            for (Map.Entry<Integer[], Integer> entry : randomEntries) {
                for(int i = 0 ; i < weightsList.size(); i ++){
                    int[] encodedLabel = Wire.encodeWireColor(entry.getValue());
                    classGradients.get(i).add(computeLossGradient(entry.getKey(), weightsList.get(i) ,encodedLabel[i] )); //Compute the loss gradient for each weight set
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

            trainingAccuracy.add(testSuccessRate(diagramMap));
            testingAccuracy.add(testSuccessRate(testingInputMap));

            iterations++;
        }

        
        
    }

     /**
      * Returns the probability of a classification's label being assigned to an input
      * @param input The input being tested
      * @param weights The weights associated with the classification
      * @return The probability
      */
    public double probabilityOfLabel(Integer[] input, double[] weights){
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
    private double eRaisedToDotProd(Integer[] input, double[] weights){
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
    private Double[] computeLossGradient(Integer[] input, double[] weights, int label) {
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
    private double calculateLoss(HashMap<Integer[], Integer> inputMap){
        double totalLoss = 0;
        
        for(Map.Entry<Integer[], Integer> entry : inputMap.entrySet()){
            int[] encodedLabel = Wire.encodeWireColor(entry.getValue());
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

        return ((totalLoss )/inputMap.size()) + l2RegressionFactor ;
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
            int actualLabel = map.get(input);
            if(predictLabel(input) == actualLabel){
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

    /**
     * Returns the predicted label of a given input based on the current model
     * @param input 
     * @return 1-4 for wire to be cut
     */
    public int predictLabel(Integer[] input){
        //Return the label with the highest probability
        
        double redProb = probabilityOfLabel(input, weightsList.get(0));
        double blueProb = probabilityOfLabel(input, weightsList.get(1));
        double yellowProb = probabilityOfLabel(input, weightsList.get(2));
        double greenProb = probabilityOfLabel(input, weightsList.get(3));

        if(redProb >= blueProb && redProb >= yellowProb && redProb >= greenProb){
            return 1;
        }
        else if(blueProb >= yellowProb && blueProb >= greenProb){
            return 2;
        }
        else if(yellowProb >= greenProb){
            return 3;
        }
        else {
            return 4;
        }
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

    public static void main(String[] args) {

        TaskTwoModel taskTwoModel = new TaskTwoModel(2000, 0.01, 0.01);
        taskTwoModel.SGD();

    }


}
