package ir.imorate.cs50rm.command.document;

import ir.imorate.cs50rm.command.SecuredCommand;
import ir.imorate.cs50rm.entity.customer.Customer;
import ir.imorate.cs50rm.entity.document.Contract;
import ir.imorate.cs50rm.entity.security.User;
import ir.imorate.cs50rm.exception.customer.CustomerNotFoundException;
import ir.imorate.cs50rm.exception.document.ContractNotFoundException;
import ir.imorate.cs50rm.exception.security.UserNotFoundException;
import ir.imorate.cs50rm.service.customer.CustomerService;
import ir.imorate.cs50rm.service.document.ContractService;
import ir.imorate.cs50rm.service.security.UserService;
import ir.imorate.cs50rm.shell.InputReader;
import ir.imorate.cs50rm.shell.ShellHelper;
import ir.imorate.cs50rm.utils.SecurityUtils;
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

@ShellComponent
public class ContractCommand extends SecuredCommand {

    private final ShellHelper shellHelper;
    private final InputReader inputReader;
    private final CustomerService customerService;
    private final ContractService contractService;
    private final UserService userService;

    public ContractCommand(@Lazy ShellHelper shellHelper, @Lazy InputReader inputReader, CustomerService customerService,
                           ContractService contractService, UserService userService) {
        this.shellHelper = shellHelper;
        this.inputReader = inputReader;
        this.customerService = customerService;
        this.contractService = contractService;
        this.userService = userService;
    }

    @ShellMethodAvailability({"isUserLoggedIn"})
    @ShellMethod(value = "Create contract for customer", key = "contract create")
    public void createContract(@ShellOption({"-e", "--email"}) String email) {
        try {
            Customer customer = customerService.findCustomer(email);
            String title = inputReader.fieldPrompt("Title", null, true);
            shellHelper.printInfo("Value must be in USD");
            String value = inputReader.fieldPrompt("Value", null, true);
            String fileName = inputReader.fieldPrompt("FileName", null, true);
            Contract contract = Contract.builder().customer(customer).status(false).title(title).fileName(fileName)
                    .value(Double.valueOf(value)).author(getCurrentAdmin()).closed(false).build();
            contractService.createContract(contract, customer);
            shellHelper.printInfo(TableUtils.beanTable(contract, getBeanTableMap(), 100));
            shellHelper.printSuccess(String.format("Contract for customer with email \"%s\" was created successfully.",
                    email));
        } catch (CustomerNotFoundException e) {
            shellHelper.printError("Customer with email \"%s\" not found.");
        }
    }

    @ShellMethodAvailability({"isUserLoggedIn"})
    @ShellMethod(value = "Edit contract for customer", key = "contract edit")
    public void editContract(@ShellOption({"-i", "--id"}) Long id) {
        try {
            Contract contract = contractService.findContract(id);
            shellHelper.printInfo("Leave blank if you want to use previous value.");
            String title = inputReader.fieldPrompt("Title", contract.getTitle(), true);
            String fileName = inputReader.fieldPrompt("File Name", contract.getFileName(), true);
            String value = inputReader.fieldPrompt("Value", String.valueOf(contract.getValue()), true);
            contract.setTitle(title);
            contract.setFileName(fileName);
            contract.setValue(Double.valueOf(value));
            contractService.save(contract);
            shellHelper.printSuccess(String.format("Contract for customer with contract id \"%s\" was edited successfully.",
                    id));
        } catch (ContractNotFoundException e) {
            shellHelper.printError("Contract with contract id \"%s\" not found.");
        }
    }

    @ShellMethodAvailability({"isUserLoggedIn"})
    @ShellMethod(value = "Accept contract for customer", key = "contract accept")
    public void acceptContract(@ShellOption({"-i", "--id"}) Long id) {
        try {
            Contract contract = contractService.findContract(id);
            contractService.acceptContract(contract);
            shellHelper.printSuccess(String.format("Contract for customer with contract id \"%s\" was accepted successfully.",
                    id));
        } catch (ContractNotFoundException e) {
            shellHelper.printError("Contract with contract id \"%s\" not found.");
        }
    }

    @ShellMethodAvailability({"isUserLoggedIn"})
    @ShellMethod(value = "Close contract for customer", key = "contract close")
    public void closeContract(@ShellOption({"-i", "--id"}) Long id) {
        try {
            Contract contract = contractService.findContract(id);
            contractService.closeContract(contract);
            shellHelper.printSuccess(String.format("Contract for customer with contract id \"%s\" was closed successfully.",
                    id));
        } catch (ContractNotFoundException e) {
            shellHelper.printError("Contract with contract id \"%s\" not found.");
        }
    }

    @ShellMethodAvailability({"isUserLoggedIn"})
    @ShellMethod(value = "List all contracts", key = "contract list")
    public void listContracts() {
        List<Contract> contractList = contractService.listAll();
        TableModel model = new BeanListTableModel<>(contractList, getBeanTableMap());
        TableBuilder tableBuilder = new TableBuilder(model);
        tableBuilder.addFullBorder(BorderStyle.fancy_light);
        shellHelper.printInfo(tableBuilder.build().render(100));
        shellHelper.printSuccess(String.format("Found %s record(s) on database.", contractList.size()));
    }

    private LinkedHashMap<String, Object> getBeanTableMap() {
        LinkedHashMap<String, Object> beanTableMap = new LinkedHashMap<>();
        beanTableMap.put("id", "ID");
        beanTableMap.put("customer", "Customer");
        beanTableMap.put("title", "Title");
        beanTableMap.put("fileName", "File Name");
        beanTableMap.put("status", "Accepted");
        beanTableMap.put("value", "Value");
        beanTableMap.put("author", "Author");
        return beanTableMap;
    }

    private User getCurrentAdmin() {
        String currentLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() ->
                new UserNotFoundException("User not found"));
        return userService.findUser(currentLogin);
    }

}
