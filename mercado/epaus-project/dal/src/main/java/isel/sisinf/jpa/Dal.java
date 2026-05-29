package isel.sisinf.jpa;

import isel.sisinf.model.*;
import jakarta.persistence.*;
import java.util.List;

public class Dal implements AutoCloseable {

    // O EntityManagerFactory é pesado de criar e deve ser partilhado.
    // Cria-se uma única instância estática para toda a aplicação.
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("ePausPU");

    // O EntityManager é leve e representa uma única unidade de trabalho (Unit of Work).
    // Deve ser criado por operação ou por sessão de utilizador.
    private final EntityManager em;

    public Dal() {
        this.em = emf.createEntityManager();
    }

    // Método original mantido para compatibilidade com App.java
    public static String version() { return "1.0"; }

    // --- Gestão de Transações ---

    public void beginTransaction() {
        em.getTransaction().begin();
    }

    public void commit() {
        em.getTransaction().commit();
    }

    public void rollback() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

    // --- Métodos de Negócio (a implementar por alínea) ---

    // Alínea 6a: Criar cliente e contacto através da vista contacto_cliente
    public void criarClienteComContacto(ContactoCliente contacto) {
        beginTransaction();
        try {
            em.persist(contacto);
            commit();
        } catch (Exception e) {
            rollback();
            throw e;
        }
    }

    // Alínea 6b: Criar portefólio para um cliente existente
    public Portefolio criarPortefolio(String nifCliente, String nomePortefolio) {
        beginTransaction();
        try {
            Cliente cliente = em.find(Cliente.class, nifCliente);
            if (cliente == null) throw new IllegalArgumentException("Cliente não encontrado: " + nifCliente);
            Portefolio p = new Portefolio(cliente, nomePortefolio);
            em.persist(p);
            commit();
            return p;
        } catch (Exception e) {
            rollback();
            throw e;
        }
    }

    // Alínea 6c: Listar posições de um cliente pelo NIF
    public List<Portefolio> listarPortefoliosPorNif(String nif) {
        Cliente cliente = em.find(Cliente.class, nif);
        if (cliente == null) throw new IllegalArgumentException("Cliente não encontrado: " + nif);
        // A lista de portefólios é lazy — o acesso aqui força o carregamento
        return cliente.getPortefolios();
    }

    // Alínea 6d: Invocar o procedimento armazenado p_actualizaValorDiario
    public void actualizaValorDiario() {
        beginTransaction();
        try {
            em.createNativeQuery("CALL p_actualizaValorDiario()").executeUpdate();
            commit();
        } catch (Exception e) {
            rollback();
            throw e;
        }
    }

    // Alínea 6e: Atualizar dados do cliente com Optimistic Locking
    public void atualizarCliente(String nif, String novoNome) {
        beginTransaction();
        try {
            Cliente cliente = em.find(Cliente.class, nif);
            if (cliente == null) throw new IllegalArgumentException("Cliente não encontrado: " + nif);
            cliente.setNome(novoNome);
            // O JPA deteta a alteração automaticamente (dirty checking) e emite
            // UPDATE cliente SET nome=?, versao=versao+1 WHERE nif=? AND versao=?
            // Se a versão não coincidir, lança OptimisticLockException no commit.
            commit();
        } catch (RollbackException e) {
            // O commit falhou — verificar se foi por conflito de versão
            if (e.getCause() instanceof OptimisticLockException) {
                throw new OptimisticLockException(
                        "Conflito de concorrência: os dados do cliente foram alterados por outro utilizador.", e);
            }
            throw e;
        } catch (Exception e) {
            rollback();
            throw e;
        }
    }

    // --- Libertação de Recursos (AutoCloseable) ---

    @Override
    public void close() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    // Fechar a factory quando a aplicação terminar (chamar no main ou shutdown hook)
    public static void shutdown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}