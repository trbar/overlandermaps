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
@property(nonatomic, readonly, nullable) NSString *overlanderDescription;
@property(nonatomic, readonly, nullable) NSString *overlanderOpen;
@property(nonatomic, readonly, nullable) NSString *overlanderElectricity;
@property(nonatomic, readonly, nullable) NSString *overlanderWifi;
@property(nonatomic, readonly, nullable) NSString *overlanderKitchen;
@property(nonatomic, readonly, nullable) NSString *overlanderParking;
@property(nonatomic, readonly, nullable) NSString *overlanderRestaurant;
@property(nonatomic, readonly, nullable) NSString *overlanderShowers;
@property(nonatomic, readonly, nullable) NSString *overlanderWater;
@property(nonatomic, readonly, nullable) NSString *overlanderToilets;
@property(nonatomic, readonly, nullable) NSString *overlanderBigrigfriendly;
@property(nonatomic, readonly, nullable) NSString *overlanderTentfriendly;
@property(nonatomic, readonly, nullable) NSString *overlanderPetfriendly;
@property(nonatomic, readonly, nullable) NSString *overlanderSanidump;
@property(nonatomic, readonly, nullable) NSString *overlanderCheckin1author;
@property(nonatomic, readonly, nullable) NSString *overlanderCheckin1time;
@property(nonatomic, readonly, nullable) NSString *overlanderCheckin2comment;
@property(nonatomic, readonly, nullable) NSString *overlanderCheckin2author;
@property(nonatomic, readonly, nullable) NSString *overlanderCheckin2time;
@property(nonatomic, readonly, nullable) NSString *overlanderCheckin3comment;
@property(nonatomic, readonly, nullable) NSString *overlanderCheckin3author;
@property(nonatomic, readonly, nullable) NSString *overlanderCheckin3time;


@end

NS_ASSUME_NONNULL_END
