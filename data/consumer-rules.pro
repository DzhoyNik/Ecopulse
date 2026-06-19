# Firestore десериализует документы в эти классы через рефлексию
# (snapshot.toObject(...) + аннотации @PropertyName).
# R8 в release-сборке :app не должен удалять или переименовывать их поля.
-keep class com.example.ecopulse.data.model.** { *; }
-keepclassmembers class com.example.ecopulse.data.model.** {
    <init>(...);
    <fields>;
    <methods>;
}
