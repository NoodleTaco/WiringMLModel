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

    static final int FEATURE_SPACE_LENGTH = 6400; //400 * (4 + 4! /2)

    static final double GRADIENT_LOSS_THRESHOLD = 0.05; //Determines where SGD can stop once the gradient of its loss goes below this threshold

    static final int MAX_ITERATIONS = 1000; //max number of iterations of SGD in case it dosen't converge

    static final int SGD_SAMPLE_SIZE = 3; //The size of the input set an iteration of SGD uses

    private int numSamples;
    private double alphha;
    private double lambda; //TODO implement regularization

    private double[] weights;


    private HashMap<Integer[], Integer> diagramMap; //Holds all the Samples that were provided

    public TaskOneModel(int numSamples, double alpha, double lambda){
        this.numSamples = numSamples;
        this.alphha = alpha;
        this.lambda = lambda;

        diagramMap = new HashMap<>();

        weights = new double[FEATURE_SPACE_LENGTH];
    }   

    private void readDiagrams() {
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

        

        int addToIndex = 0;

        matrixManager.addArraysAtIndex(featureSpace, matrixManager.flattenArray(redWireMatrix), addToIndex);

        matrixManager.addArraysAtIndex(featureSpace, matrixManager.flattenArray(blueWireMatrix), addToIndex+=400);

        matrixManager.addArraysAtIndex(featureSpace, matrixManager.flattenArray(yellowWireMatrix),addToIndex+=400);

        matrixManager.addArraysAtIndex(featureSpace, matrixManager.flattenArray(greenWireMatrix), addToIndex+=400);

        ArrayList<int[][]> colorMatrixList = new ArrayList<>();

        colorMatrixList.add(redWireMatrix); colorMatrixList.add(blueWireMatrix); colorMatrixList.add(yellowWireMatrix); colorMatrixList.add(greenWireMatrix);

        for(int[][] matrixOne: colorMatrixList){
            for(int[][] matrixTwo: colorMatrixList){
                if(matrixOne != matrixTwo){
                    matrixManager.addArraysAtIndex(featureSpace, matrixManager.flattenArray(matrixManager.multiplyMatrices(matrixOne, matrixTwo)), addToIndex += 400);

                    /* 
                    System.out.println("matrix One");
                    matrixManager.printMatrix(matrixOne);
                    System.out.println("matrix Two");
                    matrixManager.printMatrix(matrixTwo);
                    System.out.println("matrix One * matrix Two");
                    matrixManager.printMatrix(matrixManager.multiplyMatrices(matrixOne, matrixTwo));
                    System.out.println();
                    */
                }
            }
        }

        /* 
        System.out.println("Red Wire Matrix");

        matrixManager.printMatrix(redWireMatrix);

        System.out.println("Blue Wire Matrix");
        matrixManager.printMatrix(blueWireMatrix);

        System.out.println("Red Wire Matrix * Blue Wire Matrix");
        matrixManager.printMatrix(matrixManager.multiplyMatrices(redWireMatrix, blueWireMatrix));

        System.out.println("blue Wire Matrix * red Wire Matrix");
        matrixManager.printMatrix(matrixManager.multiplyMatrices(redWireMatrix, redWireMatrix));

        

        System.out.println("Feature Space Length: " + featureSpace.length);
        matrixManager.printVector(featureSpace);
        */
       
        return featureSpace;
    }

    /**
     * Performs stochastic gradient descent to update the weights vector 
     * Requires diagramMap to be initialized 
     */
    public void SGD(){
        //First pick a random selection of weights
        Random random = new Random();

        // Generating a random value between -0.1 and 0.1
        double scaleFactor = 0.2; // Adjust the scale factor based on the desired range
        for (int i = 0; i < weights.length; i++) {
            double randomValue = -scaleFactor + 2 * scaleFactor * random.nextDouble();
            weights[i] = randomValue;
        }

        //SGD will stop when the gradient of the loss function goes below a certain threshold 
        int iterations = 0;

        while(iterations < MAX_ITERATIONS){

            HashMap<Integer[], Integer> sampleSet = new HashMap<>();
            

            List<Entry<Integer[], Integer>> randomEntries = getRandomEntriesFromMap(diagramMap, SGD_SAMPLE_SIZE); //Select 3 random entries in the input sample space


            for (Map.Entry<Integer[], Integer> entry : randomEntries) {
            
                sampleSet.put(entry.getKey(), entry.getValue());
            }

            ArrayList<Double[]> gradientList = new ArrayList<>();

            for (Map.Entry<Integer[], Integer> entry : sampleSet.entrySet()) {
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

            //Stop SGD if the gradient of loss is near 0
            if(calculateL2Norm(averageLossGradient) < GRADIENT_LOSS_THRESHOLD){
                break;
            }

            //Update each weight: w_k+1 = w_k - alpha * gradient Loss
            for(int i = 0; i < weights.length; i ++){
                weights[i] -= alphha * averageLossGradient[i];
            }

            System.out.println("Loss: " + calculateLoss(diagramMap));

            iterations ++;

        }

    }   
    //Log Loss
    private double calculateLoss(HashMap<Integer[], Integer> inputMap){
        double totalLoss = 0;
        for (Map.Entry<Integer[], Integer> entry : inputMap.entrySet()) {
           totalLoss += -((entry.getValue()*Math.log(calculateSigmoid(entry.getKey(), weights))) + ((1 - entry.getValue()) * Math.log(1 - calculateSigmoid(entry.getKey(), weights))));
        }
        return totalLoss/inputMap.size();
    }

    /**
     * Calculates thes gradient of the loss over the input 
     * Derivative of loss with respect to weight j = -(y - f(x))xj
     * Details in Report
     * @param input Input considered
     * @param label label of the Input
     */
    private Double[] computeLossGradient(Integer[] input, Integer label) {
        Double[] lossGradient = new Double[input.length];
        double sigmoidMinusLabel = calculateSigmoid(input, weights) - label;
    
        for (int i = 0; i < input.length; i++) {
            lossGradient[i] = input[i] * sigmoidMinusLabel;
        }
    
        return lossGradient;
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
            weightedSum += inputVector[i] * weightVector[i];
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
    
    public static void main(String[] args) {

        TaskOneModel taskOneModel = new TaskOneModel(2000, 0.01, 0.1);
        taskOneModel.readDiagrams();

        taskOneModel.SGD();
    }




}
