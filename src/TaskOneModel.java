import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class TaskOneModel { 

    static final int FEATURE_SPACE_LENGTH = 6400; //400 * (4 + 4! /2)

    static final double GRADIENT_LOSS_THRESHOLD = 0.01; //Determines where SGD can stop once the gradient of its loss goes below this threshold

    static final int MAX_ITERATIONS = 100000; //max number of iterations of SGD in case it dosen't converge

    private int numSamples;
    private double alphha;
    private double lambda; //TODO implement regularization

    private double[] weights;


    private HashMap<String, Integer> diagramMap; //Holds all the Samples that were provided

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
                diagramMap.put(diagram, label);

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
    private int[] getFeatureSpace(String diagram){
        if(diagram.length() != 400){
            System.out.println("Error: Diagram String representation legnth != 400");
            return null;
        }

        System.out.println(diagram);

        int[] featureSpace = new int[FEATURE_SPACE_LENGTH];

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

        */

        System.out.println("Feature Space Length: " + featureSpace.length);
        matrixManager.printVector(featureSpace);

       
        return featureSpace;
    }

    /**
     * Performs stochastic gradient descent to update the weights vector 
     */
    public void SGD(){
        //First pick a random selection of weights
        Random random = new Random();
        for (int i = 0; i < weights.length; i++) {
            double randomValue = -1 + 2 * random.nextDouble(); // Generates a random value between -1 and 1
            weights[i] = randomValue;
        }

        //SGD will stop when the gradient of the loss function goes below a certain threshold 
        int iterations = 0;
        while(iterations < MAX_ITERATIONS){

        }



    }   

    /**
     * Calculates thes gradient of the loss over the input 
     * Derivative of loss with respect to weight j = -(y - f(x))xj
     * Details in Report
     * @param input Input considered
     * @param label label of the Input
     */
    private void computeLossGradient(int[] input, int label){
        double[] lossGradient = new double[input.length];
        double sigmoidMinusLabel = calculateSigmoid(input, weights) - label;

        for(int i = 0; i < input.length; i ++){
            lossGradient[i] = input[i] * sigmoidMinusLabel;
        }
    }



     /**
     * Calculates the sigmoid function given an input vector and a weight vector.
     * @param inputVector  The input vector (array of ints).
     * @param weightVector The weight vector (array of doubles).
     * @return The result of the sigmoid function.
     */
    public static double calculateSigmoid(int[] inputVector, double[] weightVector) {
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

    public static double calculateL2Norm(double[] vector) {
        double sumOfSquares = 0.0;

        // Calculate the sum of squares of each element
        for (double element : vector) {
            sumOfSquares += element * element;
        }

        // Calculate the square root of the sum of squares
        double l2Norm = Math.sqrt(sumOfSquares);

        return l2Norm;
    }


    public HashMap<String, Integer> getDiagramMap(){
        return diagramMap;
    }
    
    public static void main(String[] args) {

        TaskOneModel taskOneModel = new TaskOneModel(1, 0.1, 0.1);
        taskOneModel.readDiagrams();

        String[] stuff = taskOneModel.getDiagramMap().keySet().toArray(new String[0]);

        taskOneModel.getFeatureSpace(stuff[0]);
    }




}
