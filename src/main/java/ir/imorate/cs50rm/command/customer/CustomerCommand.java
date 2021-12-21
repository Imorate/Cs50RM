package ir.imorate.cs50rm.command.customer;

import ir.imorate.cs50rm.command.SecuredCommand;
import ir.imorate.cs50rm.entity.customer.Address;
import ir.imorate.cs50rm.entity.customer.Customer;
import ir.imorate.cs50rm.exception.customer.CustomerNotFoundException;
import ir.imorate.cs50rm.service.customer.CustomerService;
import ir.imorate.cs50rm.shell.InputReader;
import ir.imorate.cs50rm.shell.ShellHelper;
import ir.imorate.cs50rm.utils.TableUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.table.BeanListTableModel;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModel;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ShellComponent
public class CustomerCommand extends SecuredCommand {

    private final ShellHelper shellHelper;
    private final InputReader inputReader;
    private final CustomerService customerService;

    public CustomerCommand(@Lazy ShellHelper shellHelper, @Lazy InputReader inputReader, CustomerService customerService) {
        this.shellHelper = shellHelper;
        this.inputReader = inputReader;
        this.customerService = customerService;
    }

    @ShellMethodAvailability({"isUserLoggedIn"})
    @ShellMethod(value = "Add a new customer", key = "customer add")
    public void addCustomer() {
        String firstName = inputReader.fieldPrompt("First Name", null, true);
        String lastName = inputReader.fieldPrompt("Last Name", null, true);
        String email = inputReader.fieldPrompt("Email", null, true);
        if (customerService.isCustomerExist(email)) {
            shellHelper.printError(String.format("Customer with email \"%s\" is exists on database.", email));
            return;
        }
        String phoneNumber = inputReader.fieldPrompt("Phone Number", null, true);
        if (customerService.isCustomerExistWithPhoneNumber(phoneNumber)) {
            shellHelper.printError(String.format("Customer with phone number \"%s\" is exists on database.", phoneNumber));
            return;
        }
        Address customerAddress = getCustomerAddress();
        if (customerAddress.getPostCode().length() != 10) {
            shellHelper.printError("Post code must be in 10 length.");
            return;
        }
        Customer customer = Customer.builder().firstName(firstName).lastName(lastName).email(email).phoneNumber(phoneNumber)
                .address(customerAddress).build();
        customerService.createCustomer(customer);
        shellHelper.printInfo(TableUtils.beanTable(customer, getBeanTableMap(), 100));
        shellHelper.printSuccess(String.format("Customer with email \"%s\" was successfully created.", email));
    }

    @ShellMethodAvailability({"isUserLoggedIn"})
    @ShellMethod(value = "List all customers", key = "customer list")
    public void listCustomers() {
        List<Customer> customerList = customerService.listAll();
        LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
        headers.put("id", "ID");
        headers.put("firstName", "First Name");
        headers.put("lastName", "Last Name");
        headers.put("email", "Email");
        headers.put("phoneNumber", "Phone Number");
        headers.put("address", "Address");
        TableModel model = new BeanListTableModel<>(customerList, headers);
        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder.addFullBorder(BorderStyle.fancy_light);
        shellHelper.printInfo(tableBuilder.build().render(150));
        shellHelper.printSuccess(String.format("Found %s record(s) on database.", customerList.size()));
    }

    @ShellMethodAvailability({"isUserLoggedIn"})
    @ShellMethod(value = "Remove a customer", key = "customer remove")
    public void deleteCustomer(@ShellOption({"-e", "--email"}) String email) {
        try {
            Customer customer = customerService.findCustomer(email);
            customerService.removeCustomer(customer);
            shellHelper.printSuccess(String.format("Customer with email \"%s\" was removed successfully.", email));
        } catch (CustomerNotFoundException e) {
            shellHelper.printError("Customer with email \"%s\" not found.");
        }
    }

    @ShellMethodAvailability({"isUserLoggedIn"})
    @ShellMethod(value = "Edit a customer", key = "customer edit")
    public void editCustomer(@ShellOption({"-e", "--email"}) String email) {
        try {
            Customer customer = customerService.findCustomer(email);
            shellHelper.printInfo("Leave blank if you want to use previous value.");
            String newEmail = inputReader.fieldPrompt("Email", customer.getEmail(), true);
            if (customerService.isCustomerExist(newEmail) && !newEmail.equals(email)) {
                shellHelper.printError(String.format("Email %s is exists on database", newEmail));
                return;
            }
            String phoneNumber = inputReader.fieldPrompt("Phone Number", customer.getPhoneNumber(), true);
            if (customerService.isCustomerExistWithPhoneNumber(phoneNumber)) {
                shellHelper.printError(String.format("Phone Number %s is exists on database", phoneNumber));
                return;
            }
            String firstName = inputReader.fieldPrompt("First Name", customer.getFirstName(), true);
            String lastName = inputReader.fieldPrompt("Last Name", customer.getLastName(), true);
            customer.setEmail(newEmail);
            customer.setPhoneNumber(phoneNumber);
            customer.setFirstName(firstName);
            customer.setLastName(lastName);
            customerService.save(customer);
            shellHelper.printSuccess(String.format("Admin with email \"%s\" updated successfully.", email));
        } catch (CustomerNotFoundException e) {
            shellHelper.printError("Customer with email \"%s\" not found.");
        }
    }

    private Address getCustomerAddress() {
        String country = inputReader.fieldPrompt("Country", null, true);
        String state = inputReader.fieldPrompt("State", null, true);
        String city = inputReader.fieldPrompt("City", null, true);
        String street = inputReader.fieldPrompt("Street", null, true);
        String postCode = inputReader.fieldPrompt("Post Code", null, true);
        return Address.builder().country(country).state(state).city(city).street(street).postCode(postCode).build();
    }

    private Map<String, Object> getBeanTableMap() {
        Map<String, Object> beanTableMap = new LinkedHashMap<>();
        beanTableMap.put("firstName", "First Name");
        beanTableMap.put("lastName", "Last Name");
        beanTableMap.put("email", "Email");
        beanTableMap.put("phoneNumber", "phoneNumber");
        beanTableMap.put("address", "Address");
        return beanTableMap;
    }

}
