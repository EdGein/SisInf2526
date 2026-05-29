package isel.sisinf.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "portefolio", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cliente_nif", "nome"})
})
public class Portefolio implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portefolio_id")
    private Long portefolioId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_nif", referencedColumnName = "nif", nullable = false)
    private Cliente cliente;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "valor_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @OneToMany(mappedBy = "portefolio", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Posicao> posicoes;

    // Construtores
    public Portefolio() {}

    public Portefolio(Cliente cliente, String nome) {
        this.cliente = cliente;
        this.nome = nome;
        this.valorTotal = BigDecimal.ZERO;
    }

    // Getters e Setters
    public Long getPortefolioId() { return portefolioId; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public List<Posicao> getPosicoes() { return posicoes; }
}