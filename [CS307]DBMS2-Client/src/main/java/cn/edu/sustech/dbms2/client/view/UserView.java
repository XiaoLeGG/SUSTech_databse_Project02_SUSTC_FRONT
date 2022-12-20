package cn.edu.sustech.dbms2.client.view;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXDialog.DialogTransition;
import com.jfoenix.effects.JFXDepthManager;

import cn.edu.sustech.dbms2.client.DBClient;
import cn.edu.sustech.dbms2.client.packet.client.CityCountPacket;
import cn.edu.sustech.dbms2.client.packet.client.CompanyCountPacket;
import cn.edu.sustech.dbms2.client.packet.client.CourierCountPacket;
import cn.edu.sustech.dbms2.client.packet.client.ShipCountPacket;
import cn.edu.sustech.dbms2.client.packet.server.CityCountInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.CompanyCountInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.CourierCountInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.LoginInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.ShipCountInfoPacket;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class UserView {
	
	private Stage stage;
	private String user;
	private String staffType;
	private String company;
	private String city;
	private String phone;
	private int age;
	private boolean gender;
	private String cookie;
	
	public UserView(LoginInfoPacket infoPacket) {
		this.user = infoPacket.getUserName();
		this.company = infoPacket.getCompany();
		this.city = infoPacket.getCity();
		this.phone = infoPacket.getPhoneNumber();
		this.age = infoPacket.getAge();
		this.gender = infoPacket.getGender();
		this.cookie = infoPacket.getCookie();
		if (infoPacket.getStaffType().equals("SustcManager")) {
			this.staffType = "SUSTC Department Manager";
		}
		if (infoPacket.getStaffType().equals("")) {
			
		}
		this.initPane();
	}
	
	public Stage getStage() {
		return this.stage;
	}
	
	private void showDialog(String text, StackPane pane, boolean isWarning) {
		JFXDialog dialog = new JFXDialog();
		dialog.getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());
		dialog.setTransitionType(DialogTransition.CENTER);
		JFXDialogLayout layout = new JFXDialogLayout();
		layout.setHeading(new Label(isWarning ? "出错" : "成功"));
		layout.setBody(new Label(text));
		JFXButton closeButton = new JFXButton(isWarning ? "关闭" : "确认");
		closeButton.setStyle("-fx-text-fill: " + (isWarning ? "#FF0000" : "SKYBLUE"));
		closeButton.setOnAction(event -> {
			dialog.close();
		});
		layout.setActions(closeButton);
		closeButton.getStyleClass().add("dialog-accept");
		dialog.setContent(layout);
		dialog.setDialogContainer(pane);
		dialog.show();
	}
	
	public void initPane() {
		stage = new Stage();
		stage.setMaxHeight(720);
		stage.setMaxWidth(1080);
		stage.setTitle(this.staffType + " 面板");
		
		StackPane top = new StackPane();
		top.setPrefSize(1080, 720);
		HBox hbox = new HBox();
		hbox.setMaxHeight(550);
		hbox.setMaxWidth(900);
		hbox.setSpacing(60);
		
		if (this.staffType.startsWith("SUSTC")) {	
			StackPane leftPane = new StackPane();
			leftPane.getStylesheets().add(getClass().getResource("/css/userview.css").toExternalForm());
			leftPane.setPrefHeight(550);
			leftPane.setPrefWidth(590);
			leftPane.setStyle("-fx-background-color: WHITE");
			leftPane.setPadding(new Insets(30, 0, 0, 30));
			JFXDepthManager.setDepth(leftPane, 4);
			hbox.getChildren().add(leftPane);
			
			VBox leftBox = new VBox();
			leftBox.setPrefWidth(540);
			leftBox.setPrefHeight(500);
			leftBox.setSpacing(20);
			
			VBox companyBox = new VBox();
			companyBox.setSpacing(5);
			JFXButton companyButton = new JFXButton("查询公司数量");
			TextFlow companyFlow = new TextFlow();
			Text companyTitle = new Text("公司数量: ");
			companyTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			Text companyCount = new Text("尚未查询");
			companyButton.setOnAction(e -> {
				DBClient client = new DBClient();
				try {
					CompanyCountInfoPacket receive = (CompanyCountInfoPacket) client.sendAndReceivePacket(new CompanyCountPacket(this.cookie));
					int count = receive.getCount();
					if (count == -1) {
						showDialog("查询失败", top, true);
					} else {
						companyCount.setText("" + count);
						showDialog("成功查询到公司数量", top, false);
					}
				} catch (Exception e1) {
					showDialog("查询失败", top, true);
				}
			});
			companyCount.setFont(Font.font(14));
			companyFlow.getChildren().addAll(companyTitle, companyCount);
			companyBox.getChildren().addAll(companyButton, companyFlow);
			leftBox.getChildren().add(companyBox);
			
			VBox cityBox = new VBox();
			cityBox.setSpacing(5);
			JFXButton cityButton = new JFXButton("查询城市数量");
			TextFlow cityFlow = new TextFlow();
			Text cityTitle = new Text("城市数量: ");
			cityTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			Text cityCount = new Text("尚未查询");
			cityButton.setOnAction(e -> {
				DBClient client = new DBClient();
				try {
					CityCountInfoPacket receive = (CityCountInfoPacket) client.sendAndReceivePacket(new CityCountPacket(this.cookie));
					int count = receive.getCount();
					if (count == -1) {
						showDialog("查询失败", top, true);
					} else {
						cityCount.setText("" + count);
						showDialog("成功查询到城市数量", top, false);
					}
				} catch (Exception e1) {
					showDialog("查询失败", top, true);
				}
			});
			cityCount.setFont(Font.font(14));
			cityFlow.getChildren().addAll(cityTitle, cityCount);
			cityBox.getChildren().addAll(cityButton, cityFlow);
			leftBox.getChildren().add(cityBox);

			VBox courierBox = new VBox();
			courierBox.setSpacing(5);
			JFXButton courierButton = new JFXButton("查询快递员数量");
			TextFlow courierFlow = new TextFlow();
			Text courierTitle = new Text("快递员数量: ");
			courierTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			Text courierCount = new Text("尚未查询");
			courierButton.setOnAction(e -> {
				DBClient client = new DBClient();
				try {
					CourierCountInfoPacket receive = (CourierCountInfoPacket) client.sendAndReceivePacket(new CourierCountPacket(this.cookie));
					int count = receive.getCount();
					if (count == -1) {
						showDialog("查询失败", top, true);
					} else {
						courierCount.setText("" + count);
						showDialog("成功查询到快递员数量", top, false);
					}
				} catch (Exception e1) {
					showDialog("查询失败", top, true);
				}
			});
			courierCount.setFont(Font.font(14));
			courierFlow.getChildren().addAll(courierTitle, courierCount);
			courierBox.getChildren().addAll(courierButton, courierFlow);
			leftBox.getChildren().add(courierBox);
			
			VBox shipBox = new VBox();
			shipBox.setSpacing(5);
			JFXButton shipButton = new JFXButton("查询船只数量");
			TextFlow shipFlow = new TextFlow();
			Text shipTitle = new Text("船只数量: ");
			shipTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			Text shipCount = new Text("尚未查询");
			shipButton.setOnAction(e -> {
				DBClient client = new DBClient();
				try {
					ShipCountInfoPacket receive = (ShipCountInfoPacket) client.sendAndReceivePacket(new ShipCountPacket(this.cookie));
					int count = receive.getCount();
					if (count == -1) {
						showDialog("查询失败", top, true);
					} else {
						shipCount.setText("" + count);
						showDialog("成功查询到船只数量", top, false);
					}
				} catch (Exception e1) {
					showDialog("查询失败", top, true);
				}
			});
			shipCount.setFont(Font.font(14));
			shipFlow.getChildren().addAll(shipTitle, shipCount);
			shipBox.getChildren().addAll(shipButton, shipFlow);
			leftBox.getChildren().add(shipBox);	
		
			leftPane.getChildren().add(leftBox);
			
		}
		
		
		
		StackPane rightPane = new StackPane();
		rightPane.setPrefHeight(550);
		rightPane.setPrefWidth(250);
		rightPane.setStyle("-fx-background-color: WHITE");
		
		
		VBox rightBox = new VBox();
		rightBox.setPrefSize(180, 500);
		rightBox.setSpacing(40);
		rightBox.setAlignment(Pos.TOP_CENTER);

		
		ImageView image = new ImageView();
		BorderPane imageViewWrapper = new BorderPane(image);
		rightBox.setPadding(new Insets(30,0,0,0));
		
		imageViewWrapper.setCursor(Cursor.HAND);
		imageViewWrapper.setMaxSize(128, 128);
		imageViewWrapper.setStyle("-fx-border-style: SOLID; -fx-border-width: 5;-fx-border-color: BLACK");
		image.setImage(new Image(getClass().getResourceAsStream("/aiprofile.png")));
		image.setSmooth(true);
		image.setFitHeight(128);
		image.setFitWidth(128);
		rightBox.getChildren().add(imageViewWrapper);
		
		VBox infoBox = new VBox();
		infoBox.setSpacing(10);
		infoBox.setMaxWidth(180);
		
		
		TextFlow userFlow = new TextFlow();
		Text userText = new Text("用户名: ");
		userText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		Text userNameText = new Text(this.user);
		userNameText.setFont(Font.font(14));
		userFlow.getChildren().addAll(userText, userNameText);
		
		infoBox.getChildren().add(userFlow);
		
		TextFlow perFlow = new TextFlow();
		Text perText = new Text("职责: ");
		perText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		Text perNameText = new Text(this.staffType);
		perNameText.setFont(Font.font(14));
		perFlow.getChildren().addAll(perText, perNameText);
		infoBox.getChildren().add(perFlow);
		
		TextFlow genderFlow = new TextFlow();
		Text genderText = new Text("性别: ");
		genderText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		Text genderNameText = new Text((this.gender ? "女" : "男"));
		genderNameText.setFont(Font.font(14));
		genderFlow.getChildren().addAll(genderText, genderNameText);
		infoBox.getChildren().add(genderFlow);
		
		TextFlow cityFlow = new TextFlow();
		Text cityText = new Text("城市: ");
		cityText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		Text cityNameText = new Text((this.city == null ? "无" : this.city));
		cityNameText.setFont(Font.font(14));
		cityFlow.getChildren().addAll(cityText, cityNameText);
		infoBox.getChildren().add(cityFlow);
		
		TextFlow companyFlow = new TextFlow();
		Text companyText = new Text("公司: ");
		companyText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		Text companyNameText = new Text((this.company == null ? "无" : this.company));
		companyNameText.setFont(Font.font(14));
		companyFlow.getChildren().addAll(companyText, companyNameText);
		infoBox.getChildren().add(companyFlow);
		
		TextFlow ageFlow = new TextFlow();
		Text ageText = new Text("年龄: ");
		ageText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		Text ageNameText = new Text("" + this.age);
		ageNameText.setFont(Font.font(14));
		ageFlow.getChildren().addAll(ageText, ageNameText);
		infoBox.getChildren().add(ageFlow);
		
		TextFlow phoneFlow = new TextFlow();
		Text phoneText = new Text("手机号: ");
		phoneText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		Text phoneNameText = new Text("" + this.phone);
		phoneNameText.setFont(Font.font(14));
		phoneFlow.getChildren().addAll(phoneText, phoneNameText);
		infoBox.getChildren().add(phoneFlow);
		
		rightBox.getChildren().add(infoBox);
		
		rightPane.getChildren().add(rightBox);
		StackPane.setAlignment(rightBox, Pos.CENTER);
		hbox.getChildren().add(rightPane);
		
		JFXDepthManager.setDepth(rightPane, 4);
		
		top.getChildren().add(hbox);
		StackPane.setAlignment(hbox, Pos.CENTER);
		JFXDecorator dec = new JFXDecorator(stage, top);
		Scene scene = new Scene(dec, 1080, 720);
		stage.setScene(scene);
	}
	
}
