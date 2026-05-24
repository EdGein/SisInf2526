/*
MIT License

Copyright (c) 2025-2026, Nuno Datia, ISEL

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package isel.sisinf.jpa;

import jakarta.persistence.*;
import jakarta.persistence.Query;
import isel.sisinf.model.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class Dal implements AutoCloseable {
    private static EntityManagerFactory _emf;
    private final EntityManager _em;

    private static EntityManagerFactory getEMF() {
        if (_emf == null || !_emf.isOpen()) {
            _emf = Persistence.createEntityManagerFactory("epaus-pu");
        }
        return _emf;
    }

    public Dal() { this._em = getEMF().createEntityManager(); }
    public void beginTransaction() { _em.getTransaction().begin(); }
    public void commit() { _em.getTransaction().commit(); }
    public void rollback() { if (_em.getTransaction().isActive()) _em.getTransaction().rollback(); }

    // Requisito 6(a)
    public void criarClienteComContacto(String nif, String cc, String nome, String tipo, String contacto, String desc) {
        jakarta.persistence.Query q = _em.createNativeQuery(
                "INSERT INTO contacto_cliente (nif, cartao_cidadao, nome, tipo_contacto, contacto, descricao) " +
                        "VALUES (:nif, :cc, :nome, :tipo, :contacto, :desc)"
        );
        q.setParameter("nif", nif);
        q.setParameter("cc", cc);
        q.setParameter("nome", nome);
        q.setParameter("tipo", tipo);
        q.setParameter("contacto", contacto);
        q.setParameter("desc", desc);
        q.executeUpdate();
    }

    // Requisito 6(b)
    public void criarPortfolio(String nif, String nome) {
        Cliente c = _em.find(Cliente.class, nif);
        if (c != null) _em.persist(new Portefolio(c, nome));
    }

    // Requisito 6(c)
    @SuppressWarnings("unchecked")
    public List<Object[]> listarPosicoes(String nif) {
        return _em.createNativeQuery(
                "SELECT p.nome, lp.isin, lp.quantidade, lp.valor_atual, lp.perc_variacao " +
                        "FROM portefolio p JOIN LATERAL fx_portefolio_info(p.portefolio_id) lp ON TRUE " +
                        "WHERE p.cliente_nif = :nif"
        ).setParameter("nif", nif).getResultList();
    }

    // Requisito 6(d)
    public void atualizarValorDiario(String isin, BigDecimal valor) {
        _em.createNativeQuery("CALL p_actualizaValorDiario(:isin, :dt, :val)")
                .setParameter("isin", isin).setParameter("dt", new Timestamp(System.currentTimeMillis()))
                .setParameter("val", valor).executeUpdate();
    }

    // Requisito 6(e)
    public Cliente encontrarCliente(String nif) { return _em.find(Cliente.class, nif); }
    public void atualizarCliente(Cliente c) { _em.merge(c); }

    @Override
    public void close() { if (_em != null && _em.isOpen()) _em.close(); }
    public static String version() { return "1.0"; }
}