import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class TaskOneModel { 

    static final int FEATURE_SPACE_LENGTH = 11200; //400 * (4 + 4!)

    private int numSamples;
    private double alphha;
    private double lambda; //TODO implement regularization


    private HashMap<String, Integer> diagramMap;

    public TaskOneModel(int numSamples, double alpha, double lambda){
        this.numSamples = numSamples;
        this.alphha = alpha;
        this.lambda = lambda;

        diagramMap = new HashMap<>();


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

        
        return featureSpace;
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
