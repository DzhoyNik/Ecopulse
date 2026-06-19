# ============================================================
#  EcoPulse — правила R8/ProGuard для release-сборки
# ============================================================

# --- Crashlytics: сохраняем имена файлов и номера строк для читаемых стектрейсов ---
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# --- kotlinx.serialization (используется в type-safe навигации, Screen.kt) ---
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class **$$serializer { *; }
-keepclasseswithmembers class * {
    @kotlinx.serialization.Serializable <methods>;
}
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

# --- Firebase Firestore: модели данных мапятся рефлексией (см. data/consumer-rules.pro) ---
# Дополнительные правила Firestore/Crashlytics/Gemini поставляются самими библиотеками.

# --- Сохраняем сообщения об исключениях ---
-keepattributes Signature,Exceptions
