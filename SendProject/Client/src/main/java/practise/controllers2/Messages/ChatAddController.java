package practise.controllers2.Messages;

import com.dlsc.gemsfx.PhotoView;
import com.jfoenix.controls.JFXChipView;
import javafx.animation.FadeTransition;
import javafx.beans.binding.BooleanBinding;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import practise.singleton.Singleton;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ChatAddController implements Initializable {
    public StackPane stackPane;
    public PhotoView photoView;
    public TextField nameField;
    public TextField descriptionField;
    public HBox membersHBox;
    public TextField membersField;
    public Button submitButton;
    JFXChipView<String> chatMembersChips = new JFXChipView<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String[] arrStr = {"GetPersonalList"};
        String tempString = (String) Singleton.getInstance().getDataController().GetPersonalList(arrStr);
        tempString = tempString.replaceAll("\r", "");
        String[] resultSet = tempString.split("<<");
        List<String> resultSubSet = new ArrayList<>();
        for(String i: resultSet) {
            resultSubSet.add(i.split(">>")[0]);
        }
        chatMembersChips.getSuggestions().addAll(resultSubSet);
        chatMembersChips.getStylesheets().add("/css/jfxChipsView.css");
        chatMembersChips.setStyle("-fx-text-fill: white; -fx-font-size: 12px");
        chatMembersChips.setPrefWidth(399);
        chatMembersChips.setMinWidth(chatMembersChips.getPrefWidth());
        StackPane pane = new StackPane();
        pane.getChildren().add(chatMembersChips);
        StackPane.setMargin(chatMembersChips, new Insets(100));
        membersHBox.getChildren().remove(1);
        membersHBox.getChildren().add(chatMembersChips);

        BooleanBinding submitButtonBinding = new BooleanBinding() {
            {
                super.bind(nameField.textProperty(),
                        chatMembersChips.getChips(),
                        nameField.textProperty(),
                        photoView.photoProperty());
            }

            @Override
            protected boolean computeValue() {
                return (nameField.getText().isEmpty()
                        || chatMembersChips.getChips().isEmpty()
                        || nameField.getText().isEmpty()
                        || photoView.photoProperty().isNull().getValue());
            }
        };
        submitButton.disableProperty().bind(submitButtonBinding);
    }

    public void OnSubmitButton(ActionEvent event) {
        java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());
        List<String> arrStr = new ArrayList<>();
        arrStr.add("AddChat");
        arrStr.add(nameField.getText());
        arrStr.add(descriptionField.getText());
        for(String i: chatMembersChips.getChips()) {
            arrStr.add(i);
        }
        //========================================================
        BufferedImage sendImage = null;
        sendImage = SwingFXUtils.fromFXImage(photoView.getPhoto(), null);
        //========================================================
        String[] arrStrS = arrStr.toArray(new String[0]);
        String tempString = (String) Singleton.getInstance().getDataController().AddChat(arrStrS,
                sendImage);
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
            OnClose(event);
        }
    }

    public void OnClose(ActionEvent event) {
        FadeTransition ft = Singleton.getInstance().PerformFadeTransition(stackPane, 1, 0, 0.5);
        ft.setOnFinished(event1 -> {
            Stage st = (Stage) submitButton.getScene().getWindow();
            st.close();
        });
    }

}
