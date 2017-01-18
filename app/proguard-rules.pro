# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /data/android-sdk-linux/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#-dontwarn com.journeyapps.**

# warings off
-dontwarn rx.internal.util.unsafe.**
-dontwarn java.nio.file.**
-dontwarn org.codehaus.mojo.animal_sniffer.**
-dontwarn com.facebook.**
-dontwarn kotlin.collections.**


#-dontwarn com.google.android.gms.**
#-dontwarn android.support.v4.**
#
## google support lib
#-keep class android.support.v4.** { *; }
#-keep interface android.support.v4.** { *; }
#
#-keep class android.support.v7.** { *; }
#-keep interface android.support.v7.** { *; }
#
## from Intgerner
#-keepattributes Exceptions, InnerClasses, *Annotation*, EnclosingMethod
#-keepattributes Signature
#-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.examples.android.model.** { *; }
#
## own
#-keep class org.json.** { *; }
#-keep interface org.json.** { *; }
#
## google billing
#-keep class com.android.vending.billing.**
#
## google play services
#-keep class * extends java.util.ListResourceBundle {
#    protected Object[][] getContents();
#}
#
#-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
#    public static final *** NULL;
#}
#
#-keepnames @com.google.android.gms.common.annotation.KeepName class *
#-keepclassmembernames class * {
#    @com.google.android.gms.common.annotation.KeepName *;
#}
#
#-keepnames class * implements android.os.Parcelable {
#    public static final ** CREATOR;
#}
#
## OkHttp
#-keepattributes Signature
#-keepattributes *Annotation*
#-keep class okhttp3.** { *; }
#-keep interface okhttp3.** { *; }
#-dontwarn okhttp3.**
#-dontwarn okio.**