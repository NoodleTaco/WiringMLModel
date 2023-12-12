public class matrixManager {
    public static int[][] multiplyMatrices(int[][] matrixA, int[][] matrixB) {
        int rowsA = matrixA.length;
        int colsA = matrixA[0].length;
        int colsB = matrixB[0].length;

        int[][] result = new int[rowsA][colsB];

        for (int i = 0; i < rowsA; i++) {
            for (int j = 0; j < colsB; j++) {
                for (int k = 0; k < colsA; k++) {
                    result[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }

        return result;
    }

    

    // Helper method to print a matrix
    public static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }

    /**
     * Prints the contents of a vector.
     *
     * @param vector The vector to be printed.
     */
    public static void printVector(int[] vector) {
        for (int value : vector) {
            System.out.print(value);
        }
        System.out.println();
    }

    public static void printVector(Integer[] vector) {
        for (int value : vector) {
            System.out.print(value);
        }
        System.out.println();
    }

    


    public static void addArraysAtIndex(Integer[] arrayToAddTo, int[] arrayToAddFrom, int startIndex) {
        if (startIndex < 0 || startIndex >= arrayToAddTo.length) {
            System.out.println("Invalid start index.");
            return;
        }

        int arrayToAddFromIndex = 0;
        for (int i = startIndex; i < arrayToAddTo.length && arrayToAddFromIndex < arrayToAddFrom.length; i++) {
            arrayToAddTo[i] = arrayToAddFrom[arrayToAddFromIndex];
            arrayToAddFromIndex++;
        }
    }

    public static int[] flattenArray(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        int[] flattenedArray = new int[rows * cols];

        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                flattenedArray[index++] = matrix[i][j];
            }
        }

        return flattenedArray;
    }
}
