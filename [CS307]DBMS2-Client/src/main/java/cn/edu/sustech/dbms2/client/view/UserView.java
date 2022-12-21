package cn.edu.sustech.dbms2.client.view;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kordamp.ikonli.javafx.FontIcon;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXDialog.DialogTransition;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.validation.RequiredFieldValidator;
import com.jfoenix.validation.base.ValidatorBase;

import cn.edu.sustech.dbms2.client.DBClient;
import cn.edu.sustech.dbms2.client.packet.client.CityCountPacket;
import cn.edu.sustech.dbms2.client.packet.client.CompanyCountPacket;
import cn.edu.sustech.dbms2.client.packet.client.ContainerPacket;
import cn.edu.sustech.dbms2.client.packet.client.CourierCountPacket;
import cn.edu.sustech.dbms2.client.packet.client.ItemPacket;
import cn.edu.sustech.dbms2.client.packet.client.ShipCountPacket;
import cn.edu.sustech.dbms2.client.packet.client.ShipPacket;
import cn.edu.sustech.dbms2.client.packet.client.StaffPacket;
import cn.edu.sustech.dbms2.client.packet.server.CityCountInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.CompanyCountInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.ContainerInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.CourierCountInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.ItemInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.LoginInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.ShipCountInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.ShipInfoPacket;
import cn.edu.sustech.dbms2.client.packet.server.StaffInfoPacket;
import cn.edu.sustech.dbms2.client.interfaces.ContainerInfo;
import cn.edu.sustech.dbms2.client.interfaces.ItemInfo;
import cn.edu.sustech.dbms2.client.interfaces.ShipInfo;
import cn.edu.sustech.dbms2.client.interfaces.StaffInfo;
import cn.edu.sustech.dbms2.client.interfaces.ItemInfo.ImportExportInfo;
import cn.edu.sustech.dbms2.client.interfaces.ItemInfo.RetrievalDeliveryInfo;
import cn.edu.sustech.dbms2.client.interfaces.ItemState;
import cn.edu.sustech.dbms2.client.interfaces.ContainerInfo.Type;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
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
		this.staffType = staffTypeStringMap.get(infoPacket.getStaffType());
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
	
	public void initSustcManagerBoard(HBox hbox, StackPane top) {
		StackPane leftPane = new StackPane();
		leftPane.getStylesheets().add(getClass().getResource("/css/userview.css").toExternalForm());
		leftPane.setPrefHeight(550);
		leftPane.setPrefWidth(590);
		leftPane.setStyle("-fx-background-color: WHITE");
		leftPane.setPadding(new Insets(30, 0, 0, 30));
		JFXDepthManager.setDepth(leftPane, 4);
		hbox.getChildren().add(leftPane);
		
		HBox leftHBox = new HBox();
		leftHBox.setPrefHeight(500);
		leftHBox.setPrefWidth(270);
		
		
		VBox leftBox = new VBox();
		leftBox.setPrefWidth(270);
		leftBox.setPrefHeight(500);
		leftBox.setSpacing(25);
		leftHBox.getChildren().add(leftBox);
		
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
		
		VBox containerInfoBox = new VBox();
		containerInfoBox.setSpacing(5);
		JFXButton containerInfoButton = new JFXButton("查询容器信息");
		HBox containerHBox = new HBox();
		TextFlow containerInfoFlow = new TextFlow();
		Text containerInfoTitle = new Text("容器代码: ");
		containerInfoTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		
		JFXTextField codeTextField = new JFXTextField();
		ValidatorBase codeValid = new RequiredFieldValidator("容器代码不能为空");
		FontIcon codeTri = new FontIcon();
		codeTri.setIconLiteral("fas-exclamation-triangle");
		codeValid.setIcon(codeTri);
		codeTextField.setValidators(codeValid);
		
		containerInfoButton.setOnAction(e -> {
			DBClient client = new DBClient();
			try {
				if (codeTextField.validate()) {
					ContainerInfoPacket receive = (ContainerInfoPacket) client.sendAndReceivePacket(new ContainerPacket(this.cookie, codeTextField.getText()));
					ContainerInfo info = receive.getInfo();
					if (info == null) {
						showDialog("查询失败", top, true);
					} else {
						showInfo(info, top);
					}
				}
			} catch (Exception e1) {
				showDialog("查询失败", top, true);
			}
		});
		containerInfoFlow.getChildren().add(containerInfoTitle);
		containerHBox.getChildren().addAll(containerInfoFlow, codeTextField);
		containerInfoBox.getChildren().addAll(containerInfoButton, containerHBox);
		
		leftBox.getChildren().add(containerInfoBox);

		VBox shipInfoBox = new VBox();
		shipInfoBox.setSpacing(5);
		JFXButton shipInfoButton = new JFXButton("查询船只信息");
		HBox shipHBox = new HBox();
		TextFlow shipInfoFlow = new TextFlow();
		Text shipInfoTitle = new Text("船只名称: ");
		shipInfoTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		
		JFXTextField shipNameTextField = new JFXTextField();
		ValidatorBase shipNameValid = new RequiredFieldValidator("船只名称不能为空");
		FontIcon shipNameTri = new FontIcon();
		shipNameTri.setIconLiteral("fas-exclamation-triangle");
		shipNameValid.setIcon(shipNameTri);
		shipNameTextField.setValidators(shipNameValid);
		
		shipInfoButton.setOnAction(e -> {
			DBClient client = new DBClient();
			try {
				if (shipNameTextField.validate()) {
					ShipInfoPacket receive = (ShipInfoPacket) client.sendAndReceivePacket(new ShipPacket(this.cookie, shipNameTextField.getText()));
					ShipInfo info = receive.getInfo();
					if (info == null) {
						showDialog("查询失败", top, true);
					} else {
						showInfo(info, top);
					}
				}
			} catch (Exception e1) {
				showDialog("查询失败", top, true);
			}
		});
		shipInfoFlow.getChildren().add(shipInfoTitle);
		shipHBox.getChildren().addAll(shipInfoFlow, shipNameTextField);
		shipInfoBox.getChildren().addAll(shipInfoButton, shipHBox);
		
		leftBox.getChildren().add(shipInfoBox);
		
		VBox leftBox2 = new VBox();
		leftBox2.setPrefWidth(270);
		leftBox2.setPrefHeight(500);
		leftBox2.setSpacing(25);
		leftHBox.getChildren().add(leftBox2);
		
		VBox itemInfoBox = new VBox();
		itemInfoBox.setSpacing(5);
		JFXButton itemInfoButton = new JFXButton("查询项目信息");
		HBox itemHBox = new HBox();
		TextFlow itemInfoFlow = new TextFlow();
		Text itemInfoTitle = new Text("项目名称: ");
		itemInfoTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		
		JFXTextField itemNameTextField = new JFXTextField();
		ValidatorBase itemNameValid = new RequiredFieldValidator("项目名称不能为空");
		FontIcon itemNameTri = new FontIcon();
		itemNameTri.setIconLiteral("fas-exclamation-triangle");
		itemNameValid.setIcon(itemNameTri);
		itemNameTextField.setValidators(itemNameValid);
		
		itemInfoButton.setOnAction(e -> {
			DBClient client = new DBClient();
			try {
				if (itemNameTextField.validate()) {
					ItemInfoPacket receive = (ItemInfoPacket) client.sendAndReceivePacket(new ItemPacket(this.cookie, itemNameTextField.getText()));
					ItemInfo info = receive.getInfo();
					if (info == null) {
						showDialog("查询失败", top, true);
					} else {
						showInfo(info, top);
					}
				}
			} catch (Exception e1) {
				showDialog("查询失败", top, true);
			}
		});
		
		itemInfoFlow.getChildren().add(itemInfoTitle);
		itemHBox.getChildren().addAll(itemInfoFlow, itemNameTextField);
		itemInfoBox.getChildren().addAll(itemInfoButton, itemHBox);
		
		leftBox2.getChildren().add(itemInfoBox);

		VBox staffInfoBox = new VBox();
		staffInfoBox.setSpacing(5);
		JFXButton staffInfoButton = new JFXButton("查询职员信息");
		HBox staffHBox = new HBox();
		TextFlow staffInfoFlow = new TextFlow();
		Text staffInfoTitle = new Text("职员姓名: ");
		staffInfoTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
		
		JFXTextField staffNameTextField = new JFXTextField();
		ValidatorBase staffNameValid = new RequiredFieldValidator("职员姓名不能为空");
		FontIcon staffNameTri = new FontIcon();
		staffNameTri.setIconLiteral("fas-exclamation-triangle");
		staffNameValid.setIcon(staffNameTri);
		staffNameTextField.setValidators(staffNameValid);
		
		staffInfoButton.setOnAction(e -> {
			DBClient client = new DBClient();
			try {
				if (staffNameTextField.validate()) {
					StaffInfoPacket receive = (StaffInfoPacket) client.sendAndReceivePacket(new StaffPacket(this.cookie, staffNameTextField.getText()));
					StaffInfo info = receive.getInfo();
					if (info == null) {
						showDialog("查询失败", top, true);
					} else {
						showInfo(info, top);
					}
				}
			} catch (Exception e1) {
				showDialog("查询失败", top, true);
			}
		});
		
		staffInfoFlow.getChildren().add(staffInfoTitle);
		staffHBox.getChildren().addAll(staffInfoFlow, staffNameTextField);
		staffInfoBox.getChildren().addAll(staffInfoButton, staffHBox);
		
		leftBox2.getChildren().add(staffInfoBox);
		
		leftPane.getChildren().add(leftHBox);
	}
	
	private static final Map<String, String> stringStaffTypeMap = new HashMap<>();
	private static final Map<String, String> staffTypeStringMap = new HashMap<>();
	
	static {
		stringStaffTypeMap.put("Courier", "Courier");
		stringStaffTypeMap.put("SUSTC Department Manager", "SustcManager");
		stringStaffTypeMap.put("Company Manager", "CompanyManager");
		stringStaffTypeMap.put("Seaport Officer", "SeaportOfficer");
		
		for (Map.Entry<String, String> entry : stringStaffTypeMap.entrySet()) {
			staffTypeStringMap.put(entry.getValue(), entry.getKey());
		}
		
	}
	
	public void showInfo(Record record, StackPane pane) {
		JFXDialog dialog = new JFXDialog();
		dialog.getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());
		dialog.setTransitionType(DialogTransition.CENTER);
		JFXDialogLayout layout = new JFXDialogLayout();
		
		String titles[] = null;
		String texts[] = null;
		
		if (record instanceof ContainerInfo) {
			ContainerInfo info = (ContainerInfo) record;
			layout.setHeading(new Label("容器信息"));
			
			String ctitles[] = {"容器代码", "容器类型", "容器状态"};
			String ctexts[] = {info.code(), info.type().toString(), info.using() ? "使用中" : "空闲"};
			titles = ctitles;
			texts = ctexts;
		}
		if (record instanceof ShipInfo) {
			ShipInfo info = (ShipInfo) record;
			layout.setHeading(new Label("船只信息"));
			
			String ctitles[] = {"船只名称", "船只所属", "船只状态"};
			String ctexts[] = {info.name(), info.owner().toString(), info.sailing() ? "航行中" : "空闲"};
			titles = ctitles;
			texts = ctexts;
		}
		if (record instanceof ItemInfo) {
			ItemInfo info = (ItemInfo) record;
			layout.setHeading(new Label("项目信息"));
			
			String ctitles[] = {"物品名称", "物品价格", "项目状态", 
					"检录城市", "检录快递员", 
					"寄送城市", "寄送快递员",
					"进口城市", "进口税价", "进口负责人",
					"出口城市", "出口税价", "出口负责人"};
			String ctexts[] = {info.name(), "" + String.format("%,.2f", info.price()), info.state().toString(), 
					info.retrieval().city(), info.retrieval().courier(), 
					info.delivery().city(), info.delivery().courier() == null ? "[待定]" : info.delivery().courier(), 
							info.$import().city(), "" + String.format("%,.2f", info.$import().tax()), info.$import().officer() == null ? "[待定]" : info.$import().officer(), 
									info.export().city(), "" + String.format("%,.2f", info.export().tax()), info.export().officer() == null ? "[待定]" : info.export().officer()};
			titles = ctitles;
			texts = ctexts;
		}
		if (record instanceof StaffInfo) {
			StaffInfo info = (StaffInfo) record;
			layout.setHeading(new Label("职员信息"));
			
			String ctitles[] = {"姓名", "性别", "年龄", 
					"手机号", "公司", 
					"职责", "城市"};
			String ctexts[] = {info.name(), info.isFemale() ? "女" : "男", "" + info.age(), info.phoneNumber(),
					info.company() == null ? "无" : info.company(), staffTypeStringMap.get(info.type()),
						info.city() == null ? "无" : info.city()};
			titles = ctitles;
			texts = ctexts;
		}
		
		VBox vbox = new VBox();
		vbox.setPrefSize(300, 100);
		vbox.setSpacing(10);
		for (int i = 0; i < titles.length; ++i) {
			TextFlow textFlow = new TextFlow();
			Text nameTitle = new Text(titles[i] + ": ");
			nameTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			Text nameText = new Text(texts[i]);
			nameText.setFont(Font.font(14));
			textFlow.getChildren().addAll(nameTitle, nameText);
			vbox.getChildren().add(textFlow);
		}
		layout.setBody(vbox);
		JFXButton closeButton = new JFXButton("确认");
		closeButton.setStyle("-fx-text-fill: SKYBLUE");
		closeButton.setOnAction(event -> {
			dialog.close();
		});
		layout.setActions(closeButton);
		closeButton.getStyleClass().add("dialog-accept");
		dialog.setContent(layout);
		dialog.setDialogContainer(pane);
		dialog.show();
	}
	
	private static final Map<String, ItemState> stringStateMap = new HashMap<>();
	private static final Map<ItemState, String> stateStringMap = new HashMap<>();
	private static final List<String> stateList = new ArrayList<>();
	
	static {
		stringStateMap.put("Picking-up", ItemState.PickingUp);
		stringStateMap.put("To-Export Transporting", ItemState.ToExportTransporting);
		stringStateMap.put("Export Checking", ItemState.ExportChecking);
		stringStateMap.put("Export Check Fail", ItemState.ExportCheckFailed);
		stringStateMap.put("Packing to Container", ItemState.PackingToContainer);
		stringStateMap.put("Waiting for Shipping", ItemState.WaitingForShipping);
		stringStateMap.put("Shipping", ItemState.Shipping);
		stringStateMap.put("Unpacking from Container", ItemState.UnpackingFromContainer);
		stringStateMap.put("Import Checking", ItemState.ImportChecking);
		stringStateMap.put("Import Check Fail", ItemState.ImportCheckFailed);
		stringStateMap.put("From-Import Transporting", ItemState.FromImportTransporting);
		stringStateMap.put("Delivering", ItemState.Delivering);
		stringStateMap.put("Finish", ItemState.Finish);
		
		for (Map.Entry<String, ItemState> entry : stringStateMap.entrySet()) {
			stateStringMap.put(entry.getValue(), entry.getKey());
		}
		
		for (ItemState state : ItemState.values()) {
			stateList.add(stateStringMap.get(state));
		}
		
	}
	
	public void initCourierManagerBoard(HBox hbox, StackPane top) {
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
		
		JFXButton newItemButton = new JFXButton("新建项目");
		newItemButton.setOnAction(e -> {
			JFXDialog dialog = new JFXDialog();
			dialog.getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());
			dialog.setTransitionType(DialogTransition.CENTER);
			JFXDialogLayout layout = new JFXDialogLayout();
			layout.setHeading(new Label("新建项目"));
			VBox vbox = new VBox();
			vbox.setPrefSize(300, 400);
			vbox.setSpacing(10);
			
			HBox nameBox = new HBox();
			TextFlow nameTitle = new TextFlow();
			Text nameText = new Text("项目名称: ");
			nameText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			nameTitle.getChildren().add(nameText);
			JFXTextField nameField = new JFXTextField();
			ValidatorBase nameValid = new RequiredFieldValidator("项目名称不能为空");
			FontIcon nameTri = new FontIcon();
			nameTri.setIconLiteral("fas-exclamation-triangle");
			nameValid.setIcon(nameTri);
			nameField.setValidators(nameValid);
			nameBox.getChildren().addAll(nameTitle, nameField);
			vbox.getChildren().add(nameBox);
			
			HBox typeBox = new HBox();
			TextFlow typeTitle = new TextFlow();
			Text typeText = new Text("物品类型: ");
			typeText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			typeTitle.getChildren().add(typeText);
			JFXTextField typeField = new JFXTextField();
			ValidatorBase typeValid = new RequiredFieldValidator("物品类型不能为空");
			FontIcon typeTri = new FontIcon();
			typeTri.setIconLiteral("fas-exclamation-triangle");
			typeValid.setIcon(typeTri);
			typeField.setValidators(typeValid);
			typeBox.getChildren().addAll(typeTitle, typeField);
			vbox.getChildren().add(typeBox);

			HBox priceBox = new HBox();
			TextFlow priceTitle = new TextFlow();
			Text priceText = new Text("物品总价: ");
			priceText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			priceTitle.getChildren().add(priceText);
			JFXTextField priceField = new JFXTextField();
			ValidatorBase priceValid = new RequiredFieldValidator("物品总价不能为空");
			ValidatorBase numberValid = new ValidatorBase("物品总价必须为数字") {

				@Override
				protected void eval() {
					try {
						TextInputControl text = (TextInputControl) srcControl.get();
						double price = Double.parseDouble(text.getText());
						hasErrors.set(false);
					} catch (NumberFormatException e) {
						hasErrors.set(true);
					}
				}
				
			};
			FontIcon priceTri = new FontIcon();
			priceTri.setIconLiteral("fas-exclamation-triangle");
			priceValid.setIcon(priceTri);
			FontIcon numberTri = new FontIcon();
			numberTri.setIconLiteral("fas-exclamation-triangle");
			numberValid.setIcon(numberTri);
			priceField.setValidators(priceValid, numberValid);
			priceBox.getChildren().addAll(priceTitle, priceField);
			vbox.getChildren().add(priceBox);
			
			HBox stateBox = new HBox();
			TextFlow stateTitle = new TextFlow();
			Text stateText = new Text("项目状态: ");
			stateText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			JFXComboBox<String> stateCB = new JFXComboBox<>();
			stateCB.getStylesheets().add(getClass().getResource("/css/scrollpane.css").toExternalForm());
			
			
			stateCB.getSelectionModel().select(0);
			stateCB.setItems(FXCollections.observableArrayList(stateList));
			
			stateTitle.getChildren().add(stateText);

			stateBox.getChildren().addAll(stateTitle, stateCB);
			vbox.getChildren().add(stateBox);
			
			HBox retrCityBox = new HBox();
			TextFlow retrCityTitle = new TextFlow();
			Text retrCityText = new Text("检索城市: ");
			retrCityText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			retrCityTitle.getChildren().add(retrCityText);
			JFXTextField retrCityField = new JFXTextField();
			ValidatorBase retrCityValid = new RequiredFieldValidator("检索城市不能为空");
			FontIcon retrCityTri = new FontIcon();
			retrCityTri.setIconLiteral("fas-exclamation-triangle");
			retrCityValid.setIcon(retrCityTri);
			retrCityField.setValidators(retrCityValid);
			retrCityBox.getChildren().addAll(retrCityTitle, retrCityField);
			vbox.getChildren().add(retrCityBox);
			
			HBox retrCourierBox = new HBox();
			TextFlow retrCourierTitle = new TextFlow();
			Text retrCourierText = new Text("检索快递员: ");
			retrCourierText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			retrCourierTitle.getChildren().add(retrCourierText);
			JFXTextField retrCourierField = new JFXTextField();
			ValidatorBase retrCourierValid = new RequiredFieldValidator("检索快递员不能为空");
			FontIcon retrCourierTri = new FontIcon();
			retrCourierTri.setIconLiteral("fas-exclamation-triangle");
			retrCourierValid.setIcon(retrCourierTri);
			retrCourierField.setValidators(retrCourierValid);
			retrCourierBox.getChildren().addAll(retrCourierTitle, retrCourierField);
			vbox.getChildren().add(retrCourierBox);
			
			HBox deliCityBox = new HBox();
			TextFlow deliCityTitle = new TextFlow();
			Text deliCityText = new Text("寄送城市: ");
			deliCityText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			deliCityTitle.getChildren().add(deliCityText);
			JFXTextField deliCityField = new JFXTextField();
			ValidatorBase deliCityValid = new RequiredFieldValidator("寄送城市不能为空");
			FontIcon deliCityTri = new FontIcon();
			deliCityTri.setIconLiteral("fas-exclamation-triangle");
			deliCityValid.setIcon(deliCityTri);
			deliCityField.setValidators(deliCityValid);
			deliCityBox.getChildren().addAll(deliCityTitle, deliCityField);
			vbox.getChildren().add(deliCityBox);
			
			HBox deliCourierBox = new HBox();
			TextFlow deliCourierTitle = new TextFlow();
			Text deliCourierText = new Text("寄送快递员: ");
			deliCourierText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			deliCourierTitle.getChildren().add(deliCourierText);
			JFXTextField deliCourierField = new JFXTextField();
			deliCourierBox.getChildren().addAll(deliCourierTitle, deliCourierField);
			vbox.getChildren().add(deliCourierBox);
			
			HBox importCityBox = new HBox();
			TextFlow importCityTitle = new TextFlow();
			Text importCityText = new Text("进口城市: ");
			importCityText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			importCityTitle.getChildren().add(importCityText);
			JFXTextField importCityField = new JFXTextField();
			ValidatorBase importCityValid = new RequiredFieldValidator("进口城市不能为空");
			FontIcon importCityTri = new FontIcon();
			importCityTri.setIconLiteral("fas-exclamation-triangle");
			importCityValid.setIcon(importCityTri);
			importCityField.setValidators(importCityValid);
			importCityBox.getChildren().addAll(importCityTitle, importCityField);
			vbox.getChildren().add(importCityBox);
			
			HBox importTaxBox = new HBox();
			TextFlow importTaxTitle = new TextFlow();
			Text importTaxText = new Text("进口税: ");
			importTaxText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			importTaxTitle.getChildren().add(importTaxText);
			JFXTextField importTaxField = new JFXTextField();
			ValidatorBase importTaxValid = new RequiredFieldValidator("进口税不能为空");
			FontIcon importTaxTri = new FontIcon();
			importTaxTri.setIconLiteral("fas-exclamation-triangle");
			importTaxValid.setIcon(importTaxTri);
			ValidatorBase importNumberValid = new ValidatorBase("进口税必须为数字") {

				@Override
				protected void eval() {
					try {
						TextInputControl text = (TextInputControl) srcControl.get();
						double price = Double.parseDouble(text.getText());
						hasErrors.set(false);
					} catch (NumberFormatException e) {
						hasErrors.set(true);
					}
				}
				
			};
			FontIcon importNumberTri = new FontIcon();
			importNumberTri.setIconLiteral("fas-exclamation-triangle");
			importNumberValid.setIcon(importNumberTri);
			importTaxField.setValidators(importTaxValid, importNumberValid);
			importTaxBox.getChildren().addAll(importTaxTitle, importTaxField);
			vbox.getChildren().add(importTaxBox);
			
			HBox importOfficerBox = new HBox();
			TextFlow importOfficerTitle = new TextFlow();
			Text importOfficerText = new Text("进口负责人: ");
			importOfficerText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			importOfficerTitle.getChildren().add(importOfficerText);
			JFXTextField importOfficerField = new JFXTextField();
			importOfficerBox.getChildren().addAll(importOfficerTitle, importOfficerField);
			vbox.getChildren().add(importOfficerBox);
			
			HBox exportCityBox = new HBox();
			TextFlow exportCityTitle = new TextFlow();
			Text exportCityText = new Text("出口城市: ");
			exportCityText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			exportCityTitle.getChildren().add(exportCityText);
			JFXTextField exportCityField = new JFXTextField();
			ValidatorBase exportCityValid = new RequiredFieldValidator("出口城市不能为空");
			FontIcon exportCityTri = new FontIcon();
			exportCityTri.setIconLiteral("fas-exclamation-triangle");
			exportCityValid.setIcon(exportCityTri);
			exportCityField.setValidators(exportCityValid);
			exportCityBox.getChildren().addAll(exportCityTitle, exportCityField);
			vbox.getChildren().add(exportCityBox);
			
			HBox exportTaxBox = new HBox();
			TextFlow exportTaxTitle = new TextFlow();
			Text exportTaxText = new Text("出口税: ");
			exportTaxText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			exportTaxTitle.getChildren().add(exportTaxText);
			JFXTextField exportTaxField = new JFXTextField();
			ValidatorBase exportTaxValid = new RequiredFieldValidator("出口税不能为空");
			FontIcon exportTaxTri = new FontIcon();
			exportTaxTri.setIconLiteral("fas-exclamation-triangle");
			exportTaxValid.setIcon(exportTaxTri);
			ValidatorBase exportNumberValid = new ValidatorBase("出口税必须为数字") {

				@Override
				protected void eval() {
					try {
						TextInputControl text = (TextInputControl) srcControl.get();
						double price = Double.parseDouble(text.getText());
						hasErrors.set(false);
					} catch (NumberFormatException e) {
						hasErrors.set(true);
					}
				}
				
			};
			FontIcon exportNumberTri = new FontIcon();
			exportNumberTri.setIconLiteral("fas-exclamation-triangle");
			exportNumberValid.setIcon(exportNumberTri);
			exportTaxField.setValidators(exportTaxValid, exportNumberValid);
			exportTaxBox.getChildren().addAll(exportTaxTitle, exportTaxField);
			vbox.getChildren().add(exportTaxBox);
			
			HBox exportOfficerBox = new HBox();
			TextFlow exportOfficerTitle = new TextFlow();
			Text exportOfficerText = new Text("出口负责人: ");
			exportOfficerText.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
			exportOfficerTitle.getChildren().add(exportOfficerText);
			JFXTextField exportOfficerField = new JFXTextField();
			exportOfficerBox.getChildren().addAll(exportOfficerTitle, exportOfficerField);
			vbox.getChildren().add(exportOfficerBox);
			
			layout.setBody(vbox);
			
			JFXButton closeButton = new JFXButton("关闭");
			closeButton.setStyle("-fx-text-fill: #ff0000");
			closeButton.setOnAction(e1 -> {
				dialog.close();
			});
			JFXButton submitButton = new JFXButton("提交");
			
			layout.setActions(closeButton, submitButton);
			dialog.setContent(layout);
			dialog.setDialogContainer(top);
			dialog.show();
		});
		leftBox.getChildren().add(newItemButton);
		leftPane.getChildren().add(leftBox);
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
			this.initSustcManagerBoard(hbox, top);
		}
		if (this.staffType.startsWith("Company")) {
			
		}
		if (this.staffType.startsWith("Courier")) {
			this.initCourierManagerBoard(hbox, top);
		}
		if (this.staffType.startsWith("Seaport")) {
			
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
