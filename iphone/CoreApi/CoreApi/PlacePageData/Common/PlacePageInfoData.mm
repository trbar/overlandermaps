#import "PlacePageInfoData+Core.h"

#import "OpeningHours.h"

#include "map/place_page_info.hpp"

using namespace place_page;
using namespace osm;

@implementation PlacePageInfoData

@end

@implementation PlacePageInfoData (Core)

- (instancetype)initWithRawData:(Info const &)rawData ohLocalization:(id<IOpeningHoursLocalization>)localization {
  self = [super init];
  if (self) {
    auto availableProperties = rawData.AvailableProperties();
    for (auto property : availableProperties) {
      switch (property) {
        case Props::OpeningHours:
          _openingHoursString = @(rawData.GetOpeningHours().c_str());
          _openingHours = [[OpeningHours alloc] initWithRawString:@(rawData.GetOpeningHours().c_str())
                                                     localization:localization];
          break;
        case Props::Phone: {
          _phone = @(rawData.GetPhone().c_str());
          NSString *filteredDigits = [[_phone componentsSeparatedByCharactersInSet:
                                       [[NSCharacterSet decimalDigitCharacterSet] invertedSet]]
                                      componentsJoinedByString:@""];
          NSString *resultNumber = [_phone hasPrefix:@"+"] ? [NSString stringWithFormat:@"+%@", filteredDigits] : filteredDigits;
          _phoneUrl = [NSURL URLWithString:[NSString stringWithFormat:@"tel://%@", resultNumber]];
          break;
        }
        case Props::Website:
          _website = @(rawData.GetWebsite().c_str());
          break;
        case Props::Email:
          _email = @(rawData.GetEmail().c_str());
          break;
        case Props::Cuisine:
          _cuisine = @(strings::JoinStrings(rawData.GetLocalizedCuisines(), Info::kSubtitleSeparator).c_str());
          break;
        case Props::Operator:
          _ppOperator = @(rawData.GetOperator().c_str());
          break;
        case Props::Internet:
          _wifiAvailable = YES;
          break;
        case Props::OverlanderDescription:
          _overlanderDescription = @(rawData.GetOverlanderDescription().c_str());
        case Props::OverlanderOpen:
          _overlanderOpen = @(rawData.GetOverlanderOpen().c_str());
        case Props::OverlanderElectricity:
          _overlanderElectricity = @(rawData.GetOverlanderElectricity().c_str());
        case Props::OverlanderWifi:
          _overlanderWifi = @(rawData.GetOverlanderWifi().c_str());
        case Props::OverlanderKitchen:
          _overlanderKitchen = @(rawData.GetOverlanderKitchen().c_str());
        case Props::OverlanderParking:
          _overlanderParking = @(rawData.GetOverlanderParking().c_str());
        case Props::OverlanderRestaurant:
          _overlanderRestaurant = @(rawData.GetOverlanderRestaurant().c_str());
        case Props::OverlanderShowers:
          _overlanderShowers = @(rawData.GetOverlanderShowers().c_str());
        case Props::OverlanderWater:
          _overlanderWater = @(rawData.GetOverlanderWater().c_str());
        case Props::OverlanderToilets:
          _overlanderToilets = @(rawData.GetOverlanderToilets().c_str());
        case Props::OverlanderBigRigFriendly:
          _overlanderBigrigfriendly = @(rawData.GetOverlanderBigrigfriendly().c_str());
        case Props::OverlanderTentFriendly:
          _overlanderTentfriendly = @(rawData.GetOverlanderTentfriendly().c_str());
        case Props::OverlanderPetFriendly:
          _overlanderPetfriendly = @(rawData.GetOverlanderPetfriendly().c_str());
        case Props::OverlanderSaniDump:
          _overlanderSanidump = @(rawData.GetOverlanderSanidump().c_str());
        default:
          break;
      }
    }

    _address = rawData.GetAddress().empty() ? nil : @(rawData.GetAddress().c_str());
    _rawCoordinates = @(rawData.GetFormattedCoordinate(true).c_str());
    _formattedCoordinates = @(rawData.GetFormattedCoordinate(false).c_str());
  }
  return self;
}

@end
