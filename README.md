# EcoPulse 🌿

Мобильное Android-приложение для отслеживания эко-целей и формирования экологических привычек. Курсовая работа по дисциплине «Мобильная разработка».

## Стек технологий

| Категория | Технологии |
|---|---|
| Язык | Kotlin |
| UI | Jetpack Compose + XML (ViewBinding) |
| Архитектура | Clean Architecture (data / domain / presentation) |
| DI | Hilt |
| Навигация | Navigation Compose |
| Фон | WorkManager, BroadcastReceiver |
| Хранилище | Firebase Firestore |
| Push-уведомления | Firebase Cloud Messaging (FCM) |
| AI | Gemini API (`gemini-1.5-flash`) |
| Сборка | Gradle KTS, productFlavors, R8/ProGuard |

## Структура проекта

```
Ecopulse/
├── app/                        # presentation-слой + DI + навигация
│   └── src/main/java/.../
│       ├── di/
│       │   ├── DataModule.kt       # @Singleton: репозиторий, GetEcoTipsUseCase
│       │   └── DomainModule.kt     # @ViewModelScoped: Use Case'ы
│       ├── presentation/
│       │   ├── auth/               # AuthViewModel, SignInScreen, SignUpScreen
│       │   ├── goals/              # GoalsViewModel, GoalsScreen
│       │   ├── profile/            # ProfileViewModel, ProfileScreen (XML + Compose)
│       │   └── stats/              # StatsViewModel, AiAdvisorViewModel, StatsScreen
│       ├── service/
│       │   └── EcoPulseFcmService.kt   # FCM push-уведомления
│       ├── EcoPulseApp.kt          # @HiltAndroidApp
│       └── MainActivity.kt         # NavHost, WorkManager setup
│
├── data/                       # data-слой
│   └── src/main/java/.../
│       ├── model/
│       │   ├── EcoGoalEntity.kt
│       │   ├── UserProfileEntity.kt
│       │   └── mapper/
│       │       └── EcoMapper.kt    # Entity → Domain mapping
│       ├── repository/
│       │   └── EcoRepositoryImpl.kt  # Firestore: callbackFlow, await()
│       ├── receiver/
│       │   └── PowerConnectionReceiver.kt  # BroadcastReceiver
│       └── worker/
│           └── EcoSyncWorker.kt    # CoroutineWorker
│
└── domain/                     # domain-слой (java-library, без Android)
    └── src/main/java/.../
        ├── model/
        │   ├── EcoGoal.kt
        │   ├── EcoTip.kt
        │   └── UserProfile.kt
        ├── repository/
        │   └── EcoRepository.kt    # интерфейс
        └── usecase/
            ├── GetEcoGoalsUseCase.kt
            ├── GetEcoTipsUseCase.kt
            ├── GetUserProfileUseCase.kt
            └── CompleteGoalUseCase.kt
```

## Реализованные требования

### Чистая архитектура
- 3 Gradle-модуля: `:app`, `:data`, `:domain`
- Модуль `:domain` — чистый `java-library`, без зависимостей на Android SDK
- Интерфейс `EcoRepository` в domain, реализация `EcoRepositoryImpl` в data
- Маппинг Entity → Domain через extension-функции в `EcoMapper.kt`
- 4 изолированных Use Case'а
- DI через Hilt: `DataModule` (`@Singleton`) + `DomainModule` (`@ViewModelScoped`)
- Зависимости направлены только внутрь: `app → data → domain`

### Фоновые задачи и сервисы
- `EcoSyncWorker` (`CoroutineWorker`) — периодическая задача через `WorkManager`
- Ограничения: `NetworkType.CONNECTED` + `setRequiresCharging(true)`
- `enqueueUniquePeriodicWork` с политикой `KEEP` — исключает дубликаты
- `PowerConnectionReceiver` — `BroadcastReceiver`, показывает уведомление при отключении зарядки

### Анимации в Jetpack Compose
- `animateColorAsState` (tween 600ms) — плавная смена цвета карточки при выполнении цели (`GoalsScreen`)
- `animateContentSize` — плавное раскрытие карточек эко-советов (`StatsScreen`)

### XML + Compose интеграция
- `ProfileScreen` — Composable экран с `AndroidView` внутри
- `AndroidView` надувает `fragment_profile.xml` через ViewBinding
- В XML встроен `<ComposeView>` с `LinearProgressIndicator` — двусторонняя интеграция

### Gradle: конфигурация сборок
- `buildTypes`: `debug` (`applicationIdSuffix = ".debug"`) и `release` (`isMinifyEnabled`, `isShrinkResources`, ProGuard)
- 2 `productFlavors`: `free` и `premium` с разными `applicationIdSuffix`, `resValue` и `buildConfigField("IS_PREMIUM")`
- `BuildConfig.IS_PREMIUM` используется в UI — бейдж **PRO** во флаворе premium

### Firebase (бонус)
- **Firestore** — основное хранилище: цели и профиль пользователя, live-обновления через `callbackFlow + addSnapshotListener`
- **FCM** — `EcoPulseFcmService` принимает push-уведомления и отображает их в системном трее

### AI-советник (бонус)
- Вкладка «AI-советник» в экране аналитики
- Gemini API (`gemini-1.5-flash`) получает реальные данные пользователя из Firestore (имя, очки, выполненные цели) и генерирует персональную эко-рекомендацию
- Состояния: `Idle → Loading → Success / Error`

## Экраны приложения

| Экран | Описание |
|---|---|
| Вход / Регистрация | Авторизация с валидацией полей |
| Трекер целей | Список эко-целей из Firestore, отметка выполнения с анимацией |
| Профиль | XML-разметка с встроенным ComposeView, прогресс-бар уровня |
| Аналитика | Статистика, эко-советы, персональные рекомендации от AI |

## Сборка и запуск

### Требования
- Android Studio Hedgehog или новее
- JDK 17
- Android SDK 31+

### Настройка Firebase
1. Создай проект в [Firebase Console](https://console.firebase.google.com)
2. Добавь Android-приложения для всех package name:
   - `com.example.ecopulse.free`
   - `com.example.ecopulse.free.debug`
   - `com.example.ecopulse.premium`
   - `com.example.ecopulse.premium.debug`
3. Скачай `google-services.json` и положи в папку `app/`
4. В Firestore создай коллекции `goals` и `users` (см. структуру ниже)

### Структура Firestore

```
/goals/{id}
  goalId: "1"          (string)
  titleText: "..."
  subDescription: "..."
  rewardAmount: 100
  statusCompleted: false

/users/user_77
  uid: "user_77"
  fullName: "Эко-Активист"
  accountEmail: "eco@pulse.com"
  currentPoints: 350
  completedCount: 2
```

### Настройка Gemini API
Получи ключ на [aistudio.google.com/apikey](https://aistudio.google.com/apikey) и добавь в `local.properties`:
```
GEMINI_API_KEY=твой_ключ_здесь
```

### Запуск
```bash
# debug-сборка (flavor: free)
./gradlew assembleFreeDebug

# release-сборка (flavor: premium)
./gradlew assemblePremiumRelease
```

> Для тестирования FCM используй эмулятор с Google Play или реальное устройство.
