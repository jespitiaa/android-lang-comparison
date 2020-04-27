FUNCTION='projkotlin.'

for arg in "$@"
do
    case $arg in
        -f=*|--function=*)
	FUNCTION+="${arg#*=}"
        shift # Remove --initialize from processing
        ;;
    esac
done

echo $FUNCTION

adb kill-server
adb start-server
#Launch the service via broadcast
adb shell am broadcast -n com.example.projkotlin/.MyReceiver -a com.example.projkotlin.action.bencher -c android.intent.category.HOME -e function $FUNCTION
#Get the process ID
pid=$(adb shell ps | awk '{if($9=="com.example.projkotlin") print $2}')
if [ -n $pid ] 
then
   endline=""
   #Wait for the line to be logged in adb logcat with constant dumps 
   while [ -z $endline ]
   do
        endline=$(adb logcat --pid=$pid -d | grep -m 1 "\-END")
   done
   startline=$(adb logcat --pid=$pid -d | grep -m 1 "\-START")
   echo $startline 
   echo $endline
   
else
   echo "Pid is null" 
fi

adb logcat -c


