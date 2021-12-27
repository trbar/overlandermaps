#include "platform/country_file.hpp"

#include "platform/mwm_version.hpp"

#include "base/assert.hpp"

#include <sstream>

#include "defines.hpp"

using namespace std;

namespace platform
{
CountryFile::CountryFile() : m_mapSize(0) {}

CountryFile::CountryFile(std::string name)
: m_name(std::move(name)), m_mapSize(0)
{
}

CountryFile::CountryFile(std::string name, MwmSize size, std::string sha1)
: m_name(std::move(name)), m_mapSize(size), m_sha1(sha1)
{
}

string CountryFile::GetFileName(MapFileType type) const
{
  ASSERT(!m_name.empty(), ());

  switch (type)
  {
  case MapFileType::Map: return m_name + DATA_FILE_EXTENSION;
  case MapFileType::Diff: return m_name + DIFF_FILE_EXTENSION;
  case MapFileType::Count: CHECK(false, (m_name));
  }

  UNREACHABLE();
}

string DebugPrint(CountryFile const & file)
{
  ostringstream os;
  os << "CountryFile [" << file.m_name << "]";
  return os.str();
}
}  // namespace platform
