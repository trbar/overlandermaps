#include "editor/editor_config.hpp"

#include "base/stl_helpers.hpp"

#include <algorithm>
#include <cstring>
#include <unordered_map>

using namespace std;

namespace
{
using EType = feature::Metadata::EType;

// TODO(mgsergio): It would be nice to have this map generated from editor.config.
static unordered_map<string, EType> const kNamesToFMD = {
    {"opening_hours", feature::Metadata::FMD_OPEN_HOURS},
    {"phone", feature::Metadata::FMD_PHONE_NUMBER},
    {"fax", feature::Metadata::FMD_FAX_NUMBER},
    {"stars", feature::Metadata::FMD_STARS},
    {"operator", feature::Metadata::FMD_OPERATOR},
    // {"", feature::Metadata::FMD_URL},
    {"website", feature::Metadata::FMD_WEBSITE},
    {"contact_facebook", feature::Metadata::FMD_CONTACT_FACEBOOK},
    {"contact_instagram", feature::Metadata::FMD_CONTACT_INSTAGRAM},
    {"contact_twitter", feature::Metadata::FMD_CONTACT_TWITTER},
    {"contact_vk", feature::Metadata::FMD_CONTACT_VK},
    {"contact_line", feature::Metadata::FMD_CONTACT_LINE},
    {"internet", feature::Metadata::FMD_INTERNET},
    {"ele", feature::Metadata::FMD_ELE},
    // {"", feature::Metadata::FMD_TURN_LANES},
    // {"", feature::Metadata::FMD_TURN_LANES_FORWARD},
    // {"", feature::Metadata::FMD_TURN_LANES_BACKWARD},
    {"email", feature::Metadata::FMD_EMAIL},
    {"postcode", feature::Metadata::FMD_POSTCODE},
    {"wikipedia", feature::Metadata::FMD_WIKIPEDIA},
    {"overlander_location", feature::Metadata::FMD_OVERLANDER_LOCATION},
    {"overlander_description", feature::Metadata::FMD_OVERLANDER_DESCRIPTION},
    {"overlander_open", feature::Metadata::FMD_OVERLANDER_OPEN},
    {"overlander_electricity", feature::Metadata::FMD_OVERLANDER_ELECTRICITY},
    {"overlander_wifi", feature::Metadata::FMD_OVERLANDER_WIFI},
    {"overlander_kitchen", feature::Metadata::FMD_OVERLANDER_KITCHEN},
    {"overlander_parking", feature::Metadata::FMD_OVERLANDER_PARKING},
    {"overlander_restaurant", feature::Metadata::FMD_OVERLANDER_RESTAURANT},
    {"overlander_showers", feature::Metadata::FMD_OVERLANDER_SHOWERS},
    {"overlander_water", feature::Metadata::FMD_OVERLANDER_WATER},
    {"overlander_toilets", feature::Metadata::FMD_OVERLANDER_TOILETS},
    {"overlander_bigrigfriendly", feature::Metadata::FMD_OVERLANDER_BIGRIGFRIENDLY},
    {"overlander_tentfriendly", feature::Metadata::FMD_OVERLANDER_TENTFRIENDLY},
    {"overlander_petfriendly", feature::Metadata::FMD_OVERLANDER_PETFRIENDLY},
    {"overlander_sanidump", feature::Metadata::FMD_OVERLANDER_SANIDUMP},
    {"overlander_biofuel", feature::Metadata::FMD_OVERLANDER_BIOFUEL},
    {"overlander_repairsvehicles", feature::Metadata::FMD_OVERLANDER_REPAIRSVEHICLES},
    {"overlander_repairsmotorcycles", feature::Metadata::FMD_OVERLANDER_REPAIRSMOTORCYCLES},
    {"overlander_repairsbicycles", feature::Metadata::FMD_OVERLANDER_REPAIRSBICYCLES},
    {"overlander_sellsparts", feature::Metadata::FMD_OVERLANDER_SELLSPARTS},
    {"overlander_recyclesbatteries", feature::Metadata::FMD_OVERLANDER_RECYCLESBATTERIES},
    {"overlander_recyclesoil", feature::Metadata::FMD_OVERLANDER_RECYCLESOIL},
    {"overlander_checkin5id", feature::Metadata::FMD_OVERLANDER_CHECKIN5ID},
    {"overlander_checkin5time", feature::Metadata::FMD_OVERLANDER_CHECKIN5TIME},
    {"overlander_checkin5author", feature::Metadata::FMD_OVERLANDER_CHECKIN5AUTHOR},
    {"overlander_checkin5comment", feature::Metadata::FMD_OVERLANDER_CHECKIN5COMMENT},
    {"overlander_checkin4id", feature::Metadata::FMD_OVERLANDER_CHECKIN4ID},
    {"overlander_checkin4time", feature::Metadata::FMD_OVERLANDER_CHECKIN4TIME},
    {"overlander_checkin4author", feature::Metadata::FMD_OVERLANDER_CHECKIN4AUTHOR},
    {"overlander_checkin4comment", feature::Metadata::FMD_OVERLANDER_CHECKIN4COMMENT},
    {"overlander_checkin3id", feature::Metadata::FMD_OVERLANDER_CHECKIN3ID},
    {"overlander_checkin3time", feature::Metadata::FMD_OVERLANDER_CHECKIN3TIME},
    {"overlander_checkin3author", feature::Metadata::FMD_OVERLANDER_CHECKIN3AUTHOR},
    {"overlander_checkin3comment", feature::Metadata::FMD_OVERLANDER_CHECKIN3COMMENT},
    {"overlander_checkin2id", feature::Metadata::FMD_OVERLANDER_CHECKIN2ID},
    {"overlander_checkin2time", feature::Metadata::FMD_OVERLANDER_CHECKIN2TIME},
    {"overlander_checkin2author", feature::Metadata::FMD_OVERLANDER_CHECKIN2AUTHOR},
    {"overlander_checkin2comment", feature::Metadata::FMD_OVERLANDER_CHECKIN2COMMENT},
    {"overlander_checkin1id", feature::Metadata::FMD_OVERLANDER_CHECKIN1ID},
    {"overlander_checkin1time", feature::Metadata::FMD_OVERLANDER_CHECKIN1TIME},
    {"overlander_checkin1author", feature::Metadata::FMD_OVERLANDER_CHECKIN1AUTHOR},
    // {"", feature::Metadata::FMD_MAXSPEED},
    {"flats", feature::Metadata::FMD_FLATS},
    {"height", feature::Metadata::FMD_HEIGHT},
    // {"", feature::Metadata::FMD_MIN_HEIGHT},
    {"denomination", feature::Metadata::FMD_DENOMINATION},
    {"building:levels", feature::Metadata::FMD_BUILDING_LEVELS},
    {"level", feature::Metadata::FMD_LEVEL}
    // description
};

unordered_map<string, int> const kPriorityWeights = {{"high", 0}, {"", 1}, {"low", 2}};

bool TypeDescriptionFromXml(pugi::xml_node const & root, pugi::xml_node const & node,
                            editor::TypeAggregatedDescription & outDesc)
{
  if (!node || strcmp(node.attribute("editable").value(), "no") == 0)
    return false;

  auto const handleField = [&outDesc](string const & fieldName) {
    if (fieldName == "name")
    {
      outDesc.m_name = true;
      return;
    }

    if (fieldName == "street" || fieldName == "housenumber" || fieldName == "housename" ||
        fieldName == "postcode")
    {
      outDesc.m_address = true;
      return;
    }

    if (fieldName == "cuisine")
    {
      outDesc.m_cuisine = true;
      return;
    }

    // TODO(mgsergio): Add support for non-metadata fields like atm, wheelchair, toilet etc.
    auto const it = kNamesToFMD.find(fieldName);

    ASSERT(it != end(kNamesToFMD), ("Wrong field:", fieldName));
    outDesc.m_editableFields.push_back(it->second);
  };

  for (auto const & xNode : node.select_nodes("include[@group]"))
  {
    auto const node = xNode.node();
    string const groupName = node.attribute("group").value();

    string const xpath = "/mapsme/editor/fields/field_group[@name='" + groupName + "']";
    auto const group = root.select_node(xpath.data()).node();
    ASSERT(group, ("No such group", groupName));

    for (auto const & fieldRefXName : group.select_nodes("field_ref/@name"))
    {
      auto const fieldName = fieldRefXName.attribute().value();
      handleField(fieldName);
    }
  }

  for (auto const & xNode : node.select_nodes("include[@field]"))
  {
    auto const node = xNode.node();
    string const fieldName = node.attribute("field").value();
    handleField(fieldName);
  }

  base::SortUnique(outDesc.m_editableFields);
  return true;
}

/// The priority is defined by elems order, except elements with priority="high".
vector<pugi::xml_node> GetPrioritizedTypes(pugi::xml_node const & node)
{
  vector<pugi::xml_node> result;
  for (auto const & xNode : node.select_nodes("/mapsme/editor/types/type[@id]"))
    result.push_back(xNode.node());
  stable_sort(begin(result), end(result),
              [](pugi::xml_node const & lhs, pugi::xml_node const & rhs) {
                auto const lhsWeight = kPriorityWeights.find(lhs.attribute("priority").value());
                auto const rhsWeight = kPriorityWeights.find(rhs.attribute("priority").value());

                CHECK(lhsWeight != kPriorityWeights.end(), (""));
                CHECK(rhsWeight != kPriorityWeights.end(), (""));

                return lhsWeight->second < rhsWeight->second;
              });
  return result;
}
}  // namespace

namespace editor
{
bool EditorConfig::GetTypeDescription(vector<string> classificatorTypes,
                                      TypeAggregatedDescription & outDesc) const
{
  bool isBuilding = false;
  vector<string> addTypes;
  for (auto it = classificatorTypes.begin(); it != classificatorTypes.end(); ++it)
  {
    if (*it == "building")
    {
      outDesc.m_address = isBuilding = true;
      outDesc.m_editableFields.push_back(feature::Metadata::FMD_BUILDING_LEVELS);
      outDesc.m_editableFields.push_back(feature::Metadata::FMD_POSTCODE);
      classificatorTypes.erase(it);
      break;
    }
    // Adding partial types for 2..N-1 parts of a N-part type.
    auto hyphenPos = it->find('-');
    while ((hyphenPos = it->find('-', hyphenPos + 1)) != string::npos)
    {
      addTypes.push_back(it->substr(0, hyphenPos));
    }
  }
  classificatorTypes.insert(classificatorTypes.end(), addTypes.begin(), addTypes.end());

  auto const typeNodes = GetPrioritizedTypes(m_document);
  auto const it =
      find_if(begin(typeNodes), end(typeNodes), [&classificatorTypes](pugi::xml_node const & node) {
        return find(begin(classificatorTypes), end(classificatorTypes),
                    node.attribute("id").value()) != end(classificatorTypes);
      });
  if (it == end(typeNodes))
    return isBuilding;

  return TypeDescriptionFromXml(m_document, *it, outDesc);
}

vector<string> EditorConfig::GetTypesThatCanBeAdded() const
{
  auto const xpathResult =
      m_document.select_nodes("/mapsme/editor/types/type[not(@can_add='no' or @editable='no')]");

  vector<string> result;
  for (auto const & xNode : xpathResult)
    result.emplace_back(xNode.node().attribute("id").value());
  return result;
}

void EditorConfig::SetConfig(pugi::xml_document const & doc) { m_document.reset(doc); }
}  // namespace editor
