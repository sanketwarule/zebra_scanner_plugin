import 'package:flutter/material.dart';
import 'package:zebra_scanner_plugin/zebra_scanner_plugin.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  List<Barcode> scannedCodes = [];

  ScrollController listScrollController = ScrollController();

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Zebra Scanner Plugin example app'),
          actions: [
            PopupMenuButton(itemBuilder: (context) => [
              PopupMenuItem(
                child: const Text("Scan and connect"),
                onTap: () async{
                  await ZebraScannerPlugin.initScanner;
                  // await ZebraScannerPlugin.connectToScanner("48:01:c5:9e:e7:8c");
                  await ZebraScannerPlugin.connect();

                },
                value: 1,
              ),
            ])
          ],
        ),
        body: StreamBuilder<Barcode>(
            stream: ZebraScannerPlugin.barcodeStream,
            builder: (context, snapshot) {
              Barcode? data;
              if (snapshot.hasData) {
                data = snapshot.data;
                scannedCodes.add(data!);
                if (listScrollController.hasClients) {
                  final position = listScrollController.position.maxScrollExtent;
                  listScrollController.jumpTo(position);
                }
              } else if (snapshot.hasError){
                print(snapshot.error);
              }
              return Padding(
                padding: const EdgeInsets.all(10.0),
                child: ListView.builder(itemBuilder: (context, index) {
                  return ListTile(title: Text(scannedCodes[index].data),
                      leading: const Icon(Icons.qr_code,),
                  trailing: Text(scannedCodes[index].type),
                  );
                },
                  controller: listScrollController,
                  itemCount: scannedCodes.length,
                ),
              );
            }
        ),
      ),
    );
  }
}
