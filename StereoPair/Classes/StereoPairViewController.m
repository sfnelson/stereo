#import "StereoPairViewController.h"

@implementation StereoPairViewController

@synthesize servers;
@synthesize dictionaryPath;

- (void)viewDidLoad {
	self.servers = [NSMutableArray array];
	self.dictionaryPath = [self resolveDictionaryPath];
	if (dictionaryPath == nil) {
		[self alert:@"Couldn't find dictionary"];
	}
	
	NSNetServiceBrowser *browser = [[NSNetServiceBrowser alloc] init];
	[browser setDelegate:self];
	[browser searchForServicesOfType:@"_touch-able._tcp." inDomain:@"local."];
	[super viewDidLoad];
}


- (NSInteger) numberOfSectionsInTableView:(UITableView*)tv {
	return 1;
}

- (NSInteger) tableView:(UITableView*)tv numberOfRowsInSection:(NSInteger)s {
	return servers.count;
}

- (UITableViewCell*) tableView:(UITableView*)tv cellForRowAtIndexPath:(NSIndexPath*)path {
	UITableViewCell* cell = [[[UITableViewCell alloc] init] autorelease];
	NSNetService *service = [servers objectAtIndex:path.row];
	NSString *serviceName = [self serviceName:service];
	if (serviceName == nil) {
		[cell setText:service.name]; 
	} else {
		[cell setText:serviceName];
	}
	return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	NSNetService *service = [servers objectAtIndex:indexPath.row];
	[self hackMe: service];
}

- (void) alert:(NSString *)msg {
	UIAlertView *view = [[[UIAlertView alloc] initWithTitle:@"Alert"
													message:msg delegate:nil cancelButtonTitle:@"Dismiss" otherButtonTitles:nil] autorelease];
	[view show];
}

- (NSString*) serviceName:(NSNetService*)netService {
	NSDictionary *params = [NSNetService dictionaryFromTXTRecordData:netService.TXTRecordData];
	if (params == nil) return nil;
	NSData *rawName = [params objectForKey:@"CtlN"];
	if (rawName == nil) return nil;
	NSString *name = [[[NSString alloc] initWithData:rawName encoding:NSUTF8StringEncoding] autorelease];
	return name;
}

- (NSString*) resolveDictionaryPath {
	NSString *applicationsDir =	@"/private/var/mobile/Applications";
	NSString *dictionaryRelativePath = @"Library/Preferences/com.apple.Remote.plist";
	
	NSFileManager *fileMgr = [NSFileManager defaultManager];
	NSDirectoryEnumerator *apps = [fileMgr enumeratorAtPath:applicationsDir];
	NSString *path = nil;
	while (path = [apps nextObject]) {
		NSString *potentialPath = [applicationsDir stringByAppendingPathComponent:
								   [path stringByAppendingPathComponent:dictionaryRelativePath]];
		if ([fileMgr fileExistsAtPath:potentialPath]) {
			return potentialPath;
		}
	}
	
	return nil;
}

- (void)hackMe:(NSNetService*) service {
	NSString *identifier = service.name;
	NSString *description = [self serviceName:service];
	if (description == nil) {
		description = @"New Stereo";
	}
	
	if (dictionaryPath == nil) {
		[self alert:@"Failed to find dictionary"];
		return;
	}
	
	NSMutableDictionary *remoteProperties =
	[NSMutableDictionary dictionaryWithContentsOfFile:dictionaryPath];
	
	if (remoteProperties == nil) {
		[self alert:@"Failed to load Remote plist"];
		return;
	}
	
	NSMutableArray *clients = [remoteProperties valueForKey:@"clients"];
	
	if (clients == nil) {
		[self alert:@"Failed to find clients entry in Remote plist"];
		return;
	}
	
	NSMutableDictionary *client	 = [NSMutableDictionary dictionary];
	[client setObject:@"com.apple.DAAP" forKey:@"identifier"];
	[client setObject:description		forKey:@"name"];

	NSMutableDictionary *txt	 = [NSMutableDictionary dictionary];
	
	[txt setObject:[identifier dataUsingEncoding:NSUTF8StringEncoding]
			forKey:@"DbId"];
	[txt setObject:[@"1905" dataUsingEncoding:NSUTF8StringEncoding]
			forKey:@"DvSv"];
	[txt setObject:[@"iTunes" dataUsingEncoding:NSUTF8StringEncoding]
			forKey:@"DvTy"];
	[txt setObject:[@"0x1F6" dataUsingEncoding:NSUTF8StringEncoding]
			forKey:@"OSsi"];
	[txt setObject:[@"131072" dataUsingEncoding:NSUTF8StringEncoding]
			forKey:@"Ver"];
	[txt setObject:[@"1" dataUsingEncoding:NSUTF8StringEncoding]
			forKey:@"txtvers"];
	
	[client setObject:txt                    forKey:@"txt"];
	[client setObject:@"0x0000000000000000"  forKey:@"pairingGuid"];
	[client setObject:identifier             forKey:@"serviceName"];
	[client setObject:@"_touch-able._tcp."   forKey:@"serviceType"];
	[client setObject:@"local."              forKey:@"serviceDomain"];

	[clients addObject:client];
	
	[remoteProperties writeToFile:dictionaryPath atomically:YES];
	
	[self alert:@"Done"];
}

- (void)netServiceBrowser:(NSNetServiceBrowser *)netServiceBrowser
		   didFindService:(NSNetService *)netService
			   moreComing:(BOOL)moreServicesComing {
	[servers addObject:netService];
	[netService setDelegate:self];
	[netService resolve];
	if (moreServicesComing == NO) {
		[[self tableView] reloadData];
	}
}

- (void) netServiceBrowserWillSearch:(NSNetServiceBrowser *)netServiceBrowser {
	
}

- (void)netServiceBrowser:(NSNetServiceBrowser *)netServiceBrowser didNotSearch:(NSDictionary *)errorInfo {
	NSInteger *code = (NSInteger*)[errorInfo objectForKey:NSNetServicesErrorCode];
	NSString *errorReason = [NSString stringWithFormat:@"Error %@ while starting scan", code];
	
	[self alert:errorReason];
	
}

- (void)netServiceDidResolveAddress:(NSNetService *)sender {
	[self.tableView reloadData];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
	return (interfaceOrientation == UIInterfaceOrientationPortrait);
}


- (void)didReceiveMemoryWarning {
	[super didReceiveMemoryWarning];
}


- (void)dealloc {
	[super dealloc];
}

@end
