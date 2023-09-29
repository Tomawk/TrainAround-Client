# Train Around (Client) App

This project is about a wearable application called **Train Around**. This app is intended to be used by a group of athletes and a single coach to monitor their performance by analyzing real time data arriving at his/her smartphone. Real time data are gathered using the Train Around application on their smartwatches.  
  
  Data collected are:  
* Step Counter (build-in sensor)
* Heart Rate (build-in sensor)
* Speed (using GPS)
* Distance (using GPS)
* Pace (using GPS, defined as $Time \over Distance$)

We also used the Activity Recognition API to determine the state of the athlete that can be:
* Still (no activity recognized)
* Walking
* Running

Our focus was also on the network to connect the smartwatches to the Coach's Smartphone (central hub). We end-up developing a **Bluetooth Low Energy (BLE) Star Topology Network**. Part of our work involves the choice of this solution comparing it to other alternatives in terms of Compatibility, Versatibility, Simplicity and Energy Efficiency.   

# Network Schema:
![network_schema](https://github.com/Tomawk/TrainingApp_Project/blob/master/img/startopology_network.png)

# Smartphone User Interface

![smartphone_ui](https://github.com/Tomawk/TrainingApp_Project/blob/master/img/user_interface.png)

# Additional Information
This part is the client-side application developed for the smartwatches, if you want to check the code of the server-side app follow this [link](http://example.com "Title").  
You can also find more information in this [Power Point Presentation](https://github.com/Tomawk/TrainingApp_Project/blob/master/TrainAround.pptx).

