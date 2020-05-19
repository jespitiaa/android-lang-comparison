
#Launch the service via broadcast

adb shell am start -n com.example.proj_flutter_java/.MainActivity

#Get the process ID
pid=$(adb shell ps | awk '{if($9=="com.example.proj_flutter_java") print $2}')
if [ -n "$pid" ] 
then
   echo $pid
   startline=""
   while [ -z "$startline" ]
   do
      startline=$(adb logcat --pid=$pid -d | grep -m 1 "\-START")
   done
   starttime=$(echo $startline | cut -d " " -f 7)
   ./cpuavgtest-dart.sh -p="com.example.proj_flutter_java" -s=$starttime &
   cpuPID=$!
   endline=""
   #Wait for the line to be logged in adb logcat with constant dumps 
   while [ -z "$endline" ]
   do
      endline=$(adb logcat --pid=$pid -d | grep -m 1 "\-END")
   done
   
   echo $startline >> "agg-flutter-binarytrees.txt"
   echo $endline >> "agg-flutter-binarytrees.txt"
   
   
   endtime=$(echo $endline | cut -d " " -f 7)
   echo "getting cpu usage"
   lastEndCPU=$(tail -n 1 cpu-dumpsys.txt | cut -d " " -f 7)
   while [ $lastEndCPU -le $endtime ]
   do
      lastEndCPU=$(tail -n 1 cpu-dumpsys.txt | cut -d " " -f 7) 
   done
   kill $cpuPID
   cat result.txt
   echo "$lastEndCPU $endtime"
   cpu=$(cat result.txt)
   echo "cpu usage: $cpu"
   echo "$starttime $endtime $cpu" >> "cpu-flutter-binarytrees.txt"
   adb shell pm clear com.example.proj_flutter_java
else
   echo "Pid not found" 
fi

adb logcat -c


