import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import accuracy_score, classification_report
from sklearn.preprocessing import StandardScaler

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

            # Skip the next two lines
            file.readline()  # Skip the 3rd line
            file.readline()  # Skip the 4th line

            vectors.append(vector)
            labels.append(label)

    return np.array(vectors), np.array(labels)

FEATURE_SPACE_LENGTH = 6400

def get_feature_space(diagram):
    if len(diagram) != 400:
        print("Error: Diagram String representation length != 400")
        return None

    temp_dia = np.reshape(diagram, (20,20))
    # Reshape the diagram into a 20x20 matrix
    diagram_matrix = np.array(list(map(int, diagram))).reshape(20, 20)

    # Create matrices for each wire
    red_wire_matrix = (diagram_matrix == 0).astype(int)
    blue_wire_matrix = (diagram_matrix == 1).astype(int)
    yellow_wire_matrix = (diagram_matrix == 2).astype(int)
    green_wire_matrix = (diagram_matrix == 3).astype(int)

    # Combine matrices and store them
    matrices = [red_wire_matrix, blue_wire_matrix, yellow_wire_matrix, green_wire_matrix]

    # Initialize feature space with original colored matrices
    feature_space = np.concatenate([matrix.flatten() for matrix in matrices])

    for i in range(len(matrices)):
        for j in range(len(matrices)):
            if i != j:
                combined_matrix = matrices[i] * matrices[j]
                feature_space = np.concatenate([feature_space, combined_matrix.flatten()])
    return feature_space

# # Read training data
# X_train, y_train = read_data("C:\\Users\\nasus\\Desktop\\Intro to AI\\Project3\\data\\SafeOrDangerousTrainingData.txt")

# # Read testing data
# X_test, y_test = read_data("C:\\Users\\nasus\\Desktop\\Intro to AI\\Project3\\data\\SafeOrDangerousTestingData.txt")

# # Convert diagrams to feature space for training data
# X_train_feature_space = np.array([get_feature_space(diagram) for diagram in X_train])

# # Convert diagrams to feature space for testing data
# X_test_feature_space = np.array([get_feature_space(diagram) for diagram in X_test])



# Read training data
X_train, y_train = read_data("C:\\Users\\nasus\\Desktop\\Intro to AI\\Project3\\data\\SafeOrDangerousTrainingData.txt")

# Read testing data
X_test, y_test = read_data("C:\\Users\\nasus\\Desktop\\Intro to AI\\Project3\\data\\SafeOrDangerousTestingData.txt")

# Convert diagrams to feature space for training data
X_train_feature_space = np.array([get_feature_space(diagram) for diagram in X_train])

# Convert diagrams to feature space for testing data
X_test_feature_space = np.array([get_feature_space(diagram) for diagram in X_test])

print("Feature Space Length:  " +  str(X_train_feature_space.shape))

print(X_test_feature_space[0])

# Scale the feature space data
scaler = StandardScaler()
X_train_scaled = scaler.fit_transform(X_train_feature_space)
X_test_scaled = scaler.transform(X_test_feature_space)

# Create and train the logistic regression model with regularization, scaled data, and increased max_iter
model = LogisticRegression(C=1.0, max_iter=1000)  # You can adjust the value of C
model.fit(X_train_scaled, y_train)

# Make predictions on the scaled testing data
y_pred_scaled = model.predict(X_test_scaled)

# Evaluate the model with scaled data
accuracy_scaled = accuracy_score(y_test, y_pred_scaled)
report_scaled = classification_report(y_test, y_pred_scaled)

print(f"Accuracy with scaled data and regularization: {accuracy_scaled:.2f}")
print("Classification Report with scaled data and regularization:\n", report_scaled)