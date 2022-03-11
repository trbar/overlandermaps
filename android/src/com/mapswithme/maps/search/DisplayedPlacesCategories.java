package com.mapswithme.maps.search;

import androidx.annotation.NonNull;

class DisplayedPlacesCategories
{
  @NonNull
  public static String[] getKeys()
  {
    return nativeGetKeys();
  }

  @NonNull
  private static native String[] nativeGetKeys();
}