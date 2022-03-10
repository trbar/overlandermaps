#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface MWMSearchFrameworkHelper : NSObject

- (NSArray<NSString *> *)searchCategories;
- (NSArray<NSString *> *)searchPlacesCategories;
- (NSArray<NSString *> *)searchActivitiesCategories;

- (BOOL)isSearchHistoryEmpty;
- (NSArray<NSString *> *)lastSearchQueries;
- (void)clearSearchHistory;

@end

NS_ASSUME_NONNULL_END
