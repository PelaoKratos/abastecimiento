package microservice.abastecimiento.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import microservice.abastecimiento.model.RecepcionMercancia;

public interface RecepcionMercanciaRepository extends JpaRepository<RecepcionMercancia, Long> {

	List<RecepcionMercancia> findByOrdenCompraIdOrdenCompra(Long idOrdenCompra);
}
