package isel.sisinf.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "portefolio")
public class Portefolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portefolio_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_nif", nullable = false)
    private Cliente cliente;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "valor_total")
    private BigDecimal valorTotal = BigDecimal.ZERO;

    public Portefolio() {}
    public Portefolio(Cliente cliente, String nome) {
        this.cliente = cliente;
        this.nome = nome;
    }
}
