import java.util.ArrayList;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler;

/**
 * This class is used to generate graphs for the performance of tasks one and two
 */
public class ModelTesting {

    public static final int NUM_PREDICTION_TESTS = 500;

    static final double LAMBDA_USED = 0.005;

    static final double ALPHA_USED = 0.075;

    public ModelTesting(){}

    /**
     * Displays the graph of loss over iterations for task 1
     * @param numSamples Number of samples to train the task one model on 
     * @param alpha The learning rate for task one's model
     * @param lambda The penalizing factor for l2 regression
     */
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
        taskOneModelLossOverIterations.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        
        new SwingWrapper<>(taskOneModelLossOverIterations).displayChart();
    }

    /**
     * Displays the graph of loss over iterations for task 2
     * @param numSamples Number of samples to train the task two model on 
     * @param alpha The learning rate for task two's model
     * @param lambda The penalizing factor for l2 regression
     */
    public void getTaskTwoLossGraph(int numSamples, double alpha, double lambda){
        TaskTwoModel taskTwoModel = new TaskTwoModel(numSamples, alpha, lambda);
        taskTwoModel.SGD();

        ArrayList<Integer> iterations = new ArrayList<>();

        for(int i = 0; i < taskTwoModel.getTrainingLossList().size(); i ++){
            iterations.add(i +1);
        }

        XYChart taskTwoModelLossOverIterations = new XYChartBuilder()
        .width(800)
        .height(600)
        .title("Loss Over SGD")
        .xAxisTitle("Number of Iterations of SGD")
        .yAxisTitle("Loss")
        .build();

        taskTwoModelLossOverIterations.addSeries("Training Loss", iterations, taskTwoModel.getTrainingLossList());
        taskTwoModelLossOverIterations.addSeries("Testing Loss", iterations, taskTwoModel.getTestingLossList());


        taskTwoModelLossOverIterations.getStyler().setMarkerSize(8);
        taskTwoModelLossOverIterations.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        
        new SwingWrapper<>(taskTwoModelLossOverIterations).displayChart();
    }

    /**
     * Displays the graph of accuracy over iterations for task 1
     * @param numSamples Number of samples to train the task one model on 
     * @param alpha The learning rate for task one's model
     * @param lambda The penalizing factor for l2 regression
     */
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
        taskOneModelLossOverIterations.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        
        new SwingWrapper<>(taskOneModelLossOverIterations).displayChart();
    }

    /**
     * Displays the graph of accuracy over iterations for task 2
     * @param numSamples Number of samples to train the task two model on 
     * @param alpha The learning rate for task two's model
     * @param lambda The penalizing factor for l2 regression
     */
    public void getTaskTwoAccuracyGraph(int numSamples, double alpha, double lambda){
        TaskTwoModel taskTwoModel = new TaskTwoModel(numSamples, alpha, lambda);
        taskTwoModel.SGD();

        ArrayList<Integer> iterations = new ArrayList<>();

        for(int i = 0; i < taskTwoModel.getTrainingAccuracyList().size(); i ++){
            iterations.add(i +1);
        }

        XYChart taskTwoModelLossOverIterations = new XYChartBuilder()
        .width(800)
        .height(600)
        .title("Accuracy Over SGD")
        .xAxisTitle("Number of Iterations of SGD")
        .yAxisTitle("% Accuracy")
        .build();

        taskTwoModelLossOverIterations.addSeries("Training Accuracy", iterations, taskTwoModel.getTrainingAccuracyList());
        taskTwoModelLossOverIterations.addSeries("Testing Accuracy", iterations, taskTwoModel.getTestingAccuracyList());


        taskTwoModelLossOverIterations.getStyler().setMarkerSize(8);
        taskTwoModelLossOverIterations.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        
        new SwingWrapper<>(taskTwoModelLossOverIterations).displayChart();
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

        //Adjust the parameters for the following to display the graph of the model with those factors
        //Each generates a model and a graph for it, so comment out the ones not needed

        modelTesting.getTaskOneAccuracyGraph(2000, 0.05, 0.1);
        modelTesting.getTaskOneLossGraph(2000, 0.01, 0.01);
        modelTesting.getTaskTwoAccuracyGraph(2000, 0.01, 0.05);
        modelTesting.getTaskTwoLossGraph(2000, 0.01, 0.05);

        
    }
}
