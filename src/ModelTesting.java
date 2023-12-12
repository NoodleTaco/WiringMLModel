import java.util.ArrayList;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler;

public class ModelTesting {

    public static final int NUM_PREDICTION_TESTS = 500;

    static final double LAMBDA_USED = 0.005;

    static final double ALPHA_USED = 0.075;

    public ModelTesting(){}

    public void getTaskOneLossGraph(int numSamples, double alpha, double lambda){
        TaskOneModel taskOneModel = new TaskOneModel(numSamples, alpha, lambda);
        taskOneModel.SGD();

        ArrayList<Integer> iterations = new ArrayList<>();

        for(int i = 0; i < taskOneModel.getTrainingLossList().size(); i ++){
            iterations.add(i +1);
        }

        XYChart taskOneModelLossOverIterations = new XYChartBuilder()
        .width(800)
        .height(600)
        .title("Loss Over SGD")
        .xAxisTitle("Number of Iterations of SGD")
        .yAxisTitle("Loss")
        .build();

        taskOneModelLossOverIterations.addSeries("Training Loss", iterations, taskOneModel.getTrainingLossList());
        taskOneModelLossOverIterations.addSeries("Testing Loss", iterations, taskOneModel.getTestingLossList());


        taskOneModelLossOverIterations.getStyler().setMarkerSize(8);
        taskOneModelLossOverIterations.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        
        new SwingWrapper<>(taskOneModelLossOverIterations).displayChart();
    }

    public void getTaskOneAccuracyGraph(int numSamples, double alpha, double lambda){
        TaskOneModel taskOneModel = new TaskOneModel(numSamples, alpha, lambda);
        taskOneModel.SGD();

        ArrayList<Integer> iterations = new ArrayList<>();

        for(int i = 0; i < taskOneModel.getTrainingAccuracyList().size(); i ++){
            iterations.add(i +1);
        }

        XYChart taskOneModelLossOverIterations = new XYChartBuilder()
        .width(800)
        .height(600)
        .title("Accuracy Over SGD")
        .xAxisTitle("Number of Iterations of SGD")
        .yAxisTitle("% Accuracy")
        .build();

        taskOneModelLossOverIterations.addSeries("Training Accuracy", iterations, taskOneModel.getTrainingAccuracyList());
        taskOneModelLossOverIterations.addSeries("Testing Accuracy", iterations, taskOneModel.getTestingAccuracyList());


        taskOneModelLossOverIterations.getStyler().setMarkerSize(8);
        taskOneModelLossOverIterations.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        
        new SwingWrapper<>(taskOneModelLossOverIterations).displayChart();
    }
    

    /**
     * Returns the average success rate of a model 
     * @param numSamples
     * @param alpha
     * @param lambda
     * @return
     */
    public double getTaskOneAverageSuccessRate(int numSamples, double alpha, double lambda){
        TaskOneModel taskOneModel = new TaskOneModel(numSamples, alpha, lambda);
        taskOneModel.SGD();

        int[] predictions = new int[NUM_PREDICTION_TESTS];

        for(int i = 0; i < NUM_PREDICTION_TESTS; i ++){
            if(taskOneModel.testPrediction()){
                predictions[i] = 1;
            }
            else
            {
                predictions[i] = 0;
            }
        }

        double averagePredictionSuccessRate = calculateArrayAverage(predictions);

        System.out.println("Average Model Success Rate: " + averagePredictionSuccessRate);

        return averagePredictionSuccessRate;
    }

    /**
     * Overridden get average success rate that takes in assignmentThreshold
     * @param numSamples
     * @param alpha
     * @param lambda
     * @param assignmentThreshold
     * @return
     */
    public double getTaskOneAverageSuccessRate(int numSamples, double alpha, double lambda, double assignmentThreshold){
        TaskOneModel taskOneModel = new TaskOneModel(numSamples, alpha, lambda, assignmentThreshold);
        taskOneModel.SGD();

        int[] predictions = new int[NUM_PREDICTION_TESTS];

        for(int i = 0; i < NUM_PREDICTION_TESTS; i ++){
            if(taskOneModel.testPrediction()){
                predictions[i] = 1;
            }
            else
            {
                predictions[i] = 0;
            }
        }

        double averagePredictionSuccessRate = calculateArrayAverage(predictions);

        System.out.println("Average Model Success Rate: " + averagePredictionSuccessRate);

        return averagePredictionSuccessRate;
    }

    /**
     * Graphs rate of different combinations of alpha and lambda 
     * Tests 5 different alphas and lambda values
     * Generates 5 graphs, each graph has a set lambda which changes between graphs
     * Every graph has the same x axis of alpha values 
     * Done over 3000 samples
     */
    public void testTaskOneAlphaAndLambdaValues(){
        double[] alphaValues = {0.005, 0.01, 0.025, 0.05, 0.075, 0.1}; //learning rate
        double[] lambdaValues = {0.005, 0.01, 0.03, 0.05, 0.1}; //l2 regression penalizing factor

        for(double lambdaVal: lambdaValues){
            double[] successRates = new double[alphaValues.length];

            for(int i = 0 ; i < successRates.length; i ++){
                successRates[i] = getTaskOneAverageSuccessRate(3000, alphaValues[i], lambdaVal);
            }

            XYChart succesRateTestGraph = new XYChartBuilder()
            .width(800)
            .height(600)
            .title("Success Rate With Lambda = " + lambdaVal)
            .xAxisTitle("Alpha Value (Learning Rate)")
            .yAxisTitle("Average Success Rate")
            .build();

            succesRateTestGraph.addSeries("Success with Lambda: " + lambdaVal, alphaValues, successRates);


            succesRateTestGraph.getStyler().setMarkerSize(8);
            succesRateTestGraph.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
            
            new SwingWrapper<>(succesRateTestGraph).displayChart();
        }

    }

    /**
     * Graphs the success rate of task one with varying thresholds for assigning a label
     * Lambda and Alpha are set to constants and numSamples = 3000
     */
    public void testTaskOneAssignmentThresholds(){
        double[] assignmentThresholds = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9};

        double[] successRates = new double[assignmentThresholds.length];

        for(int i = 0; i < assignmentThresholds.length; i ++){
            successRates[i] = getTaskOneAverageSuccessRate(3000, ALPHA_USED, LAMBDA_USED, assignmentThresholds[i]);
        }

        XYChart succesRateTestGraph = new XYChartBuilder()
        .width(800)
        .height(600)
        .title("Success Rate with Varying Assignment Thresholds")
        .xAxisTitle("Assignment Threshold")
        .yAxisTitle("Average Success Rate")
        .build();

        succesRateTestGraph.addSeries("Task One Model", assignmentThresholds, successRates);


        succesRateTestGraph.getStyler().setMarkerSize(8);
        succesRateTestGraph.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        
        new SwingWrapper<>(succesRateTestGraph).displayChart();




    }

    public static double calculateArrayAverage(int[] array) {
        int sum = 0;
        for (int element : array) {
            sum += element;
        }

        double average = (double) sum / array.length;
        return average;
    }


    
    public static void main (String[] args){
        ModelTesting modelTesting = new ModelTesting();
        //modelTesting.getTaskOneAccuracyGraph(5000, 0.05, 0.1);
        
        modelTesting.getTaskOneLossGraph(2000, 0.01, 0.1);
        
        modelTesting.getTaskOneAccuracyGraph(3000, 0.01, 1);
        //modelTesting.testTaskOneAssignmentThresholds();

        //System.out.println("Average: " + modelTesting.getTaskOneAverageSuccessRate(3000, 0.01, 0));
        
    }
}
