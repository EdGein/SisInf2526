package isel.sisinf.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "posicao")
@IdClass(PosicaoId.class)
public class Posicao implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portefolio", referencedColumnName = "portefolio_id")
    private Portefolio portefolio;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instrumento_isin", referencedColumnName = "instrumento_id")
    private Instrumento instrumento;

    @Column(name = "quantidade", nullable = false, precision = 15, scale = 4)
    private BigDecimal quantidade;

    // Construtor sem parâmetros — OBRIGATÓRIO pela especificação JPA.
    // O JPA instancia as entidades por reflexão ao carregar dados da base de dados,
    // pelo que necessita sempre de um construtor público ou protegido sem argumentos.
    public Posicao() {}

    // Construtor de conveniência para criar uma posição com todos os dados necessários
    public Posicao(Portefolio portefolio, Instrumento instrumento, BigDecimal quantidade) {
        this.portefolio = portefolio;
        this.instrumento = instrumento;
        this.quantidade = quantidade;
    }

    // Getters
    public Portefolio getPortefolio() { return portefolio; }
    public Instrumento getInstrumento() { return instrumento; }
    public BigDecimal getQuantidade() { return quantidade; }

    // Setters
    public void setPortefolio(Portefolio portefolio) { this.portefolio = portefolio; }
    public void setInstrumento(Instrumento instrumento) { this.instrumento = instrumento; }
    public void setQuantidade(BigDecimal quantidade) { this.quantidade = quantidade; }
}