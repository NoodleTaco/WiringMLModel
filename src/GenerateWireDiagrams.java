import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateWireDiagrams {

    public static final String taskOneTrainingData = "C:\\Users\\nasus\\Desktop\\Intro to AI\\Project3\\data\\SafeOrDangerousTrainingData.txt";
    public static final String taskOneTestingData = "C:\\Users\\nasus\\Desktop\\Intro to AI\\Project3\\data\\SafeOrDangerousTestingData.txt";

    public GenerateWireDiagrams(){
    }

    public void generateWiringData(int numEntries, String filePath){
        for(int i = 0; i < numEntries; i ++){
            WireDiagram wireDiagram = new WireDiagram();
            wireDiagram.generateDiagram();
            writeDiagramToFile(wireDiagram, filePath);
        }
    }

    private void writeDiagramToFile(WireDiagram wireDiagram, String filePath){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(wireDiagram.encodeDiagram() + "\n");
            writer.write(wireDiagram.getIsDangerous() +"\n");
            writer.write(wireDiagram.getWireToCut() +"\n \n");


        } catch (IOException e) {
            System.err.println("Error writing array to file: " + e.getMessage());
        }
    }

    

    public static void main (String [] args){

        GenerateWireDiagrams generateWireDiagrams = new GenerateWireDiagrams();
        generateWireDiagrams.generateWiringData(5000, taskOneTestingData);
    }


}
