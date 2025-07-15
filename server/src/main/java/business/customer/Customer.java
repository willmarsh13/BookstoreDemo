package business.customer;

import java.util.Date;

public record Customer(long customerId, String name, String address, String phone, String email,
					   String ccNumber, Date ccExpDate) {
}
