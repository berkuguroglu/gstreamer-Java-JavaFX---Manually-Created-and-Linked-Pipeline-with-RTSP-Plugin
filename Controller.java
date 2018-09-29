package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import org.freedesktop.gstreamer.State;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class Controller
{
    @FXML
    private BorderPane imageView;

    @FXML
    void initialize()
    {
    }
    public void main_to_controller(ImageView test)
    {

        test.prefHeight(imageView.getHeight());
        test.prefWidth(imageView.getWidth());
        this.imageView.setCenter(test);

    }

}
