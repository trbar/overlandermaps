package com.mapswithme.maps.search;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mapswithme.maps.R;
import com.mapswithme.maps.base.BaseMwmRecyclerFragment;

public class SearchPlacesCategoriesFragment extends BaseMwmRecyclerFragment<PlacesCategoriesAdapter>
    implements PlacesCategoriesAdapter.CategoriesUiListener
{
  @Override
  public void onViewCreated(View view, Bundle savedInstanceState)
  {
    super.onViewCreated(view, savedInstanceState);
    getAdapter().updatePlacesCategories(this);
  }

  @NonNull
  @Override
  protected PlacesCategoriesAdapter createAdapter()
  {
    return new PlacesCategoriesAdapter(this);
  }

  @Override
  protected int getLayoutRes()
  {
    return R.layout.fragment_search_categories;
  }


  protected void safeOnActivityCreated(@Nullable Bundle savedInstanceState)
  {
    ((SearchFragment) getParentFragment()).setRecyclerScrollListener(getRecyclerView());
  }

  @Override
  public void onSearchCategorySelected(String category)
  {
    if (!passCategory(getParentFragment(), category))
      passCategory(getActivity(), category);
  }

  private static boolean passCategory(Object listener, String category)
  {
    if (!(listener instanceof PlacesCategoriesAdapter.CategoriesUiListener))
      return false;

    ((PlacesCategoriesAdapter.CategoriesUiListener)listener).onSearchCategorySelected(category);
    return true;
  }
}
