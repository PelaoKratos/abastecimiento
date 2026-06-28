package microservice.abastecimiento;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

class AbastecimientoApplicationTests {

	@Test
	void applicationClassExists() {
		assertThat(AbastecimientoApplication.class).isNotNull();
		assertThat(new AbastecimientoApplication()).isNotNull();
	}

	@Test
	void mainIniciaSpringApplication() {
		String[] args = { "--spring.profiles.active=test" };

		try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
			AbastecimientoApplication.main(args);

			springApplication.verify(() -> SpringApplication.run(eq(AbastecimientoApplication.class), eq(args)));
		}
	}
}
