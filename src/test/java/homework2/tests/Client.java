package homework2.tests;

public class Client {
	private String fullName;
	private String phoneNumber;
	private String email;
	private String address;
	private String expectedPhoneNumber;
	private String expectedAddress;

	private Client(ClientBuilder builder){
		this.fullName = builder.fullName;
		this.phoneNumber = builder.phoneNumber;
		this.email = builder.email;
		this.address = builder.address;
		this.expectedPhoneNumber = builder.expectedPhoneNumber;
		this.expectedAddress = builder.expectedAddress;
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

	public static class ClientBuilder{
		private String fullName;
		private String phoneNumber;
		private String email;
		private String address;
		private String expectedPhoneNumber;
		private String expectedAddress;

		public ClientBuilder(String fullName, String phoneNumber, String email, String address) {
			this.fullName = fullName;
			this.phoneNumber = phoneNumber;
			this.email = email;
			this.address = address;
			this.expectedPhoneNumber = generateExpectedPhoneNumber(phoneNumber);
		}

		public ClientBuilder setExpectedPhoneNumber(String expectedPhoneNumber) {
			this.expectedPhoneNumber = expectedPhoneNumber;
			return this;
		}

		public ClientBuilder setExpectedAddress(String expectedAddress) {
			this.expectedAddress = expectedAddress;
			return this;
		}

		public Client build(){
			return new Client(this);
		}

		private String generateExpectedPhoneNumber(String string){
			StringBuilder numbers = new StringBuilder();
			StringBuilder formatted = new StringBuilder("+7 ");
			char character;
			for(int i=0;i<string.length();i++)
			{
				character = string.charAt(i);
				if(Character.isDigit(character))
					numbers.append(character);
			}
			int count = numbers.length();

			if (count == 10) {
				formatted.append(String.format("(%s) %s-%s", numbers.substring(0,3), numbers.substring(3, 6), numbers.substring(6, count)));
			}

			return formatted.toString();
		}

	}


}
