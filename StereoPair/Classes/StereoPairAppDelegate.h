#import <UIKit/UIKit.h>

@class StereoPairViewController;

@interface StereoPairAppDelegate : NSObject <UIApplicationDelegate> {
	IBOutlet UIWindow *window;
	IBOutlet StereoPairViewController *viewController;
}

@property (nonatomic, retain) UIWindow *window;
@property (nonatomic, retain) StereoPairViewController *viewController;

@end

