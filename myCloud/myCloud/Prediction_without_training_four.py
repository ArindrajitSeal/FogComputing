from keras.models import load_model

classifier2 = load_model('my_model_Better2.h5')
import timeit
start_time = timeit.default_timer()
import argparse
parser = argparse.ArgumentParser()
parser.add_argument("echo")
args = parser.parse_args()
#print(args.echo)

import numpy as np
from keras.preprocessing import image

#from PIL import Image
#from IPython.display import display
#from PIL import Image
#test_image = image.load_img('dataset/single_prediction/LowTraffic3.jpg', target_size = (64, 64))
test_image = image.load_img(args.echo, target_size = (64, 64))
#test_image=Image.open(args.echo)
test_image = image.img_to_array(test_image)


test_image = np.expand_dims(test_image, axis = 0)
result = classifier2.predict(test_image)
#training_set.class_indices

if result[0][0] == 1:
    prediction = 'HighTraffic'
    print("High_Traffic")
    file = open('output.txt', 'w')
    file.write("HighTraffic")
    file.close()

if result[0][1]==1:
    prediction = 'LowTraffic'
    print("Low_Traffic")
    file = open('output.txt', 'w')
    file.write("LowTraffic")
    file.close()

if result[0][2]==1:
   prediction = 'PEDESTRIANS'
   print("Pedestrian")
   file = open('output.txt', 'w')
   file.write("PEDESTRIANS")
   file.close()
if result[0][3]==1:
    prediction='MediumTraffic'
    print("Medium_Traffic")
    file = open('output.txt', 'w')
    file.write("MediumTraffic")
    file.close()


Time_To_run_Single_prediction = timeit.default_timer() - start_time
