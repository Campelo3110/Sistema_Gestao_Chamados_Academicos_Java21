# Sistema de Gestão de Chamados Acadêmicos

Projeto final de Linguagem de Programação 2 do IFSP, orientado pelo professor Anderson Peixoto.

**Integrante:** Guilherme Campelo.

## Executar

Instale o JDK 21. No PowerShell, execute `.\mvnw.cmd clean verify` e depois `.\mvnw.cmd spring-boot:run`. O Maven Wrapper baixa o Maven na primeira execução. Em Linux/macOS, execute os mesmos objetivos com `sh ./mvnw`. Abra <http://localhost:8080/login>. Também é possível importar o `pom.xml` no IntelliJ, selecionar o JDK 21 e executar `ProjetoDtoStatusChamadosApplication`.

O acesso de demonstração é **admin / admin123**. Para alterá-lo, defina `DEMO_USUARIO` e `DEMO_SENHA`. O H2 cria a base persistente em `dados/` e carrega três chamados quando a base está vazia. A autenticação é didática e a aplicação deve ser demonstrada localmente.

## Funcionalidades

- Cadastro, listagem, detalhes, edição, atualização de status e exclusão de chamados.
- Filtros combinados por busca, status, categoria, prioridade e período de abertura.
- Indicadores e relatórios filtrados com exportação CSV, XLSX e PDF.
- API REST JSON com persistência Spring Data JPA/Hibernate, compartilhada com a interface Thymeleaf.

## Testar a API

Use o [roteiro HTTP](exemplos.http) ou reproduza as requisições no Postman. Faça `POST /login` com os campos de formulário `usuario` e `senha` e mantenha o cookie `JSESSIONID` nas requisições.

| Método | Rota | Resultado |
| --- | --- | --- |
| GET | `/api/chamados` | 200 com a lista, aceitando filtros |
| GET | `/api/chamados/{id}` | 200 ou 404 |
| POST | `/api/chamados` | 201 com o chamado criado em JSON |
| PUT | `/api/chamados/{id}` | 200, 400 para dados inválidos ou 404 |
| PUT | `/api/chamados/{id}/status` | 200, 400 para status inválido ou 404 |
| DELETE | `/api/chamados/{id}` | 204 ou 404 |
| GET | `/api/chamados/indicadores` | 200 com os indicadores gerais |

Para demonstrar o relatório, abra **Relatórios**, selecione um status e uma categoria e aplique os filtros. Compare a tabela com o download em CSV, PDF ou Excel. Os links preservam o filtro e a exportação consulta o mesmo serviço da página.

## Validação executada

O build passou com Java 21 e o Maven Wrapper. No Chrome, foram conferidos os fluxos de cadastro, edição, atendimento e exclusão. As requisições HTTP validaram GET, POST, PUT e DELETE, inclusive retornos 400 e 404. Os três formatos de exportação responderam com o recorte filtrado.

## Organização e refatoração

Controllers MVC e REST recebem as requisições. Os DTOs delimitam os dados de entrada. O Service usa verificações manuais para validar os dados de cadastro, edição e status. O Service coordena as operações, o Repository acessa o H2 por JPA e a Factory centraliza a configuração inicial de um chamado.

Na base fornecida, a edição web recebia a entidade diretamente e a API só permitia alterar o status na operação de atualização. Agora o formulário de edição e o `PUT /api/chamados/{id}` recebem `ChamadoEdicaoDTO` e chamam o mesmo método de serviço. A data de abertura permanece protegida porque é carregada do registro persistido, e o caminho REST informa 404 quando o chamado não existe.

Para a apresentação, demonstre um chamado do cadastro à exclusão, repita o CRUD via API e exporte um recorte do relatório.
