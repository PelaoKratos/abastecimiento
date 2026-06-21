package microservice.abastecimiento.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import microservice.abastecimiento.model.Proveedor;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

	List<Proveedor> findByEstado(String estado);
}
