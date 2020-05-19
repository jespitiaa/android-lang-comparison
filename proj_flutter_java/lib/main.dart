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
Future<void> callerCreateIsolate() async {
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
    newIsolateReceivePort.listen((dynamic message) async{
      CrossIsolatesMessage incomingMessage = message as CrossIsolatesMessage;
      
      print("received message from " + incomingMessage.sender.hashCode.toString());
      print(incomingMessage.message);
      
      //
      // Process the message
      //
      String newMessage;

      if(incomingMessage.message=="binarytrees"){
        newMessage = await bintrees.main(["21"]);
      }
      else if(incomingMessage.message=="fannkuchredux"){
        newMessage = await fannkuch.main(["11"]);
      }
      else if(incomingMessage.message=="fasta"){
        newMessage = await fasta.main(["250000"]);
      }
      else if(incomingMessage.message=="mandelbrot"){
        newMessage = await mandel.main(["12000"]);
      }
      else if(incomingMessage.message=="matrixdeterminant"){
        newMessage = await matrixdet.main(["9"]); //Determine the size of the matrix
      }
      else if(incomingMessage.message=="nbody"){
        newMessage = await nbody.main(["50000"]);
      }
      else if(incomingMessage.message=="spectralnorm"){
        newMessage = await spec.main(["5500"]);
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

const operations = ["binarytrees"];  
//"fannkuchredux","fasta","mandelbrot","matrixdeterminant","nbody","spectralnorm","binarytrees"
const iterations = 1;

void main() async{
  var st ;
  var ls ;
  await callerCreateIsolate();
  for(var operation in operations) {
    st = new DateTime.now().millisecondsSinceEpoch;
    for(var i = 0; i < iterations; i++){
      await iteration(operation, i, operations, iterations);
      sleep(Duration(seconds: 2));
    }//Time to start up the devtools
    ls = new DateTime.now().millisecondsSinceEpoch;
    Bencher.instance.otherLog("Operation-ended", "$operation $st $ls");
  }
  dispose();
  //Timeline.finishSync();
} 

Future<void> iteration(String operation, int i, List operations, int iterations) async{
  print("New iteration $operation $i");
  Bencher.instance.runGC();
  Bencher.instance.logStart("$operation");
  
  String msg = await sendReceive(operation);
  print("Message received from isolate: "+ msg.toString());
  Bencher.instance.logEnd(operation);
  Bencher.instance.runGC();
  if(operation==operations[operations.length-1] && i == iterations-1){
    sleep(Duration(seconds: 8));
    await Bencher.instance.logEnd("FINAL-ITERATION-ENDED");
    exit(0);
  }
}

