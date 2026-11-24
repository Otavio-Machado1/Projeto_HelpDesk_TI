package client;

import java.util.List;
import java.util.Scanner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import objects.Chamado;
import objects.Tecnico;

public class ClientHelpDesk {

    public static void main(String[] args) {

        // Fábrica de EntityManager criada uma única vez, a partir da
        // unidade de persistência "jpa" definida em persistence.xml
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa");

        // Instanciando objeto do tipo Scanner para estabelecer interações
        Scanner scanner = new Scanner(System.in);
        int opcao = -1;

        while (opcao != 0) {
            System.out.println();
            System.out.println("=== HELPDESK TI ===");
            System.out.println("1 - Cadastrar técnico");
            System.out.println("2 - Listar técnicos");
            System.out.println("3 - Abrir chamado");
            System.out.println("4 - Consultar chamado (mostra o técnico responsável)");
            System.out.println("5 - Listar chamados de um técnico");
            System.out.println("6 - Alterar chamado (prioridade, status e técnico)");
            System.out.println("7 - Excluir chamado");
            System.out.println("0 - Sair");
            opcao = lerInteiro(scanner, "Opção: ");

            switch (opcao) {
                case 1:
                    cadastrarTecnico(emf, scanner);
                    break;
                case 2:
                    listarTecnicos(emf);
                    break;
                case 3:
                    abrirChamado(emf, scanner);
                    break;
                case 4:
                    consultarChamado(emf, scanner);
                    break;
                case 5:
                    listarChamadosDoTecnico(emf, scanner);
                    break;
                case 6:
                    alterarChamado(emf, scanner);
                    break;
                case 7:
                    excluirChamado(emf, scanner);
                    break;
                case 0:
                    System.out.println("Encerrando...");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }

        // Encerrando a comunicação com o banco
        scanner.close();
        emf.close();
    }

    // Persistência (persist)
    private static void cadastrarTecnico(EntityManagerFactory emf, Scanner scanner) {
        String nome = lerTexto(scanner, "Nome: ");
        String especialidade = lerTexto(scanner, "Especialidade: ");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            // 1. Início
            tx.begin();

            // 2. Unidade de trabalho
            Tecnico tecnico = new Tecnico(nome, especialidade);
            em.persist(tecnico);

            // 3. Finalização
            tx.commit();
            System.out.println("Técnico cadastrado. ID: " + tecnico.getId());

        } catch (RuntimeException e) {
            // 4. Falha
            if (tx.isActive()) {
                tx.rollback();
            }
            System.out.println("Não foi possível cadastrar o técnico: " + e.getMessage());

        } finally {
            // 5. Liberação dos recursos
            em.close();
        }
    }

    // Consulta de todos os técnicos (createQuery)
    private static void listarTecnicos(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();

        try {
            List<Tecnico> tecnicos = em.createQuery("select t from Tecnico t", Tecnico.class).getResultList();

            if (tecnicos.isEmpty()) {
                System.out.println("Nenhum técnico cadastrado.");
            }
            for (Tecnico t : tecnicos) {
                System.out.println(t);
            }

        } finally {
            em.close();
        }
    }

    // Persistência considerando o relacionamento: o chamado recebe o
    // objeto Tecnico, que vira a chave estrangeira id_tecnico
    private static void abrirChamado(EntityManagerFactory emf, Scanner scanner) {
        listarTecnicos(emf);
        Integer idTecnico = lerInteiro(scanner, "ID do técnico responsável: ");
        String titulo = lerTexto(scanner, "Problema (título): ");
        String descricao = lerTexto(scanner, "Descrição: ");
        String prioridade = lerTexto(scanner, "Prioridade (Baixa, Média ou Alta): ");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // Consulta e recupera o técnico existente
            Tecnico tecnico = em.find(Tecnico.class, idTecnico);

            if (tecnico == null) {
                throw new RuntimeException("Técnico não encontrado.");
            }

            // Instancia o chamado conforme o construtor informando o técnico
            Chamado chamado = new Chamado(titulo, descricao, prioridade, tecnico);
            tecnico.addChamado(chamado);

            // Persiste no banco
            em.persist(chamado);
            tx.commit();
            System.out.println("Chamado aberto. ID: " + chamado.getId());

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            System.out.println("Não foi possível abrir o chamado: " + e.getMessage());

        } finally {
            em.close();
        }
    }

    // Consulta de um chamado e o técnico respectivo (find + navegação)
    private static void consultarChamado(EntityManagerFactory emf, Scanner scanner) {
        Integer id = lerInteiro(scanner, "ID do chamado: ");

        EntityManager em = emf.createEntityManager();

        try {
            // Localizando o objeto pela chave primária
            Chamado chamado = em.find(Chamado.class, id);

            if (chamado == null) {
                System.out.println("Chamado não encontrado.");
            } else {
                // O toString imprime o chamado com chamado.getTecnico().getNome()
                System.out.println();
                System.out.println(chamado);
            }

        } finally {
            em.close();
        }
    }

    // Consulta dos chamados de um determinado técnico
    // (navegação pelo lado @OneToMany: tecnico.getChamados())
    private static void listarChamadosDoTecnico(EntityManagerFactory emf, Scanner scanner) {
        listarTecnicos(emf);
        Integer id = lerInteiro(scanner, "ID do técnico: ");

        EntityManager em = emf.createEntityManager();

        try {
            Tecnico tecnico = em.find(Tecnico.class, id);

            if (tecnico == null) {
                System.out.println("Técnico não encontrado.");
                return;
            }

            System.out.println();
            System.out.println("Técnico: " + tecnico.getNome() + " (" + tecnico.getEspecialidade() + ")");
            System.out.println("Chamados:");

            if (tecnico.getChamados().isEmpty()) {
                System.out.println("  (nenhum)");
            }
            for (Chamado c : tecnico.getChamados()) {
                System.out.println("  #" + c.getId() + " - " + c.getTitulo()
                        + " | Prioridade: " + c.getPrioridade()
                        + " | Status: " + c.getStatus());
            }

        } finally {
            em.close();
        }
    }

    // Altera prioridade, status e técnico responsável de uma só vez.
    private static void alterarChamado(EntityManagerFactory emf, Scanner scanner) {
        Integer id = lerInteiro(scanner, "ID do chamado: ");
        String novaPrioridade = lerTexto(scanner, "Nova prioridade (Baixa, Média ou Alta): ");
        String novoStatus = lerTexto(scanner, "Novo status (Aberto, Em atendimento ou Concluído): ");
        listarTecnicos(emf);
        Integer idTecnico = lerInteiro(scanner, "ID do técnico responsável: ");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            // Recuperação do chamado que deseja-se atualizar
            Chamado chamado = em.find(Chamado.class, id);
            Tecnico tecnico = em.find(Tecnico.class, idTecnico);

            if (chamado == null) {
                throw new RuntimeException("Chamado não encontrado.");
            }
            if (tecnico == null) {
                throw new RuntimeException("Técnico não encontrado.");
            }

            // Atribuição dos novos valores aos atributos
            chamado.setPrioridade(novaPrioridade);
            chamado.setStatus(novoStatus);

            // Troca do técnico: no banco, só a coluna id_tecnico muda
            chamado.getTecnico().removeChamado(chamado);
            chamado.setTecnico(tecnico);
            tecnico.addChamado(chamado);

            // Efetivação da atualização no banco
            tx.commit();
            System.out.println("Chamado atualizado.");
            System.out.println(chamado);

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            System.out.println("Não foi possível alterar o chamado: " + e.getMessage());

        } finally {
            em.close();
        }
    }

    // Delete (remove)
    private static void excluirChamado(EntityManagerFactory emf, Scanner scanner) {
        Integer id = lerInteiro(scanner, "ID do chamado: ");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Chamado chamado = em.find(Chamado.class, id);

            if (chamado == null) {
                throw new RuntimeException("Chamado não encontrado.");
            }

            // Excluindo o registro
            chamado.getTecnico().removeChamado(chamado);
            em.remove(chamado);
            tx.commit();
            System.out.println("Chamado excluído.");

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            System.out.println("Não foi possível excluir o chamado: " + e.getMessage());

        } finally {
            em.close();
        }
    }


    private static String lerTexto(Scanner scanner, String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String valor = scanner.nextLine().trim();
            if (!valor.isEmpty()) {
                return valor;
            }
            System.out.println("Informe um valor.");
        }
    }

    private static Integer lerInteiro(Scanner scanner, String mensagem) {
        while (true) {
            System.out.print(mensagem);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

}
