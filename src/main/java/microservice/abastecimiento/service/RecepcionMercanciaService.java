package microservice.abastecimiento.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import microservice.abastecimiento.exception.ResourceNotFoundException;
import microservice.abastecimiento.model.DetalleRecepcionMercancia;
import microservice.abastecimiento.model.OrdenCompra;
import microservice.abastecimiento.model.RecepcionMercancia;
import microservice.abastecimiento.repository.OrdenCompraRepository;
import microservice.abastecimiento.repository.RecepcionMercanciaRepository;

@Service
public class RecepcionMercanciaService {

	private static final String ESTADO_REGISTRADA = "REGISTRADA";
	private static final String ESTADO_CONFIRMADA = "CONFIRMADA";
	private static final String ESTADO_CON_DIFERENCIAS = "CON_DIFERENCIAS";

	private final RecepcionMercanciaRepository recepcionRepository;
	private final OrdenCompraRepository ordenRepository;

	public RecepcionMercanciaService(RecepcionMercanciaRepository recepcionRepository,
			OrdenCompraRepository ordenRepository) {
		this.recepcionRepository = recepcionRepository;
		this.ordenRepository = ordenRepository;
	}

	public List<RecepcionMercancia> listar() {
		return recepcionRepository.findAll();
	}

	public RecepcionMercancia obtenerPorId(Long id) {
		return buscarRecepcion(id);
	}

	public List<RecepcionMercancia> buscarPorOrden(Long idOrdenCompra) {
		return recepcionRepository.findByOrdenCompraIdOrdenCompra(idOrdenCompra);
	}

	@Transactional
	public RecepcionMercancia registrar(Long idOrdenCompra, RecepcionMercancia recepcion) {
		OrdenCompra orden = ordenRepository.findById(idOrdenCompra)
				.orElseThrow(() -> new ResourceNotFoundException("No existe la orden de compra con id " + idOrdenCompra));
		if (!"APROBADA".equals(orden.getEstado())) {
			throw new IllegalArgumentException("Solo se puede recepcionar una orden aprobada");
		}
		if (recepcion.getFechaRecepcion() == null) {
			recepcion.setFechaRecepcion(LocalDateTime.now());
		}
		recepcion.setEstado(ESTADO_REGISTRADA);
		recepcion.setOrdenCompra(orden);
		prepararDetalles(recepcion);
		return recepcionRepository.save(recepcion);
	}

	@Transactional
	public RecepcionMercancia confirmar(Long id) {
		RecepcionMercancia recepcion = buscarRecepcion(id);
		boolean tieneDiferencias = recepcion.getDetalles().stream()
				.anyMatch(detalle -> !detalle.getCantidadEsperada().equals(detalle.getCantidadRecibida()));
		recepcion.setEstado(tieneDiferencias ? ESTADO_CON_DIFERENCIAS : ESTADO_CONFIRMADA);
		recepcion.getOrdenCompra().setEstado("RECEPCIONADA");
		return recepcionRepository.save(recepcion);
	}

	private RecepcionMercancia buscarRecepcion(Long id) {
		return recepcionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe la recepcion con id " + id));
	}

	private void prepararDetalles(RecepcionMercancia recepcion) {
		if (recepcion.getDetalles() == null || recepcion.getDetalles().isEmpty()) {
			throw new IllegalArgumentException("La recepcion debe tener al menos un detalle");
		}
		for (DetalleRecepcionMercancia detalle : recepcion.getDetalles()) {
			if (detalle.getCantidadRecibida() > detalle.getCantidadEsperada()) {
				throw new IllegalArgumentException("La cantidad recibida no puede superar la cantidad esperada");
			}
			detalle.setRecepcion(recepcion);
			detalle.setEstado(detalle.getCantidadRecibida().equals(detalle.getCantidadEsperada()) ? "COMPLETO" : "PARCIAL");
		}
	}
}
