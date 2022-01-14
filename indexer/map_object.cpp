#include "map_object.hpp"

#include "indexer/feature.hpp"
#include "indexer/feature_algo.hpp"
#include "indexer/feature_utils.hpp"
#include "indexer/ftypes_matcher.hpp"

#include "platform/localization.hpp"
#include "platform/measurement_utils.hpp"
#include "platform/preferred_languages.hpp"

#include "base/logging.hpp"
#include "base/string_utils.hpp"

using namespace std;

namespace osm
{
namespace
{
constexpr char const * kWlan = "wlan";
constexpr char const * kWired = "wired";
constexpr char const * kYes = "yes";
constexpr char const * kNo = "no";
constexpr char const * kFieldsSeparator = " • ";

void SetInetIfNeeded(FeatureType & ft, feature::Metadata & metadata)
{
  if (!ftypes::IsWifiChecker::Instance()(ft) || metadata.Has(feature::Metadata::FMD_INTERNET))
    return;

  metadata.Set(feature::Metadata::FMD_INTERNET, kWlan);
}
}  // namespace

string DebugPrint(osm::Internet internet)
{
  switch (internet)
  {
  case Internet::No: return kNo;
  case Internet::Yes: return kYes;
  case Internet::Wlan: return kWlan;
  case Internet::Wired: return kWired;
  case Internet::Unknown: break;
  }
  return {};
}

string DebugPrint(Props props)
{
  string k;
  switch (props)
  {
  case osm::Props::Phone: k = "phone"; break;
  case osm::Props::Fax: k = "fax"; break;
  case osm::Props::Email: k = "email"; break;
  case osm::Props::Website: k = "website"; break;
  case osm::Props::ContactFacebook: k = "contact:facebook"; break;
  case osm::Props::ContactInstagram: k = "contact:instagram"; break;
  case osm::Props::ContactTwitter: k = "contact:twitter"; break;
  case osm::Props::ContactVk: k = "contact:vk"; break;
  case osm::Props::ContactLine: k = "contact:line"; break;
  case osm::Props::Internet: k = "internet_access"; break;
  case osm::Props::Cuisine: k = "cuisine"; break;
  case osm::Props::OpeningHours: k = "opening_hours"; break;
  case osm::Props::Stars: k = "stars"; break;
  case osm::Props::Operator: k = "operator"; break;
  case osm::Props::Elevation: k = "ele"; break;
  case osm::Props::Wikipedia: k = "wikipedia"; break;
  case osm::Props::Flats: k = "addr:flats"; break;
  case osm::Props::BuildingLevels: k = "building:levels"; break;
  case osm::Props::Level: k = "level"; break;
  case osm::Props::OverlanderLocation: k = "overlander:location"; break;
  case osm::Props::OverlanderDescription: k = "overlander:description"; break;
  case osm::Props::OverlanderOpen: k = "overlander:open"; break;
  case osm::Props::OverlanderElectricity: k = "overlander:electricity"; break;
  case osm::Props::OverlanderWifi: k = "overlander:wifi"; break;
  case osm::Props::OverlanderKitchen: k = "overlander:kitchen"; break;
  case osm::Props::OverlanderParking: k = "overlander:parking"; break;
  case osm::Props::OverlanderRestaurant: k = "overlander:restaurant"; break;
  case osm::Props::OverlanderShowers: k = "overlander:showers"; break;
  case osm::Props::OverlanderWater: k = "overlander:water"; break;
  case osm::Props::OverlanderToilets: k = "overlander:toilets"; break;
  case osm::Props::OverlanderBigRigFriendly: k = "overlander:bigrigfriendly"; break;
  case osm::Props::OverlanderTentFriendly: k = "overlander:tentfriendly"; break;
  case osm::Props::OverlanderPetFriendly: k = "overlander:petfriendly"; break;
  case osm::Props::OverlanderSaniDump: k = "overlander:sanidump"; break;
  case osm::Props::OverlanderBioFuel: k = "overlander:biofuel"; break;
  case osm::Props::OverlanderRepairsVehicles: k = "overlander:repairsvehicles"; break;
  case osm::Props::OverlanderRepairsMotorcycles: k = "overlander:repairsmotorcycles"; break;
  case osm::Props::OverlanderRepairsBicycles: k = "overlander:repairsbicycles"; break;
  case osm::Props::OverlanderSellsParts: k = "overlander:sellsparts"; break;
  case osm::Props::OverlanderRecyclesBatteries: k = "overlander:recyclesbatteries"; break;
  case osm::Props::OverlanderRecyclesOil: k = "overlander:recyclesoil"; break;
  case osm::Props::OverlanderCheckin5Id: k = "overlander:checkin5id"; break;
  case osm::Props::OverlanderCheckin5Time: k = "overlander:checkin5time"; break;
  case osm::Props::OverlanderCheckin5Author: k = "overlander:checkin5author"; break;
  case osm::Props::OverlanderCheckin5Comment: k = "overlander:checkin5comment"; break;
  case osm::Props::OverlanderCheckin4Id: k = "overlander:checkin4id"; break;
  case osm::Props::OverlanderCheckin4Time: k = "overlander:checkin4time"; break;
  case osm::Props::OverlanderCheckin4Author: k = "overlander:checkin4author"; break;
  case osm::Props::OverlanderCheckin4Comment: k = "overlander:checkin4comment"; break;
  case osm::Props::OverlanderCheckin3Id: k = "overlander:checkin3id"; break;
  case osm::Props::OverlanderCheckin3Time: k = "overlander:checkin3time"; break;
  case osm::Props::OverlanderCheckin3Author: k = "overlander:checkin3author"; break;
  case osm::Props::OverlanderCheckin3Comment: k = "overlander:checkin3comment"; break;
  case osm::Props::OverlanderCheckin2Id: k = "overlander:checkin2id"; break;
  case osm::Props::OverlanderCheckin2Time: k = "overlander:checkin2time"; break;
  case osm::Props::OverlanderCheckin2Author: k = "overlander:checkin2author"; break;
  case osm::Props::OverlanderCheckin2Comment: k = "overlander:checkin2comment"; break;
  case osm::Props::OverlanderCheckin1Id: k = "overlander:checkin1id"; break;
  case osm::Props::OverlanderCheckin1Time: k = "overlander:checkin1time"; break;
  case osm::Props::OverlanderCheckin1Author: k = "overlander:checkin1author"; break;
  }
  return k;
}

void MapObject::SetFromFeatureType(FeatureType & ft)
{
  m_mercator = feature::GetCenter(ft);
  m_name = ft.GetNames();

  Classificator const & cl = classif();
  m_types = feature::TypesHolder(ft);
  m_types.RemoveIf([&cl](uint32_t t)
  {
    return !cl.IsTypeValid(t);
  });
  // Actually, we can't select object on map with invalid (non-drawable type).
  ASSERT(!m_types.Empty(), ());

  m_metadata = ft.GetMetadata();
  m_houseNumber = ft.GetHouseNumber();
  m_roadNumber = ft.GetRoadNumber();
  m_featureID = ft.GetID();
  m_geomType = ft.GetGeomType();
  if (m_geomType == feature::GeomType::Area)
  {
    m_triangles = ft.GetTrianglesAsPoints(FeatureType::BEST_GEOMETRY);
  }
  else if (m_geomType == feature::GeomType::Line)
  {
    ft.ParseGeometry(FeatureType::BEST_GEOMETRY);
    m_points.reserve(ft.GetPointsCount());
    ft.ForEachPoint([this](m2::PointD const & p) { m_points.push_back(p); },
                    FeatureType::BEST_GEOMETRY);
  }

  SetInetIfNeeded(ft, m_metadata);
}

FeatureID const & MapObject::GetID() const { return m_featureID; }
ms::LatLon MapObject::GetLatLon() const { return mercator::ToLatLon(m_mercator); }
m2::PointD const & MapObject::GetMercator() const { return m_mercator; }
vector<m2::PointD> const & MapObject::GetTriangesAsPoints() const { return m_triangles; }
vector<m2::PointD> const & MapObject::GetPoints() const { return m_points; }
feature::TypesHolder const & MapObject::GetTypes() const { return m_types; }

string MapObject::GetDefaultName() const
{
  string name;
  UNUSED_VALUE(m_name.GetString(StringUtf8Multilang::kDefaultCode, name));
  return name;
}

StringUtf8Multilang const & MapObject::GetNameMultilang() const
{
  return m_name;
}

string const & MapObject::GetHouseNumber() const { return m_houseNumber; }

string MapObject::GetLocalizedType() const
{
  ASSERT(!m_types.Empty(), ());
  feature::TypesHolder copy(m_types);
  copy.SortBySpec();

  return platform::GetLocalizedTypeName(classif().GetReadableObjectName(*copy.begin()));
}

vector<osm::Props> MapObject::AvailableProperties() const
{
  auto props = MetadataToProps(m_metadata.GetPresentTypes());

  auto const & isCuisine = ftypes::IsCuisineChecker::Instance();
  if (any_of(m_types.begin(), m_types.end(), [&](auto const t) { return isCuisine(t); }))
  {
    props.push_back(Props::Cuisine);
    base::SortUnique(props);
  }

  return props;
}

string MapObject::GetPhone() const { return m_metadata.Get(feature::Metadata::FMD_PHONE_NUMBER); }
string MapObject::GetFax() const { return m_metadata.Get(feature::Metadata::FMD_FAX_NUMBER); }
string MapObject::GetEmail() const { return m_metadata.Get(feature::Metadata::FMD_EMAIL); }

string MapObject::GetWebsite() const
{
  string website = m_metadata.Get(feature::Metadata::FMD_WEBSITE);
  if (website.empty())
    website = m_metadata.Get(feature::Metadata::FMD_URL);
  return website;
}

string MapObject::GetFacebookPage() const
{
  return m_metadata.Get(feature::Metadata::FMD_CONTACT_FACEBOOK);
}

string MapObject::GetInstagramPage() const
{
  return m_metadata.Get(feature::Metadata::FMD_CONTACT_INSTAGRAM);
}

string MapObject::GetTwitterPage() const
{
  return m_metadata.Get(feature::Metadata::FMD_CONTACT_TWITTER);
}

string MapObject::GetVkPage() const
{
  return m_metadata.Get(feature::Metadata::FMD_CONTACT_VK);
}

string MapObject::GetLinePage() const
{
  return m_metadata.Get(feature::Metadata::FMD_CONTACT_LINE);
}

Internet MapObject::GetInternet() const
{
  string inet = m_metadata.Get(feature::Metadata::FMD_INTERNET);
  strings::AsciiToLower(inet);
  // Most popular case.
  if (inet.empty())
    return Internet::Unknown;
  if (inet.find(kWlan) != string::npos)
    return Internet::Wlan;
  if (inet.find(kWired) != string::npos)
    return Internet::Wired;
  if (inet == kYes)
    return Internet::Yes;
  if (inet == kNo)
    return Internet::No;
  return Internet::Unknown;
}

vector<string> MapObject::GetCuisines() const { return feature::GetCuisines(m_types); }

vector<string> MapObject::GetLocalizedCuisines() const
{
  return feature::GetLocalizedCuisines(m_types);
}

vector<string> MapObject::GetRecyclingTypes() const { return feature::GetRecyclingTypes(m_types); }

vector<string> MapObject::GetLocalizedRecyclingTypes() const
{
  return feature::GetLocalizedRecyclingTypes(m_types);
}

string MapObject::FormatCuisines() const
{
  return strings::JoinStrings(GetLocalizedCuisines(), kFieldsSeparator);
}

vector<string> MapObject::GetRoadShields() const
{
  return feature::GetRoadShieldsNames(m_roadNumber);
}

string MapObject::FormatRoadShields() const
{
  return strings::JoinStrings(GetRoadShields(), kFieldsSeparator);
}

string MapObject::GetOpeningHours() const
{
  return m_metadata.Get(feature::Metadata::FMD_OPEN_HOURS);
}

string MapObject::GetOperator() const { return m_metadata.Get(feature::Metadata::FMD_OPERATOR); }

int MapObject::GetStars() const
{
  // Most popular case.
  if (m_metadata.Has(feature::Metadata::FMD_STARS))
  {
    int count;
    if (strings::to_int(m_metadata.Get(feature::Metadata::FMD_STARS), count))
      return count;
  }
  return 0;
}

string MapObject::GetElevationFormatted() const
{
  if (m_metadata.Has(feature::Metadata::FMD_ELE))
  {
    double value;
    if (strings::to_double(m_metadata.Get(feature::Metadata::FMD_ELE), value))
      return measurement_utils::FormatAltitude(value);
    else
      LOG(LWARNING,
          ("Invalid metadata for elevation:", m_metadata.Get(feature::Metadata::FMD_ELE)));
  }
  return {};
}

bool MapObject::GetElevation(double & outElevationInMeters) const
{
  return strings::to_double(m_metadata.Get(feature::Metadata::FMD_ELE), outElevationInMeters);
}

string MapObject::GetWikipediaLink() const { return m_metadata.GetWikiURL(); }

string MapObject::GetFlats() const { return m_metadata.Get(feature::Metadata::FMD_FLATS); }

string MapObject::GetLevel() const { return m_metadata.Get(feature::Metadata::FMD_LEVEL); }

string MapObject::GetBuildingLevels() const
{
  return m_metadata.Get(feature::Metadata::FMD_BUILDING_LEVELS);
}

ftraits::WheelchairAvailability MapObject::GetWheelchairType() const
{
  auto const opt = ftraits::Wheelchair::GetValue(m_types);
  return opt ? *opt : ftraits::WheelchairAvailability::No;
}

string MapObject::GetAirportIata() const
{
  if (m_metadata.Has(feature::Metadata::FMD_AIRPORT_IATA))
    return m_metadata.Get(feature::Metadata::FMD_AIRPORT_IATA);
  return {};
}

feature::Metadata const & MapObject::GetMetadata() const { return m_metadata; }
bool MapObject::IsPointType() const { return m_geomType == feature::GeomType::Point; }
bool MapObject::IsBuilding() const { return ftypes::IsBuildingChecker::Instance()(m_types); }

string MapObject::GetOverlanderLocation() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_LOCATION);
}

string MapObject::GetOverlanderDescription() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_DESCRIPTION);
}

string MapObject::GetOverlanderOpen() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_OPEN);
}

string MapObject::GetOverlanderElectricity() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_ELECTRICITY);
}

string MapObject::GetOverlanderWifi() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_WIFI);
}

string MapObject::GetOverlanderKitchen() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_KITCHEN);
}

string MapObject::GetOverlanderParking() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_PARKING);
}

string MapObject::GetOverlanderRestaurant() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_RESTAURANT);
}

string MapObject::GetOverlanderShowers() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_SHOWERS);
}

string MapObject::GetOverlanderWater() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_WATER);
}

string MapObject::GetOverlanderToilets() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_TOILETS);
}

string MapObject::GetOverlanderBigrigfriendly() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_BIGRIGFRIENDLY);
}

string MapObject::GetOverlanderTentfriendly() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_TENTFRIENDLY);
}

string MapObject::GetOverlanderPetfriendly() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_PETFRIENDLY);
}

string MapObject::GetOverlanderSanidump() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_SANIDUMP);
}

string MapObject::GetOverlanderBiofuel() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_BIOFUEL);
} 
string MapObject::GetOverlanderRepairsvehicles() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_REPAIRSVEHICLES);
}

string MapObject::GetOverlanderRepairsmotorcycles() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_REPAIRSMOTORCYCLES);
}

string MapObject::GetOverlanderRepairsbicycles() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_REPAIRSBICYCLES);
}

string MapObject::GetOverlanderSellsparts() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_SELLSPARTS);
}

string MapObject::GetOverlanderRecyclesbatteries() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_RECYCLESBATTERIES);
}

string MapObject::GetOverlanderRecyclesoil() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_RECYCLESOIL);
}

string MapObject::GetOverlanderCheckin5id() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_CHECKIN5ID);
}

string MapObject::GetOverlanderCheckin5time() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_CHECKIN5TIME);
}

string MapObject::GetOverlanderCheckin5author() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_CHECKIN5AUTHOR);
}

string MapObject::GetOverlanderCheckin5comment() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_CHECKIN5COMMENT);
}

string MapObject::GetOverlanderCheckin4id() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_CHECKIN4ID);
}

string MapObject::GetOverlanderCheckin4time() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_CHECKIN4TIME);
}

string MapObject::GetOverlanderCheckin4author() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_CHECKIN4AUTHOR);
}

string MapObject::GetOverlanderCheckin4comment() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_CHECKIN4COMMENT);
}

string MapObject::GetOverlanderCheckin3id() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_CHECKIN3ID);
}

string MapObject::GetOverlanderCheckin3time() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_CHECKIN3TIME);
}

string MapObject::GetOverlanderCheckin3author() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_CHECKIN3AUTHOR);
}

string MapObject::GetOverlanderCheckin3comment() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_CHECKIN3COMMENT);
}

string MapObject::GetOverlanderCheckin2id() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_CHECKIN2ID);
}

string MapObject::GetOverlanderCheckin2time() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_CHECKIN2TIME);
}

string MapObject::GetOverlanderCheckin2author() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_CHECKIN2AUTHOR);
}

string MapObject::GetOverlanderCheckin2comment() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_CHECKIN2COMMENT);
}

string MapObject::GetOverlanderCheckin1id() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_CHECKIN1ID);
}

string MapObject::GetOverlanderCheckin1time() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_CHECKIN1TIME);
}

string MapObject::GetOverlanderCheckin1author() const
{
  return m_metadata.Get(feature::Metadata::FMD_OVERLANDER_CHECKIN1AUTHOR);
}

}  // namespace osm
