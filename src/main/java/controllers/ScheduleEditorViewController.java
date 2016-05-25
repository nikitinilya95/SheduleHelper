package controllers;

import com.sun.jnlp.ApiDialog.DialogResult;
import entity.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import orm.Repository;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by User on 21.05.2016.
 */
public class ScheduleEditorViewController implements Initializable {
    private static Stage stage;
    private static DialogResult result;
    private static Repository db = Repository.INSTANCE;

    private static AudienceEntity audience;
    private static TeacherEntity teacher;
    private static GroupEntity group;
    private static int dayOfWeek;
    private static TimeTableEntity time;
    private static int weekOdd;
    private static ScheduleItemEntity item;

    @FXML ComboBox<DirectionEntity> directions;
    @FXML ComboBox<GroupEntity> groups;
    @FXML ComboBox<DisciplineEntity> disciplines;
    @FXML ComboBox<DisciplineTypeEntity> disciplineTypes;
    @FXML ComboBox<TeacherEntity> teachers;
    @FXML ComboBox<AudienceEntity> audiences;
    private boolean wasWarning;

    public ScheduleEditorViewController() {
    }

    public ScheduleEditorViewController(Stage parentStage, AudienceEntity audience, TeacherEntity teacher,
                                        GroupEntity group, String title, int dayOfWeek, TimeTableEntity time,
                                        int weekOdd, ScheduleItemEntity item) throws IOException {
        ScheduleEditorViewController.group = group;
        ScheduleEditorViewController.audience = audience;
        ScheduleEditorViewController.teacher = teacher;
        ScheduleEditorViewController.dayOfWeek = dayOfWeek;
        ScheduleEditorViewController.time = time;
        ScheduleEditorViewController.weekOdd = weekOdd;
        ScheduleEditorViewController.item = item;
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("views/schedule-editor.view.fxml"));
        stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.initOwner(parentStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    public ScheduleEditorViewController(Stage parentStage, TeacherEntity teacher, int dayOfWeek, TimeTableEntity time, int weekOdd) throws IOException {
        this(parentStage, null, teacher, null, "Добавить", dayOfWeek, time, weekOdd, null);
    }

    public ScheduleEditorViewController(Stage parentStage, AudienceEntity audience, int dayOfWeek, TimeTableEntity time, int weekOdd) throws IOException {
        this(parentStage, audience, null, null, "Добавить", dayOfWeek, time, weekOdd, null);
    }

    public ScheduleEditorViewController(Stage parentStage, GroupEntity group, int dayOfWeek, TimeTableEntity time, int weekOdd) throws IOException {
        this(parentStage, null, null, group, "Добавить", dayOfWeek, time, weekOdd, null);
    }

    public ScheduleEditorViewController(Stage parentStage, ScheduleItemEntity item, GroupEntity group) throws IOException {
        this(parentStage,
                item.getNavigator().getAudience(),
                item.getNavigator().getMentor().getTeacher(),
                group,
                "Изменить",
                item.getNavigator().getDayOfWeek(),
                item.getNavigator().getTime(),
                item.getNavigator().getWeekOdd(),
                item);
    }

    public ScheduleEditorViewController(Stage parentStage, ScheduleItemEntity item) throws IOException {
        this(parentStage,
                item.getNavigator().getAudience(),
                item.getNavigator().getMentor().getTeacher(),
                null,
                "Изменить",
                item.getNavigator().getDayOfWeek(),
                item.getNavigator().getTime(),
                item.getNavigator().getWeekOdd(),
                item);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initDirectionData();
        initDisciplineData();
        initDisciplineTypeData();
        initAudienceData();
        initTeachers();

        if (group != null) {
            directions.getSelectionModel().select(group.getDirection());
            disciplines.setItems(db.getDisciplineData(group.getDirection()));
            groups.setItems(db.getGroupData(group.getDirection()));
            groups.getSelectionModel().select(group);
        }

        if (item != null) {
            disciplines.getSelectionModel().select(item.getNavigator().getMentor().getDiscipline());
            disciplineTypes.getSelectionModel().select(item.getNavigator().getMentor().getDisciplineType());
        }
    }

    private void initDirectionData() {
        directions.setItems(db.getDirectionData());
        directions.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DirectionEntity>() {
            @Override
            public void changed(ObservableValue<? extends DirectionEntity> observable, DirectionEntity oldValue, DirectionEntity newValue) {
                groups.setItems(db.getGroupData(newValue));
                disciplines.setItems(db.getDisciplineData(newValue));
            }
        });
    }

    private void initDisciplineData() {
        disciplines.setItems(db.getDisciplineData());
        disciplines.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DisciplineEntity>() {
            @Override
            public void changed(ObservableValue<? extends DisciplineEntity> observable, DisciplineEntity oldValue, DisciplineEntity newValue) {
                checkNewSelect();
            }
        });
    }

    private void initDisciplineTypeData() {
        disciplineTypes.setItems(db.getDisciplineTypeData());
        disciplineTypes.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DisciplineTypeEntity>() {
            @Override
            public void changed(ObservableValue<? extends DisciplineTypeEntity> observable, DisciplineTypeEntity oldValue, DisciplineTypeEntity newValue) {
                checkNewSelect();
                //findFreeAudienceData();
            }
        });
    }

    private void initAudienceData() {
        audiences.setItems(db.getAudienceData());
        audiences.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<AudienceEntity>() {
            @Override
            public void changed(ObservableValue<? extends AudienceEntity> observable, AudienceEntity oldValue, AudienceEntity newValue) {
                checkNewSelect();
            }
        });

        if (audience != null) {
            audiences.getSelectionModel().select(audience);
        }
    }

    private void initTeachers() {
        teachers.setItems(db.getTeacherData());
        teachers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TeacherEntity>() {
            @Override
            public void changed(ObservableValue<? extends TeacherEntity> observable, TeacherEntity oldValue, TeacherEntity newValue) {
                checkNewSelect();
            }
        });

        if (teacher != null) {
            teachers.getSelectionModel().select(teacher);
        }
    }

    private void checkNewSelect() {
        AudienceEntity audience = audiences.getSelectionModel().getSelectedItem();
        DisciplineEntity discipline = disciplines.getSelectionModel().getSelectedItem();
        DisciplineTypeEntity disciplineType =  disciplineTypes.getSelectionModel().getSelectedItem();
        TeacherEntity teacher = teachers.getSelectionModel().getSelectedItem();

        if (audience == null) {
            return;
        }

        NavigatorEntity navigator = db.getNavigator(dayOfWeek, time, weekOdd, audience);
        if (navigator != null && !wasWarning) {
            new DialogController(stage, DialogController.Type.INFO)
                    .setTitle("Внимание")
                    .setMsg("В ауд. " + navigator.getAudience() +
                            " в это время проходит " +
                            navigator.getMentor().getDiscipline().getName() +
                            " (" + navigator.getMentor().getDisciplineType().getName() + "), " +
                            "учитель - " + navigator.getMentor().getTeacher().shortName() +
                            ". Группы: " + db.getGroupData(navigator)).show();
            wasWarning = true;
        }

        if (navigator == null || (navigator.getMentor().getDiscipline().equals(discipline) &&
                navigator.getMentor().getDisciplineType().equals(disciplineType) &&
                navigator.getMentor().getTeacher().equals(teacher))) {
            wasWarning = false;
        } else if (!wasWarning) {
            new DialogController(stage, DialogController.Type.INFO)
                .setTitle("Внимание")
                .setMsg("В ауд. " + navigator.getAudience() +
                        " в это время проходит " +
                        navigator.getMentor().getDiscipline().getName() +
                        " (" + navigator.getMentor().getDisciplineType().getName() + "), " +
                        "учитель - " + navigator.getMentor().getTeacher().shortName() +
                        ". Группы: " + db.getGroupData(navigator)).show();
            wasWarning = true;
        }
    }

    public void addDirection(ActionEvent actionEvent) {
        DialogController dialog = new DialogController(stage, DialogController.Type.INPUT);
        dialog.setTitle("Новое направление")
                .setMsg("Введите новое направление").show();
        if (dialog.dialogResult() == DialogResult.OK) {
            String directionName = dialog.getResultString();
            DirectionEntity newDirection = db.addDirection(directionName);
            directions.getSelectionModel().select(newDirection);
        }
    }

    public void addGroup(ActionEvent actionEvent) throws IOException {
        DirectionEntity direction = directions.getSelectionModel().getSelectedItem();
        new GroupEditorViewController(stage, direction);

        GroupEntity group = GroupEditorViewController.getGroup();
        directions.getSelectionModel().select(group.getDirection());
        groups.getSelectionModel().select(group);
    }

    public void addDiscipline(ActionEvent actionEvent) {
        DialogController dialog = new DialogController(stage, DialogController.Type.INPUT);
        dialog.setTitle("Новая дисциплина")
                .setMsg("Введите новую дисциплину")
                .setInput("").show();
        if (dialog.dialogResult() == DialogResult.OK) {
            String name = dialog.getResultString();
            DisciplineEntity discipline = db.addDiscipline(name);
            DirectionEntity direction = directions.getSelectionModel().getSelectedItem();
            if (direction != null) {
                db.addDisciplineToDirection(discipline, direction);
                disciplines.setItems(db.getDisciplineData(direction));
                disciplines.getSelectionModel().select(discipline);
            }
        }
    }

    public void addDisciplineType(ActionEvent actionEvent) {
        DialogController dialog = new DialogController(stage, DialogController.Type.INPUT);
        dialog.setTitle("Новое тип занятий")
                .setMsg("Введите новый тип занятий")
                .setInput("").show();
        if (dialog.dialogResult() == DialogResult.OK) {
            String name = dialog.getResultString();
            DisciplineTypeEntity disciplineType = db.addDisciplineType(name);
            disciplineTypes.getSelectionModel().select(disciplineType);
        }
    }

    public void addTeacher(ActionEvent actionEvent) throws IOException {
        new TeacherEditorViewController(stage);
        TeacherEntity teacher = TeacherEditorViewController.getTeacher();
        teachers.getSelectionModel().select(teacher);
    }

    public void addAudience(ActionEvent actionEvent) throws IOException {
        new AudienceEditorViewController(stage);
        AudienceEntity audience = AudienceEditorViewController.getAudience();
        audiences.getSelectionModel().select(audience);
    }

    public void apply(ActionEvent actionEvent) {
        AudienceEntity audience = audiences.getSelectionModel().getSelectedItem();
        DisciplineEntity discipline = disciplines.getSelectionModel().getSelectedItem();
        DisciplineTypeEntity type = disciplineTypes.getSelectionModel().getSelectedItem();
        GroupEntity group = groups.getSelectionModel().getSelectedItem();
        TeacherEntity teacher = teachers.getSelectionModel().getSelectedItem();

        if (audience == null ||
                discipline == null ||
                type == null ||
                group == null ||
                teacher == null) {
            new DialogController(stage, DialogController.Type.INFO)
                    .setTitle("Ошибка")
                    .setMsg("Данные не введены").show();
            return;
        }

        item = db.addScheduleItem(dayOfWeek, time, weekOdd, audience, discipline, type, group, teacher);
        result = DialogResult.OK;
        stage.close();
    }

    public void cancel(ActionEvent actionEvent) {
        result = DialogResult.CANCEL;
        stage.close();
    }

    public static DialogResult getDialogResult() {
        return result;
    }

    public static ScheduleItemEntity getScheduleItem() {
        return item;
    }
}
