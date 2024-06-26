package practise.controllers2;

import DTO.ClientDTO;
import DTO.CommentDTO;
import DTO.PersonalDTO;
import com.dlsc.gemsfx.EmailField;
import com.dlsc.gemsfx.PhoneNumberField;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.utils.others.dates.DateStringConverter;
import javafx.animation.Transition;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.apache.commons.validator.routines.DomainValidator;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import practise.HelloApplication;
import practise.controllers2.Business.BusinessController;
import practise.controllers2.Business.BusinessInfoController;
import practise.controllers2.Payment.PaymentCopyUpdateController;
import practise.controllers2.Tasks.TaskAddController;
import practise.items.PersonalItems;
import practise.singleton.Singleton;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class ClientInfoController implements Initializable {

    public StackPane stackPane;
    public JFXComboBox<String> clientTypeComboBox;
    public TextField clientNameField;
    public JFXButton responsableButton;
    public TextArea descriptionArea;
    public PhoneNumberField phoneField;
    public EmailField emailField;
    public TextField adressField;
    public JFXComboBox<String> responsableComboBox;
    public VBox taskVBox;
    public HBox primaryTaskHBox;
    public JFXButton addTaskButton;
    public ImageView addTaskImage;
    public VBox businessVBox;
    public HBox primaryBusinessHBox;
    public JFXButton addBusinessButton;
    public ImageView addBusinessImage;
    public VBox processVBox;
    public HBox primaryProcessHBox;
    public JFXButton addProcessButton;
    public ImageView addProcessImage;
    public VBox journalVBox1;
    public TextField journalTextField1;
    public JFXButton journalButton1;
    public HBox sampleHBox1;
    public JFXComboBox<String> workTypeComboBox;
    public MFXDatePicker datePicker;
    public AnchorPane businessAnchorPane;
    public ScrollPane businessScrollPane;
    boolean onChange = false;
    String old_name;
    String old_email;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        businessVBox.getChildren().addListener((ListChangeListener.Change<? extends javafx.scene.Node> change) -> {
//            while (change.next()) {
//                if (change.wasAdded() || change.wasRemoved()) {
//                    // Start the transition to animate the resize
//                    animateVBoxResize(businessVBox);
//                }
//            }
//        });
        //================================DashBoard buttons================================
        JFXButton SaveButton = new JFXButton();
        JFXButton ChangeButton = new JFXButton();
        SaveButton.setDisable(true);
        SaveButton.setText("Сохранить изменения");
        SaveButton.setStyle("-fx-pref-width: 458; -fx-pref-height: 38; -fx-text-fill: white; -fx-font-size: 16px");
        SaveButton.setOnMouseClicked(mouseEvent -> {
            try {
                ClientDTO arrStrUpdate = new ClientDTO();
                if (emailField.getEmailAddress() != null && emailField.isValid() && !clientNameField.getText().isEmpty()
                && clientNameField.getText() != null) {
                    try {
                        arrStrUpdate.setName(clientNameField.getText());
                        if(phoneField.getPhoneNumber() == null) {
                            arrStrUpdate.setPhone("");
                        }
                        else {
                            arrStrUpdate.setPhone(phoneField.getPhoneNumber());
                        }
                        arrStrUpdate.setEmail(emailField.getEmailAddress());
                        if(adressField.getText() == null) {
                            arrStrUpdate.setAdress("");
                        }
                        else {
                            arrStrUpdate.setAdress(adressField.getText());
                        }
                        arrStrUpdate.setDescription(descriptionArea.getText());
                        arrStrUpdate.setResponsible_name(responsableComboBox.getSelectionModel().getSelectedItem());
                        arrStrUpdate.setType(clientTypeComboBox.getSelectionModel().getSelectedItem());
                        arrStrUpdate.setWork_type(workTypeComboBox.getSelectionModel().getSelectedItem());
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        arrStrUpdate.setReg_date(Date.valueOf(LocalDate.parse(datePicker.getText(), formatter)));
                        arrStrUpdate.setOld_name(old_name);
                        arrStrUpdate.setOld_email(old_email);
                    } catch (Exception e) {
                        Label messageBox = new Label("Ошибка ввода данных");
                        Singleton.getInstance().ShowJFXDialogStandart(stackPane, messageBox);
                        return;
                    }
                    JSONObject tempStringUpdate = Singleton.getInstance().getDataController().UpdateClientInfo(arrStrUpdate);
                    Label messageBox = new Label();
                    if (tempStringUpdate.getString("response").equals("null")) {
                        messageBox.setText("Ошибка сохранения");
                    } else {
                        messageBox.setText("Успешно сохранено");
                    }
                    Singleton.getInstance().ShowJFXDialogStandart(stackPane, messageBox);
                }
                else {
                    Label messageBox = new Label("Ошибка ввода данных");
                    Singleton.getInstance().ShowJFXDialogStandart(stackPane, messageBox);
                }
                //PersonalInfoClass tempString = Singleton.getInstance().getDataController().GetPersonalInfo(arrStr);
                onChange = false;
                clientNameField.setEditable(false);
                adressField.setEditable(false);
                responsableComboBox.setDisable(true);
                clientTypeComboBox.setDisable(true);
                workTypeComboBox.setDisable(true);
                datePicker.setEditable(false);
                phoneField.setDisable(true);
                emailField.setDisable(true);
                descriptionArea.setEditable(false);
                SaveButton.setDisable(true);
                ChangeButton.setDisable(false);


            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    OnReload();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        ChangeButton.setText("Изменить информацию");
        ChangeButton.setStyle("-fx-pref-width: 458; -fx-pref-height: 38; -fx-text-fill: white; -fx-font-size: 16px");
        ChangeButton.setOnMouseClicked(mouseEvent -> {
            onChange = true;
            clientNameField.setEditable(true);
            adressField.setEditable(true);
            responsableComboBox.setDisable(false);
            clientTypeComboBox.setDisable(false);
            workTypeComboBox.setDisable(false);
            datePicker.setEditable(true);
            phoneField.setDisable(false);
            emailField.setDisable(false);
            descriptionArea.setEditable(true);
            SaveButton.setDisable(false);
            ChangeButton.setDisable(true);
        });


        ArrayList<JFXButton> buttons = new ArrayList<>();
        buttons.add(ChangeButton);
        buttons.add(SaveButton);
        if (Singleton.getInstance().getFinal_Role().equals("control")) {
            DashboardController temp = new DashboardController();
            temp.SetVBoxButtons(buttons);
        } else {
            if (!Singleton.getInstance().getFinal_NameSername().equals(responsableComboBox.getSelectionModel().getSelectedItem())) {
                journalButton1.setDisable(true);
            }
            addBusinessButton.setDisable(true);
            addProcessButton.setDisable(true);
            addTaskButton.setDisable(true);
        }
        //===============================/DashBoard buttons================================
        datePicker.setLocale(Locale.ENGLISH);
        datePicker.setConverterSupplier(() -> new DateStringConverter("yyyy-MM-dd", datePicker.getLocale()));
        ObservableList<String> list = FXCollections.observableArrayList("Клиент", "Подрядчик", "Партнер", "Поставщик");
        clientTypeComboBox.setItems(list);
        list = FXCollections.observableArrayList("Бытовые услуги", "Дом и дача", "Культура и спорт", "Мебель", "Медицина, здоровье и красота",
                "Образование и отдых", "Органы власти");
        workTypeComboBox.setItems(list);
        try {
            OnReload();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void OnReload() throws IOException, JSONException {
        journalVBox1.getChildren().clear();
        businessVBox.getChildren().clear();
        taskVBox.getChildren().clear();
        businessVBox.getChildren().add(primaryBusinessHBox);
        taskVBox.getChildren().add(primaryTaskHBox);

        ObservableList<String> comboList = FXCollections.observableArrayList();
        JSONObject arrStrPersonal = new JSONObject();
        arrStrPersonal.put("operationID", "GetPersonalObeyList");
        ArrayList<PersonalDTO> tempStringPersonal = Singleton.getInstance().getDataController().GetPersonalObeyList(arrStrPersonal);
        if(tempStringPersonal == null) {
            Label messageBox = new Label("Ошибка получения данных");
            Singleton.getInstance().ShowJFXDialogStandart(stackPane, messageBox);
            return;
        }
        for (PersonalDTO personalDTO: tempStringPersonal) {
            try {
                comboList.add(personalDTO.getNameSername());
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        responsableComboBox.setItems(comboList);

        ClientDTO arrStr = new ClientDTO();
        try {
            arrStr.setClientsId(Singleton.getInstance().getClientsID());
        }
        catch (Exception e) {
            Label messageBox = new Label("Ошибка передачи ИД клиента");
            Singleton.getInstance().ShowJFXDialogStandart(stackPane, messageBox);
            return;
        }
        //PersonalInfoClass tempString = Singleton.getInstance().getDataController().GetPersonalInfo(arrStr);
        JSONObject tempString = Singleton.getInstance().getDataController().GetClientInfo(arrStr);
        if (tempString.getString("response").equals("null")) {
            Label messageBox = new Label("Ошибка получения данных о клиенте");
            Singleton.getInstance().ShowJFXDialogStandart(stackPane, messageBox);
            return;
        }
        else {
            clientNameField.setText(tempString.getString("name"));
            old_name = tempString.getString("name");
            responsableComboBox.getSelectionModel().select(tempString.getString("responsibleName"));
            phoneField.setPhoneNumber(tempString.getString("contacts"));
            emailField.setEmailAddress(tempString.getString("email"));
            old_email = tempString.getString("email");
            adressField.setText(tempString.getString("address"));
            descriptionArea.setText(tempString.getString("description"));
            clientTypeComboBox.getSelectionModel().select(tempString.getString("type"));
            workTypeComboBox.getSelectionModel().select(tempString.getString("workType"));
            datePicker.setText(tempString.getString("regDate"));
            FillTaskBox(tempString.getJSONArray("taskList"));
            FillBusinessBox(tempString.getJSONArray("businessList"));
            FillCommentBox(tempString.getJSONArray("commentList"));
        }
        //FillProcessBox(tempString.getJSONArray("processList"));
    }

    public void OnAddBusinessButton() throws IOException, JSONException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/SubFXMLs/Business/Business.fxml"));
        StackPane page = fxmlLoader.load();
        Singleton.getInstance().PerformFadeTransition(page, 0.0, 1.0, 1.5);
        Scene scene = new Scene(page);
        scene.setFill(Color.TRANSPARENT);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setX(MouseInfo.getPointerInfo().getLocation().x);
        stage.setY(MouseInfo.getPointerInfo().getLocation().y);
        BusinessController controller = fxmlLoader.getController();
        controller.InitController("Клиент", clientNameField.getText());
        stage.showAndWait();
        OnReload();
    }

    public void OnAddTaskButton() throws IOException, JSONException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/SubFXMLs/Tasks/TaskAdd.fxml"));
        StackPane page = fxmlLoader.load();
        Singleton.getInstance().PerformFadeTransition(page, 0.0, 1.0, 1.5);
        Scene scene = new Scene(page);
        scene.setFill(Color.TRANSPARENT);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        TaskAddController controller = fxmlLoader.getController();
        controller.InitClientTask(clientNameField.getText());
        stage.showAndWait();
        OnReload();
    }

    public void OnAddCommentButton() throws IOException, JSONException {
        if (!journalTextField1.getText().isEmpty()) {
            CommentDTO comment = new CommentDTO();
            try {
                comment.setSenderName(Singleton.getInstance().getFinal_NameSername());
                comment.setText(journalTextField1.getText());
                comment.setLinkedEntityName(clientNameField.getText());
                comment.setType("Клиент");
            }
            catch (Exception e) {
                Label messageBox = new Label("Ошибка ввода данных");
                Singleton.getInstance().ShowJFXDialogStandart(stackPane, messageBox);
                return;
            }
            JSONObject tempString = Singleton.getInstance().getDataController().AddComment(comment);
            Label MessageLabel = new Label();
            if (tempString.getString("response").equals("null")) {
                MessageLabel.setText("Ошибка добавления");
                Singleton.getInstance().ShowJFXDialogStandart(stackPane, MessageLabel);
                return;
            }
            OnReload();
        }
    }

    public void FillTaskBox(JSONArray resultSet) throws JSONException {
        for (int i = 0; i < resultSet.length(); i++) {
            JSONObject task = resultSet.getJSONObject(i);
            HBox taskBox = new HBox();
            taskBox.setStyle("-fx-background-color: #2196f3");
            MFXButton button = new MFXButton(task.getString("taskName"));
            button.setStyle("-fx-text-fill: #151928; -fx-background-color: transparent");
            if (task.getString("taskStatus").equals("Завершена")) {
                taskBox.setStyle("-fx-background-color: green");
            }
            button.setAlignment(Pos.CENTER_LEFT);
            button.setOnMouseEntered(event -> {
                button.setText("Перейти к задаче");
                button.setStyle("-fx-text-fill: #2196f3; -fx-background-color: #151928");
            });
            button.setOnMouseExited(event -> {
                try {
                    button.setText(task.getString("taskName"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                button.setStyle("-fx-text-fill: #151928; -fx-background-color: transparent");
            });
            button.setOnMouseClicked(event -> {
                try {
                    Singleton.getInstance().setTaskInfoValues(new String[]{task.getString("taskName"),
                            task.getString("taskResponsibleName")});
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                DashboardController dashboardController = new DashboardController();
                try {
                    dashboardController.SwitchMainPane("/SubFXMLs/Tasks/TaskInfo.fxml");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    OnReload();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            taskBox.getChildren().add(button);
            taskVBox.getChildren().add(taskBox);
        }
    }

    public void FillBusinessBox(JSONArray resultSet) throws JSONException {
        for (int i = 0; i < resultSet.length(); i++) {
            JSONObject business = resultSet.getJSONObject(i);
            HBox businessBox = new HBox();
            businessBox.setStyle("-fx-background-color: #2196f3");
            //primaryBusinessHBox;
            MFXButton button = new MFXButton(business.getString("businessName"));
            button.setStyle("-fx-text-fill: #151928; -fx-background-color: transparent");
            if (business.getString("businessStatus").equals("Завершено")) {
                businessBox.setStyle("-fx-background-color: green");
            }
            button.setAlignment(Pos.CENTER_LEFT);
            button.setOnMouseEntered(event -> {
                button.setText("Перейти к делу");
                button.setStyle("-fx-text-fill: #2196f3; -fx-background-color: #151928");
            });
            button.setOnMouseExited(event -> {
                try {
                    button.setText(business.getString("businessName"));;
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                button.setStyle("-fx-text-fill: #151928; -fx-background-color: transparent");
            });
            button.setOnMouseClicked(event -> {
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/SubFXMLs/Business/BusinessInfo.fxml"));
                StackPane page = null;
                try {
                    page = fxmlLoader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Singleton.getInstance().PerformFadeTransition(page, 0.0, 1.0, 1.5);
                Scene scene = new Scene(page);
                scene.setFill(Color.TRANSPARENT);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.initStyle(StageStyle.TRANSPARENT);
                BusinessInfoController controller = fxmlLoader.getController();
                try {
                    controller.InitController(business.getString("businessID"), responsableComboBox.getSelectionModel().getSelectedItem());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                stage.showAndWait();
                try {
                    OnReload();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            businessBox.getChildren().add(button);
            businessVBox.getChildren().add(businessBox);
        }
    }

    public void FillProcessBox(JSONArray resultSet) throws JSONException {
        for (int i = 0; i < resultSet.length(); i++) {
            HBox processBox = primaryProcessHBox;
            MFXButton button = new MFXButton(resultSet.getJSONObject(i).getString("processID"));
            button.setStyle("-fx-text-fill: #151928; -fx-background-color: #2196f3");
            button.setAlignment(Pos.CENTER_LEFT);
            button.setOnMouseEntered(event -> {
                button.setText("Перейти к процессу");
                button.setStyle("-fx-text-fill: #2196f3; -fx-background-color: #151928");
            });
            int finalI = i;
            button.setOnMouseExited(event -> {
                try {
                    button.setText(resultSet.getJSONObject(finalI).getString("processID"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                button.setStyle("-fx-text-fill: #151928; -fx-background-color: #2196f3");
            });
            button.setOnMouseClicked(event -> {

            });
            processVBox.getChildren().add(button);
        }
    }

    public void FillCommentBox(JSONArray resultSet) throws JSONException {
        for (int i = 0; i < resultSet.length(); i++) {
            JSONObject comment = resultSet.getJSONObject(i);
            HBox hBox = Singleton.getInstance().SetCommentBox(sampleHBox1, comment.getString("commentText"),
                    comment.getString("commentSenderName"), comment.getString("commentDate"),
                    comment.getString("commentSenderLogin"));
            journalVBox1.getChildren().add(hBox);
        }
    }
}
