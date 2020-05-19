operations=( "spectralnorm")
#"binarytrees" "fannkuch" "fasta" "mandelbrot" "matrixdeterminant" "nbody"
adb kill-server
adb start-server

for op in "${operations[@]}"
do
   for i in {0..500}
   do
      echo $i
      ./java_base.sh -f=$op
      sleep 20
   done  
   echo "collected data for $op 500 times"
done
