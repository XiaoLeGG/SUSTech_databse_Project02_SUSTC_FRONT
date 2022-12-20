package cn.edu.sustech.dbms2.client;

import cn.edu.sustech.dbms2.client.view.LobbyView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	public static void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			ThrowableHandler.handleThrowable(throwable);
		});
		Application.launch(Main.class);
		
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		LobbyView lv = new LobbyView();
		lv.initStage();
		lv.getStage().show();
	}
}
