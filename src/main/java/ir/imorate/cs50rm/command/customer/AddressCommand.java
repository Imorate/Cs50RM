package ir.imorate.cs50rm.command.customer;

import ir.imorate.cs50rm.command.SecuredCommand;
import ir.imorate.cs50rm.entity.customer.Address;
import ir.imorate.cs50rm.exception.customer.CustomerNotFoundException;
import ir.imorate.cs50rm.service.customer.AddressService;
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
public class AddressCommand extends SecuredCommand {

    private final ShellHelper shellHelper;
    private final InputReader inputReader;
    private final AddressService addressService;
    private final CustomerService customerService;

    public AddressCommand(@Lazy ShellHelper shellHelper, @Lazy InputReader inputReader, AddressService addressService, CustomerService customerService) {
        this.shellHelper = shellHelper;
        this.inputReader = inputReader;
        this.addressService = addressService;
        this.customerService = customerService;
    }

    @ShellMethodAvailability({"isUserLoggedIn"})
    @ShellMethod(value = "List all addresses", key = "customer address list", group = "Customer Command")
    public void listAddresses() {
        List<Address> addressList = addressService.listAll();
        LinkedHashMap<String, Object> headers = new LinkedHashMap<>();
        headers.put("id", "ID");
        headers.put("country", "Country");
        headers.put("state", "State");
        headers.put("city", "City");
        headers.put("street", "Street");
        headers.put("postCode", "Post Code");
        TableModel model = new BeanListTableModel<>(addressList, headers);
        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder.addFullBorder(BorderStyle.fancy_light);
        shellHelper.printInfo(tableBuilder.build().render(100));
        shellHelper.printSuccess(String.format("Found %s record(s) on database.", addressList.size()));
    }

    @ShellMethodAvailability({"isUserLoggedIn"})
    @ShellMethod(value = "Edit an address", key = "customer address edit", group = "Customer Command")
    public void editAddress(@ShellOption({"-e", "--email"}) String email) {
        try {
            shellHelper.printInfo("Leave blank if you want to use previous value.");
            Address address = customerService.findCustomer(email).getAddress();
            String country = inputReader.fieldPrompt("Country", address.getCountry(), true);
            String state = inputReader.fieldPrompt("State", address.getState(), true);
            String city = inputReader.fieldPrompt("City", address.getCity(), true);
            String street = inputReader.fieldPrompt("Street", address.getStreet(), true);
            String postCode = inputReader.fieldPrompt("Post Code", address.getPostCode(), true);
            if (postCode.length() != 10) {
                shellHelper.printError("Post code must be in 10 length.");
                return;
            }
            address.setCountry(country);
            address.setState(state);
            address.setCity(city);
            address.setStreet(street);
            address.setPostCode(postCode);
            addressService.save(address);
            shellHelper.printInfo(TableUtils.beanTable(address, getBeanTableMap(), 100));
            shellHelper.printSuccess(String.format("Customer address with email \"%s\" was successfully created.", email));
        } catch (CustomerNotFoundException e) {
            shellHelper.printError("Customer with email \"%s\" not found.");
        }
    }

    private Map<String, Object> getBeanTableMap() {
        Map<String, Object> beanTableMap = new LinkedHashMap<>();
        beanTableMap.put("country", "Country");
        beanTableMap.put("state", "State");
        beanTableMap.put("city", "City");
        beanTableMap.put("street", "Street");
        beanTableMap.put("postCode", "Post Code");
        return beanTableMap;
    }

}
