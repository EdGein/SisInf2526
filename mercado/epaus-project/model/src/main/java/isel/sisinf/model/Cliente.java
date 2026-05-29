package isel.sisinf.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "cliente")
public class Cliente implements Serializable {
    @Id
    @Column(name = "nif", length = 20)
    private String nif;

    @Column(name = "cartao_cidadao", unique = true, nullable = false, length = 20)
    private String cartaoCidadao;

    @Column(name = "nome", nullable = false, length = 256)
    private String nome;

    @Version
    @Column(name = "versao")
    private int versao;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Portefolio> portefolios;

    // Construtor sem parâmetros — OBRIGATÓRIO pela especificação JPA
    public Cliente() {}

    // Construtor de conveniência
    public Cliente(String nif, String cartaoCidadao, String nome) {
        this.nif = nif;
        this.cartaoCidadao = cartaoCidadao;
        this.nome = nome;
    }

    // Getters
    public String getNif() { return nif; }
    public String getCartaoCidadao() { return cartaoCidadao; }
    public String getNome() { return nome; }
    public int getVersao() { return versao; }

    // getPortefolios() é o método que o JPA usa para aceder à coleção lazy.
    // O acesso a este método dentro de um contexto de persistência ativo
    // despoleta o SELECT automático à tabela portefolio (lazy loading).
    public List<Portefolio> getPortefolios() { return portefolios; }

    // Setters
    public void setNif(String nif) { this.nif = nif; }
    public void setCartaoCidadao(String cartaoCidadao) { this.cartaoCidadao = cartaoCidadao; }
    public void setNome(String nome) { this.nome = nome; }
    public void setPortefolios(List<Portefolio> portefolios) { this.portefolios = portefolios; }
}