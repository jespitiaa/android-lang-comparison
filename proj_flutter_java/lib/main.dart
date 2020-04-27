import 'dart:io';
import 'dart:isolate';
import 'package:flutter/foundation.dart';
import 'package:proj_flutter_java/bencher.dart';
import 'package:proj_flutter_java/functions/binarytrees.dart' as bintrees;
import 'package:proj_flutter_java/functions/fannkuchredux.dart' as fannkuch;
import 'package:proj_flutter_java/functions/fasta.dart' as fasta;
import 'package:proj_flutter_java/functions/mandelbrot.dart' as mandel;
import 'package:proj_flutter_java/functions/matrixdeterminant.dart' as matrixdet;
import 'package:proj_flutter_java/functions/nbody.dart' as nbody;
import 'package:proj_flutter_java/functions/spectralnorm.dart' as spec;

SendPort sendPort;
Isolate isolate;

//
// Method that launches a new isolate
// and proceeds with the initial
// hand-shaking
//
void callerCreateIsolate() async {
    //
    // Local and temporary ReceivePort to retrieve
    // the new isolate's SendPort
    //
    ReceivePort receivePort = ReceivePort();

    //
    // Instantiate the new isolate
    //
    isolate = await Isolate.spawn(
        callbackFunction,
        receivePort.sendPort,
        debugName: "other isolate"
    );

    //
    // Retrieve the port to be used for further
    // communication
    //
    sendPort = await receivePort.first;
    print("created isolate named "+isolate.debugName);
    print(""+isolate.controlPort.hashCode.toString()+" "+ receivePort.hashCode.toString());
}

//
// Method that sends a message to the new isolate
// and receives an answer
// 
// In this example, I consider that the communication
// operates with Strings (sent and received data)
//
Future<dynamic> sendReceive(String messageToBeSent) async {
    //
    // We create a temporary port to receive the answer
    //
    ReceivePort port = ReceivePort();

    //
    // We send the message to the Isolate, and also
    // tell the isolate which port to use to provide
    // any answer
    //
    sendPort.send(
        CrossIsolatesMessage<String>(
            sender: port.sendPort,
            message: messageToBeSent,
        )
    );

    //
    // Wait for the answer and return it
    //


    return port.first;
}

//
// Extension of the callback function to process incoming messages
//
void callbackFunction(SendPort callerSendPort){
  print("Entered callback");
  print(callerSendPort.hashCode);
    //
    // Instantiate a SendPort to receive message
    // from the caller
    //
    ReceivePort newIsolateReceivePort = ReceivePort();

    //
    // Provide the caller with the reference of THIS isolate's SendPort
    //
    callerSendPort.send(newIsolateReceivePort.sendPort);

    //
    // Isolate main routine that listens to incoming messages,
    // processes it and provides an answer
    //
    newIsolateReceivePort.listen((dynamic message){
      CrossIsolatesMessage incomingMessage = message as CrossIsolatesMessage;
      
      print("received message from " + incomingMessage.sender.hashCode.toString());
      print(incomingMessage.message);
      
      //
      // Process the message
      //
      String newMessage = "complemented string " + incomingMessage.message;

      if(incomingMessage.message=="binarytrees"){
        bintrees.main(["21"]);
      }
      else if(incomingMessage.message=="fannkuchredux"){
        fannkuch.main(["12"]);
      }
      else if(incomingMessage.message=="fasta"){
        fasta.main(["250000"]);
      }
      else if(incomingMessage.message=="mandelbrot"){
        mandel.main(["16000"]);
      }
      else if(incomingMessage.message=="matrixdeterminant"){
        matrixdet.main([3]); //Determine the size of the matrix
      }
      else if(incomingMessage.message=="nbody"){
        nbody.main(["5000"]);
      }
      else if(incomingMessage.message=="spectralnorm"){
        spec.main(["5500"]);
      }

      //
      // Sends the outcome of the processing
      //
      incomingMessage.sender.send(newMessage);
    });
}

//
// Helper class
//
class CrossIsolatesMessage<T> {
    final SendPort sender;
    final T message;

    CrossIsolatesMessage({
        @required this.sender,
        this.message,
    });
}

//
// Routine to dispose an isolate
//
void dispose(){
    isolate?.kill(priority: Isolate.immediate);
    isolate = null;
}

void main() async{
  Bencher.instance.runGC();
  Bencher.instance.dumpHprof("/sdcard/prevflutter.hprof");
  Bencher.instance.logStart("main");
  
  print("Waiting for devtools ");
  sleep(Duration(seconds: 5));//Time to start up the devtools
  print("Hola juanito from " + Isolate.current.debugName);
  //Timeline.startSync("function");
  await callerCreateIsolate();
  print(await sendReceive("binarytrees"));
  //Timeline.finishSync();
  //print(await sendReceive("fannkuchredux"));
  //print(await sendReceive("fasta"));
  //print(await sendReceive("mandelbrot"));
  //print(await sendReceive("matrixdeterminant"));
  //print(await sendReceive("nbody"));
  //print(await sendReceive("reversecomplement"));
  //print(await sendReceive("spectralnorm"));
  print("Finished");
} 

