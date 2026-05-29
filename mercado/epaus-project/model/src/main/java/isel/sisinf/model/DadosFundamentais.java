package isel.sisinf.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "dados_fundamentais")
public class DadosFundamentais implements Serializable {

    @Id
    @Column(name = "instrumento_isin", length = 12)
    private String instrumentoIsin;

    // A FK é também a PK — mapeada com @MapsId e @OneToOne
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "instrumento_isin")
    private Instrumento instrumento;

    @Column(name = "variacao_diaria", nullable = false, precision = 15, scale = 2)
    private BigDecimal variacaoDiaria;

    @Column(name = "valor_actual", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorActual;

    @Column(name = "media_6_meses", nullable = false, precision = 15, scale = 2)
    private BigDecimal media6Meses;

    @Column(name = "variacao_6_meses", nullable = false, precision = 15, scale = 2)
    private BigDecimal variacao6Meses;

    @Column(name = "percentagem_variacao_diaria", nullable = false, precision = 7, scale = 2)
    private BigDecimal percentagemVariacaoDiaria;

    @Column(name = "percentagem_variacao_6_meses", nullable = false, precision = 7, scale = 2)
    private BigDecimal percentagemVariacao6Meses;

    // Construtores, Getters e Setters
    public DadosFundamentais() {}

    public BigDecimal getValorActual() { return valorActual; }
    public void setValorActual(BigDecimal valorActual) { this.valorActual = valorActual; }
    public BigDecimal getVariacaoDiaria() { return variacaoDiaria; }
    public void setVariacaoDiaria(BigDecimal variacaoDiaria) { this.variacaoDiaria = variacaoDiaria; }
    public Instrumento getInstrumento() { return instrumento; }
    public void setInstrumento(Instrumento instrumento) { this.instrumento = instrumento; }
}