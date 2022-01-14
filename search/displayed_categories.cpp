#include "search/displayed_categories.hpp"

#include "base/macros.hpp"

#include <algorithm>

namespace search
{
DisplayedCategories::DisplayedCategories(CategoriesHolder const & holder) : m_holder(holder)
{
  m_keys = {"freedomcamping",
            "paidcamping",
            "laundry",
            "showers",
            "water",
            "toilet",
            "sanidump",
            "propane",
            "mechanic",
            "tourism",
            "parking",
            "wifi",
            "fuel",
            "warning",
            "police",
            "hotel",
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
            "post"};
}

void DisplayedCategories::Modify(CategoriesModifier & modifier)
{
  modifier.Modify(m_keys);
}

std::vector<std::string> const & DisplayedCategories::GetKeys() const { return m_keys; }
}  // namespace search
