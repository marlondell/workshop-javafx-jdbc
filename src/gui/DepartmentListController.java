package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangeListener{
	
	//Creating dependence to Department Service to have a reference of the other class
	private DepartmentService service;
	
	//Creating a List to receive the departments and after, to consume it.
	private ObservableList<Department> obsList;
	
	
	//Creating references to the elements of the Tableview
	
	@FXML
	private TableView<Department> tableviewDepartment;
	
	@FXML
	private TableColumn<Department, Integer> tableColumnID;
	
	@FXML
	private TableColumn<Department, String> tableColumnName;
	
	@FXML
	private Button btNew;
	
	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Department obj = new Department();
		createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);
	}
	
	//To have a weak coupling, we'll create a method instead of instantiate a object
	//of the class DepartmentService. 
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	
	//We need to create the method initializeNodes to works correctly the columns "massete"
	//to start the behavior of the columns
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnID.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		//The table is smaller than the Main screen. To fix it, we need get the current Scene
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableviewDepartment.prefHeightProperty().bind(stage.heightProperty());
	}
	
	//Access the service, charge the Departments and discharge them in the ObservableList	
	public void updateTableView() {
		//Protect the service. If the programmer forgets to do the dependence, the variable
		//will be empty so, the exception will be throw to avoid this mistake.
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		//This list will receive the departments of the class DepartmentServices through
		//the variable service that is a type of DepartmentService.
		List<Department> list = service.findAll();
		
		//The list will be charged in the observableList. After, the content will be charged
		//in the View object.
		obsList = FXCollections.observableArrayList(list);
		tableviewDepartment.setItems(obsList);
	}
	
	//Function to load the window to fill the data of the new department
	private void createDialogForm(Department obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();
			
			DepartmentFormController controller = loader.getController();
			controller.setDepartment(obj);
			controller.setDepartmentService(new DepartmentService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Department Data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
			
		}catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();
		
	}

}
