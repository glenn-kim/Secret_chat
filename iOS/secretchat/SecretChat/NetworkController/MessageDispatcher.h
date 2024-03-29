//
//  MessageDispatcher.h
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 4..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Friend.h"
#import "Message.h"

@interface MessageDispatcher : NSObject
+(MessageDispatcher*)getInstance;
-(void)sendSuccess:(long)datetime;
-(void)newChatMessage:(NSDictionary*)dictionary;
//-(Message*)sendMesssageText:(NSString*)message toFriend:(Friend*)friend;
-(Message*)sendMessage:(Message*)msg toFriend:(Friend*)friend;
-(RLMRealm*)chatRealmWithFriend:(Friend*)friend;
void sendMsg(NSString* address, NSString* msg);
void receiveSuccessfully(NSString* sender, long datetime, int idx);
@end
