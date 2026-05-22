# Выводы о жизненном цикле Activity на основе логов

**Выполнил:** Осовец Арсений, группа АС-66  
**Лабораторная работа:** №2 (№7 по методичке)

## Анализ жизненного цикла приложения Taxi

### 1. Запуск MainActivity (первый экран)

При запуске приложения в Logcat наблюдается следующая последовательность:

```
MainActivity: onCreate
MainActivity: onStart
MainActivity: onResume
```

**Вывод:** Activity проходит полный цикл инициализации: создание (`onCreate`), становление видимым (`onStart`), получение фокуса (`onResume`). После `onResume` пользователь может взаимодействовать с интерфейсом.

---

### 2. Переход MainActivity → OrderActivity (явный Intent)

При нажатии кнопки "Registration" запускается OrderActivity:

```
MainActivity: onPause
OrderActivity: onCreate
OrderActivity: onStart
OrderActivity: onResume
MainActivity: onStop
```

**Вывод:**

- Сначала текущая Activity (`MainActivity`) теряет фокус (`onPause`)
- Новая Activity (`OrderActivity`) полностью создаётся и получает фокус
- Только после этого старая Activity становится невидимой (`onStop`)
- `MainActivity` **НЕ** вызывает `onDestroy` — остаётся в back stack

---

### 3. Переход OrderActivity → RouteActivity (неявный Intent через startActivityForResult)

При нажатии "Set path":

```
OrderActivity: onPause
RouteActivity: onCreate
RouteActivity: onStart
RouteActivity: onResume
OrderActivity: onStop
```

**Вывод:** Аналогичная последовательность. `OrderActivity` остаётся в памяти (не уничтожается), ожидая результата от `RouteActivity`.

---

### 4. Возврат из RouteActivity в OrderActivity (finish после setResult)

При нажатии "OK" в RouteActivity:

```
RouteActivity: onPause
OrderActivity: onRestart
OrderActivity: onStart
OrderActivity: onResume
RouteActivity: onStop
RouteActivity: onDestroy
```

**Вывод:**

- `RouteActivity` теряет фокус (`onPause`)
- `OrderActivity` **возобновляется** через `onRestart` → `onStart` → `onResume`
- `RouteActivity` полностью уничтожается (`onStop` → `onDestroy`), так как вызван `finish()`
- Данные успешно передаются через `Intent` в `onActivityResult` / `ActivityResultLauncher`

---

### 5. Возврат из OrderActivity в MainActivity (кнопка Back)

```
OrderActivity: onPause
MainActivity: onRestart
MainActivity: onStart
MainActivity: onResume
OrderActivity: onStop
OrderActivity: onDestroy
```

**Вывод:** Та же логика возврата по back stack. `MainActivity` восстанавливается через `onRestart`.

---

### 6. Выход из приложения (закрытие MainActivity)

```
MainActivity: onPause
MainActivity: onStop
MainActivity: onDestroy
```

**Вывод:** Приложение полностью завершается, все ресурсы освобождаются.

---

### 7. Поворот экрана (если не заблокирована ориентация)

При повороте устройства текущая Activity пересоздаётся:

```
MainActivity: onPause
MainActivity: onStop
MainActivity: onDestroy
MainActivity: onCreate
MainActivity: onStart
MainActivity: onResume
```

**Вывод:**

- Android уничтожает старый экземпляр Activity и создаёт новый
- Это важно для сохранения данных через `onSaveInstanceState` или `SharedPreferences`
- В нашем приложении данные регистрации сохраняются через `SharedPreferences`, поэтому после пересоздания MainActivity восстанавливает поля и меняет текст кнопки на "Log in"

---

## Общие выводы

1. **Последовательность запуска Activity:** onCreate → onStart → onResume
2. **При переходе между Activity:** старая приостанавливается (`onPause`), новая создаётся, затем старая скрывается (`onStop`)
3. **Back stack:** Activity остаются в памяти (не `onDestroy`), пока не будут явно закрыты
4. **Возврат с результатом:** `finish()` уничтожает Activity (`onDestroy`), а вызывающая Activity возобновляется через `onRestart` → `onStart` → `onResume`
5. **Сохранение данных:** `SharedPreferences` используется в `MainActivity` для персистентности данных между запусками приложения
6. **onRestart:** вызывается только при **возврате** к ранее созданной Activity (не при первом запуске)

---

## Практическая ценность логирования

Логи жизненного цикла критически важны для:

- Отладки утечек памяти (проверка, что Activity уничтожаются)
- Оптимизации UI (загрузка данных в `onResume`, освобождение в `onPause`)
- Понимания поведения back stack и передачи данных между Activity
- Корректной работы с сенсорами/камерой (подписка в `onResume`, отписка в `onPause`)
