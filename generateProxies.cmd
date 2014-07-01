@echo off
mkdir work
copy dll\bin\Release\*.* work
copy lib\*.* work
bin\proxygen.exe work\JavaTwain.dll -wd work
cd work
call build.cmd
cd ..

echo compiling usage
javac -d work\ -cp work\jni4net.j-0.8.6.0.jar;work\JavaTwain.j4n.jar ScanDocuments.java
