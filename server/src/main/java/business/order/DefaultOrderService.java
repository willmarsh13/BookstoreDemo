package business.order;

import api.ApiException;
import business.BookstoreDbException;
import business.JdbcUtils;
import business.book.Book;
import business.book.BookDao;
import business.cart.ShoppingCart;
import business.cart.ShoppingCartItem;
import business.customer.Customer;
import business.customer.CustomerDao;
import business.customer.CustomerForm;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultOrderService implements OrderService {

    private BookDao bookDao;
    private OrderDao orderDao;
    private LineItemDao lineItemDao;
    private CustomerDao customerDao;

    public void setBookDao(BookDao bookDao) {
        this.bookDao = bookDao;
    }

    public void setOrderDao(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    public void setLineItemDao(LineItemDao lineItemDao) {
        this.lineItemDao = lineItemDao;
    }

    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public OrderDetails getOrderDetails(long orderId) {
        Order order = orderDao.findByOrderId(orderId);
        Customer customer = customerDao.findByCustomerId(order.customerId());
        List<LineItem> lineItems = lineItemDao.findByOrderId(orderId);
        List<Book> books = lineItems
                .stream()
                .map(lineItem -> bookDao.findByBookId(lineItem.bookId()))
                .toList();
        return new OrderDetails(order, customer, lineItems, books);
    }

    @Override
    public long placeOrder(CustomerForm customerForm, ShoppingCart cart) {

        validateCustomer(customerForm);
        validateCart(cart);

        try (Connection connection = JdbcUtils.getConnection()) {
            Date ccExpDate = getCardExpirationDate(
                    customerForm.getCcExpiryMonth(),
                    customerForm.getCcExpiryYear());
            return performPlaceOrderTransaction(
                    customerForm.getName(),
                    customerForm.getAddress(),
                    customerForm.getPhone(),
                    customerForm.getEmail(),
                    customerForm.getCcNumber(),
                    ccExpDate, cart, connection);
        } catch (SQLException e) {
            throw new BookstoreDbException("Error during close connection for customer order", e);
        }
    }


    private void validateCustomer(CustomerForm customerForm) {

        String name = customerForm.getName();
        String address = customerForm.getAddress();
        String phone = customerForm.getPhone();
        String email = customerForm.getEmail();
        String ccNumber = customerForm.getCcNumber();

        //Make sure values are not empty or null
        CheckIsNotEmpty(name, "name");
        CheckIsNotEmpty(address, "address");
        CheckIsNotEmpty(phone, "phone");
        CheckIsNotEmpty(email, "email");
        CheckIsNotEmpty(ccNumber, "ccNumber");

        //Check name and address to make sure they are between 4 and 45
        CheckLengthBetween4and45(name, "name");
        CheckLengthBetween4and45(address, "address");

        //Check phone number field to make sure it's 10 numbers
        CheckPhoneNumber(phone);

        //Check email field to make sure it has no spaces, contains an @ symbol, and last character is not a period.
        CheckEmail(email);

        //Check CC Number to make sure the length is between 14 and 16 without spaces and dashes.
        CheckCCNumber(ccNumber);

        if (expiryDateIsInvalid(customerForm.getCcExpiryMonth(), customerForm.getCcExpiryYear())) {
            throw new ApiException.InvalidParameter("Please enter a valid expiration date.");
        }
    }

    private boolean expiryDateIsInvalid(String ccExpiryMonth, String ccExpiryYear) {
        try {
            if (ccExpiryMonth == null || ccExpiryYear == null) {
                throw new ApiException.ValidationFailure("Please enter a valid expiration date.");
            }

            // Use Integer.parseInt and the YearMonth class
            int expiryMonth = Integer.parseInt(ccExpiryMonth);
            int expiryYear = Integer.parseInt(ccExpiryYear);

            if (expiryMonth < 1 || expiryMonth > 12) {
                throw new ApiException.ValidationFailure("Please enter a valid expiration date.");
            }

            YearMonth expiryDate = YearMonth.of(expiryYear, expiryMonth);
            YearMonth currentDate = YearMonth.now();

            return expiryDate.isBefore(currentDate);
        } catch (NumberFormatException e) {
            throw new ApiException.ValidationFailure("Please enter a valid expiration date.");
        }
    }

    private void validateCart(ShoppingCart cart) {

        if (cart.getItems().isEmpty()) {
            throw new ApiException.InvalidParameter("Cart is empty.");
        }

        cart.getItems().forEach(item -> {
            if (item.getQuantity() <= 0 || item.getQuantity() > 99) {
                throw new ApiException.InvalidParameter("All quantities must be between 1 and 99.");
            }
            Book databaseBook = bookDao.findByBookId(item.getBookId());

            if (databaseBook.price() != item.getBookForm().getPrice()) {
                throw new ApiException.InvalidParameter("Price does not match for " +
                        databaseBook.title()
                        + ". Try refreshing the page, the price may have updated!");
            }

            if (databaseBook.categoryId() != item.getBookForm().getCategoryId()) {
                throw new ApiException.InvalidParameter("Category does not match for " +
                        databaseBook.title()
                        + ". Try refreshing the page, the category may have updated!");
            }

        });
    }

    private long performPlaceOrderTransaction(
            String name, String address, String phone,
            String email, String ccNumber, Date date,
            ShoppingCart cart, Connection connection) {
        try {
            connection.setAutoCommit(false);

            long customerId = customerDao.create(
                    connection, name, address, phone, email,
                    ccNumber, date);
            long customerOrderId = orderDao.create(
                    connection,
                    (cart.getComputedSubtotal() + cart.getSurcharge()),
                    generateConfirmationNumber(), customerId);

            for (ShoppingCartItem item : cart.getItems()) {
                lineItemDao.create(connection, item.getBookId(), customerOrderId, item.getQuantity());
            }
            connection.commit();
            return customerOrderId;
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                throw new BookstoreDbException("Failed to roll back transaction", e1);
            }
            return 0;
        }
    }

    private int generateConfirmationNumber() {
        return ThreadLocalRandom.current().nextInt(999999999);
    }

    /**
     * Field Checks
     */

    private void CheckIsNotEmpty(String value, String field) {
        if (value == null || value.isEmpty()) {
            throw new ApiException.ValidationFailure(field, field + " is empty");
        }
    }

    private void CheckLengthBetween4and45(String value, String field) {
        if (value.length() > 45 || value.length() < 4) {
            throw new ApiException.ValidationFailure(field, field + " must be between 4 and 45 characters.");
        }
    }

    //this is the regex from the frontend utils file... adapted to java pattern
    private static final Pattern US_MOBILE_PHONE_PATTERN = Pattern.compile(
            "^((\\+1|1)?( |-)?)?(\\([2-9][0-9]{2}\\)|[2-9][0-9]{2})( |-)?([2-9][0-9]{2}( |-)?[0-9]{4})$"
    );

    public static boolean isMobilePhone(String input) {
        Matcher matcher = US_MOBILE_PHONE_PATTERN.matcher(input);
        return matcher.matches();
    }

    private void CheckPhoneNumber(String value) {

        if (!isMobilePhone(value)) {
            throw new ApiException.ValidationFailure("phone", "Invalid phone number");
        }
    }

    private void CheckEmail(String value) {
        if (!value.contains("@") || value.charAt(value.length() - 1) == '.' || value.contains(" ")) {
            throw new ApiException.ValidationFailure("email", "Invalid email address");
        }
    }

    private void CheckCCNumber(String value) {
        String cleanString = value.replaceAll(" ", "").replaceAll("-", "");
        if (cleanString.length() < 14 || cleanString.length() > 16) {
            throw new ApiException.ValidationFailure("ccNumber", "Invalid Credit Card number");
        }
    }

    private Date getCardExpirationDate(String monthString, String yearString) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(Calendar.MONTH, Integer.parseInt(monthString) - 1);
            calendar.set(Calendar.YEAR, Integer.parseInt(yearString));
            return (Date) calendar.getTime();
        } catch (NumberFormatException e) {
            throw new ApiException("Invalid Date format", e);
        }
    }

}
