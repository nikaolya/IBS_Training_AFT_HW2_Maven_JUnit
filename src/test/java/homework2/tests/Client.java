package homework2.tests;

public class Client {
	private String fullName;
	private String phoneNumber;
	private String email;
	private String address;
	private String expectedPhoneNumber;
	private String expectedAddress;

	public Client(String fullName, String phoneNumber, String email, String address) {
		this.fullName = fullName;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.address = address;
	}

	public void setExpectedPhoneNumber(String expectedPhoneNumber) {
		this.expectedPhoneNumber = expectedPhoneNumber;
	}

	public void setExpectedAddress(String expectedAddress) {
		this.expectedAddress = expectedAddress;
	}

	public String getFullName() {
		return fullName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public String getAddress() {
		return address;
	}

	public String getExpectedPhoneNumber() {
		return expectedPhoneNumber;
	}

	public String getExpectedAddress() {
		return expectedAddress;
	}
}
