FUNCTION='projjava.'

for arg in "$@"
do
    case $arg in
        -f=*|--function=*)
	FUNCTION+="${arg#*=}"
        shift # Remove --function= from processing
        ;;
    esac
done

echo $FUNCTION

#Launch the service via broadcast
adb shell am broadcast -n com.example.projjava/.MyReceiver -a com.example.projjava.action.bencher -c android.intent.category.HOME -e function $FUNCTION

#Get the process ID
pid=$(adb shell ps | awk '{if($9=="com.example.projjava") print $2}')
if [ -n "$pid" ] 
then
   endline=""
   #Wait for the line to be logged in adb logcat with constant dumps 
   while [ -z "$endline" ]
   do
      endline=$(adb logcat --pid=$pid -d | grep -m 1 "\-END")
   done
   startline=$(adb logcat --pid=$pid -d | grep -m 1 "\-START")
   
   echo $startline >> "agg-${FUNCTION}.txt"
   echo $endline >> "agg-${FUNCTION}.txt"
   
   starttime=$(echo $startline | cut -d " " -f 7)
   endtime=$(echo $endline | cut -d " " -f 7)

   echo "$starttime $endtime $(./cpuavgtest.sh -p="com.example.projjava" -s=$starttime -e=$endtime)" >> "cpu-${FUNCTION}.txt"
else
   echo "Pid not found" 
fi

adb logcat -c


