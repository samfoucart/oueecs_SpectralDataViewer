# Ohio University Spectrometer Data Collection Android App
This Application is a work in progress research project by Sam Foucart and Zach Hughes for Ohio university. This application will over time grow to be able to collect data from an Ocean Optics USB2000 spectrometer. 

# Usage
To use the app, clone the repository and build it with Android Studio. The app is tested on a Samsung Galaxy J3. This app must also be run alongside a bluetooth server on another device. To use it with [our sample server](https://github.com/samfoucart/Spectrometer-Data-Collection), run the testSDP.py with super user privileges. testSDP.py is dependent on libbluetooth-dev and the pybluez library.

# Screenshots/Demos
## [EARLY Youtube Demonstration](https://www.youtube.com/watch?v=PTfK9BFuKY0)
## [MIDPROJECT Youtube Demonstration](https://www.youtube.com/watch?v=v0eC8LafTMs)
## Screenshots
[Demo Parameters Entry](https://drive.google.com/open?id=1DcnOOVq47JpCfwisdSo6X4mdH2YxqXfk)
[Demo Graphing](https://drive.google.com/open?id=1dmwvHeD-1LN7u4VRNMIo36eVYFVH-K5X)
[Demo Device Selection](https://drive.google.com/open?id=1V-ZOr8NpywkSIg3lfYxT3DbuGQYfL-2K)



# The next section is copy and pasted from the other setting up guide.

Getting Started / Setting Up Application
Overview
The Ohio University Spectrometer Data Collection App is an app that connects to an Ocean Optics USB2000 spectrometer. Here’s a great video about what a spectrometer is and how it works on the inside https://www.youtube.com/watch?v=xuwHsSJ5RZ0. 
There are three main programs and services that we use, the ou-spectral-data-collector program, the ou-spectral-data-transmitter service, and the mobile phone applications. The data collector and data transmitter are run and only tested on a raspberry pi, but they should be cross platform. 
•	The ou-spectral-data-collector program is https://github.com/samfoucart/Spectrometer-Data-Collection/blob/master/src/SeabreezeCPPTest.cpp. This depends on the libseabreeze.so library. Seabreeze is a low-level USB communication library that we use to communicate with the spectrometer. 
•	The ou-spectral-data-transmitter service is https://github.com/samfoucart/Spectrometer-Data-Collection/blob/master/testSDP.py. This calls the data collector as a subprocess and acts as a Bluetooth server for sending the data to the app
•	The mobile phone application is a GUI for displaying data collected by the other two programs. It will connect to a Bluetooth server and plot and store data from it. https://github.com/samfoucart/OhioUniversitySpectrometerDataCollectionAndroidApp
Building SeaBreeze From Source
Downloading
The ou-spectral-data-collector program depends on the libseabreeze.so library. This library is written and distributed by Ocean Optics. The source code can be downloaded from https://sourceforge.net/projects/seabreeze/, and the documentation is available at https://oceanoptics.com/api/seabreeze/. 
SeaBreeze itself depends on libusb-dev. To download and install this, type
sudo apt-get install libusb-dev
into a terminal.
Building
To build SeaBreeze with a UNIX style make system, open the file ~/seabreeze-3.0.11/SeaBreeze/common.mk in a text editor. In the CFLAGS_BASE variable, remove the –Werror build flag. The flags should now look like  
Now, you can just type make into the command line while in the seabreeze-3.0.11/SeaBreeze/ folder and it should build.
Installing
Now in your SeaBreeze/lib/ folder, there should be a file called, libseabreeze.so. You can now either type cp lib/libseabreeze.so /usr/lib/ or you can type export LD_LIBRARY_PATH="$PWD/lib" into a command line. This will copy the library into a place where the system looks for libraries or will tell the system where to find the library.
Now type change directory into SeaBreeze/os-support/linux/. There should be a file called 10-oceanoptics.rules. Type cp 10-oceanoptics.rules /etc/udev/rules.d/ to allow non root users to use the program. This was necessary for me to do, and I think I also had to restart the system and plug and unplug the spectrometer a few times to get it to start working. 
Building ou-spectral-data-collector
Downloading
To download, clone the git repository https://github.com/samfoucart/Spectrometer-Data-Collection. 
Building
Change directory into the root directory of the repository. Now type 
g++ src/SeabreezeCPPTest.cpp -I YOUR-PATH-TO-SEABREEZE/seabreeze-3.0.11/SeaBreeze/include/ -L YOUR-PATH-TO-SEABREEZE/seabreeze-3.0.11/SeaBreeze/lib -lseabreeze -lpthread -lstdc++ -lusb -o build/SeabreezeCPPTest
This is all one command, and Word wraps it around hyphens, so be careful. Also, you have to manually change where it says YOUR-PATH-TO-SEABREEZE to your specific path to seabreeze.
Running
The binary executable should be in the repository’s build directory. Now plug in the spectrometer and run the program. If it produces an output file, everything ran okay. 


