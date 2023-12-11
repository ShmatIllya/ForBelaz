package practise.controllers2.Tasks;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.utils.others.dates.DateStringConverter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import practise.controllers2.DashboardController;
import practise.singleton.Singleton;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class TaskInfoController implements Initializable {
    public StackPane stackPane;
    public JFXComboBox<String> checkerComboBox;
    public TextField nameField;
    public JFXComboBox<String> responsableComboBox;
    public MFXDatePicker datePicker;
    public TextField creationDateField;
    public TextField statusField;
    public TextArea descriptionField;
    public HBox obeyButtonsHBox;
    public JFXButton completeButton;
    public JFXButton uncompletedButton;
    public HBox controlButtonsHBox;
    public JFXButton checkButton;
    public JFXButton uncheckButton;
    public JFXButton addBusinessButton;
    public VBox journalVBox1;
    public TextField journalTextField1;
    public HBox sampleHBox1;
    public JFXComboBox<String> priorityComboBox;
    public HBox primaryBusinessHBox;
    public VBox businessVBox;
    boolean onChange = false;
    String old_name;
    String old_responsable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ObservableList<String> comboList = FXCollections.observableArrayList();
        String[] arrStr = {"GetPersonalObeyList"};
        String tempString = (String) Singleton.getInstance().getDataController().GetPersonalObeyList(arrStr);
        tempString = tempString.replaceAll("\r", "");
        String[] resultSet = tempString.split("<<");
        for(String i : resultSet) {
            String[] resultSubSet = i.split(">>");
            try {
                comboList.add(resultSubSet[0]);
            }
            catch (Exception e) {
                System.out.println(e);
            }
        }
        responsableComboBox.setItems(comboList);

        comboList = FXCollections.observableArrayList();
        String[] arrStr2 = {"GetPersonalControlList"};
        tempString = (String) Singleton.getInstance().getDataController().GetPersonalControlList(arrStr2);
        tempString = tempString.replaceAll("\r", "");
        resultSet = tempString.split("<<");
        for(String i : resultSet) {
            String[] resultSubSet = i.split(">>");
            try {
                comboList.add(resultSubSet[0]);
            }
            catch (Exception e) {
                System.out.println(e);
            }
        }
        checkerComboBox.setItems(comboList);
        //================================DashBoard buttons================================
        JFXButton SaveButton = new JFXButton();
        JFXButton ChangeButton = new JFXButton();
        SaveButton.setDisable(true);

        SaveButton.setText("Сохранить изменения");
        SaveButton.setStyle("-fx-pref-width: 458; -fx-pref-height: 38; -fx-text-fill: white; -fx-font-size: 16px");
        SaveButton.setOnMouseClicked(mouseEvent -> {
            try {
                String[] arrStrUpdate = {"UpdateTaskInfo", nameField.getText(), responsableComboBox.getSelectionModel().getSelectedItem(),
                        descriptionField.getText(), checkerComboBox.getSelectionModel().getSelectedItem(), datePicker.getText(),
                        priorityComboBox.getSelectionModel().getSelectedItem(),
                        old_name, old_responsable};
                String tempStringUpdate = (String) Singleton.getInstance().getDataController().UpdateTaskInfo(arrStrUpdate);
                tempStringUpdate = tempStringUpdate.replaceAll("\r", "");
                Label messageBox = new Label();
                if(tempStringUpdate.equals("null")) {
                    messageBox.setText("Ошибка сохранения");
                }
                else {
                    Singleton.getInstance().setTaskInfoValues(new String[]{nameField.getText(),
                            responsableComboBox.getSelectionModel().getSelectedItem()});
                    messageBox.setText("Успешно сохранено");
                }
                Singleton.getInstance().ShowJFXDialogStandart(stackPane, messageBox);
                //PersonalInfoClass tempString = Singleton.getInstance().getDataController().GetPersonalInfo(arrStr);
                onChange = false;
                nameField.setEditable(false);
                checkerComboBox.setDisable(true);
                responsableComboBox.setDisable(true);
                priorityComboBox.setDisable(true);
                datePicker.setDisable(true);
                descriptionField.setEditable(false);
                SaveButton.setDisable(true);
                ChangeButton.setDisable(false);


            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            finally {
                try {
                    OnReload();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        ChangeButton.setText("Изменить информацию");
        ChangeButton.setStyle("-fx-pref-width: 458; -fx-pref-height: 38; -fx-text-fill: white; -fx-font-size: 16px");
        ChangeButton.setOnMouseClicked(mouseEvent -> {
            onChange = true;
            nameField.setEditable(true);
            checkerComboBox.setDisable(false);
            responsableComboBox.setDisable(false);
            priorityComboBox.setDisable(false);
            datePicker.setDisable(false);
            descriptionField.setEditable(true);
            SaveButton.setDisable(false);
            ChangeButton.setDisable(true);
        });
        if(Singleton.getInstance().getFinal_Role().equals("obey")) {
            ChangeButton.setDisable(true);
        }

        ArrayList<JFXButton> buttons = new ArrayList<>();
        buttons.add(ChangeButton);
        buttons.add(SaveButton);
        DashboardController temp = new DashboardController();
        temp.SetVBoxButtons(buttons);
        //===============================/DashBoard buttons================================
        datePicker.setConverterSupplier(() -> new DateStringConverter("yyyy-MM-dd", datePicker.getLocale()));
        ObservableList<String> list = FXCollections.observableArrayList("П3 Низкий", "П2 Средний", "П1 Высокий");
        priorityComboBox.setItems(list);

        try {
            OnReload();
            //=================================Role and Status Actions=======================================
            if(Singleton.getInstance().getFinal_Role().equals("control")) {
                double X1 = controlButtonsHBox.getLayoutX();
                double Y1 = controlButtonsHBox.getLayoutY();
                double X2 = obeyButtonsHBox.getLayoutX();
                double Y2 = obeyButtonsHBox.getLayoutY();
                obeyButtonsHBox.setLayoutX(X1);
                obeyButtonsHBox.setLayoutY(Y1);
                obeyButtonsHBox.setVisible(false);

                controlButtonsHBox.setLayoutX(X2);
                controlButtonsHBox.setLayoutY(Y2);
                controlButtonsHBox.setVisible(true);

            }
            else if (Singleton.getInstance().getFinal_Role().equals("obey") &&
                    !Singleton.getInstance().getFinal_NameSername().equals(responsableComboBox.getSelectionModel().getSelectedItem())) {
                obeyButtonsHBox.setVisible(false);
            }
            if(!statusField.getText().equals("Назначена")) {
                System.out.println("'" + statusField.getText() + "'");
                controlButtonsHBox.setVisible(false);
                obeyButtonsHBox.setVisible(false);
            }
            //=================================/Role ans Status Actions======================================

        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void OnReload() throws IOException {
        journalVBox1.getChildren().clear();
        String[] arrStr = {"GetTaskInfo", Singleton.getInstance().getTaskInfoValues()[0],
                Singleton.getInstance().getTaskInfoValues()[1]};
        //PersonalInfoClass tempString = Singleton.getInstance().getDataController().GetPersonalInfo(arrStr);
        String tempString = (String) Singleton.getInstance().getDataController().GetTaskInfo(arrStr);
        tempString = tempString.replaceAll("\r", "");
        
        String[] resultSet = tempString.split(">>");
        int data_type = 0;
        for(String i: resultSet) {
            String subResSet[] = i.split("<<");
            try {
                switch (data_type) {
                    case 0: {
                        nameField.setText(subResSet[0]);
                        old_name = subResSet[0];
                        responsableComboBox.getSelectionModel().select(subResSet[1]);
                        old_responsable = subResSet[1];
                        descriptionField.setText(subResSet[2]);
                        checkerComboBox.getSelectionModel().select(subResSet[3]);
                        datePicker.setText(subResSet[4]);
                        statusField.setText(subResSet[5]);
                        creationDateField.setText(subResSet[6]);
                        priorityComboBox.getSelectionModel().select(subResSet[7]);
                        break;
                    }
                    case 1: {
                        if (!i.equals("")) {
                            for (String business : subResSet) {
                                HBox businessBox = primaryBusinessHBox;
                                MFXButton button = new MFXButton(business);
                                button.setStyle("-fx-text-fill: #151928; -fx-background-color: #2196f3");
                                button.setAlignment(Pos.CENTER_LEFT);
                                button.setOnMouseEntered(event -> {
                                    button.setText("Перейти к делу");
                                    button.setStyle("-fx-text-fill: #2196f3; -fx-background-color: #151928");
                                });
                                button.setOnMouseExited(event -> {
                                    button.setText(business);
                                    button.setStyle("-fx-text-fill: #151928; -fx-background-color: #2196f3");
                                });
                                button.setOnMouseClicked(event -> {

                                });
                                businessVBox.getChildren().add(button);
                            }
                        }
                        break;
                    }
                    case 2: {
                        if (!i.equals("")) {
                            for (String comment : subResSet) {
                                String[] commentData = comment.split("\\^\\^");
                                System.out.println(comment);
                                HBox hBox = Singleton.getInstance().SetCommentBox(sampleHBox1, commentData[0], commentData[1], commentData[2], commentData[3]);
                                journalVBox1.getChildren().add(hBox);
                            }
                        }
                        break;
                    }
                }
                data_type++;
            }
            catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
