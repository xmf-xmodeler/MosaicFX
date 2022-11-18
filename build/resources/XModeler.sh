JAVA_VER=$(java -version 2>&1 | sed -n ';s/.* version "\(.*\)\.\(.*\)\..*".*/\1\2/p;')
echo $JAVA_VER
if [ $JAVA_VER -le 18 ]
then

echo "StartUp for Java 8 and older.."
java -Xmx640m -cp .:./bin\
:lib/*\
:lib/richtextfx-fat-0.8.1.jar tool.xmodeler.XModeler ./ini-linux.txt

else
echo "StartUp for Java 9 and newer.."

java --module-path ./u_javafx/lib/ --add-modules=ALL-MODULE-PATH -Xmx640m -cp .:./bin\
:lib/*\
:lib/richtextfx-fat-0.8.1.jar tool.xmodeler.XModeler ./ini-linux.txt

fi
