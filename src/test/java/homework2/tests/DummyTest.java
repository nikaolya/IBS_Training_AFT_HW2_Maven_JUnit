package homework2.tests;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;

@Epic("Компаниям")
@Feature("Страхование")
@DisplayName("Ничаво")
public class DummyTest {

	@Test
	@Tags({@Tag("UI"), @Tag("positive")})
	@Story("Добровольное медицинское страхование")
	@DisplayName("Глупый упавший тест")
	@Severity(SeverityLevel.MINOR)
	public void doNothing(){
		Assertions.fail();
	}
}
