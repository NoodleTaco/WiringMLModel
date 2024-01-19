public class Wire {
    private int wireColor;

    public static final int NO_WIRE = 0;
    public static final int RED_WIRE = 1;
    public static final int BLUE_WIRE = 2;
    public static final int YELLOW_WIRE = 3;
    public static final int GREEN_WIRE = 4;

    // Default constructor
    public Wire() {
        this.wireColor = NO_WIRE;
    }

    // Getter method for wireColor
    public int getWireColor() {
        return wireColor;
    }

    // Setter method for wireColor
    public void setWireColor(int wireColor) {
        this.wireColor = wireColor;
    }
    // toString method to return a colored square
    @Override
    public String toString() {
        String reset = "\u001B[0m";
        String red = "\u001B[31m";
        String blue = "\u001B[34m";  // ANSI escape code for blue text
        String yellow = "\u001B[33m";
        String green = "\u001B[32m";
        String white = "\u001B[37m";

        switch (wireColor) {
            case RED_WIRE:
                return red + "■ " + reset;  // Red solid square
            case BLUE_WIRE:
                return blue + "■ " + reset;  // Blue solid square
            case YELLOW_WIRE:
                return yellow + "■ " + reset;  // Yellow solid square
            case GREEN_WIRE:
                return green + "■ " + reset;  // Green solid square
            default:
                return white + "■ " + reset;  // White square for no wire (default)
        }
    }

    /**
     * Encode the Wire Color to a 4 length array 
     * @param label Numerical representation of the wire color
     * @return Array representation of the wire color
     */
    public static int[] encodeWireColor(int label){
        
        if(label == Wire.RED_WIRE){
            int[] encodedLabel = {1, 0, 0, 0}; 
            return encodedLabel;
        }
        else if(label == Wire.BLUE_WIRE){
            int[] encodedLabel = {0, 1, 0, 0}; 
            return encodedLabel;
        }
        else if(label == Wire.YELLOW_WIRE){
            int[] encodedLabel = {0, 0, 1, 0}; 
            return encodedLabel;
        }
        else if(label == Wire.GREEN_WIRE){
            int[] encodedLabel = {0, 0, 0, 1}; 
            return encodedLabel;
        }
        else{
            int[] encodedLabel = {0, 0, 0, 0}; 
            return encodedLabel;
        }

    }

    public static void main(String[] args) {
        Wire redWire = new Wire();
        redWire.setWireColor(Wire.RED_WIRE);

        Wire blueWire = new Wire();
        blueWire.setWireColor(Wire.BLUE_WIRE);

        Wire yellowWire = new Wire();
        yellowWire.setWireColor(Wire.YELLOW_WIRE);

        Wire greenWire = new Wire();
        greenWire.setWireColor(Wire.GREEN_WIRE);

        Wire noWire = new Wire(); // Default is NO_WIRE

        // Print all defined colors
        System.out.println("Red Wire: " + redWire);
        System.out.println("Blue Wire: " + blueWire);
        System.out.println("Yellow Wire: " + yellowWire);
        System.out.println("Green Wire: " + greenWire);
        System.out.println("No Wire: " + noWire);
    }

    
}