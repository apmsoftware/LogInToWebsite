package com.aarya.login;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BrowserAutomation {

	public String performLoginTest() {
		System.out.println("Performing Login...");

		System.out.println("Done Login...");
		return "SUCCESS";
	}

	public String performLoginUsingDriver(WebDriver driver, WebElement username, WebElement password,
			WebElement loginButton, String encUsername, String encPassword, String privateKey) throws Exception {
		
		PageFactory.initElements(driver, this);

		// Convert the private key string back into a PrivateKey object
		PrivateKey convertedPrivateKey = privateKeyFromString(privateKey);

		// Decrypt the encrypted string using the converted private key
		//String decryptedUsernameText = decrypt(encUsername, convertedPrivateKey);
		String decryptedPasswordText = decrypt(encPassword, convertedPrivateKey);

		 //System.out.println("************ " + decryptedUsernameText + " **********");
		 System.out.println("************ " + decryptedPasswordText + " **********");

		username.sendKeys("tomsmith");
		password.sendKeys(decryptedPasswordText);

		loginButton.click();

		return "Success";
	}

	public WebDriver performLogin(String url, String username, String password, String privateKey) throws Exception {
		// Set the path to the ChromeDriver executable
		WebDriverManager.chromedriver().setup();

		// Create a new instance of ChromeDriver
		WebDriver driver = new ChromeDriver();

		// Open the specified URL
		driver.get(url);

		// Find the username and password input elements
		WebElement usernameInput = driver.findElement(By.xpath("//input[@name='username']"));
		WebElement passwordInput = driver.findElement(By.xpath("//input[@name='password']"));

		// Convert the private key string back into a PrivateKey object
		PrivateKey convertedPrivateKey = privateKeyFromString(privateKey);

		// Decrypt the encrypted string using the converted private key
		String decryptedUsernameText = decrypt(username, convertedPrivateKey);
		String decryptedPasswordText = decrypt(password, convertedPrivateKey);

		// System.out.println("************ " + decryptedUsernameText + " **********");
		// System.out.println("************ " + decryptedPasswordText + " **********");

		// Enter the username and password
		usernameInput.sendKeys(decryptedUsernameText);
		passwordInput.sendKeys(decryptedPasswordText);

		// Find and click the login button
		WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));
		loginButton.click();

		// Return the WebDriver instance
		return driver;
	}

	public static String decrypt(String encryptedText, PrivateKey privateKey) throws Exception {
		byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
		return new String(decryptedBytes);
	}

	public static PrivateKey privateKeyFromString(String privateKeyString) throws Exception {
		byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
	}
}
