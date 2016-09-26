# Needed for Reflection
-keepattributes Signature,*Annotation*
-keepclassmembers,allowobfuscation class * {
    @io.freefair.android.injection.annotation.* *;
}
-keepclassmembers,allowobfuscation class * {
    @io.freefair.injection.annotation.* *;
}

-dontwarn org.slf4j.LoggerFactory
-dontwarn org.slf4j.MDC
-dontwarn org.slf4j.MarkerFactory
