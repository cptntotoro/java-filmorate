# java-kanban
### Java, Spring Boot, Spring MVC, REST API, JUnit, H2

Сервис для оценивания фильмов и сообщество пользователей. 

Приложение реализует следующую функциональность: 

1. Создание, получение, удаление, обновление и управление фильмами, их рейтингами и жанрами
2. Создание, получение, удаление, обновление и управление пользователями
3. Управление лайками фильмов, вывод самых популярных фильмов
4. Реализация односторонней дружбы между пользователями, получение общих друзей
5. Загрузка и сохранение данных в базу данных (H2)
6. REST API, эндпоинты которого соответствуют ключевым методам программы

## Структура базы данных
![ER-диаграмма Filmorate.](https://github.com/cptntotoro/java-filmorate/blob/main/database.png?raw=true)

### users - данные пользователей

- id - id пользователя
- email - электронная почта
- login - логин
- name - имя
- birthday - дата рождения

### films - данные о фильмах

- id - id фильма
- title - название фильма
- description - описание
- duration - продолжительность
- release_date - дата выпуска
- mpa_id - id рейтинга

### film_genres - таблица фильмов и их жанров

- id - уникальный ключ для строк
- film_id - id фильма
- genre_id - id жанра

### genres - данные о жанрах

- id - id жанра
- name - название жанра

### mpas - данные о рейтингах

- id - id рейтинга
- name - название рейтинга

### film_likes - таблица фильмов и пользователей, поставивших лайки

- id - уникальный ключ для строк
- film_id - id фильма
- user_id - id пользователя

### friendships - таблица запросов и подтвержденных статусов дружбы между пользователями

- id - уникальный ключ строк для поддержки связи "многие ко многим"
- user_sender_id - id пользователя, отправившего запрос
- user_recipient_id - id пользователя, получившего запрос
- status - статус дружбы (подтверждена или нет)
