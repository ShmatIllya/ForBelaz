package practise.controllers2;

import com.jfoenix.controls.JFXChipView;
import javafx.animation.FadeTransition;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import practise.items.ClientsItems;
import practise.items.PersonalItems;
import practise.singleton.Singleton;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ClientAddContoller implements Initializable  {
    public Button submitButton;
    public Label ErrorLabel;
    public TextField descriptionField;
    public HBox PlaceChipViewHere;
    public TextField adressField;
    public TextField phoneField;
    public TextField emailField;
    public TextField nameField;
    public StackPane stackPane;
    JFXChipView<String> chipView = new JFXChipView<String>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        List<String> personal = new ArrayList<>();
        chipView.getStylesheets().add("/css/jfxChipsView.css");
        String[] arrStr = {"GetPersonalList"};
        String tempString = (String) Singleton.getInstance().getDataController().GetPersonalList(arrStr);
        tempString = tempString.replaceAll("\r", "");
        String[] resultSet = tempString.split("<<");
        for(String i : resultSet) {
            String[] resultSubSet = i.split(">>");
            personal.add(resultSubSet[0]);
        }

        //chipView.getChips().addAll("WEF", "WWW", "JD");
        chipView.getSuggestions().addAll(personal);
        chipView.getStylesheets().add("D:\\FCP\\SEM7\\CURS\\Project\\Client\\Client\\src\\main\\resources\\css\\jfxChipsView.css");
        chipView.setStyle("-fx-text-fill: white; -fx-font-size: 12px");
        chipView.setPrefWidth(399);
        chipView.setMinWidth(chipView.getPrefWidth());
        StackPane pane = new StackPane();
        pane.getChildren().add(chipView);
        StackPane.setMargin(chipView, new Insets(100));
        PlaceChipViewHere.getChildren().add(chipView);

        BooleanBinding submitButtonBinding = new BooleanBinding() {
            {
                super.bind(nameField.textProperty(),
                        emailField.textProperty(),
                        chipView.getChips());
            }

            @Override
            protected boolean computeValue() {
                return (nameField.getText().isEmpty()
                        || emailField.getText().isEmpty()
                        || chipView.getChips().isEmpty());
            }
        };
        submitButton.disableProperty().bind(submitButtonBinding);
    }

    public void OnSubmitButton() throws IOException {
        String[] arrStr = {"AddClient", nameField.getText(), emailField.getText(), phoneField.getText(),
                adressField.getText(), descriptionField.getText(), chipView.getChips().get(0)};
        String tempString = (String) Singleton.getInstance().getDataController().AddClient(arrStr);
        tempString = tempString.replaceAll("\r", "");
        //String[] resultSet = tempString.split("<<");
        Label MessageLabel = new Label();
        if(tempString.equals("null")) {
            MessageLabel.setText("Ошибка добавления");
            Singleton.getInstance().ShowJFXDialogStandart(stackPane, MessageLabel);
        }
        else {
            MessageLabel.setText("Успешно добавлено");
            Singleton.getInstance().ShowJFXDialogStandart(stackPane, MessageLabel);
            //OnClose();
        }
    }

    public void OnClose() throws IOException {
        FadeTransition ft = Singleton.getInstance().PerformFadeTransition(stackPane, 1, 0, 0.5);
        ft.setOnFinished(event1 -> {
            Stage st = (Stage) submitButton.getScene().getWindow();
            st.close();
        });
    }
}
