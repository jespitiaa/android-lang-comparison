
adb kill-server
adb start-server

   for i in {0..500}
   do
      echo $i
      ./dart_base.sh
      sleep 20
   done  