package ar.com.dinamicaonline.rapipago.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ar.com.dinamicaonline.rapipago.models.Entidad;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

@Repository
public interface EntidadRepository extends JpaRepository<Entidad, Integer> {

    @Query(value = "SELECT ENTIDAD_ID FROM entidades WHERE DOCUMENTO = :customerId LIMIT 1", nativeQuery = true)
    public Long findByDocumentId(@Param("customerId") String customerId);

    @Procedure("proc_cash_in_app")
    String ingresoDinero(Long qIdEntidad, Double qPago, String qOrigen);
}
