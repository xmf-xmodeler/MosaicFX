JAVA_VER=$(java -version 2>&1 | sed -n ';s/.* version "\(.*\)\.\(.*\)\..*".*/\1\2/p;')
if [ $JAVA_VER -le 18 ]
then

echo "StartUp for Java 8 and older.."
java \
-Xmx640m \
-Xdock:icon=Xmodeler.app/Contents/Resources/mosaic-no-bg.icns \
-cp .:./bin\
:lib/*\
:lib/richtextfx-fat-0.8.1.jar tool.xmodeler.XModeler ./ini-linux.txt

else

echo "StartUp for Java 9 and newer.."
java \
-Xmx640m \
-Xdock:icon=Xmodeler.app/Contents/Resources/mosaic-no-bg.icns \
--module-path ./m_javafx/lib/ \
--add-modules=ALL-MODULE-PATH \
-cp .:./bin\
:lib/*\
:lib/richtextfx-fat-0.8.1.jar tool.xmodeler.XModeler ./ini-linux.txt

fi