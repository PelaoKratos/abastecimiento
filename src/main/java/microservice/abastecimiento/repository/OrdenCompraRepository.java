package microservice.abastecimiento.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import microservice.abastecimiento.model.OrdenCompra;

public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {

	List<OrdenCompra> findByEstado(String estado);

	List<OrdenCompra> findByProveedorIdProveedor(Long idProveedor);
}
