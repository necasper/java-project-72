@param hexlet.code.model.Url url
@param String flash = ""
@param String flashType = "info"

@template.layout(
    title = "Сайт",
    flash = flash,
    flashType = flashType,
    pageContent = @`
<h1 class="mb-4">Сайт: ${url.getName()}</h1>
<table class="table table-bordered" data-test="url">
    <tbody>
        <tr>
            <td>ID</td>
            <td>${url.getId()}</td>
        </tr>
        <tr>
            <td>Имя</td>
            <td>${url.getName()}</td>
        </tr>
        <tr>
            <td>Дата создания</td>
            <td>${url.getCreatedAt().toLocalDate().toString()}</td>
        </tr>
    </tbody>
</table>
<h2 class="mt-5 mb-3">Проверки</h2>
<form action="/urls/${url.getId()}/checks" method="post" class="mb-4">
    <button type="submit" class="btn btn-primary">Запустить проверку</button>
</form>
<table class="table table-bordered">
    <thead>
        <tr>
            <th>ID</th>
            <th>Код ответа</th>
            <th>h1</th>
            <th>title</th>
            <th>description</th>
            <th>Дата создания</th>
        </tr>
    </thead>
    <tbody>
    </tbody>
</table>
`
)
