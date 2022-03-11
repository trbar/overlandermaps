#import <Foundation/Foundation.h>

@class OpeningHours;

NS_ASSUME_NONNULL_BEGIN

@interface PlacePageInfoData : NSObject

@property(nonatomic, readonly, nullable) NSString *openingHoursString;
@property(nonatomic, readonly, nullable) OpeningHours *openingHours;
@property(nonatomic, readonly, nullable) NSString *phone;
@property(nonatomic, readonly, nullable) NSURL *phoneUrl;
@property(nonatomic, readonly, nullable) NSString *website;
@property(nonatomic, readonly, nullable) NSString *email;
@property(nonatomic, readonly, nullable) NSString *cuisine;
@property(nonatomic, readonly, nullable) NSString *ppOperator;
@property(nonatomic, readonly, nullable) NSString *address;
@property(nonatomic, readonly, nullable) NSString *rawCoordinates;
@property(nonatomic, readonly, nullable) NSString *formattedCoordinates;
@property(nonatomic, readonly) BOOL wifiAvailable;
@property(nonatomic, readonly, nullable) NSString *overlanderId;
@property(nonatomic, readonly, nullable) NSString *overlanderCategory;
@property(nonatomic, readonly, nullable) NSString *overlanderSource;
@property(nonatomic, readonly, nullable) NSString *overlanderDescription;
@property(nonatomic, readonly, nullable) NSString *overlanderServices;
@property(nonatomic, readonly, nullable) NSString *overlanderActivities;
@property(nonatomic, readonly, nullable) NSString *overlanderLink;
@property(nonatomic, readonly, nullable) NSString *overlanderRating;
@property(nonatomic, readonly, nullable) NSString *overlanderTags;
@property(nonatomic, readonly, nullable) NSString *overlanderCheckin3;
@property(nonatomic, readonly, nullable) NSString *overlanderCheckin2;
@property(nonatomic, readonly, nullable) NSString *overlanderCheckin1;


@end

NS_ASSUME_NONNULL_END
