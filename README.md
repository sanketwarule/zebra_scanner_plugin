# zebra_scanner_plugin

A new flutter plugin for scanning a barcode through zebra scanner device.

## Getting Started

This project is a starting point for a Flutter
[plug-in package](https://flutter.dev/developing-packages/),
a specialized package that includes platform-specific implementation code for
Android and/or iOS.

For help getting started with Flutter, view our
[online documentation](https://flutter.dev/docs), which offers tutorials,
samples, guidance on mobile development, and a full API reference.

## Output

<img src="https://github.com/sanketwarule/zebra_scanner_plugin/blob/master/zebra_scanner_plugin_example_output.gif"/>

## How to use

# First
Run this command:
```bash
flutter pub add zebra_scanner_plugin
```
This will add `zebra_scanner_plugin` to your pubspec.yaml dependencies like:
```yaml
zebra_scanner_plugin: ^latest_version
```
Then run `flutter pub get` to download the library sources to your pub-cache.

# Second
Copy the **BarcodeScannerLibrary** folder which is inside the example code sources at:

`.../.pub-cache/hosted/pub.dartlang.org/zebra_scanner_plugin-x.x.x+x/example/android/zebra_scanner_plugin`

into your android project module which is going to use this plugin. This step is necessary and crucial because the BarcodeScannerLibrary Data Collection Android library is a bundle .aar which has to be referenced as a project library.


# Third
Add this `include ':BarcodeScannerLibrary'` to your `settings.gradle` in your android project module to allow the plugin to locate the barcode_scanner_library.aar library.


# Fourth

Add `tools:replace="android:label"` under `application` tag in your **AndroidManifest.xml**, this is required because the **barcode_scanner_library.aar** library defines an `android:label="@string/app_name"` which conflicts with your project's label resulting in a *Manifest merger failed* error

