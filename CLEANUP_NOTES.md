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

---

# Исправление зависаний (по логам запуска)

Симптомы: карта не грузится, профиль и Gemini висят бесконечно. Две первопричины:

**1. Ключи API не доходили до приложения.** Gradle не загружает `local.properties` в project-свойства, а ключи читались через `project.findProperty(...)` → приходили пустыми. Лог Maps это подтверждал: `API Key:` (пусто), `INVALID_ARGUMENT`.
→ Исправлено в `app/build.gradle.kts`: добавлена функция `secret()`, читающая ключ из `local.properties` (с откатом на gradle-property / env для CI). Теперь `GEMINI_API_KEY` и `MAPS_API_KEY` реально подставляются.

**2. Профиль/цели зависали при отсутствии данных.** `getUserProfile()` эмитил только при существующем документе `users/user_77`; если его нет — Flow молчит, спиннер вечный, а `AiAdvisorViewModel.first()` ждёт эмиссии, которой нет (отсюда «Gemini думает вечно»).
→ Исправлено в `EcoRepositoryImpl`: эмит на каждое событие; если документа нет — создаётся заготовка и отдаётся дефолтный профиль; ошибки идут в Crashlytics, а не подвешивают UI. `getEcoGoals` при ошибке отдаёт пустой список.
→ В `AiAdvisorViewModel`: при пустом ключе сразу понятная ошибка; вся операция обёрнута в `withTimeout(30s)`, поэтому экран не зависнет даже при сетевых проблемах.

**Что всё ещё нужно сделать вручную для карты:**
Ключ Maps должен быть авторизован под РЕАЛЬНЫЙ запускаемый пакет. Из-за флаворов и суффикса это `com.example.ecopulse.free.debug` (а не `com.example.ecopulse`). В ограничениях ключа добавьте этот package + debug-SHA‑1 из лога:
`FC:E8:B1:D2:2C:10:23:E1:3B:10:1C:12:31:42:B0:D2:68:FE:75:09`
Для быстрого теста проще временно снять ограничения с ключа. Для всех вариантов добавьте также `.premium.debug`, `.free`, `.premium`.
