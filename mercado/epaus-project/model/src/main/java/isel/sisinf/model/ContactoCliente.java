package isel.sisinf.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "contacto_cliente")
public class ContactoCliente implements Serializable {

    // O ID da vista é o contacto_id (PK da tabela física subjacente)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contacto_id")
    private Long contactoId;

    // Dados do Cliente (provenientes do JOIN com a tabela cliente na vista)
    @Column(name = "nif", nullable = false, length = 20)
    private String nif;

    @Column(name = "cartao_cidadao", nullable = false, length = 20)
    private String cartaoCidadao;

    @Column(name = "nome", nullable = false, length = 256)
    private String nome;

    // Discriminador: 'Email' ou 'Telefone'
    @Column(name = "tipo_contacto", nullable = false, length = 10)
    private String tipoContacto;

    // O valor do contacto (endereço de email ou número de telefone)
    @Column(name = "contacto", nullable = false)
    private String contacto;

    @Column(name = "descricao", nullable = false, length = 50)
    private String descricao;

    // Construtores
    public ContactoCliente() {}

    // Getters e Setters
    public Long getContactoId() { return contactoId; }
    public String getNif() { return nif; }
    public void setNif(String nif) { this.nif = nif; }
    public String getCartaoCidadao() { return cartaoCidadao; }
    public void setCartaoCidadao(String cartaoCidadao) { this.cartaoCidadao = cartaoCidadao; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getTipoContacto() { return tipoContacto; }
    public void setTipoContacto(String tipoContacto) { this.tipoContacto = tipoContacto; }
    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
