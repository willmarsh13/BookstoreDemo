package business.cart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * A shopping cart arrives in an order form from the client.
 * This class holds the de-serialized JSON data.
 * <p>
 * (We ignore any extra elements that the client sends
 *  that this class does not require.).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShoppingCart {

	private static final int SURCHARGE = 5;

	private List<ShoppingCartItem> itemArray = new ArrayList<>();

	public ShoppingCart() {
	}

	public int getSurcharge() {
		return SURCHARGE;
	}

	public List<ShoppingCartItem> getItems() {
		return itemArray;
	}

	public void setItemArray(List<ShoppingCartItem> itemArray) {
		this.itemArray = itemArray;
	}

	/**
	 * Returns the sum of the item price multiplied by the quantity for all
	 * itemArray in shopping cart list. This is the total cost *in cents*,
	 * not including the surcharge.
	 *
	 * @return total of itemArray in cart, excluding surcharge
	 */
	@JsonIgnore
	public int getComputedSubtotal() {
		return itemArray.stream()
				.mapToInt(item -> item.getQuantity() * item.getBookForm().getPrice())
				.sum();
	}

}
