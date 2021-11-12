dpkg -s openjfx &> /dev/null
if [ $? != 1 ] 
then 

java --module-path ./u_javafx/lib/ --add-modules=ALL-MODULE-PATH -Xmx640m -cp .:./bin\
:lib/*\
:lib/richtextfx-fat-0.8.1.jar tool.xmodeler.XModeler ./ini-linux.txt

else 

if [ $EUID != 0 ]
then

echo "You have to run the application with admin permissions, 
	if the required packages are not already installed!"
echo "Please allow the access as root user to download Open JavaFX: "
sudo apt-get --assume-yes install openjfx
java --module-path ./u_javafx/lib/ --add-modules=ALL-MODULE-PATH -Xmx640m -cp .:./bin\
:lib/*\
:lib/richtextfx-fat-0.8.1.jar tool.xmodeler.XModeler ./ini-linux.txt

else

echo "Installing Open JavaFX.."
sudo apt-get --assume-yes install openjfx

java --module-path ./u_javafx/lib/ --add-modules=ALL-MODULE-PATH -Xmx640m -cp .:./bin\
:lib/*\
:lib/richtextfx-fat-0.8.1.jar tool.xmodeler.XModeler ./ini-linux.txt

fi
fi
