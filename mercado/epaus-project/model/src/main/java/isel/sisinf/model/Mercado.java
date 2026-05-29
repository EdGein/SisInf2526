package isel.sisinf.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "mercado")
public class Mercado implements Serializable {

    @Id
    @Column(name = "mercado_id", length = 20)
    private String mercadoId;

    @Column(name = "descricao", nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "nome_curto", nullable = false, length = 50)
    private String nomeCurto;

    // Relacionamento inverso: um mercado contém vários instrumentos.
    // mappedBy aponta para o campo 'mercado' declarado na entidade Instrumento.
    // FetchType.LAZY garante que a lista não é carregada automaticamente.
    @OneToMany(mappedBy = "mercado", fetch = FetchType.LAZY)
    private List<Instrumento> instrumentos;

    // Construtor sem parâmetros — OBRIGATÓRIO pela especificação JPA
    public Mercado() {}

    // Construtor de conveniência
    public Mercado(String mercadoId, String descricao, String nomeCurto) {
        this.mercadoId = mercadoId;
        this.descricao = descricao;
        this.nomeCurto = nomeCurto;
    }

    // Getters
    public String getMercadoId() { return mercadoId; }
    public String getDescricao() { return descricao; }
    public String getNomeCurto() { return nomeCurto; }
    public List<Instrumento> getInstrumentos() { return instrumentos; }

    // Setters
    public void setMercadoId(String mercadoId) { this.mercadoId = mercadoId; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setNomeCurto(String nomeCurto) { this.nomeCurto = nomeCurto; }
}
