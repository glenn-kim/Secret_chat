//
//  MasterViewController.h
//  SecretChat
//
//  Created by 김창규 on 2015. 4. 28..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import <UIKit/UIKit.h>

@class UserData;

@interface MasterViewController : UITabBarController

@property(nonatomic, strong) UserData *userData;
@property UIView* titleView;
@end

