Java TWAIN
=========
The demo shows how to implement a simple TWAIN scanner software, which calls .NET methods in Java via JNI.

Screenshots
-----------
![image](http://www.codepool.biz/wp-content/uploads/2014/06/Java_twain.png)

How-to
-----------
1. Download [Dynamic .NET TWAIN][1]
2. Download and learn [jni4net][2] v0.8.8 to understand how JVM and CLR work together
3. Correctly configure the paths of JAVA_HOME and  C:\Windows\Microsoft.NET\Framework\v3.5\csc.exe in the system environment
4. Unzip the sample code, and launch JavaTwain.sln to build JavaTwain.dll
5. Copy bin and lib folders from jni4net package to your project directory
6. Run generateProxies.cmd
7. Run run.cmd


Blog
-----------
[Java TWAIN with Dynamic .NET TWAIN and jni4net][3]

[1]:https://www.dynamsoft.com/Secure/Register_ClientInfo.aspx?productName=NetTWAIN&from=FromDownload
[2]:http://jni4net.sourceforge.net/
[3]:http://www.codepool.biz/ocr-barcode-twain/twain-sdk/java-twain-with-dynamic-net-twain-and-jni4net.html
