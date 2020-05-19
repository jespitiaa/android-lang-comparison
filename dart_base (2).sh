
#Launch the service via broadcast

adb shell am start -n com.example.proj_flutter_java/.MainActivity

#Get the process ID
pid=$(adb shell ps | awk '{if($9=="com.example.proj_flutter_java") print $2}')
if [ -n "$pid" ] 
then
   echo $pid
   endline=""
   #Wait for the line to be logged in adb logcat with constant dumps 
   while [ -z "$endline" ]
   do
      endline=$(adb logcat --pid=$pid -d | grep -m 1 "\-END")
   done
   startline=$(adb logcat --pid=$pid -d | grep -m 1 "\-START")
   
   echo $startline >> "agg-flutter-binarytrees.txt"
   echo $endline >> "agg-flutter-binarytrees.txt"
   
   adb shell pm clear com.example.proj_flutter_java
   
   starttime=$(echo $startline | cut -d " " -f 7)
   endtime=$(echo $endline | cut -d " " -f 7)
   echo "getting cpu usage"
   cpu=$(echo "$starttime $endtime $(./cpuavgtest.sh -p="com.example.proj_flutter_java" -s=$starttime -e=$endtime)" )
   echo "cpu usage: $cpu"
   echo $cpu >> "cpu-flutter-binarytrees.txt"
else
   echo "Pid not found" 
fi

adb logcat -c


