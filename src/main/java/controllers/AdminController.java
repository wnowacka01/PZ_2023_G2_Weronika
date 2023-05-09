package controllers;

import com.example.system.StageChanger;
import database.DatabaseConnector;
import database.QExecutor;
import database_classes.HistoryTaskTable;
import database_classes.TasksTable;
import database_classes.UsersTable;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    @FXML
    private Button myTasksButton;
    @FXML
    private Button tasksButton;
    @FXML
    private Button employeeButton;
    @FXML
    private Button raportButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button addEmployeeButton;
    @FXML
    private Button addTaskButton;
    @FXML
    private Button mailEditButton;
    @FXML
    private Button passwordEditButton;
    @FXML
    private GridPane gridTasks;
    @FXML
    private GridPane gridEmployee;
    @FXML
    private GridPane gridReport;
    @FXML
    private GridPane gridSettings;
    @FXML
    private GridPane gridMyTasks;
    @FXML
    private Label textLabel;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label surnameLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label zipLabel;
    @FXML
    private Label placeLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private TableColumn<?, ?> employeeAddress;
    @FXML
    private TableView<UsersTable> employeeTableView;
    @FXML
    private TableColumn<?, ?> employeeGroup;
    @FXML
    private TableColumn<?, ?> employeeID;
    @FXML
    private TableColumn<?, ?> employeeMail;
    @FXML
    private TableColumn<?, ?> employeeName;
    @FXML
    private TableColumn<?, ?> employeePhone;
    @FXML
    private TableColumn<?, ?> employeePosition;
    @FXML
    private TableColumn<?, ?> employeeSurname;
    @FXML
    private TableColumn<?, ?> myTaskDescription;
    @FXML
    private TableColumn<?, ?> myTaskEdit;
    @FXML
    private TableColumn<?, ?> myTaskID;
    @FXML
    private TableColumn<?, ?> myTaskPlannedDate;
    @FXML
    private TableColumn<?, ?> myTaskStatus;
    @FXML
    private TableColumn<?, ?> myTaskTitle;
    @FXML
    private TableView<TasksTable> myTaskTableView;
    @FXML
    private TableColumn<?, ?> taskDescription;
    @FXML
    private TableColumn<?, ?> taskEdit;
    @FXML
    private TableColumn<?, ?> taskEmployee;
    @FXML
    private TableColumn<?, ?> taskID;
    @FXML
    private TableColumn<?, ?> taskPlannedDate;
    @FXML
    private TableColumn<?, ?> taskStatus;
    @FXML
    private TableColumn<?, ?> taskTitle;
    @FXML
    private TableView<TasksTable> taskTableView;

    Timeline time;
    UsersTable usersTable = new UsersTable();
    private ObservableList<TasksTable> myTaskTable;
    private ObservableList<TasksTable> taskTable;
    private ObservableList<UsersTable> userTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        welcomeLabel.setText("Witaj " + usersTable.getLoginName() + " " + usersTable.getLoginSurname() + "!");
        gridMyTasks.toFront();
        myTask();
    }

    public void data() {
        DatabaseConnector.connect();
        try {
            ResultSet rs = QExecutor.executeSelect("SELECT * FROM users where id_user=" + UsersTable.getLoginIdUser());
            while (rs.next()) {
                UsersTable user = new UsersTable();
                user.setName(rs.getString("name"));
                user.setSurname(rs.getString("surname"));
                user.setAddress(rs.getString("address"));
                user.setPlace(rs.getString("place"));
                user.setZip(rs.getString("zip"));
                user.setPhoneNumber(rs.getInt("phone_num"));
                nameLabel.setText(user.getName());
                surnameLabel.setText(user.getSurname());
                addressLabel.setText(user.getAddress());
                zipLabel.setText(user.getZip());
                placeLabel.setText(user.getPlace());
                phoneLabel.setText(String.valueOf(user.getPhoneNumber()));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    //Wyświetlanie moich zadań
    public void myTask() {
        DatabaseConnector.connect();
        try {
            myTaskTable = FXCollections.observableArrayList();

            ResultSet result = QExecutor.executeSelect("SELECT * FROM tasks INNER JOIN statuses ON tasks.status_id = statuses.id_status WHERE user_id = " + usersTable.getLoginIdUser());
            System.out.println(usersTable.getLoginIdUser());
            while (result.next()) {
                TasksTable task = new TasksTable();
                HistoryTaskTable htask = new HistoryTaskTable();
                task.setIdTask(result.getInt("id_task"));
                task.setTitle(result.getString("title"));
                task.setDescription(result.getString("description"));
                task.setNameStatus(result.getString("name"));
                myTaskTable.add(task);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        myTaskID.setCellValueFactory(new PropertyValueFactory<>("idTask"));
        myTaskTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        myTaskDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        myTaskStatus.setCellValueFactory(new PropertyValueFactory<>("nameStatus"));
        myTaskTableView.setItems(myTaskTable);
    }

    //Wyświetlanie zadań
    public void task() {
        DatabaseConnector.connect();
        try {
            taskTable = FXCollections.observableArrayList();

            ResultSet result = QExecutor.executeSelect("SELECT * FROM tasks JOIN statuses ON tasks.status_id = statuses.id_status JOIN users ON tasks.user_id=users.id_user JOIN tasks_history ON tasks_history.tasks_id=tasks.id_task");

            while (result.next()) {
                TasksTable task = new TasksTable();
                HistoryTaskTable historyTaskTable = new HistoryTaskTable();
                task.setIdTask(result.getInt("id_task"));
                task.setTitle(result.getString("title"));
                task.setDescription(result.getString("description"));
                task.setData(result.getDate("tasks_history.planned_end"));
                task.setNameStatus(result.getString("name"));
                task.setNameUser(result.getString("users.name"));
                taskTable.add(task);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        taskID.setCellValueFactory(new PropertyValueFactory<>("idTask"));
        taskTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        taskDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        taskPlannedDate.setCellValueFactory(new PropertyValueFactory<>("data"));
        taskStatus.setCellValueFactory(new PropertyValueFactory<>("nameStatus"));
        taskEmployee.setCellValueFactory(new PropertyValueFactory<>("nameUser"));
        taskTableView.setItems(taskTable);
    }

    //Wyświetlanie pracowników
    public void employee() {
        DatabaseConnector.connect();
        try {
            userTable = FXCollections.observableArrayList();

            ResultSet result = QExecutor.executeSelect("SELECT * FROM users JOIN positions ON users.position_id = positions.id_position JOIN login ON users.token=login.token;");

            while (result.next()) {
                UsersTable user = new UsersTable();

                user.setIdUser(result.getInt("id_user"));
                user.setName(result.getString("name"));
                user.setSurname(result.getString("surname"));
                user.setEmail(result.getString("email"));
                user.setAddress(result.getString("address"));
                user.setPhoneNumber(result.getInt("phone_num"));
                user.setNamePosition(result.getString("position_name"));
                user.setGroups(result.getInt("groups"));
                userTable.add(user);

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        employeeID.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        employeeName.setCellValueFactory(new PropertyValueFactory<>("name"));
        employeeSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        employeePosition.setCellValueFactory(new PropertyValueFactory<>("namePosition"));
        employeeAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        employeeMail.setCellValueFactory(new PropertyValueFactory<>("email"));
        employeePhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        employeeGroup.setCellValueFactory(new PropertyValueFactory<>("groups"));
        employeeTableView.setItems(userTable);
    }

    //To jest do obsługi wszystkich buttonów, które zmieniają tylko grid
    public void buttonsHandlerPane(ActionEvent event) throws IOException {
        Object source = event.getSource();

        if (source == myTasksButton) {
            gridMyTasks.toFront();
            textLabel.setText("Moje zadania");
            myTask();
        } else if (source == tasksButton) {
            gridTasks.toFront();
            textLabel.setText("Zadania");
            task();
        } else if (source == employeeButton) {
            gridEmployee.toFront();
            textLabel.setText("Pracownicy");
            employee();
        } else if (source == raportButton) {
            gridReport.toFront();
            textLabel.setText("Generowanie raportów");
        } else if (source == settingsButton) {
            gridSettings.toFront();
            textLabel.setText("Ustawienia");
            data();
        }
    }

    //To jest do obsługi wszystkich buttonów, które zmieniają cały panel (Stage) i PopupWindow
    public void buttonsHandlerStages(ActionEvent event) throws IOException {
        Stage stage;
        Parent root;
        StageChanger stageChanger = new StageChanger();
        Object source = event.getSource();

        if (source == logoutButton) {
            stageChanger.changeSize(915, 630);
            stageChanger.changeScene("/main.fxml");
        } else if (source == addTaskButton) {
            stage = new Stage();
            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/addTask.fxml")));
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(addTaskButton.getScene().getWindow());
            stage.showAndWait();
        } else if (source == addEmployeeButton) {
            stage = new Stage();

            time = new Timeline(new KeyFrame(Duration.millis(1), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if (AddEmployeeController.refBool()) {
                        refresh();
                        time.stop();
                        AddEmployeeController.bool = false;
                    }
                }
            }));
            time.setCycleCount(Timeline.INDEFINITE);
            time.play();

            root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/addEmployee.fxml")));
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(addTaskButton.getScene().getWindow());
            stage.showAndWait();
        } else if (source == mailEditButton) {
            System.out.println("A");
        } else if (source == passwordEditButton) {
            System.out.println("B");
        }
    }

    private void refresh() {
        userTable.clear();
        employee();
    }

}
