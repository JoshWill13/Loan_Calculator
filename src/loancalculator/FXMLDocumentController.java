/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loancalculator;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.math.*;
import java.io.FileWriter;
import javafx.scene.Cursor;
import javafx.scene.effect.ColorAdjust;
/**
 *
 * @author Josh
 */
public class FXMLDocumentController implements Initializable {
    
    //================================================================================
    // Variable Declaration and FXML attachment
    //================================================================================
    private double xOffset = 0;
    private double yOffset = 0;
    @FXML private Label label;
    @FXML private AnchorPane anchorPane;
    @FXML private JFXButton calculate;
    @FXML private JFXButton csv;
    @FXML private Text text;
    @FXML private Text text2;
    @FXML private Text text3;
    @FXML private JFXTextField jtextField;
    @FXML private JFXTextField jtextField2;
    @FXML private JFXTextField jtextField3;
    @FXML private ChoiceBox choiceBox;
    @FXML private ImageView imageView;
    @FXML private ImageView imageView2;
    private ArrayList<ArrayList<String>> list = new ArrayList<>();
    
    //================================================================================
    // Initialization
    //================================================================================
    @Override   //Initalize TextField and ChoiceBox to accept valid input
    public void initialize(URL url, ResourceBundle rb) {
        jtextField.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,12}")) {
                    jtextField.setText(oldValue);
                }
            }
        });
        jtextField2.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,2}([\\.]\\d{0,3})?")) {
                    jtextField2.setText(oldValue);
                }
            }
        });
        jtextField3.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,3}")) {
                    jtextField3.setText(oldValue);
                }
            }
        });
        choiceBox.getItems().addAll("Monthly", "Semi-Monthly", "Biweekly*", "Weekly*");
	choiceBox.setValue("Monthly");
        choiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() { 
            // if the item of the list is changed 
            public void changed(ObservableValue ov, Number value, Number new_value) 
            { 
                switch (new_value.intValue()) {
                    case 0:
                        text.setText("Monthly loan payment:");
                        break;
                    case 1:
                        text.setText("Semi-Monthly loan payment:");
                        break;
                    case 2:
                        text.setText("Bi-Weekly loan payment:");
                        break;
                    default:
                        text.setText("Weekly loan payment:");
                        break; 
                }
            } 
        }); 
    }
    
    //================================================================================
    // Button Operations
    //================================================================================
    @FXML   //Gathers User Input and returns correct calculations to be displayed when Calculate is Pressed
    private void handleCalculateClicked(){
        System.out.println("CalculateClicked");
        BigDecimal LA = new BigDecimal(jtextField.getText());
        BigDecimal IR = new BigDecimal(jtextField2.getText());
        BigDecimal RP = new BigDecimal(jtextField3.getText());

        if(choiceBox.getValue().equals("Monthly")){
                BigDecimal[] bd = CalculatePayment(LA, IR, RP,12);
                text2.setText(""+NumberFormat.getCurrencyInstance().format(bd[0]));
                text3.setText(""+NumberFormat.getCurrencyInstance().format(bd[1]));
                list.clear();
                changeList(bd[0],LA, IR, RP, 12);
        }else if(choiceBox.getValue().equals("Semi-Monthly")){
                BigDecimal[] bd = CalculatePayment(LA, IR, RP,24);
                text2.setText(""+NumberFormat.getCurrencyInstance().format(bd[0]));
                text3.setText(""+NumberFormat.getCurrencyInstance().format(bd[1]));
                list.clear();
                changeList(bd[0],LA, IR, RP, 24);
        }else if(choiceBox.getValue().equals("Biweekly*")){
                BigDecimal[] bd = CalculatePayment(LA, IR, RP,26);
                text2.setText(""+NumberFormat.getCurrencyInstance().format(bd[0]));
                text3.setText(""+NumberFormat.getCurrencyInstance().format(bd[1]));
                list.clear();
                changeList(bd[0],LA, IR, RP, 26);
        }else if(choiceBox.getValue().equals("Weekly*")){
                BigDecimal[] bd = CalculatePayment(LA, IR, RP,52);
                list.clear();
                changeList(bd[0],LA, IR, RP, 52);
                text2.setText(""+NumberFormat.getCurrencyInstance().format(bd[0]));
                text3.setText(""+NumberFormat.getCurrencyInstance().format(bd[1]));
                list.clear();
                changeList(bd[0],LA, IR, RP, 52);
        }
    }
    
    @FXML   //Writes a .CSV file to current directory when CSV button pressed
    private void handleCSVClicked(){
        try{
            FileWriter csvFile = new FileWriter("new.csv");
            for (ArrayList<String> rowData : list) {
                    csvFile.append(String.join(",", rowData));
                    csvFile.append("\n");
            }
        csvFile.flush();
        csvFile.close();
        }catch(Exception e2){
            System.out.println("Could Not Create File");
        }
    }
    
    //================================================================================
    // Helper Methods
    //================================================================================
    //Preforms calculation for loan payments and interest.
    private BigDecimal[] CalculatePayment(BigDecimal LA, BigDecimal IR, BigDecimal RP, int payPeriods){
        MathContext mc = new MathContext(64, RoundingMode.HALF_UP);
        
        //payment = (principle*interestRate)/[payperiods(1-[1+(interestRate/payperiods)]-years*payperiods)].
        BigDecimal payment = (LA.multiply(IR.divide(BigDecimal.valueOf(100), mc), mc)).divide(
        BigDecimal.valueOf(payPeriods).multiply(BigDecimal.valueOf(1).subtract(BigDecimal.valueOf(1).divide((BigDecimal.valueOf(1).add((IR.divide(BigDecimal.valueOf(100), mc)).divide(BigDecimal.valueOf(payPeriods), mc))
        ), mc).pow(((RP.divide(BigDecimal.valueOf(12),mc)).multiply(BigDecimal.valueOf(payPeriods))).intValue(), mc) )), mc);

        //Total Interest = [Payments*years*(payperiods per year)] - Principle.
        BigDecimal totalInterestPayable = (payment.multiply(RP.divide(BigDecimal.valueOf(12), mc),mc).multiply(BigDecimal.valueOf(payPeriods), mc)).subtract(LA);

        BigDecimal[] bd = new BigDecimal[]{payment.setScale(2, RoundingMode.HALF_UP), totalInterestPayable.setScale(2, RoundingMode.HALF_UP)};

        return bd;
    }
    
    //Creates a 2D array storing a list of all payments for each Calculation
    private void changeList(BigDecimal payment, BigDecimal LA, BigDecimal IR, BigDecimal RP, int payPeriods){
        ArrayList<String> firstRow = new ArrayList<String>();
        firstRow.add("Payment #");
        firstRow.add("Starting Balance");
        firstRow.add("Interest");
        firstRow.add("Principal");
        firstRow.add("Ending Balance");
        list.add(firstRow);

        MathContext mc = new MathContext(64, RoundingMode.HALF_UP);
        BigDecimal interestPercent = (IR.divide(BigDecimal.valueOf(100), mc)).divide(BigDecimal.valueOf(payPeriods), mc);
        BigDecimal balance = LA;
        int totalPayPeriods = ((RP.divide(BigDecimal.valueOf(12), mc)).multiply(BigDecimal.valueOf(payPeriods), mc)).intValue();

        for(int i = 1; i<=totalPayPeriods; i++){
                ArrayList<String> row = new ArrayList<String>();
                row.add(""+i);
                row.add("$" + balance);
                BigDecimal interestPay = balance.multiply(interestPercent,mc);
                row.add("$"+interestPay);
                BigDecimal principalPay = payment.subtract(balance.multiply(interestPercent,mc));
                row.add("$"+principalPay);
                balance = balance.subtract(principalPay);
                row.add("$"+balance);
                list.add(row);
        }	
    }
     
    //================================================================================
    // User Interface Operations
    //================================================================================
    @FXML
    private void handleClose() {
        System.exit(0);
    }
    
    @FXML
    private void handleMinimize(MouseEvent me) {
        ((Stage)((ImageView)me.getSource()).getScene().getWindow()).setIconified(true);
    }
    
    @FXML
    private void handleMinimizeHover() {
        ColorAdjust ca = new ColorAdjust();
        ca.setBrightness(-.2);
        imageView.setEffect(ca);
    }
    
    @FXML
    private void handleButtonMinimizeExited() {
        ColorAdjust ca = new ColorAdjust();
        ca.setBrightness(.8);
        imageView.setEffect(ca);
    }
    
    @FXML
    private void handleExitHover() {
        ColorAdjust ca = new ColorAdjust();
        ca.setBrightness(-.3);
        imageView2.setEffect(ca);
    }
    
    @FXML
    private void handleExitHoverExited() {
        ColorAdjust ca = new ColorAdjust();
        ca.setBrightness(0);
        imageView2.setEffect(ca);
    }
    @FXML
    private void handleButtonHover(MouseEvent me) {
        ColorAdjust ca = new ColorAdjust();
        ca.setBrightness(-0.3);
        JFXButton b = (JFXButton) me.getSource();
        b.setEffect(ca);
    }
    
    @FXML
    private void handleButtonHoverExited(MouseEvent me) {
        ColorAdjust ca = new ColorAdjust();
        ca.setBrightness(0);
        JFXButton b = (JFXButton) me.getSource();
        b.setEffect(ca);
    }
    
    @FXML
    private void handleAnchorClick(MouseEvent me) {
        xOffset = me.getSceneX();
        yOffset = me.getSceneY();
        (((AnchorPane)me.getSource()).getScene()).setCursor(Cursor.HAND);
    }
    
    @FXML
    private void handleAnchorDrag(MouseEvent me) {
        ((Stage)((AnchorPane)me.getSource()).getScene().getWindow()).setX(me.getScreenX() - xOffset);
        ((Stage)((AnchorPane)me.getSource()).getScene().getWindow()).setY(me.getScreenY() - yOffset);
    }
    
    @FXML
    private void handleAnchorReleased(MouseEvent me) {
        (((AnchorPane)me.getSource()).getScene()).setCursor(Cursor.DEFAULT);
    }  
}