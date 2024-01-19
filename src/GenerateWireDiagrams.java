import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GenerateWireDiagrams {

    public static final String taskOneTrainingData = "C:\\Users\\nasus\\Desktop\\Intro to AI\\Project3\\data\\SafeOrDangerousTrainingData.txt";
    public static final String taskOneTestingData = "C:\\Users\\nasus\\Desktop\\Intro to AI\\Project3\\data\\SafeOrDangerousTestingData.txt";
    public static final String taskTwoTrainingData = "C:\\Users\\nasus\\Desktop\\Intro to AI\\Project3\\data\\WireCutTrainingData.txt";
    public static final String taskTwoTestingData = "C:\\Users\\nasus\\Desktop\\Intro to AI\\Project3\\data\\WireCutTestingData.txt";

    public static final String taskOneExtraPath = "C:\\Users\\nasus\\Desktop\\Intro to AI\\Project3\\data\\ExtraCreditData\\TaskOneTesting.txt";

    public GenerateWireDiagrams(){
    }

    /**
     * Generates data for task 1 and writes it to a file
     * Ensures there is a 50/50 split of safe and dangerous in the data
     * @param numEntries
     * @param filePath
     */
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

    /**
     * Generates data for task 2 and writes it to a file
     * Ensures there is an equal portion of each wire to be cut in the sample space
     * @param numEntries
     * @param filePath
     */
    public void generateWireToCutWiringData(int numEntries, String filePath){
        int numRed = 0; 
        int numBlue = 0;
        int numYellow = 0;
        int numGreen = 0;

        while(numRed + numBlue + numYellow + numGreen != numEntries){
            WireDiagram wireDiagram = new WireDiagram();
            wireDiagram.generateDiagram();

            //Diagram needs to be dangerous
            if(wireDiagram.getIsDangerous() == 1){
                if(wireDiagram.getWireToCut() == Wire.RED_WIRE && numRed < numEntries/4){
                    writeDiagramToFile(wireDiagram, filePath);
                    numRed++;
                }
                else if(wireDiagram.getWireToCut() == Wire.BLUE_WIRE && numBlue < numEntries/4){
                    writeDiagramToFile(wireDiagram, filePath);
                    numBlue++;
                }
                else if(wireDiagram.getWireToCut() == Wire.YELLOW_WIRE && numYellow < numEntries/4){
                    writeDiagramToFile(wireDiagram, filePath);
                    numYellow++;
                }
                else if(wireDiagram.getWireToCut() == Wire.GREEN_WIRE && numGreen < numEntries/4){
                    writeDiagramToFile(wireDiagram, filePath);
                    numGreen++;
                }
            }
        }

        System.out.println("numRed: " + numRed + " numBlue: " + numBlue + " numYellow: " + numYellow + " numGreen: " + numGreen);
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
        generateWireDiagrams.generateSafeDangerousWiringData(1000, "C:\\Users\\nasus\\Desktop\\Intro to AI\\Project3\\data\\ExtraCreditData\\TaskOneTesting.txt");
    }


}
