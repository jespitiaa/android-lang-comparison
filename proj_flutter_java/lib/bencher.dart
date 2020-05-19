import 'package:flutter/services.dart';

class Bencher{
  static const platform = MethodChannel("com.example.flutter/bencher");
  //Singleton declaration
  Bencher._privateConstructor();
  static final Bencher instance = Bencher._privateConstructor();

  void logStart(String tag) async{
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent("tag", () => tag);
    await platform.invokeMethod("logStart", args);
  }
  Future<void> otherLog(String tag, String content) async{
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent("tag", () => tag);
    args.putIfAbsent("content", () => content);
    await platform.invokeMethod("otherLog", args);
  }
  Future<void> logEnd(String tag) async{
    Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent("tag", () => tag);
    await platform.invokeMethod("logEndResults", args);
  }
  void runGC() async{
    await platform.invokeMethod("runGC");
  }
  void dumpHprof(String path) async{
    /*Map<String, dynamic> args = <String, dynamic>{};
    args.putIfAbsent("path", () => path);
    await platform.invokeMethod("dumpHprof", args);*/
  }
}