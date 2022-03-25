#include "search/displayed_categories.hpp"

#include "base/macros.hpp"

#include <algorithm>

namespace search
{
DisplayedCategories::DisplayedCategories(CategoriesHolder const & holder) : m_holder(holder)
{
  m_keys = {"laundry",
            "showers",
            "water",
            "toilet",
            "sanidump",
            "propane",
            "mechanic",
            "parking",
            "wifi",
            "fuel",
            "lpg",
            "pets",
            "park",
            "garbage",
            "carwash",
            "picnic",
            "warning",
            "police",
            "eat",
            "food",
            "transport",
            "shopping",
            "atm",
            "nightlife",
            "children",
            "bank",
            "entertainment",
            "hospital",
            "pharmacy",
            "post",
            "swimmingpool"};
}

void DisplayedCategories::Modify(CategoriesModifier & modifier)
{
  modifier.Modify(m_keys);
}

std::vector<std::string> const & DisplayedCategories::GetKeys() const { return m_keys; }

DisplayedPlacesCategories::DisplayedPlacesCategories(CategoriesHolder const & holder) : m_holder(holder)
{
  m_keys = {"wildfreedomcamping",
            "freedomcamping",
            "paidcamping",
            "homestaycamping",
            "wintercamping",
            "restarea",
            "parking",
            "hotel"};
}

void DisplayedPlacesCategories::Modify(CategoriesModifier & modifier)
{
  modifier.Modify(m_keys);
}

std::vector<std::string> const & DisplayedPlacesCategories::GetKeys() const { return m_keys; }

DisplayedActivitiesCategories::DisplayedActivitiesCategories(CategoriesHolder const & holder) : m_holder(holder)
{
  m_keys = {"tourism",
            "motorcycle",
            "surfing",
            "mountainbike",
            "hiking",
            "fishing",
            "beachfishing",
            "kayak",
            "climbing",
            "swimming"};
}

void DisplayedActivitiesCategories::Modify(CategoriesModifier & modifier)
{
  modifier.Modify(m_keys);
}

std::vector<std::string> const & DisplayedActivitiesCategories::GetKeys() const { return m_keys; }
}  // namespace search
