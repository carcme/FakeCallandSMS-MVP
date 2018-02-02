-keep class butterknife.**$Finder { *; }
-keep class **$$ViewInjector { *; }
-keep class **$$ViewBinder { *; }

-keep public class net.frakbot.glowpadbackport.** {public private protected *;}
-dontwarn net.frakbot.glowpadbackport.**

-dontwarn android.arch.persistence.room.paging.LimitOffsetDataSource

# Crashlytics
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception


