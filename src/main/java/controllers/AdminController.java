package controllers;

import com.example.system.StageChanger;
import controllers_pop_employee.AddEmployeeController;
import controllers_pop_employee.EditEmployeeController;
import controllers_pop_task.AddTaskController;
import controllers_pop_task.EditTaskController;
import database.DatabaseConnector;
import database.QExecutor;
import database_classes.HistoryTaskTable;
import database_classes.LoginTable;
import database_classes.TasksTable;
import database_classes.UsersTable;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import pdf_generate.PdfGenerate;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class AdminController {

    @FXML
    private TextField filterEmployeeField;

    @FXML
    private TextField filterMyTasksField;

    @FXML
    private TextField filterTasksField;
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
    private Button pdfPathButton;
    @FXML
    private Button pdfGenerateButton;
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
    private Label wrongPdfLabel;
    @FXML
    private TableColumn<?, ?> employeeAddress;
    @FXML
    private TableView<UsersTable> employeeTableView;
    @FXML
    private TableColumn<?, ?> employeeGroup;
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
    private TableColumn<?, ?> employeeEdit;
    @FXML
    private TableColumn<?, ?> myTaskDescription;
    @FXML
    private TableColumn<?, ?> myTaskEdit;
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
    private TableColumn<?, ?> taskPlannedDate;
    @FXML
    private TableColumn<?, ?> taskStatus;
    @FXML
    private TableColumn<?, ?> taskTitle;
    @FXML
    private TableView<TasksTable> taskTableView;
    @FXML
    private TextField pdfPathField;
    @FXML
    private ComboBox pdfChooseReportComboBox;
    @FXML
    private ComboBox pdfChooseDataComboBox;
    @FXML
    private AnchorPane mainAnchorPane;

    private Timeline time;
    private ObservableList<TasksTable> myTaskTable;
    private ObservableList<TasksTable> taskTable;
    private ObservableList<UsersTable> userTable;

    @FXML
    public void initialize() {
        welcomeLabel.setText("Witaj " + UsersTable.getLoginName() + " " + UsersTable.getLoginSurname() + "!");
        gridMyTasks.toFront();
        myTask();
        task();
        employee();
    }

    //To jest do obsługi wszystkich buttonów, które zmieniają tylko grid
    public void buttonsHandlerPane(ActionEvent event) {
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
            pdfPathField.clear();
            gridReport.toFront();
            textLabel.setText("Generowanie raportów");
            wrongPdfLabel.setText("");
            pdfChooseReportToGenerate();
        } else if (source == settingsButton) {
            gridSettings.toFront();
            textLabel.setText("Ustawienia");
            data();
        }
    }

    //To jest do obsługi wszystkich buttonów, które zmieniają cały panel (Stage) i PopupWindow
    public void buttonsHandlerStages(ActionEvent event) throws IOException {
        StageChanger stageChanger = new StageChanger();
        Object source = event.getSource();

        if (source == logoutButton) {
            stageChanger.changeSize(915, 630);
            stageChanger.changeScene("/main.fxml");
        } else if (source == addTaskButton) {
            String fxmlPath = "/pop_task/addTask.fxml";
            openWindow(addTaskButton, fxmlPath);
            time = new Timeline(new KeyFrame(Duration.millis(1), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if (AddTaskController.refBool()) {
                        refreshTask();
                        time.stop();
                        AddTaskController.isRefreshed = false;
                    }
                }
            }));
            time.setCycleCount(Timeline.INDEFINITE);
            time.play();
        } else if (source == addEmployeeButton) {
            time = new Timeline(new KeyFrame(Duration.millis(1), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if (AddEmployeeController.refBool()) {
                        refreshUser();
                        time.stop();
                        AddEmployeeController.isRefreshed = false;
                    }
                }
            }));
            time.setCycleCount(Timeline.INDEFINITE);
            time.play();

            String fxmlPath = "/pop_employee/addEmployee.fxml";
            openWindow(addEmployeeButton, fxmlPath);
        } else if (source == mailEditButton) {
            String fxmlPath = "/pop_settings/editEmailInSettings.fxml";
            openWindow(mailEditButton, fxmlPath);
        } else if (source == passwordEditButton) {
            String fxmlPath = "/pop_settings/editPasswordInSettings.fxml";
            openWindow(passwordEditButton, fxmlPath);
        }
    }


    public void buttonReports(ActionEvent event) {
        Object source = event.getSource();
        if (source == pdfPathButton) {
            setPathPdfGenerator();
        } else if (source == pdfGenerateButton) {
            if (!pdfPathField.getText().isEmpty()) {
                PdfGenerate.generateForAdmin(
                        pdfPathField.getText(),
                        pdfChooseReportComboBox.getSelectionModel().getSelectedItem().toString(),
                        pdfChooseDataComboBox.getSelectionModel().getSelectedItem().toString()
                );
                wrongPdfLabel.setText("PDF został wygenerowany");
            } else {
                wrongPdfLabel.setText("Ustaw ścieżkę zapisu PDF");
            }
        }
    }

    private void data() {
        try {
            DatabaseConnector.connect();
            ResultSet rs = QExecutor.executeSelect("SELECT * FROM users where id_user=" + UsersTable.getIdLoginUser());
            while (rs.next()) {
                nameLabel.setText(rs.getString("name"));
                surnameLabel.setText(rs.getString("surname"));
                addressLabel.setText(rs.getString("address"));
                zipLabel.setText(rs.getString("zip"));
                placeLabel.setText(rs.getString("place"));
                phoneLabel.setText(String.valueOf(rs.getInt("phone_num")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    //Wyświetlanie moich zadań
    private void myTask() {
        try {
            DatabaseConnector.connect();
            myTaskTable = FXCollections.observableArrayList();

            ResultSet result = QExecutor.executeSelect("SELECT * FROM tasks " +
                    "JOIN statuses ON tasks.status_id = statuses.id_status " +
                    "JOIN tasks_history ON tasks_history.tasks_id=tasks.id_task " +
                    "WHERE user_id = " + UsersTable.getIdLoginUser());
            System.out.println(UsersTable.getIdLoginUser());
            while (result.next()) {
                TasksTable myTask = new TasksTable();
                Button editButton = new Button("Edycja");
                int idTask = result.getInt("id_task");
                myTask.setEditIdTask(idTask);
                editButton.setOnAction(event -> {
                    preparePopUpWindowEditTask(String.valueOf(idTask));
                    System.out.println(myTask.getEditIdTask());
                });
                myTask.setIdTask(result.getInt("id_task"));
                myTask.setTitle(result.getString("title"));
                myTask.setData(result.getDate("planned_end"));
                myTask.setDescription(result.getString("description"));
                myTask.setNameStatus(result.getString("name"));
                myTask.setEditTaskButton(editButton);
                myTaskTable.add(myTask);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        myTaskTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        myTaskPlannedDate.setCellValueFactory(new PropertyValueFactory<>("data"));
        myTaskDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        myTaskStatus.setCellValueFactory(new PropertyValueFactory<>("nameStatus"));
        myTaskEdit.setCellValueFactory(new PropertyValueFactory<>("editTaskButton"));

        myTaskTableView.setItems(myTaskTable);

        FilteredList<TasksTable> filteredTaskData = new FilteredList<>(myTaskTable, b -> true);
        filterMyTasksField.textProperty().addListener((observable, oldValue, newValue1) -> {
            filteredTaskData.setPredicate(myTask -> {
                if (newValue1 == null || newValue1.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue1.toLowerCase();

                if (myTask.getTitle().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (myTask.getDescription().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (myTask.getNameStatus().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else {
                    return false;
                }
            });
        });
        SortedList<TasksTable> sortedData = new SortedList<>(filteredTaskData);
        sortedData.comparatorProperty().bind(myTaskTableView.comparatorProperty());
        myTaskTableView.setItems(sortedData);
    }

    //Wyświetlanie zadań
    private void task() {
        try {
            DatabaseConnector.connect();
            taskTable = FXCollections.observableArrayList();

            ResultSet result = QExecutor.executeSelect("SELECT * FROM tasks " +
                    "JOIN statuses ON tasks.status_id = statuses.id_status " +
                    "JOIN users ON tasks.user_id=users.id_user " +
                    "JOIN tasks_history ON tasks_history.tasks_id=tasks.id_task");

            while (result.next()) {
                TasksTable task = new TasksTable();
                Button editButton = new Button("Edycja");
                String idTask = result.getString("id_task");
                editButton.setOnAction(event -> {
                    preparePopUpWindowEditTask(idTask);
                });
                task.setIdTask(result.getInt("id_task"));
                task.setTitle(result.getString("title"));
                task.setDescription(result.getString("description"));
                task.setData(result.getDate("tasks_history.planned_end"));
                task.setNameStatus(result.getString("name"));
                task.setNameUser(result.getString("users.name"));
                task.setEditTaskButton(editButton);
                taskTable.add(task);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        taskTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        taskDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        taskPlannedDate.setCellValueFactory(new PropertyValueFactory<>("data"));
        taskStatus.setCellValueFactory(new PropertyValueFactory<>("nameStatus"));
        taskEmployee.setCellValueFactory(new PropertyValueFactory<>("nameUser"));
        taskEdit.setCellValueFactory(new PropertyValueFactory<>("editTaskButton"));

        taskTableView.setItems(taskTable);
        // Filtrowanie danych
        FilteredList<TasksTable> filteredData = new FilteredList<>(taskTable, b -> true);
        filterTasksField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(tasks -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();

                if (tasks.getTitle().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (tasks.getDescription().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (tasks.getNameUser().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (tasks.getNameStatus().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else {
                    return false;
                }
            });
        });
        SortedList<TasksTable> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(taskTableView.comparatorProperty());
        taskTableView.setItems(sortedData);
    }

    //Wyświetlanie pracowników
    private void employee() {
        try {
            DatabaseConnector.connect();
            userTable = FXCollections.observableArrayList();

            ResultSet result = QExecutor.executeSelect("SELECT * FROM users " +
                    "JOIN positions ON users.position_id = positions.id_position " +
                    "JOIN login ON users.id_user = login.user_id");

            while (result.next()) {
                UsersTable user = new UsersTable();
                Button editButton = new Button("Edycja");
                String tokenName = result.getString("token");
                editButton.setOnAction(event -> {
                    preparePopUpWindowEditEmployee(tokenName);
                });
                user.setName(result.getString("name"));
                user.setSurname(result.getString("surname"));
                user.setEmail(result.getString("email"));
                user.setAddress(result.getString("address"));
                user.setPhoneNumber(result.getInt("phone_num"));
                user.setNamePosition(result.getString("position_name"));
                user.setGroups(result.getInt("groups"));
                user.setEditEmployeeButton(editButton);
                userTable.add(user);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        employeeName.setCellValueFactory(new PropertyValueFactory<>("name"));
        employeeSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        employeePosition.setCellValueFactory(new PropertyValueFactory<>("namePosition"));
        employeeAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        employeeMail.setCellValueFactory(new PropertyValueFactory<>("email"));
        employeePhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        employeeGroup.setCellValueFactory(new PropertyValueFactory<>("groups"));
        employeeEdit.setCellValueFactory(new PropertyValueFactory<>("editEmployeeButton"));

        employeeTableView.setItems(userTable);

        FilteredList<UsersTable> filteredData = new FilteredList<>(userTable, b -> true);
        filterEmployeeField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (user.getName().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (user.getSurname().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (user.getAddress().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (user.getNamePosition().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                    return true;
                } else if (String.valueOf(user.getPhoneNumber()).contains(lowerCaseFilter)) {
                    return true;
                } else if (String.valueOf(user.getGroups()).contains(lowerCaseFilter)) {
                    return true;
                } else {
                    return false;
                }
            });
        });
        SortedList<UsersTable> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(employeeTableView.comparatorProperty());
        employeeTableView.setItems(sortedData);
    }

    private void refreshUser() {
        userTable.clear();
        employee();
    }

    private void refreshTask() {
        taskTable.clear();
        task();
    }

    private void refreshEditTask() {
        myTaskTable.clear();
        myTask();
        taskTable.clear();
        task();
    }

    private void preparePopUpWindowEditTask(String idTask) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pop_task/editTask.fxml"));
            AnchorPane anchorPane = loader.load();
            EditTaskController editTaskController = loader.getController();

            DatabaseConnector.connect();
            //SELECT t.title, t.description, u.name, u.surname, s.name, tk.planned_end FROM tasks AS t JOIN statuses AS s ON t.status_id = s.id_status JOIN users AS u ON t.user_id=u.id_user JOIN tasks_history AS tk ON tk.tasks_id=t.id_task WHERE t.id_task = 8
            ResultSet result = QExecutor.executeSelect("SELECT t.id_task, t.title, t.description, u.name, u.surname, s.name AS status, tk.planned_end FROM tasks AS t " +
                    "JOIN statuses AS s ON t.status_id = s.id_status " +
                    "JOIN users AS u ON t.user_id=u.id_user " +
                    "JOIN tasks_history AS tk ON tk.tasks_id=t.id_task " +
                    "WHERE t.id_task = " + idTask);
            result.next();

            editTaskController.setData(
                    result.getInt("id_task"),
                    result.getString("title"),
                    result.getString("description"),
                    result.getString("name"),
                    result.getString("surname"),
                    result.getString("status"),
                    result.getString("planned_end"));
            time = new Timeline(new KeyFrame(Duration.millis(1), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if (EditTaskController.refBool()) {
                        refreshEditTask();
                        time.stop();
                        EditTaskController.isRefreshed = false;
                    }
                }
            }));
            time.setCycleCount(Timeline.INDEFINITE);
            time.play();

            Scene scene = new Scene(anchorPane);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/ICON.png"))));
            stage.show();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void preparePopUpWindowEditEmployee(String token) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pop_employee/editEmployee.fxml"));
            AnchorPane anchorPane = loader.load();
            EditEmployeeController editEmployeeController = loader.getController();

            DatabaseConnector.connect();
            ResultSet result = QExecutor.executeSelect("SELECT u.id_user, u.name, u.surname, u.address, u.place, u.zip, u.phone_num, p.position_name, u.token, u.groups FROM users AS u " +
                    "JOIN positions AS p ON u.position_id = p.id_position " +
                    "WHERE u.token = '" + token +"'");
            result.next();

            editEmployeeController.setData(
                    result.getInt("id_user"),
                    result.getString("name"),
                    result.getString("surname"),
                    result.getString("phone_num"),
                    result.getString("place"),
                    result.getString("address"),
                    result.getString("groups"),
                    result.getString("position_name"),
                    result.getString("token"),
                    result.getString("zip")
            );

            time = new Timeline(new KeyFrame(Duration.millis(1), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    if (EditEmployeeController.refBool()) {
                        refreshEmployee();
                        time.stop();
                        EditEmployeeController.isRefreshed = false;
                    }
                }
            }));
            time.setCycleCount(Timeline.INDEFINITE);
            time.play();

            Scene scene = new Scene(anchorPane);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/ICON.png"))));
            stage.show();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void openWindow(Button button, String fxml) {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxml)));
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(button.getScene().getWindow());
            stage.setResizable(false);
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/ICON.png"))));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshEmployee(){
        userTable.clear();
        employee();
    }

    private void setPathPdfGenerator() {
        final DirectoryChooser dirChooser = new DirectoryChooser();
        Stage stage = (Stage) mainAnchorPane.getScene().getWindow();
        File file = dirChooser.showDialog(stage);

        if (file != null) {
            System.out.println("Ścieżka" + file.getAbsolutePath());
            pdfPathField.setText(file.getAbsolutePath());
        }
    }

    private void pdfChooseReportToGenerate() {
        ObservableList<String> pdfReport = FXCollections.observableArrayList();

        pdfReport.add("Zadania");
        pdfReport.add("Pracownicy");
        pdfChooseReportComboBox.setItems(pdfReport);
        pdfChooseReportComboBox.setOnAction(e -> pdfChooseDataToGenerate());
    }

    private void pdfChooseDataToGenerate() {
        ObservableList<String> pdfData = FXCollections.observableArrayList();

        if (pdfChooseReportComboBox.getSelectionModel().getSelectedItem().equals("Zadania")) {
            pdfData.clear();
            try {
                DatabaseConnector.connect();
                ResultSet rs = QExecutor.executeSelect("SELECT name FROM statuses");
                pdfData.add("Wszystko");
                while (rs.next()) {
                    pdfData.add(rs.getString(1));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (pdfChooseReportComboBox.getSelectionModel().getSelectedItem().equals("Pracownicy")) {
            pdfData.clear();
            try {
                DatabaseConnector.connect();
                ResultSet rs = QExecutor.executeSelect("SELECT position_name FROM positions");
                pdfData.add("Wszystko");
                while (rs.next()) {
                    pdfData.add(rs.getString(1));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        pdfChooseDataComboBox.setItems(pdfData);
        pdfChooseDataComboBox.setPromptText("Wybierz rodzaj");
    }
}
