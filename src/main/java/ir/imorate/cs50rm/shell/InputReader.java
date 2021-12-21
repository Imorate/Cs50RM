package ir.imorate.cs50rm.shell;

import org.jline.reader.LineReader;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.*;

public class InputReader {

    public static final Character DEFAULT_MASK = '*';

    private final Character mask;

    private final LineReader lineReader;

    private final ShellHelper shellHelper;

    public InputReader(LineReader lineReader, ShellHelper shellHelper) {
        this(lineReader, shellHelper, null);
    }

    public InputReader(LineReader lineReader, ShellHelper shellHelper, Character mask) {
        this.lineReader = lineReader;
        this.shellHelper = shellHelper;
        this.mask = mask != null ? mask : DEFAULT_MASK;
    }

    public String prompt(String prompt) {
        return prompt(prompt, null, true);
    }

    public String prompt(String prompt, String defaultValue) {
        return prompt(prompt, defaultValue, true);
    }

    /**
     * Prompts user for input.
     *
     * @param prompt       prompt
     * @param defaultValue default value
     * @param echo         echo
     * @return string
     */
    public String prompt(String prompt, String defaultValue, boolean echo) {
        String answer;
        if (echo) {
            answer = lineReader.readLine(prompt + ": ");
        } else {
            answer = lineReader.readLine(prompt + ": ", mask);
        }
        if (!StringUtils.hasText(answer)) {
            return defaultValue;
        }
        return answer;
    }

    /**
     * Loops until one of the `options` is provided. Pressing return is equivalent to
     * returning `defaultValue`.
     * <br/>
     * Passing null for defaultValue signifies that there is no default value.<br/>
     * Passing "" or null among optionsAsList means that empty answer is allowed, in these cases this method returns
     * empty String "" as the result of its execution.
     */
    public String promptWithOptions(String prompt, String defaultValue, List<String> optionsAsList) {
        String answer;
        List<String> allowedAnswers = new ArrayList<>(optionsAsList);
        if (!StringUtils.hasText(defaultValue)) {
            allowedAnswers.add("");
        }
        do {
            answer = lineReader.readLine(String.format("%s %s: ", prompt, formatOptions(defaultValue, optionsAsList)));
        } while (!allowedAnswers.contains(answer) && !"".equals(answer));

        if (!StringUtils.hasText(answer) && allowedAnswers.contains("")) {
            return defaultValue;
        }
        return answer;
    }

    private List<String> formatOptions(String defaultValue, List<String> optionsAsList) {
        List<String> result = new ArrayList<>();
        for (String option : optionsAsList) {
            String val = option;
            if ("".equals(option) || option == null) {
                val = "''";
            }
            if (defaultValue != null) {
                if (defaultValue.equals(option) || (defaultValue.equals("") && option == null)) {
                    val = shellHelper.getInfoMessage(val);
                }
            }
            result.add(val);
        }
        return result;
    }

    /**
     * Loops until one value from the list of options is selected, printing each option on its own line.
     */
    public String selectFromList(String headingMessage, String promptMessage, Map<String, String> options, boolean ignoreCase, String defaultValue) {
        String answer;
        Set<String> allowedAnswers = new HashSet<>(options.keySet());
        if (defaultValue != null && !defaultValue.equals("")) {
            allowedAnswers.add("");
        }
        shellHelper.print(String.format("%s: ", headingMessage));
        do {
            for (Map.Entry<String, String> option : options.entrySet()) {
                String defaultMarker = null;
                if (defaultValue != null) {
                    if (option.getKey().equals(defaultValue)) {
                        defaultMarker = "*";
                    }
                }
                if (defaultMarker != null) {
                    shellHelper.printInfo(String.format("%s [%s] %s ", defaultMarker, option.getKey(), option.getValue()));
                } else {
                    shellHelper.print(String.format("  [%s] %s", option.getKey(), option.getValue()));
                }
            }
            answer = lineReader.readLine(String.format("%s: ", promptMessage));
        } while (!containsString(allowedAnswers, answer, ignoreCase) && !"".equals(answer));

        if (!StringUtils.hasText(answer) && allowedAnswers.contains("")) {
            return defaultValue;
        }
        return answer;
    }

    private boolean containsString(Set<String> l, String s, boolean ignoreCase) {
        if (!ignoreCase) {
            return l.contains(s);
        }
        for (String value : l) {
            if (value.equalsIgnoreCase(s)) return true;
        }
        return false;
    }

    /**
     * Field prompt
     *
     * @param fieldName    Name of the field
     * @param defaultValue Default value
     * @param echo         Can be echo
     * @return Prompt value
     */
    public String fieldPrompt(String fieldName, @Nullable String defaultValue, boolean echo) {
        String field;
        boolean emptyField = true;
        do {
            if (echo) {
                field = prompt("Please enter " + fieldName, defaultValue);
            } else {
                field = prompt("Please enter " + fieldName, null, false);
            }
            if (StringUtils.hasText(field)) {
                emptyField = false;
            } else {
                shellHelper.printWarning(fieldName + " cannot be empty!");
            }
        } while (emptyField);
        return field.trim();
    }

}