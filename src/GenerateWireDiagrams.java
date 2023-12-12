import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateWireDiagrams {

    public static final String taskOneTrainingData = "C:\\Users\\nasus\\Desktop\\Intro to AI\\Project3\\data\\SafeOrDangerousTrainingData.txt";
    public static final String taskOneTestingData = "C:\\Users\\nasus\\Desktop\\Intro to AI\\Project3\\data\\SafeOrDangerousTestingData.txt";

    public GenerateWireDiagrams(){
    }

    public void generateSafeDangerousWiringData(int numEntries, String filePath){
        int numSafe = 0;
        int numDang = 0;

        while(numSafe + numDang != numEntries){
            WireDiagram wireDiagram = new WireDiagram();
            wireDiagram.generateDiagram();

            if(numSafe < numEntries/2){
                if(wireDiagram.getIsDangerous() == 0){
                    writeDiagramToFile(wireDiagram, filePath);
                    numSafe++;
                }
            }

             if(numDang < numEntries/2){
                if(wireDiagram.getIsDangerous() == 1){
                    writeDiagramToFile(wireDiagram, filePath);
                    numDang++;
                }
            }
        }
        System.out.println("numSafe: " + numSafe);
        System.out.println("numDang: " + numDang);
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
        generateWireDiagrams.generateSafeDangerousWiringData(5000, taskOneTrainingData);
    }


}
