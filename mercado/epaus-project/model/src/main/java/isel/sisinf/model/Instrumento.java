package isel.sisinf.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "instrumento")
public class Instrumento implements Serializable {

    @Id
    @Column(name = "instrumento_id", length = 12)
    private String instrumentoId;

    @Column(name = "descricao", nullable = false, length = 256)
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mercado", referencedColumnName = "mercado_id", nullable = false)
    private Mercado mercado;

    // Relacionamento 1-para-1 com DadosFundamentais
    // A chave primária de DadosFundamentais é também a FK para Instrumento
    @OneToOne(mappedBy = "instrumento", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private DadosFundamentais dadosFundamentais;

    // Construtores
    public Instrumento() {}

    public Instrumento(String instrumentoId, String descricao, Mercado mercado) {
        this.instrumentoId = instrumentoId;
        this.descricao = descricao;
        this.mercado = mercado;
    }

    // Getters e Setters
    public String getInstrumentoId() { return instrumentoId; }
    public void setInstrumentoId(String instrumentoId) { this.instrumentoId = instrumentoId; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Mercado getMercado() { return mercado; }
    public void setMercado(Mercado mercado) { this.mercado = mercado; }
    public DadosFundamentais getDadosFundamentais() { return dadosFundamentais; }
}