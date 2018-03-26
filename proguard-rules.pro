# Needed for Reflection
-keepattributes Signature,*Annotation*
-keepclassmembers,allowobfuscation class * {
    @io.freefair.android.injection.annotation.* *;
}
