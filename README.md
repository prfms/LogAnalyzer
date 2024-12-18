# Анализатор логов

Лог-файлы являются важной частью работы любого сервера, так как они содержат информацию о том, какие запросы были отправлены на сервер, какие ошибки возникли и какие действия были выполнены.
## Входные данные

На вход программе через аргументы командной строки задаётся:
* путь к одному или нескольким NGINX лог-файлам ***path*** в виде локального шаблона или URL
* необязательные временные параметры ***from*** и ***to*** в формате ISO8601
* необязательный параметр формата вывода результата: markdown или adoc
* необязательное поле ***filter-value***, по которому происходит фильтрация
## Начало работы

Для того чтобы собрать проект, и проверить, что все работает корректно, можно
запустить из модального окна IDEA
[Run Anything](https://www.jetbrains.com/help/idea/running-anything.html)
команду:

```shell
mvn clean verify
```

Альтернативно можно в терминале из корня проекта выполнить следующие команды.

Для Unix (Linux, macOS, Cygwin, WSL):

```shell
./mvnw clean verify
```

Для Windows:

```shell
mvnw.cmd clean verify
```

Для окончания сборки потребуется подождать какое-то время, пока maven скачает
все необходимые зависимости, скомпилирует проект и прогонит базовый набор
тестов.

## Пример запуска программы

```shell
--path ./src/main/resources/*.txt --from 2013-06-04T06:00:00+00:00 --to 2016-06-03T07:00:00+00:00 --format adoc
```
## Выходные данные
Программа выполняет следующие задачи:
* подсчитывает общее количество запросов
* определяет наиболее часто запрашиваемые ресурсы
* определяет наиболее часто встречающиеся коды ответа
* рассчитывает средний размер ответа сервера
* определяет наиболее часто встречающиеся IP-адреса, обращающиеся к серверу
* формирует отчет в заданном формате

Пример работы программы можно увидеть по [ссылке](./src/main/resources/report.md).
