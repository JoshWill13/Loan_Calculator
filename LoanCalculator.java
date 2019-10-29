import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.text.*;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ChoiceBox;
import java.math.BigDecimal;
import java.math.*;
import java.text.NumberFormat;
import java.util.*;
import java.io.FileWriter;

public class LoanCalculator extends Application{

	Stage window;
	Button button;
	ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();

	public static void main(String[] args){
		launch(args);
	}

	@Override 
	public void start(Stage primaryStage) throws Exception{
		window = primaryStage;
		window.setTitle("Loan Calculator");
		
		TextField loanAmount = new TextField();
		loanAmount.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,12}")) {
                    loanAmount.setText(oldValue);
                }
            }
        });
		
		TextField interestPercent = new TextField();
		interestPercent.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,2}([\\.]\\d{0,3})?")) {
                    interestPercent.setText(oldValue);
                }
            }
        });
		
		TextField repaymentPeriod = new TextField();
		repaymentPeriod.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,3}")) {
                    repaymentPeriod.setText(oldValue);
                }
            }
        });
		
		NumberFormat nf = NumberFormat.getInstance();
		
		button = new Button("Calculate");
		Button CSV = new Button("CSV Table");
	
		Text t1 = new Text("Loan Amount:");
		Text t1_1 = new Text("$");
		Text t2 = new Text("Interest Rate:");
		Text t2_1 = new Text("%");
		Text t3 = new Text("Repayment Period:");
		Text t3_1 = new Text("Months");
		Text t4 = new Text("Payment Frequency:");
		String m = "Monthly loan payment: ";
		String sm = "Semi-Monthly loan payment: ";
		String bw = "Bi-Weekly loan payment: ";
		String w = "Weekly loan payment: ";
		Text t5 = new Text(m);
		Text t5_1 = new Text("$0.00");
		t5_1.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
		Text t6 = new Text("Total interest payable:  ");
		Text t6_1 = new Text("$0.00");
		t6_1.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
		Text disclamer1 = new Text("*Assuming 52 weeks per year");
		
		GridPane gridPane = new GridPane();
		gridPane.setMinSize(400,300);
		gridPane.setPadding(new Insets(10,10,10,10));
		gridPane.setVgap(5);
		gridPane.setHgap(5);
		//gridPane.setAlignment(Pos.TOP_CENTER);
		gridPane.setAlignment(Pos.CENTER);
		
		
		ChoiceBox<String> choiceBox = new ChoiceBox<>();
		choiceBox.getItems().addAll("Monthly", "Semi-Monthly", "Biweekly*", "Weekly*");
		choiceBox.setValue("Monthly");
		
		//Arranging Elemnts in Grid Pane
		gridPane.add(t1, 0,0);
		gridPane.add(loanAmount, 1,0);
		gridPane.add(t1_1, 2,0);
		gridPane.add(t2, 0,1);
		gridPane.add(interestPercent, 1,1);
		gridPane.add(t2_1, 2,1);
		gridPane.add(t3, 0,2);
		gridPane.add(repaymentPeriod, 1,2);
		gridPane.add(t3_1, 2,2);
		gridPane.add(t4, 0, 3);
		gridPane.add(choiceBox, 1, 3);
		gridPane.add(button, 2, 4);
		gridPane.add(t5, 0, 8);
		gridPane.add(t5_1, 1, 8);
		gridPane.add(t6, 0, 10);
		gridPane.add(t6_1, 1, 10);
		gridPane.add(CSV, 2, 19);
		gridPane.add(disclamer1, 0, 20);
		
		gridPane.setStyle("-fx-background-color: MintCream;");
		button.setStyle("-fx-background-color: linear-gradient(#f2f2f2, #d6d6d6), linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%), linear-gradient(#dddddd 0%, #f6f6f6 50%);-fx-background-radius: 8,7,6;-fx-background-insets: 0,1,2;-fx-text-fill: black;-fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );-fx-font-weight: bold;");
		
		Scene scene = new Scene(gridPane);
		
		
		choiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() { 
  
            // if the item of the list is changed 
            public void changed(ObservableValue ov, Number value, Number new_value) 
            { 
				if(new_value.intValue() == 0){
					t5.setText(m);
				}else if(new_value.intValue() == 1){
					t5.setText(sm);
				}else if(new_value.intValue() == 2){
					t5.setText(bw);
				}else{
					t5.setText(w);
				} 
            } 
        }); 
		
		
		
		button.setOnAction(e -> {
			BigDecimal LA = new BigDecimal(loanAmount.getText());
			BigDecimal IR = new BigDecimal(interestPercent.getText());
			BigDecimal RP = new BigDecimal(repaymentPeriod.getText());

			if(choiceBox.getValue().equals("Monthly")){
				BigDecimal[] bd = CalculateMonthly(LA, IR, RP);
				t5_1.setText(""+NumberFormat.getCurrencyInstance().format(bd[0]));
				t6_1.setText(""+NumberFormat.getCurrencyInstance().format(bd[1]));
				list.clear();
				changeList(bd[0],LA, IR, RP, 12);
			}else if(choiceBox.getValue().equals("Semi-Monthly")){
				BigDecimal[] bd = CalculateSemiMonthly(LA, IR, RP);
				t5_1.setText(""+NumberFormat.getCurrencyInstance().format(bd[0]));
				t6_1.setText(""+NumberFormat.getCurrencyInstance().format(bd[1]));
				list.clear();
				changeList(bd[0],LA, IR, RP, 24);
			}else if(choiceBox.getValue().equals("Biweekly*")){
				BigDecimal[] bd = CalculateBiweekly(LA, IR, RP);
				t5_1.setText(""+NumberFormat.getCurrencyInstance().format(bd[0]));
				t6_1.setText(""+NumberFormat.getCurrencyInstance().format(bd[1]));
				list.clear();
				changeList(bd[0],LA, IR, RP, 26);
			}else if(choiceBox.getValue().equals("Weekly*")){
				BigDecimal[] bd = CalculateWeekly(LA, IR, RP);
				list.clear();
				changeList(bd[0],LA, IR, RP, 52);
				t5_1.setText(""+NumberFormat.getCurrencyInstance().format(bd[0]));
				t6_1.setText(""+NumberFormat.getCurrencyInstance().format(bd[1]));
				list.clear();
				changeList(bd[0],LA, IR, RP, 52);
			}
		});
		
		CSV.setOnAction( e3 -> {
			try{
			FileWriter csvFile = new FileWriter("new.csv");
			for (ArrayList<String> rowData : list) {
				csvFile.append(String.join(",", rowData));
				csvFile.append("\n");
			}
			csvFile.flush();
			csvFile.close();
			}catch(Exception e4){
				System.out.println("Could Not Create File");
			}
		});
		
		
		window.setScene(scene);
		window.show();
	}
	
	private BigDecimal[] CalculateMonthly(BigDecimal LA, BigDecimal IR, BigDecimal RP){
		BigDecimal[] bd = CalculatePayment(LA,IR,RP,12);
		System.out.println("Monthly Payment is $" + bd[0]);
		System.out.println("Total Interest Payable is $" + bd[1]);
		
		return bd;
	}
	
	private BigDecimal[] CalculateSemiMonthly(BigDecimal LA, BigDecimal IR, BigDecimal RP){
		BigDecimal[] bd = CalculatePayment(LA,IR,RP,24);
		System.out.println("Semi-Monthly Payment is $" + bd[0]);
		System.out.println("Total Interest Payable is $" + bd[1]);
		
		return bd;
	}
	
	private BigDecimal[] CalculateBiweekly(BigDecimal LA, BigDecimal IR, BigDecimal RP){
		BigDecimal[] bd = CalculatePayment(LA,IR,RP,26);
		System.out.println("Bi-Weekly Payment is $" + bd[0]);
		System.out.println("Total Interest Payable is $" + bd[1]);
		
		return bd;
	}
	
	private BigDecimal[] CalculateWeekly(BigDecimal LA, BigDecimal IR, BigDecimal RP){
		BigDecimal[] bd = CalculatePayment(LA,IR,RP,52);
		System.out.println("Weekly Payment is $" + bd[0]);
		System.out.println("Total Interest Payable is $" + bd[1]);
		
		return bd;
	}
	
	private BigDecimal[] CalculatePayment(BigDecimal LA, BigDecimal IR, BigDecimal RP, int payPeriods){
		MathContext mc = new MathContext(64, RoundingMode.HALF_UP);
		
		BigDecimal payment = (LA.multiply(IR.divide(BigDecimal.valueOf(100), mc), mc)).divide(
		BigDecimal.valueOf(payPeriods).multiply(BigDecimal.valueOf(1).subtract(BigDecimal.valueOf(1).divide((BigDecimal.valueOf(1).add((IR.divide(BigDecimal.valueOf(100), mc)).divide(BigDecimal.valueOf(payPeriods), mc))
		), mc).pow(((RP.divide(BigDecimal.valueOf(12),mc)).multiply(BigDecimal.valueOf(payPeriods))).intValue(), mc) )), mc);
		
		BigDecimal totalInterestPayable = (payment.multiply(RP.divide(BigDecimal.valueOf(12), mc),mc).multiply(BigDecimal.valueOf(payPeriods), mc)).subtract(LA);
		
		BigDecimal[] bd = new BigDecimal[]{payment.setScale(2, RoundingMode.HALF_UP), totalInterestPayable.setScale(2, RoundingMode.HALF_UP)};
		
		return bd;
	}
	
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
	
}