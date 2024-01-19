import java.util.ArrayList;
import java.util.Random;

public class WireDiagram {

    static final int WIRE_DIAGRAM_SIZE = 20;

    static final int NUM_WIRES = 4;

    private Wire[][] wireDiagram;

    // Represents if the algorithm is laying wires on rows or columns
    private boolean fillingRow;

    private ArrayList<Integer> colorsChosen;  
    private ArrayList<Integer> rowsChosen;    
    private ArrayList<Integer> colsChosen;    

    private int isDangerous;

    private int wireToCut;

    public WireDiagram() {
        wireDiagram = new Wire[WIRE_DIAGRAM_SIZE][WIRE_DIAGRAM_SIZE];
        colorsChosen = new ArrayList<>(NUM_WIRES);
        rowsChosen = new ArrayList<>(NUM_WIRES / 2);
        colsChosen = new ArrayList<>(NUM_WIRES / 2);
    }

    private void initializeDiagram() {
        for (int i = 0; i < WIRE_DIAGRAM_SIZE; i++) {
            for (int j = 0; j < WIRE_DIAGRAM_SIZE; j++) {
                wireDiagram[i][j] = new Wire(); 
            }
        }
    }

    /**
     * Generates the diagram based on the project description
     */
    public void generateDiagram() {
        initializeDiagram();

        if (new Random().nextDouble() >= 0.5) {
            fillingRow = true;
        } else {
            fillingRow = false;
        }

        Random random = new Random();

        for(int i = 0; i < 4; i ++){
            int chosenRowOrCol = random.nextInt(WIRE_DIAGRAM_SIZE);

            if(fillingRow){
                while (rowsChosen.contains(chosenRowOrCol)) {
                    chosenRowOrCol = random.nextInt(WIRE_DIAGRAM_SIZE);
                }
            }
            else{
                while (colsChosen.contains(chosenRowOrCol)) {
                    chosenRowOrCol = random.nextInt(WIRE_DIAGRAM_SIZE);
                }
            }

            int color = random.nextInt(NUM_WIRES) +1;

            while(colorsChosen.contains(color)){
                color = random.nextInt(NUM_WIRES) +1;
            }

            if(fillingRow){
                for(int col = 0; col < WIRE_DIAGRAM_SIZE; col ++){
                    wireDiagram[chosenRowOrCol][col].setWireColor(color);
                }
            }
            else{
                for(int row = 0; row < WIRE_DIAGRAM_SIZE; row ++){
                    wireDiagram[row][chosenRowOrCol].setWireColor(color);
                }
            }

            if(fillingRow){
                rowsChosen.add(chosenRowOrCol);
            }
            else{
                colsChosen.add(chosenRowOrCol);
            }

            colorsChosen.add(color);

            fillingRow = !fillingRow;
        }

        setLabels();

    }

    /**
     * Sets the labels of the diagram based on the colorsChosen array
     */
    private void setLabels(){
        if(colorsChosen.indexOf(Wire.RED_WIRE) < colorsChosen.indexOf(Wire.YELLOW_WIRE)){
            isDangerous = 1;
        }
        else{
            isDangerous = 0;
        }

        if(isDangerous == 0){
            wireToCut = Wire.NO_WIRE;
        }
        else{
            wireToCut = colorsChosen.get(2);
        }


    }

    // Getter method for a particular cell of the wireDiagram matrix
    public Wire getWireAt(int row, int col) {
        return wireDiagram[row][col];
    }

    // Getter method for the isDangerous property
    public int getIsDangerous() {
        return isDangerous;
    }

    // Getter method for the wireToCut property
    public int getWireToCut() {
        return wireToCut;
    }

    public void printDiagram() {
        for (int i = 0; i < WIRE_DIAGRAM_SIZE; i++) {
            for (int j = 0; j < WIRE_DIAGRAM_SIZE; j++) {
                System.out.print(wireDiagram[i][j].toString() + " ");
            }
            System.out.println(); // Move to the next line for the next row
        }
    }

    /**
     * Encodes the diagram by flattening 
     * @return String representation of the diagram
     */
    public String encodeDiagram(){

        StringBuilder encodedDiagram = new StringBuilder();

        for (int i = 0; i < WIRE_DIAGRAM_SIZE; i++) {
            for (int j = 0; j < WIRE_DIAGRAM_SIZE; j++) {
                encodedDiagram.append(wireDiagram[i][j].getWireColor());
            }
        }

        return encodedDiagram.toString();
    }

    //Run file to get a visual representation of the wiring diagram
    public static void main(String[] args) {
        WireDiagram wireDiagram = new WireDiagram();
        wireDiagram.generateDiagram();
        wireDiagram.printDiagram();
        System.out.println(wireDiagram.encodeDiagram());
    }


}
