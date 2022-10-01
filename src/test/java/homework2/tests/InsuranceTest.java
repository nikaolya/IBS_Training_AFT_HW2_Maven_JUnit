package homework2.tests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;


import static org.junit.jupiter.api.Assertions.*;


public class InsuranceTest {
	private WebDriver driver;
	private WebDriverWait wait;
	private JavascriptExecutor js;


	@BeforeEach
	public void setup(){
		System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("start-maximized");
		driver = new ChromeDriver(options);
		js = (JavascriptExecutor) driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		driver.manage().deleteAllCookies();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));

		driver.get("http://www.rgs.ru");
	}

	@DisplayName("Заявка на добровольное медицинское страхование Росгосстрах")
	@Test
	public void shouldApplyForVoluntaryInsuranceTest() throws InterruptedException {

		// Переходим в раздел "Компаниям"
		final String FOR_COMPANIES_XPATH = "//li[contains(@class, 'text--second')]/a[contains(@href, '/for-companies')]";
		WebElement forCompaniesButton = driver.findElement(By.xpath(FOR_COMPANIES_XPATH));
		wait.until(ExpectedConditions.visibilityOf(forCompaniesButton));

		closePopUpIfPresent();

		forCompaniesButton.click();
		wait.until(ExpectedConditions.attributeContains(forCompaniesButton, "class", "active"));


		// Переходим в раздел "Здоровье"
		final String HEALTH_XPATH = "//li/span[contains(@class, 'padding') and contains(text(), 'Здоровье')]";
		driver.findElement(By.xpath(HEALTH_XPATH)).click();
		wait.until(ExpectedConditions.attributeContains(By.xpath(String.format("%s/..", HEALTH_XPATH)), "class", "active"));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(@class, 'category-link') " +
				"and @href = '/for-companies/zdorove']")));

		closePopUpIfPresent();

		// Переходим в раздел "Добровольное медицинское страхование"
		final String VOLUNTARY_INSURANCE = "//a[contains(@href, 'dobrovolnoe-meditsinskoe-strakhovanie')]";
		driver.findElement(By.xpath(VOLUNTARY_INSURANCE)).click();
		wait.until(ExpectedConditions.urlContains("/dobrovolnoe-meditsinskoe-strakhovanie"));


		// Проверяем наличие заголовка h1
		final String VOLUNTARY_INSURANCE_PAGE_TITLE = "//h1[contains(@class, 'title')]";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(VOLUNTARY_INSURANCE_PAGE_TITLE)));
		WebElement voluntaryInsurancePageTitle = driver.findElement(By.xpath(VOLUNTARY_INSURANCE_PAGE_TITLE));
		assertEquals("Добровольное медицинское страхование", voluntaryInsurancePageTitle.getText(), "Заголовок страницы не верен");


		// Нажимаем на кнопку "Отправить заявку"
		final String SUBMIT_APPLICATION_BUTTON = "//button[./span[text() = 'Отправить заявку']]";
		driver.findElement(By.xpath(SUBMIT_APPLICATION_BUTTON)).click();


		// Тестовые данные
		Client client = new Client("Сидоров Иван Иванович",
				"921 999-99-09",
				"qwertyqwerty",
				"Купчинская 21-1-67");
		client.setExpectedAddress("г Санкт-Петербург, ул Купчинская, д 21 к 1, кв 67");
		client.setExpectedPhoneNumber("+7 (921) 999-9909");


		// Проматываем станицу до формы заполнения
		// Никакими другими ухищрениями это не работает, только с использованием sleep
		js.executeScript("window.scrollBy(0, 1900);");
		Thread.sleep(1000);

		// Проверяем наличие заголовка h2 на странице
		WebElement sectionTitle = driver.findElement(By.xpath("//h2[contains(@class, 'title--h2') and contains(text(), 'перезвоним')]"));
		assertTrue(sectionTitle.getText().contains("Оперативно перезвоним"), "Секция заполнения заявки не открыта");

		// Заполняем поля имя, телефон, email
		fillInputFieldWithAttributeName("userName", client.getFullName(), client.getFullName());
		fillInputFieldWithAttributeName("userTel", client.getPhoneNumber(), client.getExpectedPhoneNumber());
		fillInputFieldWithAttributeName("userEmail", client.getEmail(), client.getEmail());

		// Заполняем поле адрес
		fillInputFieldAddress(client, client.getExpectedAddress());

		// Устанавливаем чекбокс "Я соглашаюсь с условиями"
//		js.executeScript("window.scrollBy(0, 300);");
//		Thread.sleep(500);
		WebElement checkbox = driver.findElement(By.xpath("//input[@type='checkbox']"));
		js.executeScript("arguments[0].click();", checkbox);
		wait.until(ExpectedConditions.attributeToBe(checkbox, "value", "true"));

		// Нажимаем кнопку "Свяжитесь со мной"
		final String SUBMIT_BUTTON_XPATH = "//button[@type = 'submit']";
		WebElement submitButton = driver.findElement(By.xpath(SUBMIT_BUTTON_XPATH));
		submitButton.click();

		WebElement emailError = driver.findElement(By.xpath("//input[@name='userEmail']/../following-sibling::span"));
		assertAll("Неверное сообщение об ошибке поля email",
				() -> assertTrue(emailError.isDisplayed()),
				() -> assertEquals("Введите корректный адрес электронной почты", emailError.getText()),
				() -> assertEquals("rgba(183, 0, 55, 1)", emailError.getCssValue("color")));

	}

	@AfterEach
	public void tearDown(){
		if (driver != null){
			driver.manage().deleteAllCookies();
			driver.quit();
		}
	}

	private void fillInputFieldAddress(Client client, String expectedAddress) throws InterruptedException {
		WebElement inputFieldEmail = driver.findElement(By.xpath("//input[@type='text' and @placeholder = 'Введите']"));
		scrollToElementJs(inputFieldEmail);
		wait.until(ExpectedConditions.elementToBeClickable(inputFieldEmail));
		inputFieldEmail.click();
		WebElement flex = inputFieldEmail.findElement(By.xpath("./../following-sibling::div"));
		wait.until(ExpectedConditions.attributeContains(flex, "class", "vue-dadata__suggestions"));
		inputFieldEmail.clear();
		inputFieldEmail.sendKeys(client.getAddress());
		Thread.sleep(1000); // снова по-другому у меня ничего не работает
		inputFieldEmail.sendKeys(Keys.ARROW_DOWN);
		inputFieldEmail.sendKeys(Keys.ENTER);

		String script = "return document.querySelectorAll('input.vue-dadata__input')[0].value;";
		String suggestedAddress = js.executeScript(script).toString();

		assertEquals(expectedAddress, suggestedAddress, "Введенное значение неверно");
	}


	private void fillInputFieldWithAttributeName(String name, String dataToInsert, String expectedData) {

		final String INPUT_FIELD_XPATH = "//input[@name=\'%s\']";
		WebElement element = driver.findElement(By.xpath(String.format(INPUT_FIELD_XPATH, name)));
		scrollToElementJs(element);
		wait.until(ExpectedConditions.elementToBeClickable(element));
		element.click();
		WebElement label = element.findElement(By.xpath("./../preceding-sibling::label"));
		wait.until(ExpectedConditions.attributeContains(label, "class", "active"));
		element.sendKeys(dataToInsert);

		String script = String.format("return document.getElementsByName('%s')[0].value;", name);
		String actualDataInserted = js.executeScript(script).toString();
		//String actualDataInserted = element.getAttribute("value");
		assertEquals(expectedData, actualDataInserted, "Введенное значение неверно");
	}

	private void scrollToElementJs(WebElement element) {
		js.executeScript("arguments[0].scrollIntoView(true);", element);
	}

	private void closePopUpIfPresent(){
		try {
			WebElement popup = driver.findElement(By.xpath("//iframe[contains(@title, 'widget')]"));
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(popup));

			WebElement closePopupButton = driver.findElement(By.xpath("//div[@data-fl-track = 'click-close-login']"));
			wait.until(ExpectedConditions.visibilityOf(closePopupButton));
			closePopupButton.click();

			//System.out.println("CLOSED POPUP");
		} catch (Exception e){

		} finally {
			driver.switchTo().defaultContent();
		}
	}
}

