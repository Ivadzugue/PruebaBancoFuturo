package stepDefinitions;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.WebDriver;
import Utils.DriverFactory;
import pageObjects.Login;

class LoginStep extends DriverFactory {
    WebDriver driver;



    @When("^I log in using valid credentials$")
    public void i_log_in_using_valid_credentials() throws Throwable {


        final Login login = DriverFactory.login.enterEmailAddress("user1@gmail.com");
        DriverFactory.login.enterPassword("&6Mu2tafa");
        DriverFactory.login.clickOnSubmiButton();

    }

    @Then("^I should be logged in$")
    public void i_should_be_logged_in() throws Throwable {

        login.confirmLoginWasSuccessful();

    }

    @When("^I log in using invalid credentials$")
    public void i_log_in_using_invalid_credentials() throws Throwable {

        login.enterEmailAddress("yolmustafa@gmail.com");
        login.enterPassword("eeeee");
        login.clickOnSubmiButton();
    }

    @Then("^I should not be logged in$")
    public void i_should_not_be_logged_in() throws Throwable {

        login.confirmLoginWasNotSuccessful();

    }
}