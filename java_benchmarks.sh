operations=("binarytrees" "fannkuch" "fasta" "mandelbrot" "matrixdeterminant" "nbody" "spectralnorm")

for op in "${operations[@]}"
do
   for i in {0..50}
   do
      ./java_base.sh -f=$op
   done  
   echo "collected data for $op 50 times"
done
