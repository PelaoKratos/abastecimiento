package microservice.abastecimiento.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DetalleRecepcionMercancia {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idDetalleRecepcion;

	@NotNull(message = "El id de producto es obligatorio")
	private Long idProducto;

	@NotNull(message = "La cantidad esperada es obligatoria")
	@PositiveOrZero(message = "La cantidad esperada no puede ser negativa")
	private Integer cantidadEsperada;

	@NotNull(message = "La cantidad recibida es obligatoria")
	@PositiveOrZero(message = "La cantidad recibida no puede ser negativa")
	private Integer cantidadRecibida;

	private String estado;
	private String observacion;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_recepcion", nullable = false)
	private RecepcionMercancia recepcion;
}
