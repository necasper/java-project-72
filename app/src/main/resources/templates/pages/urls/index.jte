@param java.util.List<hexlet.code.dto.UrlView> urls
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
            <th>Последняя проверка</th>
            <th>Код ответа</th>
        </tr>
    </thead>
    <tbody>
        @for(var view : urls)
            <tr>
                <td>${view.getUrl().getId()}</td>
                <td><a href="/urls/${view.getUrl().getId()}">${view.getUrl().getName()}</a></td>
                <td>
                    @if(view.getLastCheck() != null)
                        ${view.getLastCheck().getCreatedAt().toLocalDate().toString()}
                    @endif
                </td>
                <td>
                    @if(view.getLastCheck() != null)
                        ${view.getLastCheck().getStatusCode()}
                    @endif
                </td>
            </tr>
        @endfor
    </tbody>
</table>
`
)
