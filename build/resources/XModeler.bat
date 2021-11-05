SETLOCAL EnableDelayedExpansion
for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do @set v=%%g
@set version=!v:"=! 
@set oldsyn=!version:~1,1!
if %%oldsyn%% EQU "." ( 
@echo StartUp for Java 8 and older.
java ^
-Xmx640m ^
-cp .;.\bin^
;lib\*^
;lib\richtextfx-fat-0.8.1.jar tool.xmodeler.XModeler ini-win.txt 
) else ( if %version% LEQ 8 (
@echo StartUp for Java 8 and older.
java ^
-Xmx640m ^
-cp .;.\bin^
;lib\*^
;lib\richtextfx-fat-0.8.1.jar tool.xmodeler.XModeler ini-win.txt 
) else (
@echo StartUp for Java 9 and newer
java ^
-p .\javafx\lib ^
--add-modules=ALL-MODULE-PATH ^
-Xmx640m ^
-cp .;.\bin^
;lib\*^
;lib\richtextfx-fat-0.8.1.jar tool.xmodeler.XModeler ini-win.txt
) )