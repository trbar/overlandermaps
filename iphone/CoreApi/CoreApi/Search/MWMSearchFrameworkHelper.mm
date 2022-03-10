#import "MWMSearchFrameworkHelper.h"

#include "platform/preferred_languages.hpp"

#include "Framework.h"

@implementation MWMSearchFrameworkHelper

- (NSArray<NSString *> *)searchCategories
{
  NSMutableArray * result = [NSMutableArray array];
  auto const & categories = GetFramework().GetDisplayedCategories().GetKeys();
  for (auto const & item : categories)
  {
    [result addObject:@(item.c_str())];
  }
  return [result copy];
}

- (NSArray<NSString *> *)searchPlacesCategories
{
  NSMutableArray * result = [NSMutableArray array];
  auto const & places = GetFramework().GetDisplayedPlacesCategories().GetKeys();
  for (auto const & item : places)
  {
    [result addObject:@(item.c_str())];
  }
  return [result copy];
}

- (NSArray<NSString *> *)searchActivitiesCategories
{
  NSMutableArray * result = [NSMutableArray array];
  auto const & activities = GetFramework().GetDisplayedActivitiesCategories().GetKeys();
  for (auto const & item : activities)
  {
    [result addObject:@(item.c_str())];
  }
  return [result copy];
}

- (BOOL)isSearchHistoryEmpty
{
  return GetFramework().GetSearchAPI().GetLastSearchQueries().empty();
}

- (NSArray<NSString *> *)lastSearchQueries
{
  NSMutableArray * result = [NSMutableArray array];
  auto const & queries = GetFramework().GetSearchAPI().GetLastSearchQueries();
  for (auto const & item : queries)
  {
    [result addObject:@(item.second.c_str())];
  }
  return [result copy];
}

- (void)clearSearchHistory
{
  GetFramework().GetSearchAPI().ClearSearchHistory();
}

@end
