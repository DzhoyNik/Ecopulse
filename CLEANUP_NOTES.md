# EcoPulse — чистка под release

Архив содержит модули `app/`, `data/`, `domain/` и корневые gradle-файлы в очищенном виде.
Папку `gradle/` (wrapper + `libs.versions.toml`) и `google-services.json` оставьте из своего репозитория — они не менялись.

## Что сделано

**Безопасность**
- `local.properties` с реальными ключами в архив НЕ включён. Добавлены `local.properties.example` и `keystore.properties.example`.
- Расширен `.gitignore`: `local.properties`, `keystore.properties`, `*.jks`, `*.keystore`, `.kotlin`, captures, логи.

**Release-сборка**
- `app/build.gradle.kts`: добавлен `signingConfigs.release`, читающий `keystore.properties`. Если файла нет — откат на debug-ключ, сборка не падает.
- `app/proguard-rules.pro`: добавлены keep-правила для Crashlytics (имена файлов/строки) и kotlinx.serialization (type-safe навигация).
- `data/consumer-rules.pro`: keep-правила для Firestore-сущностей (`data.model.**`) — без них R8 переименует поля и `toObject()` сломается в release.

**Чистка кода**
- Убраны debug-логи: `Log.d` в `EcoSyncWorker` и `EcoPulseFcmService` (FCM-токен больше не светится в logcat), `println` и `printStackTrace` в `EcoRepositoryImpl` (оставлен корректный `Crashlytics.recordException`).

**Корректность на Android 13+**
- В манифест добавлено разрешение `POST_NOTIFICATIONS` (без него уведомления молча не показываются на API 33+).

## Что нужно сделать вручную (не вошло в автоправки)

1. **Отозвать и перевыпустить ключи** Gemini и Google Maps — они были в открытом виде. На Maps-ключ поставить ограничения по package + SHA-1.
2. **Запросить `POST_NOTIFICATIONS` в рантайме** (declaration в манифесте уже есть, но на API 33+ нужен runtime-запрос разрешения у пользователя).
3. **`PowerConnectionReceiver`**: `ACTION_POWER_DISCONNECTED` не доставляется manifest-ресиверу на API 26+. Регистрировать в рантайме через `Context.registerReceiver(...)`.
4. **Архитектура (по желанию)**: внедрить `FirebaseFirestore` в `EcoRepositoryImpl` через конструктор (`@Inject`), задействовать `Local/RemoteDataSource`, заменить `@Provides`-фабрику репозитория на `@Binds`, убрать хардкод `user_77` (брать uid из авторизации).
