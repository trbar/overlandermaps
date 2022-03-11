package com.mapswithme.maps.search;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.mapswithme.maps.R;
import com.mapswithme.util.ThemeUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

class PlacesCategoriesAdapter extends RecyclerView.Adapter<PlacesCategoriesAdapter.ViewHolder>
{
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({ TYPE_CATEGORY })
  @interface ViewType {}
  private static final int TYPE_CATEGORY = 0;

  @StringRes
  private int[] mPlacesCategoryResIds;
  @DrawableRes
  private int[] mPlacesIconResIds;


  private final LayoutInflater mInflater;
  private final Resources mResources;

  interface CategoriesUiListener
  {
    void onSearchCategorySelected(@Nullable String category);
  }

  private CategoriesUiListener mListener;

  PlacesCategoriesAdapter(@NonNull Fragment fragment)
  {
    if (fragment instanceof CategoriesUiListener)
      mListener = (CategoriesUiListener) fragment;
    mResources = fragment.getResources();
    mInflater = LayoutInflater.from(fragment.getActivity());
  }

  void updatePlacesCategories(@NonNull Fragment fragment)
  {
    final Activity activity = fragment.getActivity();
    final String packageName = activity.getPackageName();

    final String[] keys = getAllPlacesCategories();
    final int numKeys = keys.length;

    mPlacesCategoryResIds = new int[numKeys];
    mPlacesIconResIds = new int[numKeys];
    for (int i = 0; i < numKeys; i++)
    {
      String key = keys[i];
      mPlacesCategoryResIds[i] = getStringResIdByKey(activity.getResources(), packageName, key);

      if (mPlacesCategoryResIds[i] == 0)
        throw new IllegalStateException("Can't get string resource id for category:" + key);

      mPlacesIconResIds[i] = getDrawableResIdByKey(activity.getApplicationContext(), packageName, key);
      if (mPlacesIconResIds[i] == 0)
        throw new IllegalStateException("Can't get icon resource id for category:" + key);
    }
  }

  @NonNull
  private static String[] getAllPlacesCategories()
  {
    String[] searchPlacesCategories = DisplayedPlacesCategories.getKeys();
    int amountSize = searchPlacesCategories.length;
    String[] allPlacesCategories = new String[amountSize];

    for (int i = 0, j = 0; i < amountSize; i++)
    {
      if (allPlacesCategories[i] == null)
      {
        allPlacesCategories[i] = searchPlacesCategories[j];
        j++;
      }
    }

    return allPlacesCategories;
  }


  @StringRes
  private static int getStringResIdByKey(@NonNull Resources resources, @NonNull String packageName,
                                         @NonNull String key)
  {
    return resources.getIdentifier(key, "string", packageName);
  }

  @DrawableRes
  private static int getDrawableResIdByKey(@NonNull Context context,
                                           @NonNull String packageName,
                                           @NonNull String key)
  {
    final boolean isNightTheme = ThemeUtils.isNightTheme(context);
    String iconId = "ic_category_" + key;
    if (isNightTheme)
      iconId = iconId + "_night";
    return context.getResources().getIdentifier(iconId, "drawable", packageName);
  }

  @Override
  @ViewType
  public int getItemViewType(int position)
  {
    return TYPE_CATEGORY;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, @ViewType int viewType)
  {
    View view;
    ViewHolder viewHolder;
    switch (viewType)
    {
      case TYPE_CATEGORY:
        view = mInflater.inflate(R.layout.item_search_category, parent, false);
        viewHolder = new ViewHolder(view, (TextView) view);
        break;
      default:
        throw new AssertionError("Unsupported type detected: " + viewType);
    }

    viewHolder.setupClickListeners();
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position)
  {
    holder.setTextAndIcon(mPlacesCategoryResIds[position], mPlacesIconResIds[position]);
  }

  @Override
  public int getItemCount()
  {
    return mPlacesCategoryResIds.length;
  }

  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
  {
    @NonNull
    private final TextView mTitle;
    @NonNull
    private final View mView;

    ViewHolder(@NonNull View v, @NonNull TextView tv)
    {
      super(v);
      mView = v;
      mTitle = tv;
    }

    void setupClickListeners()
    {
      mView.setOnClickListener(this);
    }

    @Override
    public final void onClick(View v)
    {
      final int position = getAdapterPosition();
      onItemClicked(position);
    }

    void onItemClicked(int position)
    {
      String categoryEntryName = mResources.getResourceEntryName(mPlacesCategoryResIds[position]);
      if (mListener != null)
      {
        @StringRes
        int categoryId = mPlacesCategoryResIds[position];
        mListener.onSearchCategorySelected(mResources.getString(categoryId) + " ");
      }
    }

    void setTextAndIcon(@StringRes int textResId, @DrawableRes int iconResId)
    {
      mTitle.setText(textResId);
      mTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(iconResId, 0, 0, 0);
    }

    @NonNull
    TextView getTitle()
    {
      return mTitle;
    }

    @NonNull
    View getView()
    {
      return mView;
    }
  }
}