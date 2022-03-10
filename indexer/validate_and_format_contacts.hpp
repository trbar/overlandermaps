#pragma once

#include <string>

namespace osm {
  std::string ValidateAndFormat_facebook(std::string const & v);
  std::string ValidateAndFormat_instagram(std::string const & v);
  std::string ValidateAndFormat_twitter(std::string const & v);
  std::string ValidateAndFormat_vk(std::string const & v);
  std::string ValidateAndFormat_contactLine(std::string const & v);
  std::string ValidateAndFormat_overlander(std::string const & v);

  bool ValidateFacebookPage(std::string const & v);
  bool ValidateInstagramPage(std::string const & v);
  bool ValidateTwitterPage(std::string const & v);
  bool ValidateVkPage(std::string const & v);
  bool ValidateLinePage(std::string const & v);
}
