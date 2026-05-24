package isel.sisinf.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cliente" )
public class Cliente {
    @Id
    @Column(name = "nif", length = 20)
    private String nif;

    @Column(name = "cartao_cidadao", unique = true, nullable = false)
    private String cartaoCidadao;

    @Column(name = "nome", nullable = false)
    private String nome;

    // Requisito 6(e): Optimistic Locking
    @Version
    @Column(name = "versao")
    private Long versao;

    public Cliente() {}

    public String getNif() { return nif; }
    public void setNif(String nif) { this.nif = nif; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Long getVersao() { return versao; }
}