@import gg.jte.Content

@param String title
@param Content pageContent
@param String flash = ""

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${title}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
          crossorigin="anonymous">
</head>
<body class="d-flex flex-column min-vh-100">
<nav class="navbar navbar-dark bg-dark">
    <div class="container-fluid">
        <a class="navbar-brand" href="/">Анализатор страниц</a>
        <a class="nav-link text-light" href="/urls">Сайты</a>
    </div>
</nav>
<main class="container my-4">
    @if(flash != null && !flash.isBlank())
        <div class="alert alert-info mb-3" role="alert">${flash}</div>
    @endif
    ${pageContent}
</main>
<footer class="footer border-top py-3 mt-auto bg-light">
    <div class="container text-center">
        <a href="https://ru.hexlet.io" target="_blank">Hexlet</a>
    </div>
</footer>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
        crossorigin="anonymous"></script>
</body>
</html>
