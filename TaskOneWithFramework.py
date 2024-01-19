import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import accuracy_score, classification_report
from sklearn.preprocessing import StandardScaler

MAX_ITERATIONS = 1000  # Adjust num iterations

NO_WIRE = 0
RED_WIRE = 1
BLUE_WIRE = 2
YELLOW_WIRE = 3
GREEN_WIRE = 4

def encode_wire_color(label):
    if label == RED_WIRE:
        encoded_label = [1, 0, 0, 0]
    elif label == BLUE_WIRE:
        encoded_label = [0, 1, 0, 0]
    elif label == YELLOW_WIRE:
        encoded_label = [0, 0, 1, 0]
    elif label == GREEN_WIRE:
        encoded_label = [0, 0, 0, 1]
    else:
        encoded_label = [0, 0, 0, 0]
    return encoded_label

# Function to read data from a text file
def read_data(file_path):
    vectors = []
    labels = []

    with open(file_path, 'r') as file:
        while True:
            # Read the series of integers (6400 in length)
            diagram_line = file.readline().strip()
            if not diagram_line:
                break  # Break if end of file is reached
            
            vector = np.array(list(map(int, diagram_line)))

            # Read the integer label
            label_line = file.readline().strip()
            label = int(label_line)

            file.readline()  # Skip the 3rd line
            file.readline()  # Skip the 4th line

            vectors.append(vector)
            labels.append(label)

    return np.array(vectors), np.array(labels)

FEATURE_SPACE_LENGTH = 6400

def get_full_feature_space(diagram):
    if len(diagram) != 400:
        print("Error: Diagram String representation length != 400")
        return None

    temp_dia = np.reshape(diagram, (20,20))
    # Reshape the diagram into a 20x20 matrix
    diagram_matrix = np.array(list(map(int, diagram))).reshape(20, 20)
    
    # Initialize an empty array to store the one-hot encoded representations
    encoded_matrix = [1]
    # Loop through each cell in the diagram_matrix
    for row in diagram_matrix:
        for cell in row:
            # Apply the encoding function to each cell and concatenate to the encoded_matrix
            encoded_matrix.extend(encode_wire_color(cell))

    # Features that represent if a given color overlaps another
    red_overlaps = [0, 0, 0]  # Ex: red_overlaps[0] = 1 means the red wire overlaps blue
    blue_overlaps = [0, 0, 0]
    yellow_overlaps = [0, 0, 0]
    green_overlaps = [0, 0, 0]

    # Populate overlap arrays
    for x in range(len(diagram_matrix)):
        for y in range(len(diagram_matrix[0])):
            if diagram_matrix[x][y] == RED_WIRE:
                neighbor_results = neighbor_color_checker(diagram_matrix, x, y, RED_WIRE)
                if neighbor_results != 0:
                    red_overlaps[neighbor_results - 2] = 1
            elif diagram_matrix[x][y] == BLUE_WIRE:
                neighbor_results = neighbor_color_checker(diagram_matrix, x, y, BLUE_WIRE)
                if neighbor_results == 1:
                    blue_overlaps[neighbor_results - 1] = 1
                elif neighbor_results == 3 or neighbor_results == 4:
                    blue_overlaps[neighbor_results - 2] = 1
            elif diagram_matrix[x][y] == YELLOW_WIRE:
                neighbor_results = neighbor_color_checker(diagram_matrix, x, y, YELLOW_WIRE)
                if neighbor_results == 1 or neighbor_results == 2:
                    yellow_overlaps[neighbor_results - 1] = 1
                elif neighbor_results == 4:
                    yellow_overlaps[neighbor_results - 2] = 1
            elif diagram_matrix[x][y] == GREEN_WIRE:
                neighbor_results = neighbor_color_checker(diagram_matrix, x, y, GREEN_WIRE)
                if neighbor_results != 0:
                    green_overlaps[neighbor_results - 1] = 1
    
    encoded_matrix.extend(red_overlaps) 
    encoded_matrix.extend(blue_overlaps)
    encoded_matrix.extend(yellow_overlaps)
    encoded_matrix.extend(green_overlaps)

    encoded_matrix = np.array(encoded_matrix)

    return encoded_matrix

def get_overrlap_feature_space(diagram):
    if len(diagram) != 400:
        print("Error: Diagram String representation length != 400")
        return None

    temp_dia = np.reshape(diagram, (20,20))
    # Reshape the diagram into a 20x20 matrix
    diagram_matrix = np.array(list(map(int, diagram))).reshape(20, 20)
    
    # Initialize an empty array to store the one-hot encoded representations
    encoded_matrix = [1]

    # Features that represent if a given color overlaps another
    red_overlaps = [0, 0, 0]  # Ex: red_overlaps[0] = 1 means the red wire overlaps blue
    blue_overlaps = [0, 0, 0]
    yellow_overlaps = [0, 0, 0]
    green_overlaps = [0, 0, 0]

    # Populate overlap arrays
    for x in range(len(diagram_matrix)):
        for y in range(len(diagram_matrix[0])):
            if diagram_matrix[x][y] == RED_WIRE:
                neighbor_results = neighbor_color_checker(diagram_matrix, x, y, RED_WIRE)
                if neighbor_results != 0:
                    red_overlaps[neighbor_results - 2] = 1
            elif diagram_matrix[x][y] == BLUE_WIRE:
                neighbor_results = neighbor_color_checker(diagram_matrix, x, y, BLUE_WIRE)
                if neighbor_results == 1:
                    blue_overlaps[neighbor_results - 1] = 1
                elif neighbor_results == 3 or neighbor_results == 4:
                    blue_overlaps[neighbor_results - 2] = 1
            elif diagram_matrix[x][y] == YELLOW_WIRE:
                neighbor_results = neighbor_color_checker(diagram_matrix, x, y, YELLOW_WIRE)
                if neighbor_results == 1 or neighbor_results == 2:
                    yellow_overlaps[neighbor_results - 1] = 1
                elif neighbor_results == 4:
                    yellow_overlaps[neighbor_results - 2] = 1
            elif diagram_matrix[x][y] == GREEN_WIRE:
                neighbor_results = neighbor_color_checker(diagram_matrix, x, y, GREEN_WIRE)
                if neighbor_results != 0:
                    green_overlaps[neighbor_results - 1] = 1

    encoded_matrix.extend(red_overlaps) 
    encoded_matrix.extend(blue_overlaps)
    encoded_matrix.extend(yellow_overlaps)
    encoded_matrix.extend(green_overlaps)

    encoded_matrix = np.array(encoded_matrix)

    return encoded_matrix

def get_red_yellow_feature_space(diagram):
    if len(diagram) != 400:
        print("Error: Diagram String representation length != 400")
        return None

    temp_dia = np.reshape(diagram, (20,20))
    # Reshape the diagram into a 20x20 matrix
    diagram_matrix = np.array(list(map(int, diagram))).reshape(20, 20)

    # Create matrices for each wire
    red_wire_matrix = (diagram_matrix == RED_WIRE).astype(int)
    yellow_wire_matrix = (diagram_matrix == YELLOW_WIRE).astype(int)

    encoded_matrix = [1]


    # Features that represent if a given color overlaps another
    red_overlaps = [0, 0, 0]  # Ex: red_overlaps[0] = 1 means the red wire overlaps blue
    blue_overlaps = [0, 0, 0]
    yellow_overlaps = [0, 0, 0]
    green_overlaps = [0, 0, 0]

    # Populate overlap arrays
    for x in range(len(diagram_matrix)):
        for y in range(len(diagram_matrix[0])):
            if diagram_matrix[x][y] == RED_WIRE:
                neighbor_results = neighbor_color_checker(diagram_matrix, x, y, RED_WIRE)
                if neighbor_results != 0:
                    red_overlaps[neighbor_results - 2] = 1
            elif diagram_matrix[x][y] == BLUE_WIRE:
                neighbor_results = neighbor_color_checker(diagram_matrix, x, y, BLUE_WIRE)
                if neighbor_results == 1:
                    blue_overlaps[neighbor_results - 1] = 1
                elif neighbor_results == 3 or neighbor_results == 4:
                    blue_overlaps[neighbor_results - 2] = 1
            elif diagram_matrix[x][y] == YELLOW_WIRE:
                neighbor_results = neighbor_color_checker(diagram_matrix, x, y, YELLOW_WIRE)
                if neighbor_results == 1 or neighbor_results == 2:
                    yellow_overlaps[neighbor_results - 1] = 1
                elif neighbor_results == 4:
                    yellow_overlaps[neighbor_results - 2] = 1
            elif diagram_matrix[x][y] == GREEN_WIRE:
                neighbor_results = neighbor_color_checker(diagram_matrix, x, y, GREEN_WIRE)
                if neighbor_results != 0:
                    green_overlaps[neighbor_results - 1] = 1
    
    encoded_matrix = np.concatenate(([1], red_wire_matrix.flatten(), yellow_wire_matrix.flatten(), red_overlaps, blue_overlaps, yellow_overlaps, green_overlaps))                              
    return encoded_matrix





def neighbor_color_checker(diagram, x_coord, y_coord, wire_color):
    # Loop through each color
    for color in range(RED_WIRE, GREEN_WIRE + 1):
        # Don't check the color's own wire
        if color != wire_color:
            neighbor_count = 0  # Will hold how many neighbors of a single color there are

            # A wire overlaps another if at any point, one of its cells has two neighbors of the same color wire
            # If the cell has one neighbor but that colored neighbor is perpendicular to it, it's also an overlap
            if x_coord + 1 < len(diagram) and diagram[x_coord + 1][y_coord] == color:
                neighbor_count += 1
                if x_coord + 2 < len(diagram) and diagram[x_coord + 2][y_coord] == color:
                    return color

            if x_coord - 1 > -1 and diagram[x_coord - 1][y_coord] == color:
                neighbor_count += 1
                if x_coord - 2 > -1 and diagram[x_coord - 2][y_coord] == color:
                    return color

            if y_coord + 1 < len(diagram) and diagram[x_coord][y_coord + 1] == color:
                neighbor_count += 1
                if y_coord + 2 < len(diagram) and diagram[x_coord][y_coord + 2] == color:
                    return color

            if y_coord - 1 > -1 and diagram[x_coord][y_coord - 1] == color:
                neighbor_count += 1
                if y_coord - 2 > -1 and diagram[x_coord][y_coord - 2] == color:
                    return color

            if neighbor_count == 2:
                return color

    return 0  # Doesn't overlap, return 0




def main():
    # Read training data, switch path for differing sample sizes
    X_train, y_train = read_data("C:\\Users\\nasus\\Desktop\\Intro to AI\\Project3\\data\\BonusData\\TaskOneTraining2000.txt") 

    # Read testing data 
    X_test, y_test = read_data("C:\\Users\\nasus\\Desktop\\Intro to AI\\Project3\\data\\BonusData\\TaskOneTesting.txt")

    # Convert diagrams to feature space for training data
    X_train_feature_space = np.array([get_red_yellow_feature_space(diagram) for diagram in X_train])
    X_test_feature_space = np.array([get_red_yellow_feature_space(diagram) for diagram in X_test])

    print("Feature Space Length:  " +  str(X_train_feature_space.shape))
    
    model = LogisticRegression(max_iter=MAX_ITERATIONS)

    # Train the model
    model.fit(X_train_feature_space, y_train)

    # Make predictions on the testing data
    predictions = model.predict(X_test_feature_space)

    # Evaluate the model
    accuracy = accuracy_score(y_test, predictions)

    print("Accuracy: {:.2f}%".format(accuracy * 100))

if __name__ == "__main__":
    main()
