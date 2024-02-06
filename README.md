# Курсовая работа: Telegram-бот для отслеживания перемещения
Бот записывает пройденный пользователем маршрут, основываясь на координатах, получаемых из трансляции геопозиции пользователя. В течение записи и по её окончании выводит карту с пройденным маршрутом и расчитывает пройденное расстояние.
- Язык программирования – Kotlin
- Библиотека для работы с Telegram Bot API – [Kotlin Telegram Bot API Library](https://github.com/InsanusMokrassar/ktgbotapi)
- Отображение маршрута на карте – [2ГИС API](https://dev.2gis.ru/)
### Принцип работы:
1. Пользователь отправляет боту трансляцию геопозиции
2. Из полученного сообщения извлекается широта и долгота начала маршрута
3. При каждом обновлении геопозиции извлекаются новые координаты пользователя, которые в дальнейшем используются для расчета расстояния. Также обновляется изображение карты путем запроса к 2ГИС API для получения нового изображения карты с отмеченным на ней пройденным маршрутом
4. По завершении трансляции геопозиции рассчитывается итоговое пройденное расстояние и выводится карта всего пройденного маршрута

# University Coursework: Telegram bot for location tracking
The bot records the route traveled by the user based on the coordinates obtained from the user's live location. During and after the recording, it displays a map with the traveled route and calculates the distance traveled.
- Language - Kotlin
- Library for Telegram Bot API interaction with Kotlin - [Kotlin Telegram Bot API Library](https://github.com/InsanusMokrassar/ktgbotapi).
- To display route on the map - [2GIS API](https://dev.2gis.ru/)
### How it works:
1. User sends the bot a live location
2. The latitude and longitude of the route start are extracted from the received message
3. Each time the geoposition is updated, new coordinates of the user are extracted which are further used to calculate the distance. The map image is also updated by querying the 2GIS API to retrieve a new map image with the traveled route marked on it
4. When the live location is expired, the final distance traveled is calculated and a map of the entire route traveled is displayed
