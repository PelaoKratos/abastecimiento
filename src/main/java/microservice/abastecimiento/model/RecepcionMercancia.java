package microservice.abastecimiento.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RecepcionMercancia {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idRecepcion;

	@Column(name = "id_orden_compra", insertable = false, updatable = false)
	private Long idOrdenCompra;

	@NotNull(message = "El id de usuario es obligatorio")
	private Long idUsuario;

	@Column(nullable = false)
	private LocalDateTime fechaRecepcion;

	private String observacion;

	@Column(nullable = false)
	private String estado;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_orden_compra", nullable = false)
	private OrdenCompra ordenCompra;

	public void registrarRecepcion() {
		if (fechaRecepcion == null) {
			fechaRecepcion = LocalDateTime.now();
		}
		estado = "REGISTRADA";
	}

	public void validarMercancia() {
		estado = "VALIDADA";
	}

	public void confirmarRecepcion() {
		estado = "CONFIRMADA";
	}

	public void setOrdenCompra(OrdenCompra ordenCompra) {
		this.ordenCompra = ordenCompra;
		this.idOrdenCompra = ordenCompra != null ? ordenCompra.getIdOrdenCompra() : null;
	}
}
