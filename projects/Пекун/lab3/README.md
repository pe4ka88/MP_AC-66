# 📱 Лабораторная работа №8 - Android приложение

**Автор:** Пекун Марк Сергеевич  
**Группа:** АС-66

---

## 🎯 Описание проекта

Android-приложение для загрузки и отображения данных пользователей с удалённого сервера через REST API.

**Реализовано:**
- ✅ MVVM архитектура
- ✅ Navigation Component
- ✅ ViewBinding
- ✅ Retrofit + Gson
- ✅ Material Design 3
- ✅ RecyclerView с кастомным Adapter
- ✅ Загрузка изображений через Glide
- ✅ SharedPreferences для настроек
- ✅ Обработка всех типов ошибок
- ✅ Экспорт данных (JSON)
- ✅ Splash screen с авторством

---

## 🔧 Исправление критической ошибки

### ❌ **Проблема: IllegalStateException**

**Причина:**
1. В `UserViewModel` использовался `observeForever()` без удаления observer
2. Это вызывало утечку памяти и крах при уничтожении Fragment
3. Отсутствовала проверка на null при работе с binding

### ✅ **Решение:**

1. **UserViewModel** - убран `observeForever()`, добавлен callback-подход:
```java
// БЫЛО (неправильно):
LiveData<Result<List<User>>> resultLiveData = repository.loadUsers(limit);
resultLiveData.observeForever(result -> { ... });

// СТАЛО (правильно):
repository.loadUsers(limit, new UserRepository.DataCallback<List<User>>() {
    @Override
    public void onSuccess(List<User> data) {
        loadingLiveData.postValue(false);
        usersLiveData.postValue(data);
    }
    @Override
    public void onError(String message) {
        errorLiveData.postValue(message);
    }
});
```

2. **UserListFragment** - использование `getViewLifecycleOwner()`:
```java
// КРИТИЧЕСКИ ВАЖНО: getViewLifecycleOwner() вместо this
viewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
    // Обработка данных
});
```

3. **UserRepository** - добавлена отмена активных запросов:
```java
@Override
protected void onCleared() {
    super.onCleared();
    repository.cancelRequests();
}
```

4. **Обработка всех типов ошибок:**
- ❌ Нет интернета → `UnknownHostException`
- ⏱️ Таймаут → `SocketTimeoutException`
- 🚫 Ошибка сервера → HTTP код 500/404
- 📭 Пустой ответ → проверка `isEmpty()`

---

## 🏗️ Структура проекта

```
app/src/main/java/com/example/thirdlab8/
├── MainActivity.java                    # Главная активность с Navigation
├── ui/
│   ├── SplashFragment.java             # Splash экран с авторством
│   ├── AboutFragment.java              # Экран "О приложении"
│   ├── list/
│   │   └── UserListFragment.java       # Список пользователей
│   ├── detail/
│   │   └── UserDetailFragment.java     # Детали пользователя
│   └── settings/
│       └── SettingsFragment.java       # Настройки (URL, лимит)
├── viewmodel/
│   └── UserViewModel.java              # ViewModel (MVVM)
├── repository/
│   └── UserRepository.java             # Репозиторий данных
├── model/
│   └── User.java                       # Модель данных (Parcelable)
├── adapter/
│   └── UserAdapter.java                # Adapter для RecyclerView
└── api/
    ├── ApiService.java                 # Retrofit API интерфейс
    └── RetrofitClient.java             # Singleton Retrofit клиент

app/src/main/res/
├── layout/
│   ├── fragment_splash.xml             # Splash экран
│   ├── fragment_user_list.xml          # Главный экран
│   ├── fragment_user_detail.xml        # Детальный экран
│   ├── fragment_settings.xml           # Настройки
│   ├── fragment_about.xml              # О приложении
│   └── item_user.xml                   # Item для RecyclerView
├── navigation/
│   └── nav_graph.xml                   # Navigation граф
└── menu/
    └── menu_user_list.xml              # Меню главного экрана
```

---

## 🚀 Сборка и запуск

### **Вариант 1: Android Studio**

1. Откройте проект в Android Studio
2. Дождитесь синхронизации Gradle
3. Нажмите **Run** (Shift+F10) или **Debug** (Shift+F9)

### **Вариант 2: Командная строка (Git Bash)**

```bash
# Перейдите в папку проекта
cd e:/android/thirdLab8

# Сборка Debug APK
./gradlew assembleDebug

# Результат:
# app/build/outputs/apk/debug/app-debug.apk

# Установка на подключенное устройство
./gradlew installDebug

# Очистка проекта
./gradlew clean
```

### **Вариант 3: Windows CMD**

```cmd
cd e:\android\thirdLab8
gradlew.bat assembleDebug
gradlew.bat installDebug
```

---

## 📱 Использование приложения

### **1. Splash Screen**
- Отображается 3 секунды при запуске
- Показывает авторство: **Пекун Марк Сергеевич, АС-66**
- Можно пропустить нажатием на экран

### **2. Главный экран**
- Кнопка **"Загрузить"** - загружает данные с сервера
- Кнопка **"Настройки"** - переход к настройкам
- **Меню** (3 точки):
  - ⚙️ Настройки
  - 📤 Поделиться (экспорт JSON)
  - ℹ️ О приложении

### **3. Настройки**
- **URL сервера** - изменение адреса API
- **Количество элементов** - выбор: 10, 20, 30, 50, 100
- По умолчанию: `https://jsonplaceholder.typicode.com/`

### **4. Список пользователей**
- Прокрутка списка (RecyclerView)
- Клик по элементу → переход к деталям
- Аватары загружаются через Glide

### **5. Детали пользователя**
- Полная информация о пользователе
- Большой аватар
- Кнопка "Назад" для возврата

---

## 🌐 API Endpoints

### **По умолчанию: JSONPlaceholder**

```
GET https://jsonplaceholder.typicode.com/users?_limit=20
```

**Пример ответа:**
```json
[
  {
    "id": 1,
    "name": "Leanne Graham",
    "email": "Sincere@april.biz",
    "phone": "1-770-736-8031 x56442",
    "website": "hildegard.org",
    "company": {
      "name": "Romaguera-Crona"
    },
    "address": {
      "street": "Kulas Light",
      "city": "Gwenborough"
    }
  }
]
```

### **Альтернативные API (можно настроить):**

```
https://reqres.in/api/users?per_page=20
https://randomuser.me/api/?results=20
https://jsonplaceholder.typicode.com/posts?_limit=20
```

---

## 🎨 Дизайн

- **Material Design 3** - современный UI
- **Темная тема** - поддержка системной темы
- **Анимации** - плавные переходы между экранами
- **Иконки** - Material Icons
- **Типографика** - Material Type Scale
- **Цвета** - Material Color System

---

## 🛡️ Обработка ошибок

Приложение корректно обрабатывает все типы ошибок:

| Ошибка | Обработка | Сообщение |
|--------|-----------|-----------|
| ❌ Нет интернета | `UnknownHostException` | "Нет подключения к интернету" |
| ⏱️ Таймаут | `SocketTimeoutException` | "Превышено время ожидания" |
| 🚫 Ошибка 404 | HTTP 404 | "Сервер не найден (404)" |
| 🚫 Ошибка 500 | HTTP 500 | "Внутренняя ошибка сервера (500)" |
| 📭 Пустой ответ | `isEmpty()` | "Пустой ответ от сервера" |

**Все ошибки показываются через Snackbar с кнопкой "Повторить"**

---

## 📦 Зависимости

```gradle
dependencies {
    // AndroidX
    implementation 'androidx.appcompat:appcompat:1.7.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.2.1'
    implementation 'androidx.lifecycle:lifecycle-viewmodel:2.8.7'
    implementation 'androidx.lifecycle:lifecycle-livedata:2.8.7'
    
    // Material Design 3
    implementation 'com.google.android.material:material:1.13.0'
    
    // Navigation Component
    implementation 'androidx.navigation:navigation-fragment:2.8.5'
    implementation 'androidx.navigation:navigation-ui:2.8.5'
    
    // Retrofit + Gson
    implementation 'com.squareup.retrofit2:retrofit:2.11.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.11.0'
    
    // Glide (загрузка изображений)
    implementation 'com.github.bumptech.glide:glide:4.16.0'
}
```

---

## 📋 Соответствие ТЗ Лабораторной №8

| Требование | Статус | Реализация |
|-----------|--------|------------|
| Fragment со списком | ✅ | `UserListFragment` |
| RecyclerView | ✅ | С кастомным `UserAdapter` |
| Список > 1 экрана | ✅ | Загрузка 20+ элементов |
| Кастомный item layout | ✅ | `item_user.xml` (Material Card) |
| Загрузка JSON с сервера | ✅ | Retrofit + Gson |
| JSON → List<Model> | ✅ | `User` модель |
| Клик → DetailFragment | ✅ | Navigation + Bundle |
| Собственный Adapter | ✅ | `UserAdapter` с ViewHolder |
| Изображения (Glide) | ✅ | Круглые аватары |
| Экран настроек | ✅ | `SettingsFragment` |
| Выбор URL | ✅ | TextInputLayout + SharedPreferences |
| Выбор количества | ✅ | Spinner (10/20/30/50/100) |
| Передача через Intent | ✅ | `ACTION_SEND` для экспорта |
| Авторство | ✅ | Splash, About, Footer, комментарии |
| Material Design 3 | ✅ | Полная реализация |
| Темная тема | ✅ | Автоматическая поддержка |

---

## 🐛 Решённые проблемы

### 1. **IllegalStateException при уничтожении Fragment**
- ❌ Использовался `observeForever()` без cleanup
- ✅ Переход на callback-подход + `getViewLifecycleOwner()`

### 2. **Утечка памяти**
- ❌ Активные Retrofit запросы не отменялись
- ✅ Добавлен `cancelRequests()` в `onCleared()`

### 3. **Binding null после onDestroyView**
- ❌ Доступ к binding после уничтожения View
- ✅ Проверка `binding != null` + `binding = null` в `onDestroyView()`

### 4. **ViewBinding классы не генерировались**
- ❌ Отсутствовали XML layout файлы
- ✅ Созданы все необходимые layouts

---

## 📚 Дополнительная информация

### **Минимальная версия Android**
- **minSdk:** 24 (Android 7.0 Nougat)
- **targetSdk:** 35 (Android 15)

### **Поддерживаемые языки**
- Русский (основной)
- English (fallback)

### **Ориентация экрана**
- Portrait (вертикальная)
- Landscape (горизонтальная) - адаптивная вёрстка

---

## 👨‍💻 Автор

**Пекун Марк Сергеевич**  
Группа: **АС-66**  
Лабораторная работа №8  
Android Development

---

## 📄 Лицензия

Учебный проект. Все права защищены.

---

**Версия:** 1.0  
**Дата:** Февраль 2026
