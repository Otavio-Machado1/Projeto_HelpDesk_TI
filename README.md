# HelpDesk TI — Projeto 1 (Mapeamento Objeto Relacional)

Aplicação de console para controle de chamados de TI: técnicos e chamados, com Java, JPA, Hibernate, Maven e MySQL. Segue a estrutura dos exemplos da disciplina (`MorP2_Projetc1`, `MorP3_Relations`) e o relacionamento da Prática 4 (`@OneToMany` / `@ManyToOne`).

Repositório de referência: https://github.com/pgustavodias/mapeamento-objeto-relacional

## Situação
- Compila com `mvn clean compile` (verificado em 16/11/2026 com JDK 24 e Maven 3.9.11).
- Executado contra o MySQL 8.0 local em 16/11/2026: senha configurada e os casos de `docs/03_testes_manuais.md` aprovados (cadastro, consultas, alteração com transferência e exclusão).
- Verificado em 17/11/2026 com um schema vazio: no MySQL foi criado só o banco, e o Hibernate criou as tabelas `tecnico` e `chamado` com a chave estrangeira `id_tecnico` ao iniciar o programa.

## Funcionalidades (menu de 7 opções)
1. Cadastrar técnico | 2. Listar técnicos
3. Abrir chamado | 4. Consultar chamado (mostra o técnico responsável) | 5. Listar chamados de um técnico
6. Alterar chamado (prioridade, status e técnico) | 7. Excluir chamado

Regras: cada chamado tem exatamente um técnico; um técnico pode ter zero ou muitos chamados.

## 1. Ambiente
- JDK 8 ou superior (o Eclipse desta máquina usa o JDK 21).
- Eclipse com Maven (m2e) ou Maven 3.6+ no terminal.
- MySQL 8.0 ativo (serviço `MySQL80`, porta 3306).
- Internet na primeira importação, se as dependências ainda não estiverem no `.m2`.

Dependências (as mesmas do repositório da disciplina): hibernate-core 5.6.15.Final, hibernate-entitymanager 5.6.15.Final, mysql-connector-java 8.0.28. Imports em `javax.persistence`.

## 2. Criar o schema (único passo no MySQL)
No MySQL Command Line Client, crie apenas o schema vazio. No MySQL, `CREATE DATABASE` e `CREATE SCHEMA` são o mesmo comando.
```sql
CREATE DATABASE helpdesk_ti;
```
O mesmo comando está em `sql/01_criar_banco.sql` (pode rodar com `SOURCE C:/Users/labunivas/Documents/eclipse_gustavo/helpdesk-ti/sql/01_criar_banco.sql;`).

**As tabelas não são criadas no MySQL.** Quem cria é o Hibernate, na primeira execução do programa, porque o `persistence.xml` tem `hibernate.hbm2ddl.auto = update`. Ele lê as anotações das entidades:
- `@Entity` cria as tabelas `tecnico` e `chamado`.
- `@Id` com `@GeneratedValue(IDENTITY)` cria `id INT AUTO_INCREMENT` como chave primária.
- `@Column` cria as colunas `VARCHAR(255)`.
- `@ManyToOne` com `@JoinColumn(name = "id_tecnico")` cria a coluna `id_tecnico` e a FOREIGN KEY.
- `@OneToMany(mappedBy = "tecnico")` não cria coluna, porque é o lado inverso.

Para conferir depois de rodar o programa uma vez:
```sql
USE helpdesk_ti;
SHOW TABLES;
SHOW CREATE TABLE chamado;
```
O nome da chave estrangeira é gerado pelo Hibernate (algo como `FKhsb3nirh7irlf8fvv0kvcfrqn`).

Para refazer do zero (apaga os dados de teste):
```sql
DROP DATABASE helpdesk_ti;
CREATE DATABASE helpdesk_ti;
```
Depois rode o programa: as tabelas aparecem de novo.

## 3. Configurar a conexão
Em `src/main/resources/META-INF/persistence.xml`, a propriedade `javax.persistence.jdbc.password` deve conter a senha do usuário `root` deste computador (já configurada nesta máquina). Se o MySQL estiver em outra porta, ajuste a URL `jdbc:mysql://localhost/helpdesk_ti?...`.
Não publique a senha: o `.gitignore` não protege o XML.

## 4. Importar e executar no Eclipse
1. File > Import > Maven > Existing Maven Projects.
2. Root Directory: a pasta que contém o `pom.xml`.
3. Finish e aguarde o Maven baixar/resolver as dependências.
4. Botão direito no projeto > Maven > Update Project.
5. Abra `src/main/java/client/ClientHelpDesk.java` > Run As > Java Application.
6. Clique no Console para digitar as opções.

## 5. Executar no terminal (alternativa)
```
mvn clean compile
mvn exec:java -Dexec.mainClass=client.ClientHelpDesk
```

## 6. Primeiro teste guiado
1. Opção 1: cadastre Otávio / Impressoras. Anote o ID.
2. Opção 1: cadastre Ana / Redes. Anote o ID.
3. Opção 3: técnico Otávio, problema "Impressora do financeiro não imprime", prioridade Alta.
4. Opção 4: informe o ID do chamado. Deve mostrar "Técnico responsável: Otávio".
5. Opção 5: liste os chamados de Otávio.
6. Opção 6: prioridade Média, status "Em atendimento", técnico Ana. Opção 5: agora o chamado aparece na lista de Ana.
7. Opção 7: exclua o chamado. Opção 4 responde "Chamado não encontrado".

## 7. Estrutura
```
helpdesk-ti/
├── pom.xml                                  dependências (hibernate-core, hibernate-entitymanager, mysql-connector-java)
├── src/main/java/objects/Tecnico.java       @Entity, @OneToMany(mappedBy = "tecnico")
├── src/main/java/objects/Chamado.java       @Entity, @ManyToOne + @JoinColumn(name = "id_tecnico")
├── src/main/java/client/ClientHelpDesk.java main, menu de 7 opções com Scanner, persist / find / set + commit / remove
├── src/main/resources/META-INF/persistence.xml   unidade "jpa", hbm2ddl.auto = update
├── sql/                                     01: CREATE DATABASE (só o schema); 02: consultas de demonstração
├── docs/                                    contexto, modelagem (UML, MER, relacional), testes, checklist e roteiro
└── apresentacao/                            slides (PPTX e PDF)
```

## 8. Diagnóstico
- `Access denied for user 'root'`: senha errada no persistence.xml.
- `Communications link failure`: MySQL parado ou porta diferente.
- `Unknown database 'helpdesk_ti'`: execute o passo 2 (o Hibernate cria as tabelas, mas não cria o schema).
- `package javax.persistence does not exist`: Maven > Update Project para baixar as dependências.
- Acentos estranhos no console: Window > Preferences > General > Workspace > Text file encoding = UTF-8.

## 9. Apresentação
17 e 24/11/2026, 5 a 10 minutos, equipe de até 4. Os slides (`apresentacao/slides_helpdesk.pptx`, 14 slides com notas do apresentador; os trechos de código são imagens dos arquivos reais com número de linha) estão divididos em 3 partes para 3 apresentadores: contexto e modelagem (slides 1 a 6), arquitetura e mapeamento (7 a 10), CRUD, consulta e demonstração (11 a 14). Preencha os nomes nos slides 1 e 2, ensaie com `docs/05_roteiro_apresentacao.md`, estude `docs/06_explicacao_codigo_e_tema.md` e deixe o projeto pronto para o professor testar. O projeto já está importado no Eclipse desta máquina (workspace `C:\Users\labunivas\eclipse-workspace`). A versão anterior dos slides ficou em `apresentacao/_versao_anterior/`.
