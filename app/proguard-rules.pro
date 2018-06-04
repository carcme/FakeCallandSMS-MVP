-keep class butterknife.**$Finder { *; }
-keep class **$$ViewInjector { *; }
-keep class **$$ViewBinder { *; }

-keep public class net.frakbot.glowpadbackport.** {public private protected *;}
-dontwarn net.frakbot.glowpadbackport.**

-dontwarn android.arch.persistence.room.paging.LimitOffsetDataSource


-keep class com.squareup.okhttp.** { *; }
-keep class retrofit.** { *; }
-keep interface com.squareup.okhttp.** { *; }

-dontwarn com.squareup.okhttp.**
-dontwarn okio.**
-dontwarn retrofit.**
-dontwarn rx.**

-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

# If in your rest service interface you use methods with Callback argument.
-keepattributes Exceptions

# If your rest service methods throw custom exceptions, because you've defined an ErrorHandler.
-keepattributes Signature

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
-keepclassmembers class com.yourcompany.models.** { *; }


-keep class public
-keep class android.support.v8.renderscript.** { *; }

# Crashlytics
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception


