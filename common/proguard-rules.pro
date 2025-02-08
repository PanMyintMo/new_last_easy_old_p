# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/macpro/Library/Android/sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#忽略警告
-dontwarn com.project.codeinstallsdk.**
#避免混淆
-keep public class com.project.codeinstallsdk.CodeInstall{*;}
-keep public class com.project.codeinstallsdk.model.ConfigInfo**{*;}
-keep public class com.project.codeinstallsdk.model.Rouse**{*;}
-keep public class com.project.codeinstallsdk.model.Transfer**{*;}
-keep public class com.project.codeinstallsdk.inter.ConfigCallBack{* ;}
-keep public class com.project.codeinstallsdk.inter.ReceiptCallBack{* ;}
-keep public class com.project.codeinstallsdk.Version{* ;}
