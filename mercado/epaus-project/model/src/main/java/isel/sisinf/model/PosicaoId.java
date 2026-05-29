package isel.sisinf.model;

import java.io.Serializable;
import java.util.Objects;

public class PosicaoId implements Serializable {
    private Long portefolio;
    private String instrumento;

    public PosicaoId() {}

    public PosicaoId(Long portefolio, String instrumento) {
        this.portefolio = portefolio;
        this.instrumento = instrumento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PosicaoId entity = (PosicaoId) o;
        return Objects.equals(this.portefolio, entity.portefolio) &&
                Objects.equals(this.instrumento, entity.instrumento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(portefolio, instrumento);
    }
}
