
package business;

import business.book.BookDao;
import business.book.BookDaoJdbc;
import business.category.CategoryDao;
import business.category.CategoryDaoJdbc;
import business.customer.CustomerDao;
import business.customer.CustomerDaoJdbc;
import business.order.*;

public class ApplicationContext {

    private final BookDao bookDao;
    private final CategoryDao categoryDao;
    private final OrderDao orderDao;
    private final LineItemDao lineItemDao;
    private final CustomerDao customerDao;

    private final DefaultOrderService orderService;

    public static ApplicationContext INSTANCE = new ApplicationContext();

    private ApplicationContext() {
        categoryDao = new CategoryDaoJdbc();
        bookDao = new BookDaoJdbc();
        orderDao = new OrderDaoJdbc();
        lineItemDao = new LineItemDaoJdbc();
        customerDao = new CustomerDaoJdbc();

        orderService = new DefaultOrderService();

        orderService.setBookDao(bookDao);
        orderService.setOrderDao(orderDao);
        orderService.setLineItemDao(lineItemDao);
        orderService.setCustomerDao(customerDao);
    }

    public CategoryDao getCategoryDao() {
        return categoryDao;
    }
    public BookDao getBookDao() {
        return bookDao;
    }
    public OrderDao getOrderDao() { return orderDao; }
    public LineItemDao getLineItemDao() { return lineItemDao; }
    public CustomerDao getCustomerDao() { return customerDao; }
    public DefaultOrderService getOrderService() {
        return orderService;
    }
}
