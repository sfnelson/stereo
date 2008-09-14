#import <UIKit/UIKit.h>

@interface StereoPairViewController : UITableViewController {
	NSMutableArray *servers;
	NSString *dictionaryPath;
}

@property (nonatomic, retain) NSMutableArray* servers;
@property (nonatomic, retain) NSString* dictionaryPath;


-(NSString*) serviceName:(NSNetService*)netService;

-(NSString*) resolveDictionaryPath;

-(void)hackMe:(NSNetService*) service;

-(void) alert:(NSString*) msg;

@end

