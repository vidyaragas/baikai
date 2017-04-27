package com.ajna;

import javafx.scene.text.Font;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;



@SuppressWarnings({"restriction" })
public class MainApp extends Application implements BaikaiInterface{

	final BaikaiServer m_baikaiServer = new BaikaiServer();
	public ObservableList<NseBaikaiModel> data = FXCollections.observableArrayList();
	public TableView<NseBaikaiModel> tableView = new TableView<NseBaikaiModel>();
	GridPane grid;
	final HBox hb_row1 = new HBox();
	final HBox hb_row2 = new HBox();
	final HBox hb_bottom = new HBox();
	final VBox vb_top = new VBox();
	final Button switchBtn = new Button();
	final Label switchLbl = new Label();
	private SimpleBooleanProperty switchedOn = new SimpleBooleanProperty(true);
	Timer m_scheduler;
	
	final int GUI_HEIGHT = 850;
	final int GUI_WIDTH = 400;
	
	String m_symbol, m_prevSymbol;
	final ComboBox<String> m_expiryComboBox = new ComboBox<String>();
	ComboBox<String> m_symbolComboBox = new ComboBox<String>();
	Label m_lastPrcDisplay = new Label("0.0");
	Label m_lastTradeDateDisplay = new Label(" ");
	private double m_lastPrc = 0.0;

	Vector<String> allSyms  = new Vector<String>(Arrays.asList("AUROPHARMA","AXISBANK","BANKBARODA","BHEL","BPCL","BHARTIARTL","CIPLA","COALINDIA","GAIL","HINDALCO","ITC","ICICIBANK","LICHSGFIN","NTPC","ONGC","POWERGRID","TATAMOTORS","TATAPOWER","TATASTEEL","TECHM","WIPRO","INFY","SBIN","IDEA","ADANIPORTS"));  

	@SuppressWarnings("unchecked")
	@Override
	public void start(Stage primaryStage){


		primaryStage.setTitle("Baikai View");

		Label userName = new Label("Symbol: ");

		m_symbolComboBox.setEditable(true);

		m_symbol = "SBIN";

		refreshSymbolCombo();

		Button btn = new Button("Get");

		Label expiryLabel = new Label("Expiry: ");

		Button refreshBtn = new Button("Refresh");
		
		Label refreshLabel = new Label("Auto Refresh every min");
		
		switchBtn.setPrefWidth(40);

		switchLbl.setUserData(switchBtn);
		
		btn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				String sym = m_symbolComboBox.getValue().toString();


				try {
					//System.out.println("creating new thread to call initSubscribe: " + Thread.currentThread().getName());
					Thread th = new Thread(() -> initSubscribe(sym));
					th.start();
				} catch (Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		});

		switchBtn.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent t)
            {
                switchedOn.set(!switchedOn.get());
                
                if(switchedOn.get()){
                	//System.out.println("Switched on: " + Thread.currentThread().getName());
                	//System.out.println("Refresh immediately : " + Thread.currentThread().getName());
					Thread th = new Thread(() -> refreshExpiry());
					th.start();
					//System.out.println("Refresh task scheduling : " + Thread.currentThread().getName());
					m_scheduler = new Timer();
                	m_scheduler.scheduleAtFixedRate(new refresherTimerTask(), 60*1000,60*1000);
                } else {
                	//System.out.println("Switched off: " + Thread.currentThread().getName());
                	m_scheduler.cancel();
                	//System.out.println("Refresh task Cancelling : " + Thread.currentThread().getName());
                }
            }
        });

		switchLbl.setGraphic(switchBtn);

	        switchedOn.addListener(new ChangeListener<Boolean>()
	        {
	            @Override
	            public void changed(ObservableValue<? extends Boolean> ov,
	                Boolean t, Boolean t1)
	            {
	                if (t1)
	                {
	                	switchLbl.setText("ON");
	                	switchLbl.setStyle("-fx-background-color: green;-fx-text-fill:white;");
	                	switchLbl.setContentDisplay(ContentDisplay.RIGHT);
	                }
	                else
	                {
	                	switchLbl.setText("OFF");
	                	switchLbl.setStyle("-fx-background-color: grey;-fx-text-fill:black;");
	                	switchLbl.setContentDisplay(ContentDisplay.LEFT);
	                }
	            }
	        });

	        switchedOn.set(false);
	    

		m_expiryComboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override 
			public void changed(ObservableValue ov, String oExp, String nExp) {
				//System.out.println(oExp);
				//System.out.println(nExp);
				try {
					//System.out.println("creating new thread to refresh expiry: " + Thread.currentThread().getName());
					Thread th = new Thread(() -> refreshExpiry());
					th.start();
				} catch (Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}    
		});

		TableColumn<NseBaikaiModel, String> callPrcColumn = new TableColumn<NseBaikaiModel, String>("Call");
		callPrcColumn.setCellValueFactory(new PropertyValueFactory<NseBaikaiModel, String>("callString"));
		callPrcColumn.setPrefWidth(150);
		
		TableColumn<NseBaikaiModel, Double> strkPrcColumn = new TableColumn<NseBaikaiModel, Double>("Strike");
		strkPrcColumn.setCellValueFactory(new PropertyValueFactory<NseBaikaiModel, Double>("strkPrc"));
		strkPrcColumn.setPrefWidth(100);
		
		TableColumn<NseBaikaiModel, String> putPrcColumn = new TableColumn<NseBaikaiModel, String>("Put");
		putPrcColumn.setCellValueFactory(new PropertyValueFactory<NseBaikaiModel, String>("putString"));
		putPrcColumn.setPrefWidth(150);
		
		tableView.getColumns().addAll(callPrcColumn, strkPrcColumn, putPrcColumn);



		BorderPane borderPane = new BorderPane();


		hb_row1.getChildren().addAll(userName, m_symbolComboBox, btn);
		hb_row1.setSpacing(10);
		hb_row1.setAlignment(Pos.CENTER);

		Label lastPrcLabel = new Label("Spot Price: ");

		Label lastTradeDateLabel = new Label("Time: ");

		hb_row2.getChildren().addAll(expiryLabel, m_expiryComboBox, refreshLabel,switchLbl);
		hb_row2.setSpacing(10);
		hb_row2.setAlignment(Pos.CENTER);

		vb_top.setSpacing(10);
		vb_top.getChildren().addAll(hb_row1,hb_row2);

		Font lpfont = new Font("Arial", 25);
		m_lastPrcDisplay.setFont(lpfont);
		
		hb_bottom.getChildren().addAll(lastPrcLabel, m_lastPrcDisplay, lastTradeDateLabel, m_lastTradeDateDisplay);
		hb_bottom.setSpacing(10);
		hb_bottom.setAlignment(Pos.CENTER);

		borderPane.setTop(vb_top);
		borderPane.setCenter(tableView);
		borderPane.setBottom(hb_bottom);
		final Scene scene1 = new Scene(borderPane, GUI_WIDTH, GUI_HEIGHT);

		// show the stage.
		primaryStage.setScene(scene1);
		primaryStage.show();
	}

	/**
	 * 
	 * @param symbol
	 */
	public void initSubscribe(String symbol)  {
		m_symbol = symbol;

		if (m_prevSymbol == null){
			m_baikaiServer.subscribe(symbol, this);
		} else { 
			if (m_prevSymbol.equalsIgnoreCase(m_symbol)){
				//just refresh
				m_baikaiServer.updateBaikaiForAllExpiries(symbol);
			} else {
				m_baikaiServer.unSubscribe(m_prevSymbol, this);
				m_baikaiServer.subscribe(symbol, this);
			}
		}
		m_prevSymbol = m_symbol;
		//check if this symbol is in the populated list, if not add
		boolean found = false;
		if (allSyms.contains(m_symbol)){
			
		}else {
			allSyms.add(m_symbol);
			Platform.runLater(
					() -> {
						// Update UI here.
						refreshSymbolCombo();
					});
		}
	}
	/**
	 * call this function either in timer or manual refresh
	 */
	void refreshSymbolCombo(){

		Collections.sort(allSyms);
		m_symbolComboBox.getItems().clear();
		m_symbolComboBox.getItems().addAll(allSyms);
		m_symbolComboBox.setValue(m_symbol);
	}
	/**
	 * 
	 */
	public void refreshExpiry(){
		String sym = m_symbolComboBox.getValue().toString();
		String expry = m_expiryComboBox.getValue().toString();
		if (!sym.isEmpty() && !expry.isEmpty()){
			//System.out.println("Refreshing with " + sym + " for expiry " + expry);
			m_baikaiServer.updateBaikaiForExpiry(sym, expry);
		}
	}
	/**
	 * 
	 * @param expry
	 * @param t
	 */
	@SuppressWarnings("unchecked")
	public void updateUI(String expry, NseBaikai t){
		data.clear();
		data.removeAll(data);
		String symb = t.getExpiry();
		 m_lastPrc = t.getLastPrice();
		m_lastPrcDisplay.setText(""+m_lastPrc);
		m_lastTradeDateDisplay.setText(t.getLastTraded());
		ArrayList<OptionQuote> qts = t.getQuote(expry);
		if(m_expiryComboBox.getItems().contains(expry)){
		} else {
			m_expiryComboBox.getItems().addAll(expry);
		}
		m_expiryComboBox.setValue(expry);
		for (OptionQuote qte : qts){
			data.add(new NseBaikaiModel(expry, qte.getStrikePrice(), qte.getCallPrice(),qte.getPutPrice(), qte.getCallNC(), qte.getPutNC()));
			//System.out.println("Adding: " + expry + "|" + qte.getStrikePrice() + "|" + qte.getCallPrice() + "|" + qte.getPutPrice() + "\n");
		}
		tableView.setItems(data);
		recheck_table();
	}

	/**
	 * 
	 */
	private void recheck_table() {
		tableView.setRowFactory(new Callback<TableView<NseBaikaiModel>, TableRow<NseBaikaiModel>>() {
			@Override
			public TableRow<NseBaikaiModel> call(TableView<NseBaikaiModel> paramP) {
				return new TableRow<NseBaikaiModel>() {

					@Override
					protected void updateItem(NseBaikaiModel paramT, boolean paramBoolean) {

						super.updateItem(paramT, paramBoolean);
						if (!isEmpty()) {
							String style = "-fx-control-inner-background: #007F0E;"
									+ "-fx-control-inner-background-alt: #007F0E;";	 
							if(paramT != null) {
								if( paramT.getStrkPrc() > m_lastPrc) {
									  style = "-fx-control-inner-background: #787F00;"
											+ "-fx-control-inner-background-alt: #787F00;";
								} else {
									style = "-fx-control-inner-background: #7F0040;"
											+ "-fx-control-inner-background-alt: #7F0040;";
								}
							}
							setStyle(style);
						}
					}
				};
			}
		});
	}
	/**
	 * 
	 */
	@Override
	public void onOptionUpdate(String expiry, NseBaikai nbk) {
		// TODO Auto-generated method stub
		Platform.runLater(
				() -> {
					// Update UI here.
					updateUI(expiry, nbk);
				});
	}
	/**
	 * 
	 *  
	 *
	 */
	private class refresherTimerTask extends TimerTask {
		private final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        public void run() {
        	Date date = new Date(); 
        	System.out.println(sdf.format(date) + " Refresher Timer : " + Thread.currentThread().getName() );
            refreshExpiry();
        }
    }
	/*****************************************************
	 * 
	 * @param args
	 * @throws InterruptedException
	 *****************************************************/
	public static void main(String[] args) throws InterruptedException {
		launch(args);
	}
}

