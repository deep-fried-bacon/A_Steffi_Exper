javac -cp "C:\Users\localuser\Desktop\Baylies Lab\fiji-win64\Fiji.app\jars\*;C:\Amelia\Fiji plugins\*;." jsteffi\utilities\*.java
javac -cp "C:\Users\localuser\Desktop\Baylies Lab\fiji-win64\Fiji.app\jars\*;C:\Amelia\Fiji plugins\*;." .\jsteffi\*.java
javac -cp "C:\Users\localuser\Desktop\Baylies Lab\fiji-win64\Fiji.app\jars\*;." A_Steffi_Exper.java
jar cf A_Steffi_Exper.jar .
move /y "C:\Amelia\A_Steffi_Exper\A_Steffi_Exper.jar" "C:\Users\localuser\Desktop\Baylies Lab\fiji-win64\Fiji.app\plugins\A_Steffi_Exper.jar"
