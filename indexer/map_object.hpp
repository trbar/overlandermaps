#pragma once

#include "indexer/feature_data.hpp"
#include "indexer/feature_decl.hpp"
#include "indexer/feature_meta.hpp"
#include "indexer/ftraits.hpp"

#include "geometry/latlon.hpp"
#include "geometry/mercator.hpp"

#include "coding/string_utf8_multilang.hpp"

#include "base/stl_helpers.hpp"

#include <string>
#include <vector>

class FeatureType;

namespace osm
{
class EditableMapObject;
/// OSM internet_access tag values.
enum class Internet
{
  Unknown,  //!< Internet state is unknown (default).
  Wlan,     //!< Wireless Internet access is present.
  Wired,    //!< Wired Internet access is present.
  Yes,      //!< Unspecified Internet access is available.
  No        //!< There is definitely no any Internet access.
};
std::string DebugPrint(Internet internet);

// Object details in the sorted order, visible to users.
// Must correspond MapObject.java
enum class Props : uint8_t
{
  OpeningHours = 0,
  Phone = 1,
  Fax = 2,
  Website = 3,
  Email = 4,
  Cuisine = 5,
  Stars = 6,
  Operator = 7,
  Elevation = 8,
  Internet = 9,
  Wikipedia = 10,
  Flats = 11,
  BuildingLevels = 12,
  Level = 13,
  ContactFacebook = 14,
  ContactInstagram = 15,
  ContactTwitter = 16,
  ContactVk = 17,
  ContactLine = 18,
  OverlanderLocation = 19,
  OverlanderDescription = 20,
  OverlanderOpen = 21,
  OverlanderElectricity = 22,
  OverlanderWifi = 23,
  OverlanderKitchen = 24,
  OverlanderParking = 25,
  OverlanderRestaurant = 26,
  OverlanderShowers = 27,
  OverlanderWater = 28,
  OverlanderToilets = 29,
  OverlanderBigRigFriendly = 30,
  OverlanderTentFriendly = 31,
  OverlanderPetFriendly = 32,
  OverlanderSaniDump = 33,
  OverlanderBioFuel = 34,
  OverlanderRepairsVehicles = 35,
  OverlanderRepairsMotorcycles = 36,
  OverlanderRepairsBicycles = 37,
  OverlanderSellsParts = 38,
  OverlanderRecyclesBatteries = 39,
  OverlanderRecyclesOil = 40,
  OverlanderCheckin5Id = 41,
  OverlanderCheckin5Time = 42,
  OverlanderCheckin5Author = 43,
  OverlanderCheckin5Comment = 44,
  OverlanderCheckin4Id = 45,
  OverlanderCheckin4Time = 46,
  OverlanderCheckin4Author = 47,
  OverlanderCheckin4Comment = 48,
  OverlanderCheckin3Id = 49,
  OverlanderCheckin3Time = 50,
  OverlanderCheckin3Author = 51,
  OverlanderCheckin3Comment = 52,
  OverlanderCheckin2Id = 53,
  OverlanderCheckin2Time = 54,
  OverlanderCheckin2Author = 55,
  OverlanderCheckin2Comment = 56,
  OverlanderCheckin1Id = 57,
  OverlanderCheckin1Time = 58,
  OverlanderCheckin1Author = 59,
};
std::string DebugPrint(Props props);

class MapObject
{
public:
  void SetFromFeatureType(FeatureType & ft);

  FeatureID const & GetID() const;

  ms::LatLon GetLatLon() const;
  m2::PointD const & GetMercator() const;
  std::vector<m2::PointD> const & GetTriangesAsPoints() const;
  std::vector<m2::PointD> const & GetPoints() const;

  feature::TypesHolder const & GetTypes() const;
  std::string GetDefaultName() const;
  StringUtf8Multilang const & GetNameMultilang() const;

  std::string const & GetHouseNumber() const;

  /// @name Metadata fields.
  //@{
  std::vector<Props> AvailableProperties() const;
  std::string GetPhone() const;
  std::string GetFax() const;
  std::string GetEmail() const;
  std::string GetWebsite() const;
  std::string GetFacebookPage() const;
  std::string GetInstagramPage() const;
  std::string GetTwitterPage() const;
  std::string GetVkPage() const;
  std::string GetLinePage() const;
  Internet GetInternet() const;
  /// @returns non-localized cuisines keys.
  std::vector<std::string> GetCuisines() const;
  /// @returns translated cuisine(s).
  std::vector<std::string> GetLocalizedCuisines() const;
  /// @returns non-localized recycling type(s).
  std::vector<std::string> GetRecyclingTypes() const;
  /// @returns translated recycling type(s).
  std::vector<std::string> GetLocalizedRecyclingTypes() const;
  /// @returns translated and formatted cuisines.
  std::string FormatCuisines() const;
  std::vector<std::string> GetRoadShields() const;
  std::string FormatRoadShields() const;
  std::string GetOpeningHours() const;
  std::string GetOperator() const;
  int GetStars() const;
  /// @returns formatted elevation in feet or meters, or empty string.
  std::string GetElevationFormatted() const;
  bool GetElevation(double & outElevationInMeters) const;
  /// @returns URL to Wikipedia or empty string.
  std::string GetWikipediaLink() const;
  std::string GetFlats() const;
  std::string GetBuildingLevels() const;
  std::string GetLevel() const;
  ftraits::WheelchairAvailability GetWheelchairType() const;
  std::string GetAirportIata() const;
  std::string GetOverlanderLocation() const;
  std::string GetOverlanderDescription() const;
  std::string GetOverlanderOpen() const;
  std::string GetOverlanderElectricity() const;
  std::string GetOverlanderWifi() const;
  std::string GetOverlanderKitchen() const;
  std::string GetOverlanderParking() const;
  std::string GetOverlanderRestaurant() const;
  std::string GetOverlanderShowers() const;
  std::string GetOverlanderWater() const;
  std::string GetOverlanderToilets() const;
  std::string GetOverlanderBigrigfriendly() const;
  std::string GetOverlanderTentfriendly() const;
  std::string GetOverlanderPetfriendly() const;
  std::string GetOverlanderSanidump() const;
  std::string GetOverlanderBiofuel() const;
  std::string GetOverlanderRepairsvehicles() const;
  std::string GetOverlanderRepairsmotorcycles() const;
  std::string GetOverlanderRepairsbicycles() const;
  std::string GetOverlanderSellsparts() const;
  std::string GetOverlanderRecyclesbatteries() const;
  std::string GetOverlanderRecyclesoil() const;
  std::string GetOverlanderCheckin5id() const;
  std::string GetOverlanderCheckin5time() const;
  std::string GetOverlanderCheckin5author() const;
  std::string GetOverlanderCheckin5comment() const;
  std::string GetOverlanderCheckin4id() const;
  std::string GetOverlanderCheckin4time() const;
  std::string GetOverlanderCheckin4author() const;
  std::string GetOverlanderCheckin4comment() const;
  std::string GetOverlanderCheckin3id() const;
  std::string GetOverlanderCheckin3time() const;
  std::string GetOverlanderCheckin3author() const;
  std::string GetOverlanderCheckin3comment() const;
  std::string GetOverlanderCheckin2id() const;
  std::string GetOverlanderCheckin2time() const;
  std::string GetOverlanderCheckin2author() const;
  std::string GetOverlanderCheckin2comment() const;
  std::string GetOverlanderCheckin1id() const;
  std::string GetOverlanderCheckin1time() const;
  std::string GetOverlanderCheckin1author() const;

  // TODO(Vlad, yunikkk): Use Props enum + getters instead of direct metadata access.
  // TODO: Remove this method.
  feature::Metadata const & GetMetadata() const;

  bool IsPointType() const;
  feature::GeomType GetGeomType() const { return m_geomType; };
  /// @returns true if object is of building type.
  bool IsBuilding() const;

protected:
  /// @returns "the best" type to display in UI.
  std::string GetLocalizedType() const;

  FeatureID m_featureID;
  m2::PointD m_mercator;

  std::vector<m2::PointD> m_points;
  std::vector<m2::PointD> m_triangles;

  StringUtf8Multilang m_name;
  std::string m_houseNumber;
  std::string m_roadNumber;
  feature::TypesHolder m_types;
  feature::Metadata m_metadata;

  feature::GeomType m_geomType = feature::GeomType::Undefined;
};

/// Helper to convert internal feature::Metadata::FMD_* enum into a users-visible one.
template <class T>
std::vector<Props> MetadataToProps(std::vector<T> const & metadata)
{
  std::vector<Props> res;
  using feature::Metadata;
  for (auto const type : metadata)
  {
    switch (static_cast<Metadata::EType>(type))
    {
    case Metadata::FMD_OPEN_HOURS: res.push_back(Props::OpeningHours); break;
    case Metadata::FMD_PHONE_NUMBER: res.push_back(Props::Phone); break;
    case Metadata::FMD_FAX_NUMBER: res.push_back(Props::Fax); break;
    case Metadata::FMD_STARS: res.push_back(Props::Stars); break;
    case Metadata::FMD_OPERATOR:
      res.push_back(Props::Operator);
      break;
    // Url is not used in UI and should be matched to Website.
    case Metadata::FMD_URL:
    case Metadata::FMD_WEBSITE: res.push_back(Props::Website); break;
    case Metadata::FMD_CONTACT_FACEBOOK: res.push_back(Props::ContactFacebook); break;
    case Metadata::FMD_CONTACT_INSTAGRAM: res.push_back(Props::ContactInstagram); break;
    case Metadata::FMD_CONTACT_TWITTER: res.push_back(Props::ContactTwitter); break;
    case Metadata::FMD_CONTACT_VK: res.push_back(Props::ContactVk); break;
    case Metadata::FMD_CONTACT_LINE: res.push_back(Props::ContactLine); break;
    case Metadata::FMD_INTERNET: res.push_back(Props::Internet); break;
    case Metadata::FMD_ELE: res.push_back(Props::Elevation); break;
    case Metadata::FMD_EMAIL: res.push_back(Props::Email); break;
    case Metadata::FMD_WIKIPEDIA: res.push_back(Props::Wikipedia); break;
    case Metadata::FMD_OVERLANDER_LOCATION: res.push_back(Props::OverlanderLocation); break;
    case Metadata::FMD_OVERLANDER_DESCRIPTION: res.push_back(Props::OverlanderDescription); break;
    case Metadata::FMD_OVERLANDER_OPEN: res.push_back(Props::OverlanderOpen); break;
    case Metadata::FMD_OVERLANDER_ELECTRICITY: res.push_back(Props::OverlanderElectricity); break; 
    case Metadata::FMD_OVERLANDER_WIFI: res.push_back(Props::OverlanderWifi); break; 
    case Metadata::FMD_OVERLANDER_KITCHEN: res.push_back(Props::OverlanderKitchen); break;
    case Metadata::FMD_OVERLANDER_PARKING: res.push_back(Props::OverlanderParking); break;
    case Metadata::FMD_OVERLANDER_RESTAURANT: res.push_back(Props::OverlanderRestaurant); break;
    case Metadata::FMD_OVERLANDER_SHOWERS: res.push_back(Props::OverlanderShowers); break;
    case Metadata::FMD_OVERLANDER_WATER: res.push_back(Props::OverlanderWater); break;
    case Metadata::FMD_OVERLANDER_TOILETS: res.push_back(Props::OverlanderToilets); break;
    case Metadata::FMD_OVERLANDER_BIGRIGFRIENDLY: res.push_back(Props::OverlanderBigRigFriendly); break;
    case Metadata::FMD_OVERLANDER_TENTFRIENDLY: res.push_back(Props::OverlanderTentFriendly); break;
    case Metadata::FMD_OVERLANDER_PETFRIENDLY: res.push_back(Props::OverlanderPetFriendly); break; 
    case Metadata::FMD_OVERLANDER_SANIDUMP: res.push_back(Props::OverlanderSaniDump); break;
    case Metadata::FMD_OVERLANDER_BIOFUEL: res.push_back(Props::OverlanderBioFuel); break;
    case Metadata::FMD_OVERLANDER_REPAIRSVEHICLES: res.push_back(Props::OverlanderRepairsVehicles); break;
    case Metadata::FMD_OVERLANDER_REPAIRSMOTORCYCLES: res.push_back(Props::OverlanderRepairsMotorcycles); break;
    case Metadata::FMD_OVERLANDER_REPAIRSBICYCLES: res.push_back(Props::OverlanderRepairsBicycles); break;
    case Metadata::FMD_OVERLANDER_SELLSPARTS: res.push_back(Props::OverlanderSellsParts); break;
    case Metadata::FMD_OVERLANDER_RECYCLESBATTERIES: res.push_back(Props::OverlanderRecyclesBatteries); break;
    case Metadata::FMD_OVERLANDER_RECYCLESOIL: res.push_back(Props::OverlanderRecyclesOil); break;
    case Metadata::FMD_OVERLANDER_CHECKIN5ID: res.push_back(Props::OverlanderCheckin5Id); break;
    case Metadata::FMD_OVERLANDER_CHECKIN5TIME: res.push_back(Props::OverlanderCheckin5Time); break;
    case Metadata::FMD_OVERLANDER_CHECKIN5AUTHOR: res.push_back(Props::OverlanderCheckin5Author); break;
    case Metadata::FMD_OVERLANDER_CHECKIN5COMMENT: res.push_back(Props::OverlanderCheckin5Comment); break;
    case Metadata::FMD_OVERLANDER_CHECKIN4ID: res.push_back(Props::OverlanderCheckin4Id); break;
    case Metadata::FMD_OVERLANDER_CHECKIN4TIME: res.push_back(Props::OverlanderCheckin4Time); break;
    case Metadata::FMD_OVERLANDER_CHECKIN4AUTHOR: res.push_back(Props::OverlanderCheckin4Author); break;
    case Metadata::FMD_OVERLANDER_CHECKIN4COMMENT: res.push_back(Props::OverlanderCheckin4Comment); break;
    case Metadata::FMD_OVERLANDER_CHECKIN3ID: res.push_back(Props::OverlanderCheckin3Id); break;
    case Metadata::FMD_OVERLANDER_CHECKIN3TIME: res.push_back(Props::OverlanderCheckin3Time); break;
    case Metadata::FMD_OVERLANDER_CHECKIN3AUTHOR: res.push_back(Props::OverlanderCheckin3Author); break;
    case Metadata::FMD_OVERLANDER_CHECKIN3COMMENT: res.push_back(Props::OverlanderCheckin3Comment); break;
    case Metadata::FMD_OVERLANDER_CHECKIN2ID: res.push_back(Props::OverlanderCheckin2Id); break;
    case Metadata::FMD_OVERLANDER_CHECKIN2TIME: res.push_back(Props::OverlanderCheckin2Time); break;
    case Metadata::FMD_OVERLANDER_CHECKIN2AUTHOR: res.push_back(Props::OverlanderCheckin2Author); break; 
    case Metadata::FMD_OVERLANDER_CHECKIN2COMMENT: res.push_back(Props::OverlanderCheckin2Comment); break;
    case Metadata::FMD_OVERLANDER_CHECKIN1ID: res.push_back(Props::OverlanderCheckin1Id); break;
    case Metadata::FMD_OVERLANDER_CHECKIN1TIME: res.push_back(Props::OverlanderCheckin1Time); break;
    case Metadata::FMD_OVERLANDER_CHECKIN1AUTHOR: res.push_back(Props::OverlanderCheckin1Author); break;
    case Metadata::FMD_FLATS: res.push_back(Props::Flats); break;
    case Metadata::FMD_BUILDING_LEVELS: res.push_back(Props::BuildingLevels); break;
    case Metadata::FMD_LEVEL: res.push_back(Props::Level); break;
    // Cuisines should be processed sepatately since release 10.0.
    case Metadata::FMD_CUISINE:
    case Metadata::FMD_TURN_LANES:
    case Metadata::FMD_TURN_LANES_FORWARD:
    case Metadata::FMD_TURN_LANES_BACKWARD:
    // Postcode should be processed separately, in the address.
    case Metadata::FMD_POSTCODE:
    case Metadata::FMD_HEIGHT:
    case Metadata::FMD_MIN_HEIGHT:
    case Metadata::FMD_DENOMINATION:
    case Metadata::FMD_TEST_ID:
    case Metadata::FMD_AIRPORT_IATA:
    case Metadata::FMD_BRAND:
    case Metadata::FMD_DURATION:
    case Metadata::FMD_COUNT:
      break;
      // Please add new cases when compiler issues an "unhandled switch case" warning here.
    }
  }
  base::SortUnique(res);
  return res;
}
}  // namespace osm
