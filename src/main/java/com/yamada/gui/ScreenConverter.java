package com.yamada.gui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.yamada.models.Currency;
import com.yamada.models.Degree;
import com.yamada.utils.ApiConnector;
import com.yamada.utils.DegreesConverter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Pattern;

public class ScreenConverter {

    private final Window mainWindow;
    private final Window currencyWindow;
    private final Window tempWindow;
    private final Window creditsWindow;

    private WindowBasedTextGUI textGUI;

    private List<Currency> currencies;


    public ScreenConverter() {
        this.mainWindow = new BasicWindow();
        this.currencyWindow = new BasicWindow();
        this.tempWindow = new BasicWindow();
        this.creditsWindow = new BasicWindow();
    }

    public void initScreen() {
        final DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        try (Screen screen = terminalFactory.createScreen()) {
            screen.startScreen();
            this.textGUI = new MultiWindowTextGUI(screen, new DefaultWindowManager(),
                    new EmptySpace(TextColor.ANSI.DEFAULT)
            );
            this.initMainWindow();
            this.textGUI.addWindowAndWait(this.mainWindow);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void initMainWindow() {
        this.mainWindow.setHints(List.of(Window.Hint.CENTERED));
        this.mainWindow.setFixedSize(new TerminalSize(50, 9));

        Panel panel = new Panel(new GridLayout(3));
        panel.addComponent(new Label("Choose an Option"));
        panel.addComponent(new EmptySpace().setLayoutData(GridLayout
                .createHorizontallyFilledLayoutData(3)));

        panel.addComponent(new Separator(Direction.HORIZONTAL).setLayoutData(GridLayout
                .createHorizontallyFilledLayoutData(3)));

        new Button("Currency Converter", () -> {
            this.initCurrencyWindow();
            this.textGUI.addWindowAndWait(this.currencyWindow);
        }).addTo(panel);

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout
                .createHorizontallyFilledLayoutData(2)));

        new Button("Temp Converter", () -> {
            this.initTempWindow();
            this.textGUI.addWindowAndWait(this.tempWindow);
        }).addTo(panel);

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout
                .createHorizontallyFilledLayoutData(2)));

        new Button("Credits", () -> {
            this.initCreditsWindow();
            this.textGUI.addWindowAndWait(this.creditsWindow);
        }).addTo(panel);

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout
                .createHorizontallyFilledLayoutData(2)));

        new Button("Exit!", this.mainWindow::close).addTo(panel);

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout
                .createHorizontallyFilledLayoutData(2)));

        this.mainWindow.setComponent(panel.withBorder(Borders.singleLine("Main Menu")));

    }

    private void initCreditsWindow() {
        this.creditsWindow.setHints(List.of(Window.Hint.CENTERED));
        this.creditsWindow.setFixedSize(new TerminalSize(50, 15));

        Panel panel = new Panel(new GridLayout(3));

        new Label("""
                This program was created by 'Leví Hernández' as a challenge by Alura Latam.
                
                please follow me in my social media and watch my other projects:
                
                    GitHub: Isaac03483.
                    LinkedIn: Leví Hernández.
                    
                    
                    .
                """).addTo(panel);

        panel.addComponent(new EmptySpace().setLayoutData(GridLayout
                .createHorizontallyFilledLayoutData(3)));
        new Button("Close!", this.creditsWindow::close).addTo(panel);

        this.creditsWindow.setComponent(panel.withBorder(Borders.singleLine("Credits!")));
    }

    public void initCurrencyWindow() {
        ApiConnector connector = ApiConnector.getInstance();

        try {
//            System.out.println("PRINTING ALL CODES:\n");
            if(currencies == null) {
                currencies = connector.getCodes();
            }

        } catch (IOException e) {
            MessageDialog.showMessageDialog(textGUI, "Oops!",
                    "Sorry, something went wrong. Try later.", MessageDialogButton.Retry);
        }

        this.currencyWindow.setHints(List.of(Window.Hint.CENTERED));
        this.currencyWindow.setFixedSize(new TerminalSize(65, 10));

        Panel panel = new Panel(new GridLayout(3));

        Label out = new Label("");

        panel.addComponent(new Label("Value:"));
        panel.addComponent(new Separator(Direction.VERTICAL));
        TextBox textValue = new TextBox().setValidationPattern(Pattern.compile("[0-9]*")).addTo(panel);

        panel.addComponent(new Label("From:"));
        panel.addComponent(new Separator(Direction.VERTICAL));
        ComboBox<Currency> fromCombo = new ComboBox<>(currencies).setReadOnly(true).addTo(panel);

        panel.addComponent(new Label("To:"));
        panel.addComponent(new Separator(Direction.VERTICAL));
        ComboBox<Currency> toCombo = new ComboBox<>(currencies).setReadOnly(true).addTo(panel);

        panel.addComponent(new EmptySpace(new TerminalSize(0,0)));

        panel.addComponent(new Separator(Direction.VERTICAL));

        new Button("Calculate!", () -> {
            if(textValue.getText().isBlank()) {
                MessageDialog.showMessageDialog(textGUI, "Oops!",
                        "It seems like you forgot something.", MessageDialogButton.Retry);
                return;
            }

            double result = 0;

            Currency fromCurrency = fromCombo.getSelectedItem();
            Currency toCurrency = toCombo.getSelectedItem();
            try {

                result = connector.getConversionResult(fromCurrency.currencyCode(),
                        toCurrency.currencyCode(), textValue.getText());
            } catch (IOException e) {
                MessageDialog.showMessageDialog(textGUI, "Oops!",
                        "Sorry, something went wrong. Try later.", MessageDialogButton.Retry);
            }

            out.setText(toCurrency.currencyCode()+" - "+result);

        }).addTo(panel);

        panel.addComponent(new Separator(Direction.HORIZONTAL).setLayoutData(GridLayout
                .createHorizontallyFilledLayoutData(3)));

        panel.addComponent(new Label("Result:"));
        panel.addComponent(new Separator(Direction.VERTICAL));

        panel.addComponent(out);
        panel.addComponent(new Separator(Direction.HORIZONTAL).setLayoutData(GridLayout
                .createHorizontallyFilledLayoutData(3)));

        new Button("Close!", this.currencyWindow::close).addTo(panel);
        this.currencyWindow.setComponent(panel.withBorder(Borders.singleLine("Currency Converter")));
    }

    public void initTempWindow() {
        DegreesConverter converter = DegreesConverter.getInstance();

        this.tempWindow.setHints(List.of(Window.Hint.CENTERED));
        this.tempWindow.setFixedSize(new TerminalSize(55, 10));

        Panel panel = new Panel(new GridLayout(3));

        Label out = new Label("");

        panel.addComponent(new Label("Value:"));
        panel.addComponent(new Separator(Direction.VERTICAL));
        TextBox textValue = new TextBox().setValidationPattern(Pattern.compile("[0-9]*")).addTo(panel);

        panel.addComponent(new Label("From:"));
        panel.addComponent(new Separator(Direction.VERTICAL));
        ComboBox<Degree> fromCombo = new ComboBox<>(Degree.CELSIUS, Degree.FAHRENHEIT, Degree.KELVIN).addTo(panel);

        panel.addComponent(new Label("To:"));
        panel.addComponent(new Separator(Direction.VERTICAL));
        ComboBox<Degree> toCombo = new ComboBox<>(Degree.CELSIUS, Degree.FAHRENHEIT, Degree.KELVIN).addTo(panel);

        panel.addComponent(new EmptySpace(new TerminalSize(0,0)));
        panel.addComponent(new Separator(Direction.VERTICAL));
        new Button("Calculate!", () -> {
           if(textValue.getText().isBlank()) {
               MessageDialog.showMessageDialog(textGUI, "Oops!",
                       "It seems like you forgot something.", MessageDialogButton.Retry);
               return;
           }

           Degree fromDegree = fromCombo.getSelectedItem();
           Degree toDegree = toCombo.getSelectedItem();

           double doubleValue = Double.parseDouble(textValue.getText());
           DecimalFormat formatter = new DecimalFormat("#0.00");
           double result;

           if(fromDegree == toDegree) {

               out.setText(fromDegree.getValue()+" "+formatter.format(doubleValue));
               return;
           }

           if(fromDegree == Degree.CELSIUS) {
               if(toDegree == Degree.KELVIN) {
                   result = converter.celsiusToKelvin(doubleValue);
                   out.setText(toDegree.getValue()+" "+formatter.format(result));
                   return;
               }

               result = converter.celsiusToFahrenheit(doubleValue);
               out.setText(toDegree.getValue()+" "+formatter.format(result));
               return;
           }

           if(fromDegree == Degree.FAHRENHEIT) {
               if(toDegree == Degree.KELVIN) {
                   result = converter.fahrenheitToKelvin(doubleValue);
                   out.setText(toDegree.getValue()+" "+formatter.format(result));
                   return;
               }

               result = converter.fahrenheitToCelsius(doubleValue);
               out.setText(toDegree.getValue()+" "+formatter.format(result));
               return;
           }

            if(toDegree == Degree.CELSIUS) {
                result = converter.kelvinToCelsius(doubleValue);
                out.setText(toDegree.getValue()+" "+formatter.format(result));
                return;
            }

            result = converter.kelvinToFahrenheit(doubleValue);
            out.setText(toDegree.getValue()+" "+formatter.format(result));

        }).addTo(panel);

        panel.addComponent(new Separator(Direction.HORIZONTAL).setLayoutData(GridLayout
                .createHorizontallyFilledLayoutData(3)));

        panel.addComponent(new Label("Result:"));
        panel.addComponent(new Separator(Direction.VERTICAL));
        panel.addComponent(out);

        panel.addComponent(new Separator(Direction.HORIZONTAL).setLayoutData(GridLayout
                .createHorizontallyFilledLayoutData(3)));

        new Button("Close!", this.tempWindow::close).addTo(panel);

        this.tempWindow.setComponent(panel.withBorder(Borders.singleLine("Temp Converter")));
    }
}
