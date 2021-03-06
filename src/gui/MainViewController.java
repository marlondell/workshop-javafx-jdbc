package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemSeller;

	@FXML
	private MenuItem menuItemDepartment;

	@FXML
	private MenuItem menuItemAbout;

	@FXML
	public void onMenuItemSellerAction() {
		System.out.println("onMenuItemSellerAction");
	}

	@FXML
	public void onMenuItemDepartmentAction() {
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller) -> {
			controller.setDepartmentService(new DepartmentService());
			controller.updateTableView();
		});
	}

	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {});
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {
	}

	//The word synchronized assures that the processing will be executed without interruption
	//because the multi thread processing
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializinhgAction) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			
			//To show the VBox in the Main Scene
			
			//Reference of the Main Scene
			Scene mainScene = Main.getMainScene();
			
			//Reference of the VBox of the Main Scene. Inside the Scroll Pane, there is
			//a VBox with content and the children of the ScrollPane of the Main Scene
			//the menu and menu items
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			
			//Get the children (first child = menuBar) of the main menu
			Node mainMenu = mainVBox.getChildren().get(0);
			
			//Delete all children
			mainVBox.getChildren().clear();
			
			//Set the children of the Main Menu again
			mainVBox.getChildren().add(mainMenu);
			
			//Set the children of the new VBox, the screen created in this class
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			// These command will enable the function passed like parameter
			T controller = loader.getController();
			initializinhgAction.accept(controller);
			
		} catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}
	
	
}
