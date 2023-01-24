
package image.gallery.organize.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdsInfo {
    private Integer AppOpen;
    private Boolean LoaderNativeOnOff;
    private String GoogleBannerAds;
    private String GoogleInterAds;
    private String GoogleNativeAds;
    private String GoogleAppOpenAds;
    private String GoogleAppIdAds;
    private Boolean AdsOnOff;
    private Boolean GoogleAdsOnOff;
    private Boolean GoogleSplashOpenAdsOnOff;
    private Boolean GoogleExitSplashInterOnOff;
    private Boolean GoogleAppOpenAdsOnOff;
    private Boolean GoogleBannerOnOff;
    private Integer BannerAdWhichOne;
    private Integer IntervalCount;
    private Integer BackIntervalCount;
    private Boolean GoogleInterOnOff;
    private Boolean GoogleBackInterOnOff;
    private Boolean GoogleMiniNativeOnOff;
    private Boolean GoogleLargeNativeOnOff;
    private Boolean GoogleListNativeOnOff;
    private Integer ListNativeWhichOne;
    private Integer ListNativeAfterCount;
    private Boolean ShowDialogBeforeAds;
    private Integer DialogTimeInSec;

    public AdsInfo() {
    }

    public AdsInfo(Integer appOpen, Boolean loaderNativeOnOff, String googleBannerAds, String googleInterAds, String googleNativeAds, String googleAppOpenAds, String googleAppIdAds, Boolean adsOnOff, Boolean googleAdsOnOff, Boolean googleSplashOpenAdsOnOff, Boolean googleExitSplashInterOnOff, Boolean googleAppOpenAdsOnOff, Boolean googleBannerOnOff, Integer bannerAdWhichOne, Integer intervalCount, Integer backIntervalCount, Boolean googleInterOnOff, Boolean googleBackInterOnOff, Boolean googleMiniNativeOnOff, Boolean googleLargeNativeOnOff, Boolean googleListNativeOnOff, Integer listNativeWhichOne, Integer listNativeAfterCount, Boolean showDialogBeforeAds, Integer dialogTimeInSec) {
        AppOpen = appOpen;
        LoaderNativeOnOff = loaderNativeOnOff;
        GoogleBannerAds = googleBannerAds;
        GoogleInterAds = googleInterAds;
        GoogleNativeAds = googleNativeAds;
        GoogleAppOpenAds = googleAppOpenAds;
        GoogleAppIdAds = googleAppIdAds;
        AdsOnOff = adsOnOff;
        GoogleAdsOnOff = googleAdsOnOff;
        GoogleSplashOpenAdsOnOff = googleSplashOpenAdsOnOff;
        GoogleExitSplashInterOnOff = googleExitSplashInterOnOff;
        GoogleAppOpenAdsOnOff = googleAppOpenAdsOnOff;
        GoogleBannerOnOff = googleBannerOnOff;
        BannerAdWhichOne = bannerAdWhichOne;
        IntervalCount = intervalCount;
        BackIntervalCount = backIntervalCount;
        GoogleInterOnOff = googleInterOnOff;
        GoogleBackInterOnOff = googleBackInterOnOff;
        GoogleMiniNativeOnOff = googleMiniNativeOnOff;
        GoogleLargeNativeOnOff = googleLargeNativeOnOff;
        GoogleListNativeOnOff = googleListNativeOnOff;
        ListNativeWhichOne = listNativeWhichOne;
        ListNativeAfterCount = listNativeAfterCount;
        ShowDialogBeforeAds = showDialogBeforeAds;
        DialogTimeInSec = dialogTimeInSec;
    }

    public Integer getAppOpen() {
        return AppOpen;
    }

    public void setAppOpen(Integer appOpen) {
        AppOpen = appOpen;
    }

    public Boolean getLoaderNativeOnOff() {
        return LoaderNativeOnOff;
    }

    public void setLoaderNativeOnOff(Boolean loaderNativeOnOff) {
        LoaderNativeOnOff = loaderNativeOnOff;
    }

    public String getGoogleBannerAds() {
        return GoogleBannerAds;
    }

    public void setGoogleBannerAds(String googleBannerAds) {
        GoogleBannerAds = googleBannerAds;
    }

    public String getGoogleInterAds() {
        return GoogleInterAds;
    }

    public void setGoogleInterAds(String googleInterAds) {
        GoogleInterAds = googleInterAds;
    }

    public String getGoogleNativeAds() {
        return GoogleNativeAds;
    }

    public void setGoogleNativeAds(String googleNativeAds) {
        GoogleNativeAds = googleNativeAds;
    }

    public String getGoogleAppOpenAds() {
        return GoogleAppOpenAds;
    }

    public void setGoogleAppOpenAds(String googleAppOpenAds) {
        GoogleAppOpenAds = googleAppOpenAds;
    }

    public String getGoogleAppIdAds() {
        return GoogleAppIdAds;
    }

    public void setGoogleAppIdAds(String googleAppIdAds) {
        GoogleAppIdAds = googleAppIdAds;
    }

    public Boolean getAdsOnOff() {
        return AdsOnOff;
    }

    public void setAdsOnOff(Boolean adsOnOff) {
        AdsOnOff = adsOnOff;
    }

    public Boolean getGoogleAdsOnOff() {
        return GoogleAdsOnOff;
    }

    public void setGoogleAdsOnOff(Boolean googleAdsOnOff) {
        GoogleAdsOnOff = googleAdsOnOff;
    }

    public Boolean getGoogleSplashOpenAdsOnOff() {
        return GoogleSplashOpenAdsOnOff;
    }

    public void setGoogleSplashOpenAdsOnOff(Boolean googleSplashOpenAdsOnOff) {
        GoogleSplashOpenAdsOnOff = googleSplashOpenAdsOnOff;
    }

    public Boolean getGoogleExitSplashInterOnOff() {
        return GoogleExitSplashInterOnOff;
    }

    public void setGoogleExitSplashInterOnOff(Boolean googleExitSplashInterOnOff) {
        GoogleExitSplashInterOnOff = googleExitSplashInterOnOff;
    }

    public Boolean getGoogleAppOpenAdsOnOff() {
        return GoogleAppOpenAdsOnOff;
    }

    public void setGoogleAppOpenAdsOnOff(Boolean googleAppOpenAdsOnOff) {
        GoogleAppOpenAdsOnOff = googleAppOpenAdsOnOff;
    }

    public Boolean getGoogleBannerOnOff() {
        return GoogleBannerOnOff;
    }

    public void setGoogleBannerOnOff(Boolean googleBannerOnOff) {
        GoogleBannerOnOff = googleBannerOnOff;
    }

    public Integer getBannerAdWhichOne() {
        return BannerAdWhichOne;
    }

    public void setBannerAdWhichOne(Integer bannerAdWhichOne) {
        BannerAdWhichOne = bannerAdWhichOne;
    }

    public Integer getIntervalCount() {
        return IntervalCount;
    }

    public void setIntervalCount(Integer intervalCount) {
        IntervalCount = intervalCount;
    }

    public Integer getBackIntervalCount() {
        return BackIntervalCount;
    }

    public void setBackIntervalCount(Integer backIntervalCount) {
        BackIntervalCount = backIntervalCount;
    }

    public Boolean getGoogleInterOnOff() {
        return GoogleInterOnOff;
    }

    public void setGoogleInterOnOff(Boolean googleInterOnOff) {
        GoogleInterOnOff = googleInterOnOff;
    }

    public Boolean getGoogleBackInterOnOff() {
        return GoogleBackInterOnOff;
    }

    public void setGoogleBackInterOnOff(Boolean googleBackInterOnOff) {
        GoogleBackInterOnOff = googleBackInterOnOff;
    }

    public Boolean getGoogleMiniNativeOnOff() {
        return GoogleMiniNativeOnOff;
    }

    public void setGoogleMiniNativeOnOff(Boolean googleMiniNativeOnOff) {
        GoogleMiniNativeOnOff = googleMiniNativeOnOff;
    }

    public Boolean getGoogleLargeNativeOnOff() {
        return GoogleLargeNativeOnOff;
    }

    public void setGoogleLargeNativeOnOff(Boolean googleLargeNativeOnOff) {
        GoogleLargeNativeOnOff = googleLargeNativeOnOff;
    }

    public Boolean getGoogleListNativeOnOff() {
        return GoogleListNativeOnOff;
    }

    public void setGoogleListNativeOnOff(Boolean googleListNativeOnOff) {
        GoogleListNativeOnOff = googleListNativeOnOff;
    }

    public Integer getListNativeWhichOne() {
        return ListNativeWhichOne;
    }

    public void setListNativeWhichOne(Integer listNativeWhichOne) {
        ListNativeWhichOne = listNativeWhichOne;
    }

    public Integer getListNativeAfterCount() {
        return ListNativeAfterCount;
    }

    public void setListNativeAfterCount(Integer listNativeAfterCount) {
        ListNativeAfterCount = listNativeAfterCount;
    }

    public Boolean getShowDialogBeforeAds() {
        return ShowDialogBeforeAds;
    }

    public void setShowDialogBeforeAds(Boolean showDialogBeforeAds) {
        ShowDialogBeforeAds = showDialogBeforeAds;
    }

    public Integer getDialogTimeInSec() {
        return DialogTimeInSec;
    }

    public void setDialogTimeInSec(Integer dialogTimeInSec) {
        DialogTimeInSec = dialogTimeInSec;
    }
}
