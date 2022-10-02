package homework2.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@Epic("Компаниям")
@Feature("Страхование")
@DisplayName("Страхование")
public class InsuranceTest {
	private static String browser;
	private WebDriver driver;
	private WebDriverWait wait;
	private JavascriptExecutor js;


	@BeforeAll
	public static void beforeAll(){
		browser = System.getProperty("browser", "chrome");
	}

	@BeforeEach
	public void setup(){

		driver = driverFactory(browser);

		js = (JavascriptExecutor) driver;
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		driver.manage().deleteAllCookies();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));

		// прерывание загрузки страницы через 5 секунд
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(5));
		try {
			driver.get("http://www.rgs.ru");
		} catch (TimeoutException ignore) {
		}

	}

	private WebDriver driverFactory(String browser){
		WebDriver driver = null;
		if (browser.equalsIgnoreCase("chrome")){
			WebDriverManager.chromedriver().setup();
			ChromeOptions options = new ChromeOptions();
			options.addArguments("start-maximized");
			driver = new ChromeDriver(options);
		} else if (browser.equalsIgnoreCase("firefox")) {
			WebDriverManager.firefoxdriver().setup();
			driver = new FirefoxDriver();
			driver.manage().window().maximize();
		} else {
			Assertions.fail("Некорректное имя браузера");
		}
		return driver;
	}

	@ParameterizedTest(name = "clientFullName = {0}, clientPhoneNumber = {1}")
	@CsvFileSource(resources = "/testData.csv", numLinesToSkip = 1)
	@Tags({@Tag("UI"), @Tag("negative")})
	@Story("Добровольное медицинское страхование")
	@DisplayName("Заявка на добровольное медицинское страхование Росгосстрах")
	@Severity(SeverityLevel.CRITICAL)
	void shouldApplyForVoluntaryInsuranceTest(String fullName, String phoneNumber) throws InterruptedException {

		// Переходим в раздел "Компаниям"
		final String FOR_COMPANIES_XPATH = "//li[contains(@class, 'text--second')]/a[contains(@href, '/for-companies')]";
		WebElement forCompaniesButton = driver.findElement(By.xpath(FOR_COMPANIES_XPATH));
		//js.executeScript("return window.stop");

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
		final String VOLUNTARY_INSURANCE_XPATH = "//a[contains(@href, 'dobrovolnoe-meditsinskoe-strakhovanie')]";
		driver.findElement(By.xpath(VOLUNTARY_INSURANCE_XPATH)).click();
		wait.until(ExpectedConditions.urlContains("/dobrovolnoe-meditsinskoe-strakhovanie"));


		// Проверяем наличие заголовка h1
		final String VOLUNTARY_INSURANCE_PAGE_TITLE_XPATH = "//h1[contains(@class, 'title')]";
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(VOLUNTARY_INSURANCE_PAGE_TITLE_XPATH)));
		WebElement voluntaryInsurancePageTitle = driver.findElement(By.xpath(VOLUNTARY_INSURANCE_PAGE_TITLE_XPATH));
		assertThat("Заголовок страницы не верен",
				voluntaryInsurancePageTitle.getText(), is("Добровольное медицинское страхование"));

		// Нажимаем на кнопку "Отправить заявку"
		final String SUBMIT_APPLICATION_BUTTON = "//button[./span[text() = 'Отправить заявку']]";
		driver.findElement(By.xpath(SUBMIT_APPLICATION_BUTTON)).click();

		// Тестовые данные
		Client client = new Client.ClientBuilder(
				fullName,
				phoneNumber,
				"qwertyqwerty",
				"Купчинская 21-1-67")
				.setExpectedAddress("г Санкт-Петербург, ул Купчинская, д 21 к 1, кв 67")
				.build();


		// Проматываем станицу до формы заполнения
		js.executeScript("window.scrollBy(0, 1900);");
		Thread.sleep(1000);

		// Проверяем наличие заголовка h2 на странице
		WebElement sectionTitle = driver.findElement(By.xpath("//h2[contains(@class, 'title--h2') and contains(text(), 'перезвоним')]"));
		assertThat("Секция заполнения заявки не открыта",
				sectionTitle.getText(), containsString("Оперативно перезвоним"));

		// Заполняем поля имя, телефон, email
		fillInputFieldWithAttributeName("userName", client.getFullName(), client.getFullName());
		fillInputFieldWithAttributeName("userTel", client.getPhoneNumber(), client.getExpectedPhoneNumber());
		fillInputFieldWithAttributeName("userEmail", client.getEmail(), client.getEmail());

		// Заполняем поле адрес
		fillInputFieldAddress(client, client.getExpectedAddress());

		// Устанавливаем чекбокс "Я соглашаюсь с условиями"
		WebElement checkbox = driver.findElement(By.xpath("//input[@type='checkbox']"));
		js.executeScript("arguments[0].click();", checkbox);
		wait.until(ExpectedConditions.attributeToBe(checkbox, "value", "true"));

		// Нажимаем кнопку "Свяжитесь со мной"
		final String SUBMIT_BUTTON_XPATH = "//button[@type = 'submit']";
		WebElement submitButton = driver.findElement(By.xpath(SUBMIT_BUTTON_XPATH));
		submitButton.click();

		WebElement emailError = driver.findElement(By.xpath("//input[@name='userEmail']/../../span"));
		assertAll("Неверное сообщение об ошибке поля email",
				() -> assertTrue(emailError.isDisplayed()),
				() -> assertThat(emailError.getText(), is("Введите корректный адрес электронной почты")),
				() -> assertThat(emailError.getCssValue("color"), is("rgba(183, 0, 55, 1)")));

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
		Thread.sleep(1500); // снова по-другому у меня ничего не работает
		inputFieldEmail.sendKeys(Keys.ARROW_DOWN);
		inputFieldEmail.sendKeys(Keys.ENTER);

		String script = "return document.querySelectorAll('input.vue-dadata__input')[0].value;";
		String suggestedAddress = js.executeScript(script).toString();
		assertThat("Введенное значение адреса неверно",
				suggestedAddress, is(expectedAddress));
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

		String actualDataInserted = element.getAttribute("value");
		assertThat("Введенное значение неверно",
				actualDataInserted, is(expectedData));
	}

	private void scrollToElementJs(WebElement element) {
		js.executeScript("arguments[0].scrollIntoView(true);", element);
	}

	private void closePopUpIfPresent(){
//		try {
//			WebElement popup = driver.findElement(By.xpath("//iframe[contains(@title, 'widget')]"));
//			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(popup));
//
//			WebElement closePopupButton = driver.findElement(By.xpath("//div[@data-fl-track = 'click-close-login']"));
//			wait.ignoring(StaleElementReferenceException.class)
//					.until(ExpectedConditions.textToBePresentInElement(closePopupButton, "×"));
//			closePopupButton.click();
//		} catch (NoSuchElementException e){
//		} finally {
			driver.switchTo().defaultContent();
//		}
	}


}

