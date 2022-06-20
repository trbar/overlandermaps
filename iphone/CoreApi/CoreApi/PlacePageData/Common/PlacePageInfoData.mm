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
        case Props::OverlanderId:
          _overlanderId = @(rawData.GetOverlanderId().c_str());
        case Props::OverlanderSource:
          _overlanderSource = @(rawData.GetOverlanderSource().c_str());
        case Props::OverlanderDescription:
          _overlanderDescription = @(rawData.GetOverlanderDescription().c_str());
        case Props::OverlanderServices:
          _overlanderServices = @(rawData.GetOverlanderServices().c_str());
        case Props::OverlanderActivities:
          _overlanderActivities = @(rawData.GetOverlanderActivities().c_str());
        case Props::OverlanderLink:
          _overlanderLink = @(rawData.GetOverlanderLink().c_str());
        case Props::OverlanderRating:
          _overlanderRating = @(rawData.GetOverlanderRating().c_str());
        case Props::OverlanderTags:
          _overlanderTags = @(rawData.GetOverlanderTags().c_str());
        case Props::OverlanderCheckin3:
          _overlanderCheckin3 = @(rawData.GetOverlanderCheckin3().c_str());
        case Props::OverlanderCheckin2:
          _overlanderCheckin2 = @(rawData.GetOverlanderCheckin2().c_str());
        case Props::OverlanderCheckin1:
          _overlanderCheckin1 = @(rawData.GetOverlanderCheckin1().c_str());
        case Props::OverlanderSpots:
          _overlanderSpots = @(rawData.GetOverlanderSpots().c_str());
        case Props::OverlanderOpen:
          _overlanderOpen = @(rawData.GetOverlanderOpen().c_str());
        case Props::OverlanderServiceprices:
          _overlanderServiceprices = @(rawData.GetOverlanderServiceprices().c_str());
        case Props::OverlanderParkingprices:
          _overlanderParkingprices = @(rawData.GetOverlanderParkingprices().c_str());
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
