#import "ZebraScannerPlugin.h"
#if __has_include(<zebra_scanner_plugin/zebra_scanner_plugin-Swift.h>)
#import <zebra_scanner_plugin/zebra_scanner_plugin-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "zebra_scanner_plugin-Swift.h"
#endif

@implementation ZebraScannerPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftZebraScannerPlugin registerWithRegistrar:registrar];
}
@end
