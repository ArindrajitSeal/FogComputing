
from keras.models import Sequential
from keras.layers import Conv2D
from keras.layers import MaxPooling2D
from keras.layers import Flatten
from keras.layers import Dense

# Initialising the CNN
classifier = Sequential()

# Step 1 - Convolution
classifier.add(Conv2D(32, (3, 3), input_shape = (64, 64, 3), activation = 'relu'))

# Step 2 - Pooling
classifier.add(MaxPooling2D(pool_size = (2, 2)))

# Adding a second convolutional layer
classifier.add(Conv2D(32, (3, 3), activation = 'relu'))
classifier.add(MaxPooling2D(pool_size = (2, 2)))

# Step 3 - Flattening
classifier.add(Flatten())

# Step 4 - Full connection
classifier.add(Dense(units = 128, activation = 'relu'))
classifier.add(Dense(units = 4, activation = 'softmax'))

# Compiling the CNN
#classifier.compile(optimizer = 'adam', loss = 'binary_crossentropy', metrics = ['accuracy'])
classifier.compile(optimizer = 'adam', loss = 'categorical_crossentropy', metrics = ['accuracy'])

# Part 2 - Fitting the CNN to the images

from keras.preprocessing.image import ImageDataGenerator

train_datagen = ImageDataGenerator(rescale = 1./255,
                                   shear_range = 0.2,
                                   zoom_range = 0.2,
                                   horizontal_flip = True)

test_datagen = ImageDataGenerator(rescale = 1./255)

training_set = train_datagen.flow_from_directory('dataset/training_set',
                                                 target_size = (64, 64),
                                                 batch_size = 32,
                                                 class_mode = 'categorical')

test_set = test_datagen.flow_from_directory('dataset/test_set',
                                            target_size = (64, 64),
                                            batch_size = 32,
                                            class_mode = 'categorical')

classifier.fit_generator(training_set,
                         steps_per_epoch = (17567/32),
                         epochs = 25,
                         validation_data = test_set,
                         validation_steps = (540/32))
from keras.models import load_model
classifier.save('my_model_Better2.h5')
del classifier  # deletes the existing model

del model  # deletes the existing model

# Part 3 - Making new predictions


# Part 3 - Making new predictions

#from keras.models import load_model

#classifier2 = load_model('my_model.h5')
#import timeit
#start_time = timeit.default_timer()

#import numpy as np
#from keras.preprocessing import image
#test_image = image.load_img('dataset/single_prediction/HighTraffic_or_LowTraffic_1.jpg', target_size = (64, 64))
#test_image = image.img_to_array(test_image)
#test_image = np.expand_dims(test_image, axis = 0)
#result1 = classifier1.predict(test_image)
#training_set.class_indices
#if result1[0][0] == 1:
#    prediction = 'LowTraffic'
#else:
#    prediction = 'HighTraffic'
    
#Time_To_run_Single_prediction1 = timeit.default_timer() - start_time
    
    
