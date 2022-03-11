package com.mapswithme.maps.search;

import androidx.annotation.NonNull;

class DisplayedActivitiesCategories
{
  @NonNull
  public static String[] getKeys()
  {
    return nativeGetKeys();
  }

  @NonNull
  private static native String[] nativeGetKeys();
}