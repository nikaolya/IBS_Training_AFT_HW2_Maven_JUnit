package homework2.tests;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Test Suite: Страхование")
@SelectClasses({InsuranceTest.class, DummyTest.class})
public class InsuranceTestSuite {

}
