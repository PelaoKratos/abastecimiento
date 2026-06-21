package microservice.abastecimiento.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import microservice.abastecimiento.exception.ResourceNotFoundException;
import microservice.abastecimiento.model.DetalleOrdenCompra;
import microservice.abastecimiento.model.OrdenCompra;
import microservice.abastecimiento.model.Proveedor;
import microservice.abastecimiento.repository.OrdenCompraRepository;
import microservice.abastecimiento.repository.ProveedorRepository;

@Service
public class OrdenCompraService {

	private static final String ESTADO_CREADA = "CREADA";
	private static final String ESTADO_APROBADA = "APROBADA";
	private static final String ESTADO_CANCELADA = "CANCELADA";
	private static final String ESTADO_RECEPCIONADA = "RECEPCIONADA";

	private final OrdenCompraRepository ordenRepository;
	private final ProveedorRepository proveedorRepository;

	public OrdenCompraService(OrdenCompraRepository ordenRepository, ProveedorRepository proveedorRepository) {
		this.ordenRepository = ordenRepository;
		this.proveedorRepository = proveedorRepository;
	}

	public List<OrdenCompra> listar() {
		return ordenRepository.findAll();
	}

	public OrdenCompra obtenerPorId(Long id) {
		return buscarOrden(id);
	}

	public List<OrdenCompra> buscarPorEstado(String estado) {
		return ordenRepository.findByEstado(normalizarEstado(estado));
	}

	public List<OrdenCompra> buscarPorProveedor(Long idProveedor) {
		return ordenRepository.findByProveedorIdProveedor(idProveedor);
	}

	@Transactional
	public OrdenCompra crear(Long idProveedor, OrdenCompra orden) {
		Proveedor proveedor = proveedorRepository.findById(idProveedor)
				.orElseThrow(() -> new ResourceNotFoundException("No existe el proveedor con id " + idProveedor));
		if (orden.getFechaOrden() == null) {
			orden.setFechaOrden(LocalDateTime.now());
		}
		if (orden.getEstado() == null || orden.getEstado().isBlank()) {
			orden.setEstado(ESTADO_CREADA);
		} else {
			orden.setEstado(normalizarEstado(orden.getEstado()));
		}
		orden.setProveedor(proveedor);
		prepararDetalles(orden);
		calcularTotales(orden);
		return ordenRepository.save(orden);
	}

	@Transactional
	public OrdenCompra aprobar(Long id) {
		OrdenCompra orden = buscarOrden(id);
		if (ESTADO_CANCELADA.equals(orden.getEstado())) {
			throw new IllegalArgumentException("No se puede aprobar una orden cancelada");
		}
		orden.setEstado(ESTADO_APROBADA);
		return ordenRepository.save(orden);
	}

	@Transactional
	public OrdenCompra cancelar(Long id) {
		OrdenCompra orden = buscarOrden(id);
		if (ESTADO_RECEPCIONADA.equals(orden.getEstado())) {
			throw new IllegalArgumentException("No se puede cancelar una orden recepcionada");
		}
		orden.setEstado(ESTADO_CANCELADA);
		return ordenRepository.save(orden);
	}

	public void eliminar(Long id) {
		OrdenCompra orden = buscarOrden(id);
		ordenRepository.delete(orden);
	}

	private OrdenCompra buscarOrden(Long id) {
		return ordenRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe la orden de compra con id " + id));
	}

	private void prepararDetalles(OrdenCompra orden) {
		if (orden.getDetalles() == null || orden.getDetalles().isEmpty()) {
			throw new IllegalArgumentException("La orden debe tener al menos un detalle");
		}
		for (DetalleOrdenCompra detalle : orden.getDetalles()) {
			detalle.setOrdenCompra(orden);
			detalle.setSubtotal(detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad())));
		}
	}

	private void calcularTotales(OrdenCompra orden) {
		BigDecimal total = orden.getDetalles().stream()
				.map(DetalleOrdenCompra::getSubtotal)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		orden.setSubtotal(total);
		orden.setTotal(total);
	}

	private String normalizarEstado(String estado) {
		if (estado == null || estado.isBlank()) {
			throw new IllegalArgumentException("El estado no puede estar vacio");
		}
		String estadoNormalizado = estado.trim().toUpperCase();
		if (!List.of(ESTADO_CREADA, ESTADO_APROBADA, ESTADO_CANCELADA, ESTADO_RECEPCIONADA).contains(estadoNormalizado)) {
			throw new IllegalArgumentException("Estado de orden no valido: " + estado);
		}
		return estadoNormalizado;
	}
}
