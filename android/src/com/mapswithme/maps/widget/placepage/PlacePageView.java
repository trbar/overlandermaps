package com.mapswithme.maps.widget.placepage;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.location.Location;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollViewClickFixed;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.mapswithme.maps.Framework;
import com.mapswithme.maps.MwmActivity;
import com.mapswithme.maps.MwmApplication;
import com.mapswithme.maps.R;
import com.mapswithme.maps.api.ParsedMwmRequest;
import com.mapswithme.maps.bookmarks.data.Bookmark;
import com.mapswithme.maps.bookmarks.data.BookmarkManager;
import com.mapswithme.maps.bookmarks.data.DistanceAndAzimut;
import com.mapswithme.maps.bookmarks.data.MapObject;
import com.mapswithme.maps.bookmarks.data.Metadata;
import com.mapswithme.maps.bookmarks.data.RoadWarningMarkType;
import com.mapswithme.maps.downloader.CountryItem;
import com.mapswithme.maps.downloader.DownloaderStatusIcon;
import com.mapswithme.maps.downloader.MapManager;
import com.mapswithme.maps.editor.Editor;
import com.mapswithme.maps.editor.OpeningHours;
import com.mapswithme.maps.editor.data.TimeFormatUtils;
import com.mapswithme.maps.editor.data.Timespan;
import com.mapswithme.maps.editor.data.Timetable;
import com.mapswithme.maps.location.LocationHelper;
import com.mapswithme.maps.routing.RoutingController;
import com.mapswithme.maps.search.Popularity;
import com.mapswithme.maps.settings.RoadType;
import com.mapswithme.maps.widget.ArrowView;
import com.mapswithme.util.Graphics;
import com.mapswithme.util.SharingUtils;
import com.mapswithme.util.StringUtils;
import com.mapswithme.util.ThemeUtils;
import com.mapswithme.util.UiUtils;
import com.mapswithme.util.Utils;
import com.mapswithme.util.concurrency.UiThread;
import com.mapswithme.util.log.Logger;
import com.mapswithme.util.log.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PlacePageView extends NestedScrollViewClickFixed
    implements View.OnClickListener,
               View.OnLongClickListener,
               EditBookmarkFragment.EditBookmarkListener

{
  private static final Logger LOGGER = LoggerFactory.INSTANCE.getLogger(LoggerFactory.Type.MISC);
  private static final String TAG = PlacePageView.class.getSimpleName();
  private static final String PREF_COORDINATES_FORMAT = "coordinates_format";
  private static final List<CoordinatesFormat> visibleCoordsFormat =
      Arrays.asList(CoordinatesFormat.LatLonDMS,
                    CoordinatesFormat.LatLonDecimal,
                    CoordinatesFormat.OLCFull,
                    CoordinatesFormat.OSMLink);

  private boolean mIsDocked;
  private boolean mIsFloating;

  // Preview.
  private ViewGroup mPreview;
  private Toolbar mToolbar;
  private TextView mTvTitle;
  private TextView mTvSecondaryTitle;
  private TextView mTvSubtitle;
  private ArrowView mAvDirection;
  private TextView mTvDistance;
  private TextView mTvAddress;

  // Details.
  private ViewGroup mDetails;
  private RecyclerView mPhoneRecycler;
  private PlacePhoneAdapter mPhoneAdapter;
  private View mWebsite;
  private TextView mTvWebsite;
  private TextView mTvLatlon;
  //Social links
  private View mFacebookPage;
  private TextView mTvFacebookPage;
  private View mInstagramPage;
  private TextView mTvInstagramPage;
  private View mTwitterPage;
  private TextView mTvTwitterPage;
  private View mVkPage;
  private TextView mTvVkPage;
  private View mLinePage;
  private TextView mTvLinePage;

  private View mOpeningHours;
  private TextView mTodayLabel;
  private TextView mTodayOpenTime;
  private TextView mTodayNonBusinessTime;
  private RecyclerView mFullWeekOpeningHours;
  private PlaceOpeningHoursAdapter mOpeningHoursAdapter;
  private View mWifi;
  private View mEmail;
  private TextView mTvEmail;
  private View mOperator;
  private TextView mTvOperator;
  private View mCuisine;
  private TextView mTvCuisine;
  private View mWiki;
  private View mEntrance;
  private TextView mTvEntrance;
  private View mEditPlace;
  private View mAddOrganisation;
  private View mAddPlace;
  private View mEditTopSpace;
  // Bookmark
  private View mBookmarkFrame;
  private WebView mWvBookmarkNote;
  private TextView mTvBookmarkNote;
  private boolean mBookmarkSet;
  // Place page buttons
  private PlacePageButtons mButtons;
  private ImageView mBookmarkButtonIcon;

  @Nullable
  private View mBookmarkButtonFrame;

  @SuppressWarnings("NullableProblems")
  @NonNull
  private View mPlaceDescriptionContainer;

  @SuppressWarnings("NotNullFieldNotInitialized")
  @NonNull
  private View mPlaceDescriptionHeaderContainer;

  @SuppressWarnings("NotNullFieldNotInitialized")
  @NonNull
  private View mPlaceDetailsHeaderContainer;

  @SuppressWarnings("NotNullFieldNotInitialized")
  @NonNull
  private View mPlaceOverlanderCheckinsHeaderContainer;

  @SuppressWarnings("NotNullFieldNotInitialized")
  @NonNull
  private View mPlaceOverlanderCheckin1Container;

  @SuppressWarnings("NotNullFieldNotInitialized")
  @NonNull
  private TextView mPlaceOverlanderCheckin1AuthorDate;

  @SuppressWarnings("NotNullFieldNotInitialized")
  @NonNull
  private TextView mPlaceOverlanderCheckin1Comment;

  @SuppressWarnings("NotNullFieldNotInitialized")
  @NonNull
  private View mPlaceOverlanderCheckin2Container;

  @SuppressWarnings("NotNullFieldNotInitialized")
  @NonNull
  private TextView mPlaceOverlanderCheckin2AuthorDate;

  @SuppressWarnings("NotNullFieldNotInitialized")
  @NonNull
  private TextView mPlaceOverlanderCheckin2Comment;

  @SuppressWarnings("NotNullFieldNotInitialized")
  @NonNull
  private View mPlaceOverlanderCheckin3Container;

  @SuppressWarnings("NotNullFieldNotInitialized")
  @NonNull
  private TextView mPlaceOverlanderCheckin3AuthorDate;

  @SuppressWarnings("NotNullFieldNotInitialized")
  @NonNull
  private TextView mPlaceOverlanderCheckin3Comment;

  @SuppressWarnings("NullableProblems")
  @NonNull
  private TextView mPlaceDescriptionView;

  @SuppressWarnings("NullableProblems")
  @NonNull
  private View mPlaceDescriptionMoreBtn;

  @SuppressWarnings("NullableProblems")
  @NonNull
  private View mPopularityView;

  // Data
  @Nullable
  private MapObject mMapObject;
  private CoordinatesFormat mCoordsFormat = CoordinatesFormat.LatLonDecimal;

  // Downloader`s stuff
  private DownloaderStatusIcon mDownloaderIcon;
  private TextView mDownloaderInfo;
  private int mStorageCallbackSlot;
  @Nullable
  private CountryItem mCurrentCountry;
  private boolean mScrollable = true;

  private final MapManager.StorageCallback mStorageCallback = new MapManager.StorageCallback()
  {
    @Override
    public void onStatusChanged(List<MapManager.StorageCallbackData> data)
    {
      if (mCurrentCountry == null)
        return;

      for (MapManager.StorageCallbackData item : data)
        if (mCurrentCountry.id.equals(item.countryId))
        {
          updateDownloader();
          return;
        }
    }

    @Override
    public void onProgress(String countryId, long localSize, long remoteSize)
    {
      if (mCurrentCountry != null && mCurrentCountry.id.equals(countryId))
        updateDownloader();
    }
  };

  private final Runnable mDownloaderDeferredDetachProc = new Runnable()
  {
    @Override
    public void run()
    {
      detachCountry();
    }
  };
  @NonNull
  private final EditBookmarkClickListener mEditBookmarkClickListener = new EditBookmarkClickListener();

  @NonNull
  private final OnClickListener mDownloadClickListener = new OnClickListener()
  {
    @Override
    public void onClick(View v)
    {
      MapManager.warn3gAndDownload(getActivity(), mCurrentCountry.id, null);
    }
  };

  @NonNull
  private final OnClickListener mCancelDownloadListener = new OnClickListener()
  {
    @Override
    public void onClick(View v)
    {
      MapManager.nativeCancel(mCurrentCountry.id);
    }
  };

  @Nullable
  private Closable mClosable;

  @Nullable
  private PlacePageGestureListener mPlacePageGestureListener;

  @Nullable
  private RoutingModeListener mRoutingModeListener;

  void setScrollable(boolean scrollable)
  {
    mScrollable = scrollable;
  }

  void addPlacePageGestureListener(@NonNull PlacePageGestureListener ppGestureListener)
  {
    mPlacePageGestureListener = ppGestureListener;
  }

  void addClosable(@NonNull Closable closable)
  {
    mClosable = closable;
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev)
  {
    switch (ev.getAction())
    {
      case MotionEvent.ACTION_DOWN:
        return mScrollable && super.onTouchEvent(ev);
      default:
        return super.onTouchEvent(ev);
    }
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent event)
  {
    return mScrollable && super.onInterceptTouchEvent(event);
  }

  public interface SetMapObjectListener
  {
    void onSetMapObjectComplete(boolean isSameObject);
  }

  public PlacePageView(Context context)
  {
    this(context, null, 0);
  }

  public PlacePageView(Context context, AttributeSet attrs)
  {
    this(context, attrs, 0);
  }

  public PlacePageView(Context context, AttributeSet attrs, int defStyleAttr)
  {
    super(context, attrs);
    mCoordsFormat = CoordinatesFormat.fromId(MwmApplication.prefs(context).getInt(PREF_COORDINATES_FORMAT, CoordinatesFormat.LatLonDecimal.getId()));
    init(attrs, defStyleAttr);
  }

  @Override
  protected void onFinishInflate()
  {
    super.onFinishInflate();
    mPreview = findViewById(R.id.pp__preview);
    mTvTitle = mPreview.findViewById(R.id.tv__title);
    mTvTitle.setOnLongClickListener(this);
    mTvTitle.setOnClickListener(this);
    mTvSecondaryTitle = mPreview.findViewById(R.id.tv__secondary_title);
    mTvSecondaryTitle.setOnLongClickListener(this);
    mTvSecondaryTitle.setOnClickListener(this);
    mToolbar = findViewById(R.id.toolbar);
    mTvSubtitle = mPreview.findViewById(R.id.tv__subtitle);

    View directionFrame = mPreview.findViewById(R.id.direction_frame);
    mTvDistance = mPreview.findViewById(R.id.tv__straight_distance);
    mAvDirection = mPreview.findViewById(R.id.av__direction);
    directionFrame.setOnClickListener(this);

    mTvAddress = mPreview.findViewById(R.id.tv__address);
    mTvAddress.setOnLongClickListener(this);
    mTvAddress.setOnClickListener(this);

    mDetails = findViewById(R.id.pp__details_frame);
    RelativeLayout address = findViewById(R.id.ll__place_name);
    mPhoneRecycler = findViewById(R.id.rw__phone);
    mPhoneAdapter = new PlacePhoneAdapter();
    mPhoneRecycler.setAdapter(mPhoneAdapter);
    mWebsite = findViewById(R.id.ll__place_website);
    mWebsite.setOnClickListener(this);
    mTvWebsite = findViewById(R.id.tv__place_website);
    //Social links
    mFacebookPage = findViewById(R.id.ll__place_facebook);
    mFacebookPage.setOnClickListener(this);
    mFacebookPage.setOnLongClickListener(this);
    mTvFacebookPage = findViewById(R.id.tv__place_facebook_page);

    mInstagramPage = findViewById(R.id.ll__place_instagram);
    mInstagramPage.setOnClickListener(this);
    mInstagramPage.setOnLongClickListener(this);
    mTvInstagramPage = findViewById(R.id.tv__place_instagram_page);

    mTwitterPage = findViewById(R.id.ll__place_twitter);
    mTwitterPage.setOnClickListener(this);
    mTwitterPage.setOnLongClickListener(this);
    mTvTwitterPage = findViewById(R.id.tv__place_twitter_page);

    mVkPage = findViewById(R.id.ll__place_vk);
    mVkPage.setOnClickListener(this);
    mVkPage.setOnLongClickListener(this);
    mTvVkPage = findViewById(R.id.tv__place_vk_page);

    mLinePage = findViewById(R.id.ll__place_line);
    mLinePage.setOnClickListener(this);
    mLinePage.setOnLongClickListener(this);
    mTvLinePage = findViewById(R.id.tv__place_line_page);
    LinearLayout latlon = findViewById(R.id.ll__place_latlon);
    latlon.setOnClickListener(this);
    mTvLatlon = findViewById(R.id.tv__place_latlon);
    mOpeningHours = findViewById(R.id.ll__place_schedule);
    mTodayLabel = findViewById(R.id.oh_today_label);
    mTodayOpenTime = findViewById(R.id.oh_today_open_time);
    mTodayNonBusinessTime = findViewById(R.id.oh_nonbusiness_time);
    mFullWeekOpeningHours = findViewById(R.id.rw__full_opening_hours);
    mOpeningHoursAdapter = new PlaceOpeningHoursAdapter();
    mFullWeekOpeningHours.setAdapter(mOpeningHoursAdapter);
    mWifi = findViewById(R.id.ll__place_wifi);
    mEmail = findViewById(R.id.ll__place_email);
    mEmail.setOnClickListener(this);
    mTvEmail = findViewById(R.id.tv__place_email);
    mOperator = findViewById(R.id.ll__place_operator);
    mOperator.setOnClickListener(this);
    mTvOperator = findViewById(R.id.tv__place_operator);
    mCuisine = findViewById(R.id.ll__place_cuisine);
    mTvCuisine = findViewById(R.id.tv__place_cuisine);
    mWiki = findViewById(R.id.ll__place_wiki);
    mWiki.setOnClickListener(this);
    mEntrance = findViewById(R.id.ll__place_entrance);
    mTvEntrance = mEntrance.findViewById(R.id.tv__place_entrance);
    mEditPlace = findViewById(R.id.ll__place_editor);
    mEditPlace.setOnClickListener(this);
    mAddOrganisation = findViewById(R.id.ll__add_organisation);
    mAddOrganisation.setOnClickListener(this);
    mAddPlace = findViewById(R.id.ll__place_add);
    mAddPlace.setOnClickListener(this);
    mEditTopSpace = findViewById(R.id.edit_top_space);
    latlon.setOnLongClickListener(this);
    address.setOnLongClickListener(this);
    mWebsite.setOnLongClickListener(this);
    mOpeningHours.setOnLongClickListener(this);
    mEmail.setOnLongClickListener(this);
    mOperator.setOnLongClickListener(this);
    mWiki.setOnLongClickListener(this);

    mBookmarkFrame = findViewById(R.id.bookmark_frame);
    mWvBookmarkNote = mBookmarkFrame.findViewById(R.id.wv__bookmark_notes);
    final WebSettings settings = mWvBookmarkNote.getSettings();
    settings.setJavaScriptEnabled(false);
    settings.setDefaultTextEncodingName("utf-8");
    mTvBookmarkNote = mBookmarkFrame.findViewById(R.id.tv__bookmark_notes);
    mTvBookmarkNote.setOnLongClickListener(this);
    initEditMapObjectBtn();

    mDownloaderIcon = new DownloaderStatusIcon(mPreview.findViewById(R.id.downloader_status_frame));

    mDownloaderInfo = mPreview.findViewById(R.id.tv__downloader_details);

    setElevation(UiUtils.dimen(getContext(), R.dimen.placepage_elevation));

    if (UiUtils.isLandscape(getContext()))
      setBackgroundResource(0);

    initPlaceDescriptionView();
    initPlaceOverlanderCheckinsView();
  }

  public void initButtons(@NonNull ViewGroup buttons)
  {
    mButtons = new PlacePageButtons(this, buttons, new PlacePageButtons.ItemListener()
    {
      public void onPrepareVisibleView(@NonNull PlacePageButtons.PlacePageButton item,
                                       @NonNull View frame, @NonNull ImageView icon,
                                       @NonNull TextView title)
      {
        int color;

        switch (item.getType())
        {
          case BOOKING:
//          case BOOKING_SEARCH:
          case OPENTABLE:
            // ---------------------------------------------------------------------------------------
            // Warning: the following code is autogenerated.
            // Do NOT change it manually.
            // %PartnersExtender.Switch
          case PARTNER1:
          case PARTNER3:
          case PARTNER18:
          case PARTNER19:
          case PARTNER20:
            // /%PartnersExtender.Switch
            // End of autogenerated code.
            // ---------------------------------------------------------------------------------------
            frame.setBackgroundResource(item.getBackgroundResource());
            color = Color.WHITE;
            break;

          case PARTNER2:
            frame.setBackgroundResource(item.getBackgroundResource());
            color = Color.BLACK;
            break;

          case BOOKMARK:
            mBookmarkButtonIcon = icon;
            mBookmarkButtonFrame = frame;
            updateBookmarkButton();
            color = ThemeUtils.getColor(getContext(), R.attr.iconTint);
            break;

          default:
            color = ThemeUtils.getColor(getContext(), R.attr.iconTint);
            icon.setColorFilter(color);
            break;
        }

        title.setTextColor(color);
      }

      @Override
      public void onItemClick(PlacePageButtons.PlacePageButton item)
      {
        switch (item.getType())
        {
          case BOOKMARK:
            onBookmarkBtnClicked();
            break;

          case SHARE:
            onShareBtnClicked();
            break;

          case BACK:
            onBackBtnClicked();
            break;

          case ROUTE_FROM:
            onRouteFromBtnClicked();
            break;

          case ROUTE_TO:
            onRouteToBtnClicked();
            break;

          case ROUTE_ADD:
            onRouteAddBtnClicked();
            break;

          case ROUTE_REMOVE:
            onRouteRemoveBtnClicked();
            break;

          case ROUTE_AVOID_TOLL:
            onAvoidTollBtnClicked();
            break;

          case ROUTE_AVOID_UNPAVED:
            onAvoidUnpavedBtnClicked();
            break;

          case ROUTE_AVOID_FERRY:
            onAvoidFerryBtnClicked();
            break;

          case CALL:
            onCallBtnClicked(buttons);
            break;
        }
      }
    });
  }

  private void onBookmarkBtnClicked()
  {
    if (mMapObject == null)
    {
      LOGGER.e(TAG, "Bookmark cannot be managed, mMapObject is null!");
      return;
    }
    toggleIsBookmark(mMapObject);
  }

  private void onShareBtnClicked()
  {
    if (mMapObject == null)
    {
      LOGGER.e(TAG, "A map object cannot be shared, it's null!");
      return;
    }
    SharingUtils.shareMapObject(getContext(), mMapObject);
  }

  private void onBackBtnClicked()
  {
    if (mMapObject == null)
    {
      LOGGER.e(TAG, "A mwm request cannot be handled, mMapObject is null!");
      getActivity().finish();
      return;
    }

    if (ParsedMwmRequest.hasRequest())
    {
      ParsedMwmRequest request = ParsedMwmRequest.getCurrentRequest();
      if (ParsedMwmRequest.isPickPointMode())
        request.setPointData(mMapObject.getLat(), mMapObject.getLon(), mMapObject.getTitle(), "");

      request.sendResponseAndFinish(getActivity(), true);
    }
    else
      getActivity().finish();
  }

  private void onRouteFromBtnClicked()
  {
    RoutingController controller = RoutingController.get();
    if (!controller.isPlanning())
    {
      controller.prepare(mMapObject, null);
      close();
    }
    else if (controller.setStartPoint(mMapObject))
    {
      close();
    }
  }

  private void onRouteToBtnClicked()
  {
    if (RoutingController.get().isPlanning())
    {
      RoutingController.get().setEndPoint(mMapObject);
      close();
    }
    else
    {
      getActivity().startLocationToPoint(getMapObject(), true);
    }
  }

  private void onRouteAddBtnClicked()
  {
    if (mMapObject != null)
      RoutingController.get().addStop(mMapObject);
  }

  private void onRouteRemoveBtnClicked()
  {
    if (mMapObject != null)
      RoutingController.get().removeStop(mMapObject);
  }

  private List<String> getAllPhones()
  {
    return mPhoneAdapter.getPhonesList();
  }

  private void onCallBtnClicked(View parentView)
  {
    final List<String> phones = getAllPhones();
    if (phones.size() == 1)
    {
      Utils.callPhone(getContext(), phones.get(0));
    }
    else
    {
      // Show popup menu with all phones
      final PopupMenu popup = new PopupMenu(getContext(), parentView);
      final Menu menu = popup.getMenu();

      for (int i = 0; i < phones.size(); i++)
        menu.add(Menu.NONE, i, i, phones.get(i));

      popup.setOnMenuItemClickListener(item -> {
        final int id = item.getItemId();
        final Context ctx = getContext();
        Utils.callPhone(ctx, phones.get(id));
        return true;
      });

      popup.show();
    }
  }

  public void setRoutingModeListener(@Nullable RoutingModeListener routingModeListener)
  {
    mRoutingModeListener = routingModeListener;
  }

  private void onAvoidUnpavedBtnClicked()
  {
    onAvoidBtnClicked(RoadType.Dirty);
  }

  private void onAvoidFerryBtnClicked()
  {
    onAvoidBtnClicked(RoadType.Ferry);
  }

  private void onAvoidTollBtnClicked()
  {
    onAvoidBtnClicked(RoadType.Toll);
  }

  private void onAvoidBtnClicked(@NonNull RoadType roadType)
  {
    if (mRoutingModeListener == null)
      return;

    mRoutingModeListener.toggleRouteSettings(roadType);
  }

  private void initPlaceDescriptionView()
  {
    mPlaceDescriptionContainer = findViewById(R.id.poi_description_container);
    mPlaceDescriptionView = findViewById(R.id.poi_description);
    mPlaceDescriptionView.setOnLongClickListener(this);
    mPlaceDescriptionHeaderContainer = findViewById(R.id.pp_description_header_container);
    mPlaceDescriptionMoreBtn = findViewById(R.id.more_btn);
    mPlaceDescriptionMoreBtn.setOnClickListener(v -> showDescriptionScreen());
  }

  private void initPlaceOverlanderCheckinsView()
  {
    mPlaceDetailsHeaderContainer = findViewById(R.id.pp_details_header_container);
    mPlaceOverlanderCheckinsHeaderContainer = findViewById(R.id.pp_checkins_header_container);
    mPlaceOverlanderCheckin1Container = findViewById(R.id.pp_checkins_checkin1_container);
    mPlaceOverlanderCheckin1AuthorDate = findViewById(R.id.pp_checkins_checkin1_author_date);
    mPlaceOverlanderCheckin1Comment = findViewById(R.id.pp_checkins_checkin1_comment);
    mPlaceOverlanderCheckin2Container = findViewById(R.id.pp_checkins_checkin2_container);
    mPlaceOverlanderCheckin2AuthorDate = findViewById(R.id.pp_checkins_checkin2_author_date);
    mPlaceOverlanderCheckin2Comment = findViewById(R.id.pp_checkins_checkin2_comment);
    mPlaceOverlanderCheckin3Container = findViewById(R.id.pp_checkins_checkin3_container);
    mPlaceOverlanderCheckin3AuthorDate = findViewById(R.id.pp_checkins_checkin3_author_date);
    mPlaceOverlanderCheckin3Comment = findViewById(R.id.pp_checkins_checkin3_comment);
  }

  private void showDescriptionScreen()
  {
    Context context = mPlaceDescriptionContainer.getContext();
    String description = Objects.requireNonNull(mMapObject).getDescription();
    PlaceDescriptionActivity.start(context, description);
  }

  private void initEditMapObjectBtn()
  {
    final View editBookmarkBtn = mBookmarkFrame.findViewById(R.id.tv__bookmark_edit);
    editBookmarkBtn.setVisibility(View.VISIBLE);
    editBookmarkBtn.setOnClickListener(mEditBookmarkClickListener);
  }

  private void init(AttributeSet attrs, int defStyleAttr)
  {
    LayoutInflater.from(getContext()).inflate(R.layout.place_page, this);

    if (isInEditMode())
      return;

    final TypedArray attrArray = getContext().obtainStyledAttributes(attrs, R.styleable.PlacePageView, defStyleAttr, 0);
    mIsDocked = attrArray.getBoolean(R.styleable.PlacePageView_docked, false);
    mIsFloating = attrArray.getBoolean(R.styleable.PlacePageView_floating, false);
    attrArray.recycle();
  }

  public boolean isDocked()
  {
    return mIsDocked;
  }

  public boolean isFloating()
  {
    return mIsFloating;
  }

  @Nullable
  public MapObject getMapObject()
  {
    return mMapObject;
  }

  /**
   * @param mapObject new MapObject
   * @param listener  listener
   */
  public void setMapObject(@Nullable MapObject mapObject, @Nullable final SetMapObjectListener listener)
  {
    if (MapObject.same(mMapObject, mapObject))
    {
      mMapObject = mapObject;
      refreshViews();
      if (listener != null)
      {
        listener.onSetMapObjectComplete(true);
      }
      if (mCurrentCountry == null)
        setCurrentCountry();
      return;
    }

    mMapObject = mapObject;

    setMapObjectInternal();
    if (listener != null)
      listener.onSetMapObjectComplete(false);
  }

  private void setMapObjectInternal()
  {
    detachCountry();
    if (mMapObject != null)
    {
      initEditMapObjectBtn();

      setCurrentCountry();
    }
    refreshViews();
  }

  private void setCurrentCountry()
  {
    if (mCurrentCountry != null)
      throw new AssertionError("country should be detached before!");
    String country = MapManager.nativeGetSelectedCountry();
    if (country != null && !RoutingController.get().isNavigating())
      attachCountry(country);
  }

  void refreshViews()
  {
    if (mMapObject == null)
    {
      LOGGER.e(TAG, "A place page views cannot be refreshed, mMapObject is null");
      return;
    }
    refreshPreview(mMapObject);
    refreshDetails(mMapObject);
    refreshViewsInternal(mMapObject);

    //
    // The view is completely broken after the first call to refreshView():
    // https://github.com/organicmaps/organicmaps/issues/722
    // https://github.com/organicmaps/organicmaps/issues/1065
    //
    // Force re-layout explicitly on the next event loop after the first call to refreshView().
    //
    if (Utils.isAndroid11OrLater()) {
      post(() -> {
        mPreview.requestLayout();
        mDetails.requestLayout();
        mButtons.getFrame().requestLayout();
      });
    }
  }

  private void refreshViewsInternal(@NonNull MapObject mapObject)
  {
    final Location loc = LocationHelper.INSTANCE.getSavedLocation();
    switch (mapObject.getMapObjectType())
    {
      case MapObject.BOOKMARK:
        refreshDistanceToObject(mapObject, loc);
        showBookmarkDetails(mapObject);
        updateBookmarkButton();
        setButtons(mapObject, false, true);
        break;
      case MapObject.POI:
      case MapObject.SEARCH:
        refreshDistanceToObject(mapObject, loc);
        hideBookmarkDetails();
        setButtons(mapObject, false, true);
        setPlaceDescription(mapObject);
        setPlaceOverlanderCheckins(mapObject);
        break;
      case MapObject.API_POINT:
        refreshDistanceToObject(mapObject, loc);
        hideBookmarkDetails();
        setButtons(mapObject, true, true);
        break;
      case MapObject.MY_POSITION:
        refreshMyPosition(mapObject, loc);
        hideBookmarkDetails();
        setButtons(mapObject, false, false);
        break;
    }
  }

  private void setPlaceDescription(@NonNull MapObject mapObject)
  {
    boolean isBookmark = MapObject.isOfType(MapObject.BOOKMARK, mapObject);
    if (TextUtils.isEmpty(mapObject.getDescription()) && !isBookmark)
    {
      if (!TextUtils.isEmpty(mapObject.getMetadata(Metadata.MetadataType.FMD_OVERLANDER_DESCRIPTION))) {
        mPlaceDescriptionView.setText(mapObject.getMetadata(Metadata.MetadataType.FMD_OVERLANDER_DESCRIPTION));
      } else {
        UiUtils.hide(mPlaceDescriptionContainer, mPlaceDescriptionHeaderContainer);
        return;
      }
    }

    if (isBookmark && TextUtils.isEmpty(mapObject.getMetadata(Metadata.MetadataType.FMD_OVERLANDER_DESCRIPTION)))
    {
      final Bookmark bmk = (Bookmark) mapObject;
      UiUtils.showIf(!TextUtils.isEmpty(bmk.getBookmarkDescription()), mPlaceDescriptionHeaderContainer);
      UiUtils.hide(mPlaceDescriptionContainer);
      return;
    }
    UiUtils.show(mPlaceDescriptionContainer, mPlaceDescriptionHeaderContainer);
    if (TextUtils.isEmpty(mapObject.getMetadata(Metadata.MetadataType.FMD_OVERLANDER_DESCRIPTION))) {
      mPlaceDescriptionView.setText(Html.fromHtml(mapObject.getDescription()));
    }
  }

  private void setPlaceOverlanderCheckins(@NonNull MapObject mapObject)
  {
    UiUtils.showIf(!TextUtils.isEmpty(mapObject.getMetadata(Metadata.MetadataType.FMD_OVERLANDER_CHECKIN1)), mPlaceDetailsHeaderContainer, mPlaceOverlanderCheckinsHeaderContainer);
    if (!TextUtils.isEmpty(mapObject.getMetadata(Metadata.MetadataType.FMD_OVERLANDER_CHECKIN1))){
      final String[] checkin1Array = mapObject.getMetadata(Metadata.MetadataType.FMD_OVERLANDER_CHECKIN1).split("\\|");
      final String checkin1AuthorAndDate = checkin1Array[0] + " " + getResources().getString(R.string.placepage_place_overlander_by) + " " + checkin1Array[1];
      mPlaceOverlanderCheckin1AuthorDate.setText(checkin1AuthorAndDate);
      mPlaceOverlanderCheckin1Comment.setText(checkin1Array[3]);
      UiUtils.show(mPlaceOverlanderCheckin1Container);
    } else {
      UiUtils.hide(mPlaceOverlanderCheckin1Container);
    }
    if (!TextUtils.isEmpty(mapObject.getMetadata(Metadata.MetadataType.FMD_OVERLANDER_CHECKIN2))){
      final String[] checkin2Array = mapObject.getMetadata(Metadata.MetadataType.FMD_OVERLANDER_CHECKIN2).split("\\|");
      final String checkin2AuthorAndDate = checkin2Array[0] + " " + getResources().getString(R.string.placepage_place_overlander_by) + " " + checkin2Array[1];
      mPlaceOverlanderCheckin2AuthorDate.setText(checkin2AuthorAndDate);
      mPlaceOverlanderCheckin2Comment.setText(checkin2Array[3]);
      UiUtils.show(mPlaceOverlanderCheckin2Container);
    } else {
      UiUtils.hide(mPlaceOverlanderCheckin2Container);
    }
    if (!TextUtils.isEmpty(mapObject.getMetadata(Metadata.MetadataType.FMD_OVERLANDER_CHECKIN3))){
      final String[] checkin3Array = mapObject.getMetadata(Metadata.MetadataType.FMD_OVERLANDER_CHECKIN3).split("\\|");
      final String checkin3AuthorAndDate = checkin3Array[0] + " " + getResources().getString(R.string.placepage_place_overlander_by) + " " + checkin3Array[1];
      mPlaceOverlanderCheckin3AuthorDate.setText(checkin3AuthorAndDate);
      mPlaceOverlanderCheckin3Comment.setText(checkin3Array[3]);
      UiUtils.show(mPlaceOverlanderCheckin3Container);
    } else {
      UiUtils.hide(mPlaceOverlanderCheckin3Container);
    }
  }

  private void setTextAndColorizeSubtitle(@NonNull MapObject mapObject)
  {
    String text = mapObject.getSubtitle();
    UiUtils.setTextAndHideIfEmpty(mTvSubtitle, text);
    if (!TextUtils.isEmpty(text))
    {
      SpannableStringBuilder sb = new SpannableStringBuilder(text);
      int start = text.indexOf("★");
      int end = text.lastIndexOf("★") + 1;
      if (start > -1)
      {
        sb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.base_yellow)),
                   start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

      }
      mTvSubtitle.setText(sb);
    }
  }

  private void refreshPreview(@NonNull MapObject mapObject)
  {
    UiUtils.setTextAndHideIfEmpty(mTvTitle, mapObject.getTitle());
    UiUtils.setTextAndHideIfEmpty(mTvSecondaryTitle, mapObject.getSecondaryTitle());
    boolean isPopular = mapObject.getPopularity().getType() == Popularity.Type.POPULAR;
    if (mPopularityView != null)
      UiUtils.showIf(isPopular, mPopularityView);
    if (mToolbar != null)
      mToolbar.setTitle(mapObject.getTitle());
    setTextAndColorizeSubtitle(mapObject);
    UiUtils.hide(mAvDirection);
    UiUtils.setTextAndHideIfEmpty(mTvAddress, mapObject.getAddress());
  }

  private void refreshDetails(@NonNull MapObject mapObject)
  {
    refreshLatLon(mapObject);

    String website = mapObject.getMetadata(Metadata.MetadataType.FMD_WEBSITE);
    String url = mapObject.getMetadata(Metadata.MetadataType.FMD_URL);
    String overlanderLink = mapObject.getMetadata(Metadata.MetadataType.FMD_OVERLANDER_LINK);
    refreshMetadataOrHide(TextUtils.isEmpty(website) ? (TextUtils.isEmpty(url) ? overlanderLink : url) : website, mWebsite, mTvWebsite);
    refreshPhoneNumberList(mapObject.getMetadata(Metadata.MetadataType.FMD_PHONE_NUMBER));
    refreshMetadataOrHide(mapObject.getMetadata(Metadata.MetadataType.FMD_EMAIL), mEmail, mTvEmail);
    refreshMetadataOrHide(mapObject.getMetadata(Metadata.MetadataType.FMD_OPERATOR), mOperator, mTvOperator);
    refreshMetadataOrHide(Framework.nativeGetActiveObjectFormattedCuisine(), mCuisine, mTvCuisine);
    refreshMetadataOrHide(mapObject.getMetadata(Metadata.MetadataType.FMD_WIKIPEDIA), mWiki, null);
    refreshMetadataOrHide(mapObject.getMetadata(Metadata.MetadataType.FMD_INTERNET), mWifi, null);
    refreshMetadataOrHide(mapObject.getMetadata(Metadata.MetadataType.FMD_FLATS), mEntrance, mTvEntrance);
    refreshOpeningHours(mapObject);
    refreshSocialLinks(mapObject);
    refreshOverlanderDetails(mapObject);

//    showTaxiOffer(mapObject);

    if (RoutingController.get().isNavigating() || RoutingController.get().isPlanning())
    {
      UiUtils.hide(mEditPlace, mAddOrganisation, mAddPlace, mEditTopSpace);
    }
    else
    {
      UiUtils.showIf(Editor.nativeShouldShowEditPlace(), mEditPlace);
      UiUtils.showIf(Editor.nativeShouldShowAddBusiness(), mAddOrganisation);
      UiUtils.showIf(Editor.nativeShouldShowAddPlace(), mAddPlace);
      UiUtils.showIf(UiUtils.isVisible(mEditPlace)
                     || UiUtils.isVisible(mAddOrganisation)
                     || UiUtils.isVisible(mAddPlace), mEditTopSpace);
    }
    setPlaceOverlanderCheckins(mapObject);
    setPlaceDescription(mapObject);
  }

  private void refreshOpeningHours(@NonNull MapObject mapObject)
  {
    final String ohStr = mapObject.getMetadata(Metadata.MetadataType.FMD_OPEN_HOURS);
    final Timetable[] timetables = OpeningHours.nativeTimetablesFromString(ohStr);
    final boolean isEmptyTT = (timetables == null || timetables.length == 0);
    final int color = ThemeUtils.getColor(getContext(), android.R.attr.textColorPrimary);

    if (isEmptyTT)
    {
      // 'opening_hours' tag wasn't parsed either because it's empty or wrong format.
      if (!ohStr.isEmpty())
      {
        UiUtils.show(mOpeningHours);
        refreshTodayOpeningHours(ohStr, color);
        UiUtils.hide(mTodayNonBusinessTime);
        UiUtils.hide(mFullWeekOpeningHours);
      }
      else
      {
        UiUtils.hide(mOpeningHours);
      }
      return;
    }

    UiUtils.show(mOpeningHours);

    final Resources resources = getResources();

    if (timetables[0].isFullWeek())
    {
      final Timetable tt = timetables[0];
      if (tt.isFullday)
      {
        refreshTodayOpeningHours(resources.getString(R.string.twentyfour_seven), color);
        UiUtils.clearTextAndHide(mTodayNonBusinessTime);
        UiUtils.hide(mTodayNonBusinessTime);
      }
      else
      {
        refreshTodayOpeningHours(resources.getString(R.string.daily), tt.workingTimespan.toWideString(), color);
        refreshTodayNonBusinessTime(tt.closedTimespans);
      }

      UiUtils.hide(mFullWeekOpeningHours);
      return;
    }

    // Show whole week time table.
    mOpeningHoursAdapter.setTimetables(timetables);
    UiUtils.show(mFullWeekOpeningHours);

    // Show today's open time + non-business time.
    boolean containsCurrentWeekday = false;
    final int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    for (Timetable tt : timetables)
    {
      if (tt.containsWeekday(currentDay))
      {
        containsCurrentWeekday = true;
        String openTime;

        if (tt.isFullday)
        {
          String allDay = resources.getString(R.string.editor_time_allday);
          openTime = Utils.unCapitalize(allDay);
        }
        else
        {
          openTime = tt.workingTimespan.toWideString();
        }

        refreshTodayOpeningHours(resources.getString(R.string.today), openTime, color);
        refreshTodayNonBusinessTime(tt.closedTimespans);

        break;
      }
    }

    // Show that place is closed today.
    if (!containsCurrentWeekday)
    {
      refreshTodayOpeningHours(resources.getString(R.string.day_off_today), resources.getColor(R.color.base_red));
      UiUtils.hide(mTodayNonBusinessTime);
    }
  }

  private void refreshTodayNonBusinessTime(Timespan[] closedTimespans)
  {
    final String hoursClosedLabel = getResources().getString(R.string.editor_hours_closed);
    if (closedTimespans==null || closedTimespans.length == 0)
      UiUtils.clearTextAndHide(mTodayNonBusinessTime);
    else
      UiUtils.setTextAndShow(mTodayNonBusinessTime, TimeFormatUtils.formatNonBusinessTime(closedTimespans, hoursClosedLabel));
  }

  private void refreshSocialLinks(@NonNull MapObject mapObject)
  {
    final String facebookPageLink = mapObject.getMetadata(Metadata.MetadataType.FMD_CONTACT_FACEBOOK);
    refreshSocialPageLink(mFacebookPage, mTvFacebookPage, facebookPageLink, "facebook.com");
    final String instagramPageLink = mapObject.getMetadata(Metadata.MetadataType.FMD_CONTACT_INSTAGRAM);
    refreshSocialPageLink(mInstagramPage, mTvInstagramPage, instagramPageLink, "instagram.com");
    final String twitterPageLink = mapObject.getMetadata(Metadata.MetadataType.FMD_CONTACT_TWITTER);
    refreshSocialPageLink(mTwitterPage, mTvTwitterPage, twitterPageLink, "twitter.com");
    final String vkPageLink = mapObject.getMetadata(Metadata.MetadataType.FMD_CONTACT_VK);
    refreshSocialPageLink(mVkPage, mTvVkPage, vkPageLink, "vk.com");
    final String linePageLink = mapObject.getMetadata(Metadata.MetadataType.FMD_CONTACT_LINE);
    refreshLinePageLink(mLinePage, mTvLinePage, linePageLink);
  }

  private void refreshSocialPageLink(View view, TextView tvSocialPage, String socialPage, String webDomain)
  {
    if (TextUtils.isEmpty(socialPage))
    {
      view.setVisibility(GONE);
    }
    else
    {
      view.setVisibility(VISIBLE);
      if (socialPage.indexOf('/') >= 0)
        tvSocialPage.setText("https://" + webDomain + "/" + socialPage);
      else
        tvSocialPage.setText("@" + socialPage);
    }
  }

  private void refreshOverlanderDetails(@NonNull MapObject mapObject)
  {
    final String[] servicesArray = mapObject.getMetadata(Metadata.MetadataType.FMD_OVERLANDER_SERVICES).split("\\|");
    for (String service: servicesArray) {
      refreshOverlanderServices(service);
    }
    final String[] activitiesArray = mapObject.getMetadata(Metadata.MetadataType.FMD_OVERLANDER_ACTIVITIES).split("\\|");
    for (String activity: activitiesArray) {
      refreshOverlanderActivities(activity);
    }
  }

  // Tag `contact:line` could contain urls from domains: line.me, liff.line.me, page.line.me, etc.
  // And `socialPage` should not be prepended with domain, but only with "https://" protocol.
  private void refreshLinePageLink(View view, TextView tvSocialPage, String socialPage)
  {
    if (TextUtils.isEmpty(socialPage))
    {
      view.setVisibility(GONE);
    }
    else
    {
      view.setVisibility(VISIBLE);
      if (socialPage.indexOf('/') >= 0)
        tvSocialPage.setText("https://" + socialPage);
      else
        tvSocialPage.setText("@" + socialPage);
    }
  }

  private void refreshTodayOpeningHours(String label, String openTime, @ColorInt int color)
  {
    UiUtils.setTextAndShow(mTodayLabel, label);
    UiUtils.setTextAndShow(mTodayOpenTime, openTime);

    mTodayLabel.setTextColor(color);
    mTodayOpenTime.setTextColor(color);
  }

  private void refreshTodayOpeningHours(String label, @ColorInt int color)
  {
    UiUtils.setTextAndShow(mTodayLabel, label);
    UiUtils.hide(mTodayOpenTime);

    mTodayLabel.setTextColor(color);
    mTodayOpenTime.setTextColor(color);
  }

  private void refreshPhoneNumberList(String phones)
  {
    mPhoneAdapter.refreshPhones(phones);
  }

  private void updateBookmarkButton()
  {
    if (mBookmarkButtonIcon == null || mBookmarkButtonFrame == null)
      return;

    if (mBookmarkSet)
      mBookmarkButtonIcon.setImageDrawable(Graphics.tint(getContext(), R.drawable.ic_bookmarks_on, R.attr.iconTintActive));
    else
      mBookmarkButtonIcon.setImageDrawable(Graphics.tint(getContext(), R.drawable.ic_bookmarks_off, R.attr.iconTint));

    mBookmarkButtonFrame.setEnabled(true);
  }

  private void hideBookmarkDetails()
  {
    mBookmarkSet = false;
    UiUtils.hide(mBookmarkFrame);
    updateBookmarkButton();
  }

  private void showBookmarkDetails(@NonNull MapObject mapObject)
  {
    mBookmarkSet = true;
    UiUtils.show(mBookmarkFrame);

    final String notes = ((Bookmark) mapObject).getBookmarkDescription();

    if (TextUtils.isEmpty(notes))
    {
      UiUtils.hide(mTvBookmarkNote, mWvBookmarkNote);
      return;
    }

    if (StringUtils.nativeIsHtml(notes))
    {
      mWvBookmarkNote.loadData(notes, Utils.TEXT_HTML, Utils.UTF_8);
      UiUtils.show(mWvBookmarkNote);
      UiUtils.hide(mTvBookmarkNote);
    }
    else
    {
      mTvBookmarkNote.setText(notes);
      Linkify.addLinks(mTvBookmarkNote, Linkify.ALL);
      UiUtils.show(mTvBookmarkNote);
      UiUtils.hide(mWvBookmarkNote);
    }
  }

  @NonNull
  private static PlacePageButtons.Item toPlacePageButton(@NonNull RoadWarningMarkType type)
  {
    switch (type)
    {
      case DIRTY:
        return PlacePageButtons.Item.ROUTE_AVOID_UNPAVED;
      case FERRY:
        return PlacePageButtons.Item.ROUTE_AVOID_FERRY;
      case TOLL:
        return PlacePageButtons.Item.ROUTE_AVOID_TOLL;
      default:
        throw new AssertionError("Unsupported road warning type: " + type);
    }
  }

  private void setButtons(@NonNull MapObject mapObject, boolean showBackButton, boolean showRoutingButton)
  {
    List<PlacePageButtons.PlacePageButton> buttons = new ArrayList<>();

    if (mapObject.getRoadWarningMarkType() != RoadWarningMarkType.UNKNOWN)
    {
      RoadWarningMarkType markType = mapObject.getRoadWarningMarkType();
      PlacePageButtons.Item roadType = toPlacePageButton(markType);
      buttons.add(roadType);
      mButtons.setItems(buttons);
      return;
    }

    if (RoutingController.get().isRoutePoint(mapObject))
    {
      buttons.add(PlacePageButtons.Item.ROUTE_REMOVE);
      mButtons.setItems(buttons);
      return;
    }

    if (showBackButton || ParsedMwmRequest.isPickPointMode())
      buttons.add(PlacePageButtons.Item.BACK);

    final boolean hasNumber = mapObject.hasPhoneNumber();

    if (hasNumber)
    {
      buttons.add(PlacePageButtons.Item.CALL);
      mPhoneRecycler.setVisibility(VISIBLE);
    }
    else
      mPhoneRecycler.setVisibility(GONE);

    boolean needToShowRoutingButtons = RoutingController.get().isPlanning() || showRoutingButton;

    if (needToShowRoutingButtons && !hasNumber)
      buttons.add(PlacePageButtons.Item.ROUTE_FROM);

    buttons.add(mapObject.getMapObjectType() == MapObject.BOOKMARK ? PlacePageButtons.Item.BOOKMARK_DELETE
        : PlacePageButtons.Item.BOOKMARK_SAVE);

    if (needToShowRoutingButtons)
    {
      buttons.add(PlacePageButtons.Item.ROUTE_TO);
      if (hasNumber)
        buttons.add(PlacePageButtons.Item.ROUTE_FROM);
      if (RoutingController.get().isStopPointAllowed())
        buttons.add(PlacePageButtons.Item.ROUTE_ADD);
    }

    buttons.add(PlacePageButtons.Item.SHARE);

    mButtons.setItems(buttons);
  }

  public void refreshLocation(Location l)
  {
    if (mMapObject == null)
    {
      // TODO: This method is constantly called even when nothing is selected on the map.
      //LOGGER.e(TAG, "A location cannot be refreshed, mMapObject is null!");
      return;
    }

    if (MapObject.isOfType(MapObject.MY_POSITION, mMapObject))
      refreshMyPosition(mMapObject, l);
    else
      refreshDistanceToObject(mMapObject, l);
  }

  private void refreshMyPosition(@NonNull MapObject mapObject, Location l)
  {
    UiUtils.hide(mTvDistance);

    if (l == null)
      return;

    final StringBuilder builder = new StringBuilder();
    if (l.hasAltitude())
    {
      double altitude = l.getAltitude();
      builder.append(altitude >= 0 ? "▲" : "▼");
      builder.append(Framework.nativeFormatAltitude(altitude));
    }
    if (l.hasSpeed())
      builder.append("   ")
             .append(Framework.nativeFormatSpeed(l.getSpeed()));
    UiUtils.setTextAndHideIfEmpty(mTvSubtitle, builder.toString());

    mapObject.setLat(l.getLatitude());
    mapObject.setLon(l.getLongitude());
    refreshLatLon(mapObject);
  }

  private void refreshDistanceToObject(@NonNull MapObject mapObject, Location l)
  {
    UiUtils.showIf(l != null, mTvDistance);
    if (l == null)
      return;

    mTvDistance.setVisibility(View.VISIBLE);
    double lat = mapObject.getLat();
    double lon = mapObject.getLon();
    DistanceAndAzimut distanceAndAzimuth =
        Framework.nativeGetDistanceAndAzimuthFromLatLon(lat, lon, l.getLatitude(), l.getLongitude(), 0.0);
    mTvDistance.setText(distanceAndAzimuth.getDistance());
  }

  private void refreshLatLon(@NonNull MapObject mapObject)
  {
    final double lat = mapObject.getLat();
    final double lon = mapObject.getLon();
    final String latLon = Framework.nativeFormatLatLon(lat, lon, mCoordsFormat.getId());
    mTvLatlon.setText(latLon);
  }

  private void refreshOverlanderServices(String service)
  {
    switch (service)
    {
      case "Electricity":
        View mOverlanderElectricity;
        TextView mTvOverlanderElectricity;
        mOverlanderElectricity = findViewById(R.id.ll__place_overlander_electricity);
        mTvOverlanderElectricity = findViewById(R.id.tv__place_overlander_electricity);
        mOverlanderElectricity.setVisibility(View.VISIBLE);
        mTvOverlanderElectricity.setText(service);
        break;
      case "Atm":
        View mOverlanderAtm;
        TextView mTvOverlanderAtm;
        mOverlanderAtm = findViewById(R.id.ll__place_overlander_atm);
        mTvOverlanderAtm = findViewById(R.id.tv__place_overlander_atm);
        mOverlanderAtm.setVisibility(View.VISIBLE);
        mTvOverlanderAtm.setText(service);
        break;
      case "CampervanFriendly":
        View mOverlanderCampervanfriendly;
        TextView mTvOverlanderCampervanfriendly;
        mOverlanderCampervanfriendly = findViewById(R.id.ll__place_overlander_campervanfriendly);
        mTvOverlanderCampervanfriendly = findViewById(R.id.tv__place_overlander_campervanfriendly);
        mOverlanderCampervanfriendly.setVisibility(View.VISIBLE);
        mTvOverlanderCampervanfriendly.setText(service);
        break;
      case "Carwash":
        View mOverlanderCarwash;
        TextView mTvOverlanderCarwash;
        mOverlanderCarwash = findViewById(R.id.ll__place_overlander_carwash);
        mTvOverlanderCarwash = findViewById(R.id.tv__place_overlander_carwash);
        mOverlanderCarwash.setVisibility(View.VISIBLE);
        mTvOverlanderCarwash.setText(service);
        break;
      case "Children":
        View mOverlanderChildren;
        TextView mTvOverlanderChildren;
        mOverlanderChildren = findViewById(R.id.ll__place_overlander_children);
        mTvOverlanderChildren = findViewById(R.id.tv__place_overlander_children);
        mOverlanderChildren.setVisibility(View.VISIBLE);
        mTvOverlanderChildren.setText(service);
        break;
      case "Eat":
        View mOverlanderEat;
        TextView mTvOverlanderEat;
        mOverlanderEat = findViewById(R.id.ll__place_overlander_eat);
        mTvOverlanderEat = findViewById(R.id.tv__place_overlander_eat);
        mOverlanderEat.setVisibility(View.VISIBLE);
        mTvOverlanderEat.setText(service);
        break;
      case "Entertainment":
        View mOverlanderEntertainment;
        TextView mTvOverlanderEntertainment;
        mOverlanderEntertainment = findViewById(R.id.ll__place_overlander_entertainment);
        mTvOverlanderEntertainment = findViewById(R.id.tv__place_overlander_entertainment);
        mOverlanderEntertainment.setVisibility(View.VISIBLE);
        mTvOverlanderEntertainment.setText(service);
        break;
      case "Food":
        View mOverlanderFood;
        TextView mTvOverlanderFood;
        mOverlanderFood = findViewById(R.id.ll__place_overlander_food);
        mTvOverlanderFood = findViewById(R.id.tv__place_overlander_food);
        mOverlanderFood.setVisibility(View.VISIBLE);
        mTvOverlanderFood.setText(service);
        break;
      case "Fuel":
        View mOverlanderFuel;
        TextView mTvOverlanderFuel;
        mOverlanderFuel = findViewById(R.id.ll__place_overlander_fuel);
        mTvOverlanderFuel = findViewById(R.id.tv__place_overlander_fuel);
        mOverlanderFuel.setVisibility(View.VISIBLE);
        mTvOverlanderFuel.setText(service);
        break;
      case "Garbage":
        View mOverlanderGarbage;
        TextView mTvOverlanderGarbage;
        mOverlanderGarbage = findViewById(R.id.ll__place_overlander_garbage);
        mTvOverlanderGarbage = findViewById(R.id.tv__place_overlander_garbage);
        mOverlanderGarbage.setVisibility(View.VISIBLE);
        mTvOverlanderGarbage.setText(service);
        break;
      case "Hospital":
        View mOverlanderHospital;
        TextView mTvOverlanderHospital;
        mOverlanderHospital = findViewById(R.id.ll__place_overlander_hospital);
        mTvOverlanderHospital = findViewById(R.id.tv__place_overlander_hospital);
        mOverlanderHospital.setVisibility(View.VISIBLE);
        mTvOverlanderHospital.setText(service);
        break;
      case "Laundry":
        View mOverlanderLaundry;
        TextView mTvOverlanderLaundry;
        mOverlanderLaundry = findViewById(R.id.ll__place_overlander_laundry);
        mTvOverlanderLaundry = findViewById(R.id.tv__place_overlander_laundry);
        mOverlanderLaundry.setVisibility(View.VISIBLE);
        mTvOverlanderLaundry.setText(service);
        break;
      case "LPG":
        View mOverlanderLPG;
        TextView mTvOverlanderLPG;
        mOverlanderLPG = findViewById(R.id.ll__place_overlander_lpg);
        mTvOverlanderLPG = findViewById(R.id.tv__place_overlander_lpg);
        mOverlanderLPG.setVisibility(View.VISIBLE);
        mTvOverlanderLPG.setText(service);
        break;
      case "Mechanic":
        View mOverlanderMechanic;
        TextView mTvOverlanderMechanic;
        mOverlanderMechanic = findViewById(R.id.ll__place_overlander_mechanic);
        mTvOverlanderMechanic = findViewById(R.id.tv__place_overlander_mechanic);
        mOverlanderMechanic.setVisibility(View.VISIBLE);
        mTvOverlanderMechanic.setText(service);
        break;
      case "Kitchen":
        View mOverlanderKitchen;
        TextView mTvOverlanderKitchen;
        mOverlanderKitchen = findViewById(R.id.ll__place_overlander_kitchen);
        mTvOverlanderKitchen = findViewById(R.id.tv__place_overlander_kitchen);
        mOverlanderKitchen.setVisibility(View.VISIBLE);
        mTvOverlanderKitchen.setText(service);
        break;
      case "Monuments":
        View mOverlanderMonuments;
        TextView mTvOverlanderMonuments;
        mOverlanderMonuments = findViewById(R.id.ll__place_overlander_monuments);
        mTvOverlanderMonuments = findViewById(R.id.tv__place_overlander_monuments);
        mOverlanderMonuments.setVisibility(View.VISIBLE);
        mTvOverlanderMonuments.setText(service);
        break;
      case "Nightlife":
        View mOverlanderNightlife;
        TextView mTvOverlanderNightlife;
        mOverlanderNightlife = findViewById(R.id.ll__place_overlander_nightlife);
        mTvOverlanderNightlife = findViewById(R.id.tv__place_overlander_nightlife);
        mOverlanderNightlife.setVisibility(View.VISIBLE);
        mTvOverlanderNightlife.setText(service);
        break;
      case "Pharmacy":
        View mOverlanderPharmacy;
        TextView mTvOverlanderPharmacy;
        mOverlanderPharmacy = findViewById(R.id.ll__place_overlander_pharmacy);
        mTvOverlanderPharmacy = findViewById(R.id.tv__place_overlander_pharmacy);
        mOverlanderPharmacy.setVisibility(View.VISIBLE);
        mTvOverlanderPharmacy.setText(service);
        break;
      case "Police":
        View mOverlanderPolice;
        TextView mTvOverlanderPolice;
        mOverlanderPolice = findViewById(R.id.ll__place_overlander_police);
        mTvOverlanderPolice = findViewById(R.id.tv__place_overlander_police);
        mOverlanderPolice.setVisibility(View.VISIBLE);
        mTvOverlanderPolice.setText(service);
        break;
      case "Post":
        View mOverlanderPost;
        TextView mTvOverlanderPost;
        mOverlanderPost = findViewById(R.id.ll__place_overlander_post);
        mTvOverlanderPost = findViewById(R.id.tv__place_overlander_post);
        mOverlanderPost.setVisibility(View.VISIBLE);
        mTvOverlanderPost.setText(service);
        break;
      case "Propane":
        View mOverlanderPropane;
        TextView mTvOverlanderPropane;
        mOverlanderPropane = findViewById(R.id.ll__place_overlander_propane);
        mTvOverlanderPropane = findViewById(R.id.tv__place_overlander_propane);
        mOverlanderPropane.setVisibility(View.VISIBLE);
        mTvOverlanderPropane.setText(service);
        break;
      case "Park":
        View mOverlanderPark;
        TextView mTvOverlanderPark;
        mOverlanderPark = findViewById(R.id.ll__place_overlander_park);
        mTvOverlanderPark = findViewById(R.id.tv__place_overlander_park);
        mOverlanderPark.setVisibility(View.VISIBLE);
        mTvOverlanderPark.setText(service);
        break;
      case "Parking":
        View mOverlanderParking;
        TextView mTvOverlanderParking;
        mOverlanderParking = findViewById(R.id.ll__place_overlander_parking);
        mTvOverlanderParking = findViewById(R.id.tv__place_overlander_parking);
        mOverlanderParking.setVisibility(View.VISIBLE);
        mTvOverlanderParking.setText(service);
        break;
      case "PetFriendly":
        View mOverlanderPetfriendly;
        TextView mTvOverlanderPetfriendly;
        mOverlanderPetfriendly = findViewById(R.id.ll__place_overlander_petfriendly);
        mTvOverlanderPetfriendly = findViewById(R.id.tv__place_overlander_petfriendly);
        mOverlanderPetfriendly.setVisibility(View.VISIBLE);
        mTvOverlanderPetfriendly.setText(service);
        break;
      case "Picnic":
        View mOverlanderPicnic;
        TextView mTvOverlanderPicnic;
        mOverlanderPicnic = findViewById(R.id.ll__place_overlander_picnic);
        mTvOverlanderPicnic = findViewById(R.id.tv__place_overlander_picnic);
        mOverlanderPicnic.setVisibility(View.VISIBLE);
        mTvOverlanderPicnic.setText(service);
        break;
      case "RestArea":
        View mOverlanderRestarea;
        TextView mTvOverlanderRestarea;
        mOverlanderRestarea = findViewById(R.id.ll__place_overlander_restarea);
        mTvOverlanderRestarea = findViewById(R.id.tv__place_overlander_restarea);
        mOverlanderRestarea.setVisibility(View.VISIBLE);
        mTvOverlanderRestarea.setText(service);
        break;
      case "Restaurant":
        View mOverlanderRestaurant;
        TextView mTvOverlanderRestaurant;
        mOverlanderRestaurant = findViewById(R.id.ll__place_overlander_restaurant);
        mTvOverlanderRestaurant = findViewById(R.id.tv__place_overlander_restaurant);
        mOverlanderRestaurant.setVisibility(View.VISIBLE);
        mTvOverlanderRestaurant.setText(service);
        break;
      case "Sanidump":
        View mOverlanderSanidump;
        TextView mTvOverlanderSanidump;
        mOverlanderSanidump = findViewById(R.id.ll__place_overlander_sanidump);
        mTvOverlanderSanidump = findViewById(R.id.tv__place_overlander_sanidump);
        mOverlanderSanidump.setVisibility(View.VISIBLE);
        mTvOverlanderSanidump.setText(service);
        break;
      case "Shopping":
        View mOverlanderShopping;
        TextView mTvOverlanderShopping;
        mOverlanderShopping = findViewById(R.id.ll__place_overlander_shopping);
        mTvOverlanderShopping = findViewById(R.id.tv__place_overlander_shopping);
        mOverlanderShopping.setVisibility(View.VISIBLE);
        mTvOverlanderShopping.setText(service);
        break;
      case "Showers":
        View mOverlanderShowers;
        TextView mTvOverlanderShowers;
        mOverlanderShowers = findViewById(R.id.ll__place_overlander_showers);
        mTvOverlanderShowers = findViewById(R.id.tv__place_overlander_showers);
        mOverlanderShowers.setVisibility(View.VISIBLE);
        mTvOverlanderShowers.setText(service);
        break;
      case "Swimmingpool":
        View mOverlanderSwimmingpool;
        TextView mTvOverlanderSwimmingpool;
        mOverlanderSwimmingpool = findViewById(R.id.ll__place_overlander_swimmingpool);
        mTvOverlanderSwimmingpool = findViewById(R.id.tv__place_overlander_swimmingpool);
        mOverlanderSwimmingpool.setVisibility(View.VISIBLE);
        mTvOverlanderSwimmingpool.setText(service);
        break;
      case "TentFriendly":
        View mOverlanderTentfriendly;
        TextView mTvOverlanderTentfriendly;
        mOverlanderTentfriendly = findViewById(R.id.ll__place_overlander_tentfriendly);
        mTvOverlanderTentfriendly = findViewById(R.id.tv__place_overlander_tentfriendly);
        mOverlanderTentfriendly.setVisibility(View.VISIBLE);
        mTvOverlanderTentfriendly.setText(service);
        break;
      case "Toilets":
        View mOverlanderToilets;
        TextView mTvOverlanderToilets;
        mOverlanderToilets = findViewById(R.id.ll__place_overlander_toilets);
        mTvOverlanderToilets = findViewById(R.id.tv__place_overlander_toilets);
        mOverlanderToilets.setVisibility(View.VISIBLE);
        mTvOverlanderToilets.setText(service);
        break;
      case "Transport":
        View mOverlanderTransport;
        TextView mTvOverlanderTransport;
        mOverlanderTransport = findViewById(R.id.ll__place_overlander_transport);
        mTvOverlanderTransport = findViewById(R.id.tv__place_overlander_transport);
        mOverlanderTransport.setVisibility(View.VISIBLE);
        mTvOverlanderTransport.setText(service);
        break;
      case "Viewpoint":
        View mOverlanderViewpoint;
        TextView mTvOverlanderViewpoint;
        mOverlanderViewpoint = findViewById(R.id.ll__place_overlander_viewpoint);
        mTvOverlanderViewpoint = findViewById(R.id.tv__place_overlander_viewpoint);
        mOverlanderViewpoint.setVisibility(View.VISIBLE);
        mTvOverlanderViewpoint.setText(service);
        break;
      case "Warning":
        View mOverlanderWarning;
        TextView mTvOverlanderWarning;
        mOverlanderWarning = findViewById(R.id.ll__place_overlander_warning);
        mTvOverlanderWarning = findViewById(R.id.tv__place_overlander_warning);
        mOverlanderWarning.setVisibility(View.VISIBLE);
        mTvOverlanderWarning.setText(service);
        break;
      case "Water":
        View mOverlanderWater;
        TextView mTvOverlanderWater;
        mOverlanderWater = findViewById(R.id.ll__place_overlander_water);
        mTvOverlanderWater = findViewById(R.id.tv__place_overlander_water);
        mOverlanderWater.setVisibility(View.VISIBLE);
        mTvOverlanderWater.setText(service);
        break;
      case "Wifi":
        View mOverlanderWifi;
        TextView mTvOverlanderWifi;
        mOverlanderWifi = findViewById(R.id.ll__place_overlander_wifi);
        mTvOverlanderWifi = findViewById(R.id.tv__place_overlander_wifi);
        mOverlanderWifi.setVisibility(View.VISIBLE);
        mTvOverlanderWifi.setText(service);
        break;
      case "BigRigFriendly":
        View mOverlanderBigrigfriendly;
        TextView mTvOverlanderBigrigfriendly;
        mOverlanderBigrigfriendly = findViewById(R.id.ll__place_overlander_bigrigfriendly);
        mTvOverlanderBigrigfriendly = findViewById(R.id.tv__place_overlander_bigrigfriendly);
        mOverlanderBigrigfriendly.setVisibility(View.VISIBLE);
        mTvOverlanderBigrigfriendly.setText(service);
        break;
    }
  }

  private void refreshOverlanderActivities(String activity)
  {
    switch (activity)
    {
      case "Tourism":
        View mOverlanderTourism;
        TextView mTvOverlanderTourism;
        mOverlanderTourism = findViewById(R.id.ll__place_overlander_tourism);
        mTvOverlanderTourism = findViewById(R.id.tv__place_overlander_tourism);
        mOverlanderTourism.setVisibility(View.VISIBLE);
        mTvOverlanderTourism.setText(activity);
        break;
      case "Climbing":
        View mOverlanderClimbing;
        TextView mTvOverlanderClimbing;
        mOverlanderClimbing = findViewById(R.id.ll__place_overlander_climbing);
        mTvOverlanderClimbing = findViewById(R.id.tv__place_overlander_climbing);
        mOverlanderClimbing.setVisibility(View.VISIBLE);
        mTvOverlanderClimbing.setText(activity);
        break;
      case "Fishing":
        View mOverlanderFishing;
        TextView mTvOverlanderFishing;
        mOverlanderFishing = findViewById(R.id.ll__place_overlander_fishing);
        mTvOverlanderFishing = findViewById(R.id.tv__place_overlander_fishing);
        mOverlanderFishing.setVisibility(View.VISIBLE);
        mTvOverlanderFishing.setText(activity);
        break;
      case "Hiking":
        View mOverlanderHiking;
        TextView mTvOverlanderHiking;
        mOverlanderHiking = findViewById(R.id.ll__place_overlander_hiking);
        mTvOverlanderHiking = findViewById(R.id.tv__place_overlander_hiking);
        mOverlanderHiking.setVisibility(View.VISIBLE);
        mTvOverlanderHiking.setText(activity);
        break;
      case "Kayak":
        View mOverlanderKayak;
        TextView mTvOverlanderKayak;
        mOverlanderKayak = findViewById(R.id.ll__place_overlander_kayak);
        mTvOverlanderKayak = findViewById(R.id.tv__place_overlander_kayak);
        mOverlanderKayak.setVisibility(View.VISIBLE);
        mTvOverlanderKayak.setText(activity);
        break;
      case "Motorcycle":
        View mOverlanderMotorcycle;
        TextView mTvOverlanderMotorcycle;
        mOverlanderMotorcycle = findViewById(R.id.ll__place_overlander_motorcycle);
        mTvOverlanderMotorcycle = findViewById(R.id.tv__place_overlander_motorcycle);
        mOverlanderMotorcycle.setVisibility(View.VISIBLE);
        mTvOverlanderMotorcycle.setText(activity);
        break;
      case "Mountainbike":
        View mOverlanderMountainbike;
        TextView mTvOverlanderMountainbike;
        mOverlanderMountainbike = findViewById(R.id.ll__place_overlander_mountainbike);
        mTvOverlanderMountainbike = findViewById(R.id.tv__place_overlander_mountainbike);
        mOverlanderMountainbike.setVisibility(View.VISIBLE);
        mTvOverlanderMountainbike.setText(activity);
        break;
      case "Playground":
        View mOverlanderPlayground;
        TextView mTvOverlanderPlayground;
        mOverlanderPlayground = findViewById(R.id.ll__place_overlander_playground);
        mTvOverlanderPlayground = findViewById(R.id.tv__place_overlander_playground);
        mOverlanderPlayground.setVisibility(View.VISIBLE);
        mTvOverlanderPlayground.setText(activity);
        break;
      case "Swimming":
        View mOverlanderSwimming;
        TextView mTvOverlanderSwimming;
        mOverlanderSwimming = findViewById(R.id.ll__place_overlander_swimming);
        mTvOverlanderSwimming = findViewById(R.id.tv__place_overlander_swimming);
        mOverlanderSwimming.setVisibility(View.VISIBLE);
        mTvOverlanderSwimming.setText(activity);
        break;
      case "Surfing":
        View mOverlanderSurfing;
        TextView mTvOverlanderSurfing;
        mOverlanderSurfing = findViewById(R.id.ll__place_overlander_surfing);
        mTvOverlanderSurfing = findViewById(R.id.tv__place_overlander_surfing);
        mOverlanderSurfing.setVisibility(View.VISIBLE);
        mTvOverlanderSurfing.setText(activity);
        break;
      case "BeachFishing":
        View mOverlanderBeachfishing;
        TextView mTvOverlanderBeachfishing;
        mOverlanderBeachfishing = findViewById(R.id.ll__place_overlander_beachfishing);
        mTvOverlanderBeachfishing = findViewById(R.id.tv__place_overlander_beachfishing);
        mOverlanderBeachfishing.setVisibility(View.VISIBLE);
        mTvOverlanderBeachfishing.setText(activity);
        break;
    }
  }

  private static void refreshMetadataOrHide(String metadata, View metaLayout, TextView metaTv)
  {
    if (!TextUtils.isEmpty(metadata))
    {
      metaLayout.setVisibility(View.VISIBLE);
      if (metaTv != null)
        metaTv.setText(metadata);
    }
    else
      metaLayout.setVisibility(View.GONE);
  }

  public void refreshAzimuth(double northAzimuth)
  {
    if (mMapObject == null || MapObject.isOfType(MapObject.MY_POSITION, mMapObject))
      return;

    final Location location = LocationHelper.INSTANCE.getSavedLocation();
    if (location == null)
      return;

    final double azimuth = Framework.nativeGetDistanceAndAzimuthFromLatLon(mMapObject.getLat(),
                                                                           mMapObject.getLon(),
                                                                           location.getLatitude(),
                                                                           location.getLongitude(),
                                                                           northAzimuth).getAzimuth();
    if (azimuth >= 0)
    {
      UiUtils.show(mAvDirection);
      mAvDirection.setAzimuth(azimuth);
    }
  }

  private void addOrganisation()
  {
    getActivity().showPositionChooser(true, false);
  }

  private void addPlace()
  {
    getActivity().showPositionChooser(false, true);
  }

  @Override
  public void onClick(View v)
  {
    switch (v.getId())
    {
      case R.id.tv__title:
      case R.id.tv__secondary_title:
      case R.id.tv__address:
        // A workaround to make single taps toggle the bottom sheet.
        if (mPlacePageGestureListener != null)
        {
          mPlacePageGestureListener.onSingleTapConfirmed(null);
        }
        break;
      case R.id.ll__place_editor:
        if (mMapObject == null)
        {
          LOGGER.e(TAG, "Cannot start editor, map object is null!");
          break;
        }
        getActivity().showEditor();
        break;
      case R.id.ll__add_organisation:
        addOrganisation();
        break;
      case R.id.ll__place_add:
        addPlace();
        break;
      case R.id.ll__place_latlon:
        final int formatIndex = visibleCoordsFormat.indexOf(mCoordsFormat);
        mCoordsFormat = visibleCoordsFormat.get((formatIndex + 1) % visibleCoordsFormat.size());
        MwmApplication.prefs(getContext()).edit().putInt(PREF_COORDINATES_FORMAT, mCoordsFormat.getId()).apply();
        if (mMapObject == null)
        {
          LOGGER.e(TAG, "A LatLon cannot be refreshed, mMapObject is null");
          break;
        }
        refreshLatLon(mMapObject);
        break;
      case R.id.ll__place_website:
        Utils.openUrl(getContext(), mTvWebsite.getText().toString());
        break;
      case R.id.ll__place_facebook:
        final String facebookPage = mMapObject.getMetadata(Metadata.MetadataType.FMD_CONTACT_FACEBOOK);
        Utils.openUrl(getContext(), "https://m.facebook.com/"+facebookPage);
        break;
      case R.id.ll__place_instagram:
        final String instagramPage = mMapObject.getMetadata(Metadata.MetadataType.FMD_CONTACT_INSTAGRAM);
        Utils.openUrl(getContext(), "https://instagram.com/"+instagramPage);
        break;
      case R.id.ll__place_twitter:
        final String twitterPage = mMapObject.getMetadata(Metadata.MetadataType.FMD_CONTACT_TWITTER);
        Utils.openUrl(getContext(), "https://mobile.twitter.com/"+twitterPage);
        break;
      case R.id.ll__place_vk:
        final String vkPage = mMapObject.getMetadata(Metadata.MetadataType.FMD_CONTACT_VK);
        Utils.openUrl(getContext(), "https://vk.com/" + vkPage);
        break;
      case R.id.ll__place_line:
        final String linePage = mMapObject.getMetadata(Metadata.MetadataType.FMD_CONTACT_LINE);
        if (linePage.indexOf('/') >= 0)
          Utils.openUrl(getContext(), "https://" + linePage);
        else
          Utils.openUrl(getContext(), "https://line.me/R/ti/p/@" + linePage);
        break;
      case R.id.ll__place_wiki:
        // TODO: Refactor and use separate getters for Wiki and all other PP meta info too.
        if (mMapObject == null)
        {
          LOGGER.e(TAG, "Cannot follow url, mMapObject is null!");
          break;
        }
        Utils.openUrl(getContext(), mMapObject.getMetadata(Metadata.MetadataType.FMD_WIKIPEDIA));
        break;
      case R.id.direction_frame:
        showBigDirection();
        break;
      case R.id.ll__place_email:
        Utils.sendTo(getContext(), mTvEmail.getText().toString());
        break;
    }
  }

  private void toggleIsBookmark(@NonNull MapObject mapObject)
  {
    if (MapObject.isOfType(MapObject.BOOKMARK, mapObject))
      setMapObject(Framework.nativeDeleteBookmarkFromMapObject(), null);
    else
      setMapObject(BookmarkManager.INSTANCE.addNewBookmark(mapObject.getLat(), mapObject.getLon()), null);
  }

  private void showBigDirection()
  {
    final DirectionFragment fragment = (DirectionFragment) Fragment.instantiate(getActivity(), DirectionFragment.class
        .getName(), null);
    fragment.setMapObject(mMapObject);
    fragment.show(getActivity().getSupportFragmentManager(), null);
  }

  @Override
  public boolean onLongClick(View v)
  {
    final Object tag = v.getTag();

    final List<String> items = new ArrayList<>();
    switch (v.getId())
    {
      case R.id.tv__title:
        items.add(mTvTitle.getText().toString());
        break;
      case R.id.tv__secondary_title:
        items.add(mTvSecondaryTitle.getText().toString());
        break;
      case R.id.tv__address:
        items.add(mTvAddress.getText().toString());
        break;
      case R.id.tv__bookmark_notes:
        items.add(mTvBookmarkNote.getText().toString());
        break;
      case R.id.poi_description:
        items.add(mPlaceDescriptionView.getText().toString());
        break;
      case R.id.ll__place_latlon:
        if (mMapObject == null)
        {
          LOGGER.e(TAG, "A long click tap on LatLon cannot be handled, mMapObject is null!");
          break;
        }
        final double lat = mMapObject.getLat();
        final double lon = mMapObject.getLon();
        for(CoordinatesFormat format: visibleCoordsFormat)
          items.add(Framework.nativeFormatLatLon(lat, lon, format.getId()));
        break;
      case R.id.ll__place_website:
        items.add(mTvWebsite.getText().toString());
        break;
      case R.id.ll__place_facebook:
        final String facebookPage = mMapObject.getMetadata(Metadata.MetadataType.FMD_CONTACT_FACEBOOK);
        if (facebookPage.indexOf('/') == -1)
          items.add(facebookPage); // Show username along with URL.
        items.add("https://m.facebook.com/" + facebookPage);
        break;
      case R.id.ll__place_instagram:
        final String instagramPage = mMapObject.getMetadata(Metadata.MetadataType.FMD_CONTACT_INSTAGRAM);
        if (instagramPage.indexOf('/') == -1)
          items.add(instagramPage); // Show username along with URL.
        items.add("https://instagram.com/" + instagramPage);
        break;
      case R.id.ll__place_twitter:
        final String twitterPage = mMapObject.getMetadata(Metadata.MetadataType.FMD_CONTACT_TWITTER);
        if (twitterPage.indexOf('/') == -1)
          items.add(twitterPage); // Show username along with URL.
        items.add("https://mobile.twitter.com/" + twitterPage);
        break;
      case R.id.ll__place_vk:
        final String vkPage = mMapObject.getMetadata(Metadata.MetadataType.FMD_CONTACT_VK);
        if (vkPage.indexOf('/') == -1)
          items.add(vkPage); // Show username along with URL.
        items.add("https://vk.com/" + vkPage);
        break;
      case R.id.ll__place_line:
        final String linePage = mMapObject.getMetadata(Metadata.MetadataType.FMD_CONTACT_LINE);
        if (linePage.indexOf('/') >= 0)
          items.add("https://" + linePage);
        else
        {
          items.add(linePage); // Show username along with URL.
          items.add("https://line.me/R/ti/p/@" + linePage);
        }
        break;
      case R.id.ll__place_email:
        items.add(mTvEmail.getText().toString());
        break;
      case R.id.ll__place_schedule:
        final String ohStr = mMapObject.getMetadata(Metadata.MetadataType.FMD_OPEN_HOURS);
        final Timetable[] timetables = OpeningHours.nativeTimetablesFromString(ohStr);
        items.add(TimeFormatUtils.formatTimetables(getResources(), ohStr, timetables));
        break;
      case R.id.ll__place_operator:
        items.add(mTvOperator.getText().toString());
        break;
      case R.id.ll__place_wiki:
        items.add(mMapObject.getMetadata(Metadata.MetadataType.FMD_WIKIPEDIA));
        break;
    }

    final Context ctx = getContext();
    if (items.size() == 1)
    {
      Utils.copyTextToClipboard(ctx, items.get(0));
      Utils.showSnackbarAbove(mDetails,
                              getRootView().findViewById(R.id.menu_frame),
                              ctx.getString(R.string.copied_to_clipboard, items.get(0)));
    }
    else
    {
      final PopupMenu popup = new PopupMenu(getContext(), v);
      final Menu menu = popup.getMenu();
      final String copyText = getResources().getString(android.R.string.copy);

      for (int i = 0; i < items.size(); i++)
        menu.add(Menu.NONE, i, i, String.format("%s %s", copyText, items.get(i)));

      popup.setOnMenuItemClickListener(item -> {
        final int id = item.getItemId();
        Utils.copyTextToClipboard(ctx, items.get(id));
        Utils.showSnackbarAbove(mDetails,
                                getRootView().findViewById(R.id.menu_frame),
                                ctx.getString(R.string.copied_to_clipboard, items.get(id)));
        return true;
      });
      popup.show();
    }

    return true;
  }

  private void close()
  {
    if (mClosable != null)
      mClosable.closePlacePage();
  }

  void reset()
  {
    resetScroll();
    detachCountry();
  }

  void resetScroll()
  {
    scrollTo(0, 0);
  }

  private static boolean isInvalidDownloaderStatus(int status)
  {
    return (status != CountryItem.STATUS_DOWNLOADABLE &&
            status != CountryItem.STATUS_ENQUEUED &&
            status != CountryItem.STATUS_FAILED &&
            status != CountryItem.STATUS_PARTLY &&
            status != CountryItem.STATUS_PROGRESS &&
            status != CountryItem.STATUS_APPLYING);
  }

  private void updateDownloader(CountryItem country)
  {
    if (isInvalidDownloaderStatus(country.status))
    {
      if (mStorageCallbackSlot != 0)
        UiThread.runLater(mDownloaderDeferredDetachProc);
      return;
    }

    mDownloaderIcon.update(country);

    StringBuilder sb = new StringBuilder(StringUtils.getFileSizeString(getContext(), country.totalSize));
    if (country.isExpandable())
      sb.append(String.format(Locale.US, "  •  %s: %d", getContext().getString(R.string.downloader_status_maps), country.totalChildCount));

    mDownloaderInfo.setText(sb.toString());
  }

  private void updateDownloader()
  {
    if (mCurrentCountry == null)
      return;

    mCurrentCountry.update();
    updateDownloader(mCurrentCountry);
  }

  private void attachCountry(String country)
  {
    CountryItem map = CountryItem.fill(country);
    if (isInvalidDownloaderStatus(map.status))
      return;

    mCurrentCountry = map;
    if (mStorageCallbackSlot == 0)
      mStorageCallbackSlot = MapManager.nativeSubscribe(mStorageCallback);

    mDownloaderIcon.setOnIconClickListener(mDownloadClickListener)
                   .setOnCancelClickListener(mCancelDownloadListener);
    mDownloaderIcon.show(true);
    UiUtils.show(mDownloaderInfo);
    updateDownloader(mCurrentCountry);
  }

  private void detachCountry()
  {
    if (mStorageCallbackSlot == 0)
      return;

    MapManager.nativeUnsubscribe(mStorageCallbackSlot);
    mStorageCallbackSlot = 0;
    mCurrentCountry = null;
    mDownloaderIcon.setOnIconClickListener(null)
                   .setOnCancelClickListener(null);
    mDownloaderIcon.show(false);
    UiUtils.hide(mDownloaderInfo);
  }

  MwmActivity getActivity()
  {
    return (MwmActivity) getContext();
  }

  @Override
  public void onBookmarkSaved(long bookmarkId, boolean movedFromCategory)
  {
    Bookmark updatedBookmark = BookmarkManager.INSTANCE.updateBookmarkPlacePage(bookmarkId);
    if (updatedBookmark == null)
      return;

    setMapObject(updatedBookmark, null);
    refreshViews();
  }

  int getPreviewHeight()
  {
    return mPreview.getHeight();
  }

  private class EditBookmarkClickListener implements OnClickListener
  {
    @Override
    public void onClick(View v)
    {
      if (mMapObject == null)
      {
        LOGGER.e(TAG, "A bookmark cannot be edited, mMapObject is null!");
        return;
      }
      Bookmark bookmark = (Bookmark) mMapObject;
      EditBookmarkFragment.editBookmark(bookmark.getCategoryId(),
                                        bookmark.getBookmarkId(),
                                        getActivity(),
                                        getActivity().getSupportFragmentManager(),
                                        PlacePageView.this);
    }
  }
}
