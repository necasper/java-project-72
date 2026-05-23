@param java.util.List<hexlet.code.model.Url> urls
@param String flash = ""
@param String flashType = "info"

@template.layout(
    title = "Сайты",
    flash = flash,
    flashType = flashType,
    pageContent = @`
<h1 class="mb-4">Сайты</h1>
<table class="table table-bordered table-hover" data-test="urls">
    <thead>
        <tr>
            <th>ID</th>
            <th>Имя</th>
            <th>Дата создания</th>
            <th>Код ответа</th>
        </tr>
    </thead>
    <tbody>
        @for(var url : urls)
            <tr>
                <td>${url.getId()}</td>
                <td><a href="/urls/${url.getId()}">${url.getName()}</a></td>
                <td>${url.getCreatedAt().toLocalDate().toString()}</td>
                <td></td>
            </tr>
        @endfor
    </tbody>
</table>
`
)
