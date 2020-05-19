#Define a function that returns current unix timestamp in ms
timestamp(){
  echo $(($(date +%s%N)/1000000))
}

#the way to call this method is 
#rangeContained foo $num
#being foo a previously declared array and num a previously declared number
rangeContained(){
    #$1 is array $2 is the number
    eval array_internally=("$(echo '${'$1'[@]}')")
    num=$2
    # access array now via array_internally
    for i in "${array_internally[@]}"; do
        dif=$(($num - $i))
        if [ $dif -lt 4000 ]  && [ $dif -gt -4000 ]  #Calculations may vary, so we let the values diverge in around 4 seconds
        then
            echo 'y'
            break
        fi
    done
}

#Define the default period
startTime=$(timestamp)
package="com."

#Read parameters
for arg in "$@"
do
    case $arg in
        -s=*|--start=*)
	startTime="${arg#*=}"
        shift # Remove --start= from processing
        ;;
	-p=*|--package=*)
	package="${arg#*=}"
        shift # Remove --package= from processing
        ;;
    esac
done

cpu=""
tmpCpu=""
checkedStarts=()
checkedEnds=()

echo $startTime

#Dumpsys until the time period includes the desired period
while [ -z "" ]
do
    #echo "iteration"
    #Store a variable containing start ms ago of start and end of the last cpuinfo and create file with dumpsys output
    startendago=$(adb shell dumpsys cpuinfo | tee out.tmp | awk '{if($1~/CPU/)print $4, $6}')
    #echo "$startendago"
    #Separate variables
    startagoms=$(echo $startendago | cut -d ' ' -f 1)
    startago=${startagoms%ms}
    endagoms=$(echo $startendago | cut -d ' ' -f 2)
    endago=${endagoms%ms}

    curTS=$(timestamp)
    #Get timestamp of the last cpuinfo
    startcpu=$(($curTS - $startago))
    lastcpu=$(($curTS - $endago))


    echo "$curTS | Last found: $startcpu - $lastcpu | Looking for $startTime - ?" > cpu-dumpsys.txt
    if [ $startTime -le $lastcpu ]
    then
        startChecked=$(rangeContained checkedStarts $startcpu)
        endChecked=$(rangeContained checkedEnds $lastcpu)
        if [ -z "$startChecked" ] && [ -z "$endChecked" ]
        then
            checkedStarts+=("$startcpu")
            checkedEnds+=("$lastcpu")
            tmpCpu+="$(cat out.tmp | grep -m 1 $package), "
            echo "value changed!"
            echo $tmpCpu > result.txt
        fi
    fi
done
