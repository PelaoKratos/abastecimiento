package microservice.abastecimiento.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import microservice.abastecimiento.exception.ResourceNotFoundException;
import microservice.abastecimiento.model.OrdenCompra;
import microservice.abastecimiento.model.RecepcionMercancia;
import microservice.abastecimiento.repository.OrdenCompraRepository;
import microservice.abastecimiento.repository.RecepcionMercanciaRepository;

@Service
public class RecepcionMercanciaService {

	private static final String ESTADO_CONFIRMADA = "CONFIRMADA";

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
		recepcion.setOrdenCompra(orden);
		recepcion.registrarRecepcion();
		return recepcionRepository.save(recepcion);
	}

	@Transactional
	public RecepcionMercancia confirmar(Long id) {
		RecepcionMercancia recepcion = buscarRecepcion(id);
		recepcion.setEstado(ESTADO_CONFIRMADA);
		recepcion.getOrdenCompra().setEstado("RECEPCIONADA");
		return recepcionRepository.save(recepcion);
	}

	private RecepcionMercancia buscarRecepcion(Long id) {
		return recepcionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No existe la recepcion con id " + id));
	}
}
