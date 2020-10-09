#import "DataDomePlugin.h"
#if __has_include(<data_dome_plugin/data_dome_plugin-Swift.h>)
#import <data_dome_plugin/data_dome_plugin-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "data_dome_plugin-Swift.h"
#endif

@implementation DataDomePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftDataDomePlugin registerWithRegistrar:registrar];
}
@end
