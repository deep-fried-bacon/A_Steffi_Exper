javac -cp "C:\Users\localuser\Desktop\Baylies Lab\fiji-win64\Fiji.app\jars\*" .\jsteffi\utilities\*.java
javac -cp "C:\Users\localuser\Desktop\Baylies Lab\fiji-win64\Fiji.app\jars\*;.\jsteffi" .\jsteffi\*.java
javac -cp "C:\Users\localuser\Desktop\Baylies Lab\fiji-win64\Fiji.app\jars\*;." A_Steffi_Exper.java
jar cf A_Steffi_Exper.jar .
move /y "C:\Users\localuser\Desktop\Code Laboratory\jsteffi\A_Steffi_Exper\A_Steffi_Exper.jar" "C:\Users\localuser\Desktop\Baylies Lab\fiji-win64\Fiji.app\plugins\A_Steffi_Exper.jar"
