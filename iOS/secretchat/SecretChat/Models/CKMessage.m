//
//  CKMessage.m
//  SecretChat
//
//  Created by 김창규 on 2015. 5. 4..
//  Copyright (c) 2015년 the.accidental.billionaire. All rights reserved.
//

#import "CKMessage.h"

@implementation CKMessage

// Specify default values for properties

+ (NSDictionary *)defaultPropertyValues
{
    return @{
             @"url":@"",
             @"text":@""
             };
}

// Specify properties to ignore (Realm won't persist these)

//+ (NSArray *)ignoredProperties
//{
//    return @[];
//}

@end