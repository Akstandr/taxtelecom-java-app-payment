# TaxTelecom Java Test App

Swing-приложение для создания, просмотра, сохранения и загрузки документов трех типов:

- Накладная
- Платёжка
- Заявка на оплату

## Запуск

Нужен JDK 8 или новее.

```powershell
mkdir out
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src/main/java/*.java).FullName
java -cp out ru.taxtelecom.docs.DocumentApp
```

## Тесты

```powershell
mkdir out
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src/main/java/*.java).FullName (Get-ChildItem -Recurse src/test/java/*.java).FullName
java -ea -cp out ru.taxtelecom.docs.DocumentSerializerTest
```

Файлы сохраняются в текстовом UTF-8 формате `key=value`.
