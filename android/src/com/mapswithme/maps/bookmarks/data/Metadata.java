package com.mapswithme.maps.bookmarks.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Metadata implements Parcelable
{
  // Values must correspond to definitions from feature_meta.hpp.
  public enum MetadataType
  {
    FMD_CUISINE(1),
    FMD_OPEN_HOURS(2),
    FMD_PHONE_NUMBER(3),
    FMD_FAX_NUMBER(4),
    FMD_STARS(5),
    FMD_OPERATOR(6),
    FMD_URL(7),
    FMD_WEBSITE(8),
    FMD_INTERNET(9),
    FMD_ELE(10),
    FMD_TURN_LANES(11),
    FMD_TURN_LANES_FORWARD(12),
    FMD_TURN_LANES_BACKWARD(13),
    FMD_EMAIL(14),
    FMD_POSTCODE(15),
    // TODO: It is hacked in jni and returns full Wikipedia url. Should use separate getter instead.
    FMD_WIKIPEDIA(16),
    // FMD_MAXSPEED(17),
    FMD_FLATS(18),
    FMD_HEIGHT(19),
    FMD_MIN_HEIGHT(20),
    FMD_DENOMINATION(21),
    FMD_BUILDING_LEVELS(22),
    FWD_TEST_ID(23),
    // FMD_SPONSORED_ID(24),
    // FMD_PRICE_RATE(25),
    // FMD_RATING(26),
    // FMD_BANNER_URL(27),
    FMD_LEVEL(28),
    FMD_AIRPORT_IATA(29),
    FMD_BRAND(30),
    FMD_DURATION(31),
    FMD_CONTACT_FACEBOOK(32),
    FMD_CONTACT_INSTAGRAM(33),
    FMD_CONTACT_TWITTER(34),
    FMD_CONTACT_VK(35),
    FMD_CONTACT_LINE(36),
    FMD_OVERLANDER_LOCATION(37),
    FMD_OVERLANDER_DESCRIPTION(38),
    FMD_OVERLANDER_OPEN(39),
    FMD_OVERLANDER_ELECTRICITY(40),
    FMD_OVERLANDER_WIFI(41),
    FMD_OVERLANDER_KITCHEN(42),
    FMD_OVERLANDER_PARKING(43),
    FMD_OVERLANDER_RESTAURANT(44),
    FMD_OVERLANDER_SHOWERS(45),
    FMD_OVERLANDER_WATER(46),
    FMD_OVERLANDER_TOILETS(47),
    FMD_OVERLANDER_BIGRIGFRIENDLY(48),
    FMD_OVERLANDER_TENTFRIENDLY(49),
    FMD_OVERLANDER_PETFRIENDLY(50),
    FMD_OVERLANDER_SANIDUMP(51),
    FMD_OVERLANDER_BIOFUEL(52),
    FMD_OVERLANDER_REPAIRSVEHICLES(53),
    FMD_OVERLANDER_REPAIRSMOTORCYCLES(54),
    FMD_OVERLANDER_REPAIRSBICYCLES(55),
    FMD_OVERLANDER_SELLSPARTS(56),
    FMD_OVERLANDER_RECYCLESBATTERIES(57),
    FMD_OVERLANDER_RECYCLESOIL(58),
    FMD_OVERLANDER_CHECKIN5ID(59),
    FMD_OVERLANDER_CHECKIN5TIME(60),
    FMD_OVERLANDER_CHECKIN5AUTHOR(61),
    FMD_OVERLANDER_CHECKIN5COMMENT(62),
    FMD_OVERLANDER_CHECKIN4ID(63),
    FMD_OVERLANDER_CHECKIN4TIME(64),
    FMD_OVERLANDER_CHECKIN4AUTHOR(65),
    FMD_OVERLANDER_CHECKIN4COMMENT(66),
    FMD_OVERLANDER_CHECKIN3ID(67),
    FMD_OVERLANDER_CHECKIN3TIME(68),
    FMD_OVERLANDER_CHECKIN3AUTHOR(69),
    FMD_OVERLANDER_CHECKIN3COMMENT(70),
    FMD_OVERLANDER_CHECKIN2ID(71),
    FMD_OVERLANDER_CHECKIN2TIME(72),
    FMD_OVERLANDER_CHECKIN2AUTHOR(73),
    FMD_OVERLANDER_CHECKIN2COMMENT(74),
    FMD_OVERLANDER_CHECKIN1ID(75),
    FMD_OVERLANDER_CHECKIN1TIME(76),
    FMD_OVERLANDER_CHECKIN1AUTHOR(77);
    private final int mMetaType;

    MetadataType(int metadataType)
    {
      mMetaType = metadataType;
    }

    @NonNull
    public static MetadataType fromInt(@IntRange(from = 1, to = 28) int metaType)
    {
      for (MetadataType type : values())
        if (type.mMetaType == metaType)
          return type;

      throw new IllegalArgumentException("Illegal metaType arg!");
    }

    public int toInt()
    {
      return mMetaType;
    }
  }

  private final Map<MetadataType, String> mMetadataMap = new HashMap<>();

  /**
   * Adds metadata with type code and value. Returns false if metaType is wrong or unknown
   *
   * @return true, if metadata was added, false otherwise
   */
  boolean addMetadata(int metaType, String metaValue)
  {
    final MetadataType type = MetadataType.fromInt(metaType);
    mMetadataMap.put(type, metaValue);
    return true;
  }

  /**
   * Adds metadata with type and value.
   *
   * @return true, if metadata was added, false otherwise
   */
  public boolean addMetadata(MetadataType type, String value)
  {
    mMetadataMap.put(type, value);
    return true;
  }

  @Nullable
  String getMetadata(MetadataType type)
  {
    return mMetadataMap.get(type);
  }

  boolean isEmpty()
  {
    return mMetadataMap.isEmpty();
  }

  @Override
  public int describeContents()
  {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeInt(mMetadataMap.size());
    for (Map.Entry<MetadataType, String> metaEntry : mMetadataMap.entrySet())
    {
      dest.writeInt(metaEntry.getKey().mMetaType);
      dest.writeString(metaEntry.getValue());
    }
  }

  public static Metadata readFromParcel(Parcel source)
  {
    final Metadata metadata = new Metadata();
    final int size = source.readInt();
    for (int i = 0; i < size; i++)
      metadata.addMetadata(source.readInt(), source.readString());
    return metadata;
  }

  public static final Creator<Metadata> CREATOR = new Creator<Metadata>()
  {
    @Override
    public Metadata createFromParcel(Parcel source)
    {
      return readFromParcel(source);
    }

    @Override
    public Metadata[] newArray(int size)
    {
      return new Metadata[size];
    }
  };
}
