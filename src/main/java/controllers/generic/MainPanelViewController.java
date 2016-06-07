package controllers.generic;

import controllers.DialogController;
import customgui.CustomRadioMenuItem;
import entity.UserEntity;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import orm.HibernateGenericDao;
import other.ClassCollector;
import other.Session;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;

import static other.Session.user;

/**
 * Created by User on 07.06.2016.
 */
public class MainPanelViewController implements Initializable {
    private static Stage stage;
    private static UserEntity user;
    private static ClassCollector classCollector = ClassCollector.INSTANCE;

    @FXML Label status;
    @FXML Menu tablesMenu;
    @FXML TableView table;

    public MainPanelViewController() {
    }

    public static void open(Stage parentStage) throws IOException {
        Parent root = FXMLLoader.load(MainPanelViewController.class.getClassLoader().getResource("views/generic/main-panel.view.fxml"));
        stage = new Stage();
        stage.setTitle("Добро пожаловать " + Session.user.getLogin());
        stage.setScene(new Scene(root));
        MainPanelViewController.user = Session.user;
        parentStage.hide();
        stage.show();
        (new DialogController(stage, DialogController.Type.INFO))
                .setTitle("Уведомление")
                .setMsg("Добро пожаловать " + user.getLogin()).show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup group = new ToggleGroup();
        try {
            for (Class aClass : classCollector.getClasses("entity")) {
                String des = classCollector.getDescription(aClass);
                if (des == null) continue;

                CustomRadioMenuItem item = new CustomRadioMenuItem(des, aClass);
                item.setToggleGroup(group);

                item.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        initTable(item.getCl());
                        status.setText("Открыта таблица \"" + item.getText() + "\"");
                    }
                });
                tablesMenu.getItems().add(item);
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    private void initTable(Class cl) {
        while (table.getColumns().size() != 0)
            table.getColumns().remove(0);
        Field[] fields = cl.getDeclaredFields();
        for (Field field : fields) {
            String des = classCollector.getDescription(field);
            TableColumn column = new TableColumn(des);
            column.setCellValueFactory(new PropertyValueFactory<>(field.getName()));
            table.getColumns().add(column);
        }
        Collection collection = HibernateGenericDao.readCollection(cl);
        ObservableList list = FXCollections.observableArrayList();
        collection.forEach(list::add);
        table.setItems(list);
    }


    public void add(ActionEvent actionEvent) {
    }

    public void edit(ActionEvent actionEvent) {
    }

    public void del(ActionEvent actionEvent) {
    }

    public void exit(ActionEvent actionEvent) {
    }
}
