    package com.shreyo.calendar;

    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.scene.Node;
    import javafx.scene.control.Alert;
    import javafx.scene.control.Button;
    import javafx.scene.control.Label;
    import javafx.scene.control.MenuItem;
    import javafx.scene.layout.GridPane;
    import javafx.scene.layout.VBox;
    import javafx.scene.layout.HBox;
    import javafx.scene.layout.Priority;
    import javafx.scene.layout.Region;
    import javafx.scene.layout.FlowPane;
    import javafx.scene.control.TextArea;
    import javafx.scene.control.CheckBox;
    import javafx.scene.control.ContextMenu;
    import javafx.scene.control.TextField;
    import javafx.scene.layout.StackPane;
    import javafx.scene.control.Alert;
    import javafx.scene.control.Alert.AlertType;
    import javafx.scene.input.MouseEvent;

    import java.time.LocalDate;
    import java.time.format.DateTimeFormatter;
    import java.util.Set;
    import java.util.List;
    import java.util.ArrayList;
    import java.util.HashSet;
    import java.time.YearMonth;

    public class LayoutHandler {
        public enum TaskPriority {
            HIGH,
            REG,
            DONE
        }

        private int taskCount = 0;

        @FXML
        private Label textLabel;

        @FXML
        private Button actionButton;

        @FXML
        private Button addTask;

        @FXML
        private Label taskCountLabel;

        @FXML
        private Label dateLabel;

        @FXML
        private GridPane calendarGrid;

        @FXML
        private Label hdrDate;

        @FXML
        private FlowPane highTasks, regularTasks, doneTasks;

        @FXML
        private StackPane createTaskPane;

        @FXML
        private TextField taskTopic;

        @FXML
        private TextArea taskContent;

        @FXML
        private Button createTaskButton, cancelTaskButton;

        @FXML
        private CheckBox urgChkBox;

        LocalDate currentCalendarDate = LocalDate.now();

        private String[] month_names_short = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        private String[] month_names_full = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

        private boolean display_full_month = true;

        private List<Task> tasklist = FileStorage.loadTasks();

        private List<Task> currTasks = new ArrayList<>();

        @FXML
        private void initialize() {
            populateCalendar(currentCalendarDate, updateEventList(tasklist));

            renderTasks(selectedDate);
        }

        private Set<Integer> updateEventList(List<Task> tasklist) {
            Set<Integer> eventlist = new HashSet<>();

            for (Task task : tasklist) {
                if (YearMonth.from(task.getDate()).equals(YearMonth.from(currentCalendarDate))) {
                    if (!eventlist.contains(task.getDate().getDayOfMonth()))
                    eventlist.add(task.getDate().getDayOfMonth());
                }
            }

            return eventlist;
        }

        private List<Task> getDayTasks(LocalDate date, List<Task> tasklist) {
            currTasks.clear();

            for (Task task : tasklist) {
                if (task.getDate().isEqual(date)) {
                    currTasks.add(task);
                }
            }

            return currTasks;
        }

        private void updateHdrDate(LocalDate date) {
            int day = date.getDayOfMonth();
            int month = date.getMonthValue();
            int year = date.getYear();

            hdrDate.setText(String.valueOf(day) + " " + month_names_short[month - 1] + " " + String.valueOf(year));
        }

        private void updateTaskCount(int count) {
            taskCount = count;
            taskCountLabel.setText(taskCount>1?taskCount + " Tasks":taskCount + " Task");
        }

        @FXML
        private void handleClick(ActionEvent event) {
            textLabel.setText(textLabel.getText() + " I am Shreyo Pal!");
        }

        @FXML
        private void newTask(ActionEvent event) {
            if (selectedDate.isBefore(LocalDate.now())) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Cannot create task in the past");
                alert.setHeaderText(null);
                alert.show();
            } else {
                createTaskPane.setVisible(true);
                createTaskPane.setManaged(true);
            }
        }

        private void clearTaskPane() {
            taskTopic.clear();
            taskContent.clear();
            urgChkBox.setSelected(false);

            createTaskPane.setVisible(false);
            createTaskPane.setManaged(false);
        }

        @FXML
        private void createTask() {
            if (taskTopic.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please enter a task topic!");
                alert.setHeaderText(null);
                alert.show();
            } else {
                TaskPriority priority = TaskPriority.REG;
                if (urgChkBox.isSelected()) priority = TaskPriority.HIGH;
                Task newtask = new Task(taskTopic.getText(), taskContent.getText(), priority, selectedDate);

                tasklist.add(newtask);

                FileStorage.saveTasks(tasklist);

                populateCalendar(currentCalendarDate, updateEventList(tasklist));

                updateTaskCount(taskCount+1);
            }

            clearTaskPane();

            renderTasks(selectedDate);
        }

        @FXML
        private void cancelTask() {
            clearTaskPane();
        }

        private LocalDate selectedDate = currentCalendarDate;

        public void setSelectedDate(LocalDate date) {
            this.selectedDate = date;
        }

        @FXML
        private void incMonth() {
            currentCalendarDate = currentCalendarDate.plusMonths(1);
            setSelectedDate(currentCalendarDate);
            populateCalendar(currentCalendarDate, updateEventList(tasklist));
            renderTasks(currentCalendarDate);
        }

        @FXML
        private void decMonth() {
            currentCalendarDate = currentCalendarDate.minusMonths(1);
            setSelectedDate(currentCalendarDate);
            populateCalendar(currentCalendarDate, updateEventList(tasklist));
            renderTasks(currentCalendarDate);
        }

        private void populateCalendar(LocalDate targetDate, Set<Integer> eventlist) {
            String[] daynames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

            calendarGrid.getChildren().clear(); // clear all nodes

            for (int i=0; i<7; i++) {
                Label dayLabel = new Label(daynames[i]);
                dayLabel.getStyleClass().add("day-label");
                calendarGrid.add(dayLabel, i, 0);
            }


            LocalDate firstOfMonth = targetDate.withDayOfMonth(1);
            int startCol = firstOfMonth.getDayOfWeek().getValue() % 7;
            int totalDays = targetDate.lengthOfMonth();

            int currCol = 0;
            int currRow = 1;
            LocalDate date = firstOfMonth;

            System.out.println(firstOfMonth.minusDays(3));

            // adding previous month's days to grid

            date = date.minusDays(startCol);

            for (int i=0; i<startCol; i++) {
                boolean isToday = false;
                boolean isEvent = false;
                boolean isSelected = false;

                DateCellPane dayCell = new DateCellPane(this, date, isToday, isEvent, isSelected, false);

                calendarGrid.add(dayCell, i, currRow);

                currCol++;
                if (currCol > 6) {
                    currCol = 0;
                    currRow++;
                }

                date = date.plusDays(1);
            }

            // adding current month's days to grid
            for (int i=1; i<=totalDays; i++) {
                boolean isToday = date.equals(LocalDate.now());
                boolean isEvent = eventlist.contains(i);
                boolean isSelected = date.equals(selectedDate);
                DateCellPane dayCell = new DateCellPane(this, date, isToday, isEvent, isSelected, true);

                calendarGrid.add(dayCell, currCol, currRow);

                currCol++;
                if (currCol > 6) {
                    currCol = 0;
                    currRow++;
                }

                date = date.plusDays(1);
            }

            // adding next month's days
            while (currRow<7) {
                boolean isToday = false;
                boolean isEvent = false;
                boolean isSelected = false;

                DateCellPane dayCell = new DateCellPane(this, date, isToday, isEvent, isSelected, false);

                calendarGrid.add(dayCell, currCol, currRow);

                currCol++;
                if (currCol > 6) {
                    currCol = 0;
                    currRow++;
                }

                date = date.plusDays(1);


            }

            DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("M");
            DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");

            String monthString = "";
            if (display_full_month) monthString = month_names_full[Integer.parseInt(targetDate.format(monthFormatter)) - 1] + " " + targetDate.format(yearFormatter);
            else monthString = month_names_short[Integer.parseInt(targetDate.format(monthFormatter)) - 1] + " " + targetDate.format(yearFormatter);
            dateLabel.setText(monthString);

            updateHdrDate(targetDate);
        }

        private class DateCellPane extends VBox {
            private final LocalDate displayDate;

            public DateCellPane(LayoutHandler root, LocalDate date, boolean today, boolean event, boolean selected, boolean currMon) {
                this.displayDate = date;
                this.getStyleClass().clear();
                this.getStyleClass().add("date-cell");
                if (today) this.getStyleClass().add("today");
                if (currMon) this.getStyleClass().add("curr-month");

                Label dayLabel = new Label(String.valueOf(this.displayDate.getDayOfMonth()));
                dayLabel.getStyleClass().add("day-label");
                this.getChildren().add(dayLabel);

                if (event) {
                    Region eventDot = new Region();

                    eventDot.getStyleClass().add("event-dot");

                    this.getChildren().add(eventDot);
                }

                if (selected) this.getStyleClass().add("selected");

                this.setOnMouseClicked(e -> {
                    root.selectDateCell(e, this, root);
                });
            }

            public LocalDate getDate() {
                return this.displayDate;
            }
        }

        private void selectDateCell(MouseEvent e, DateCellPane t, LayoutHandler root) {
            Node siblings = t.getParent().lookup(".date-cell.selected");
            if (siblings != null) siblings.getStyleClass().remove("selected");
            t.getStyleClass().add("selected");

            root.setSelectedDate(t.getDate());

            YearMonth selectedMonth = YearMonth.from(t.getDate());
            YearMonth displayMonth = YearMonth.from(currentCalendarDate);

            if (selectedMonth.isBefore(displayMonth)) {
                decMonth();
                root.setSelectedDate(t.getDate());
                populateCalendar(t.getDate(), updateEventList(tasklist));
            } else if (selectedMonth.isAfter(displayMonth)) {
                incMonth();
                root.setSelectedDate(t.getDate());
                populateCalendar(t.getDate(), updateEventList(tasklist));
            }

            currentCalendarDate = t.getDate();
            renderTasks(t.displayDate);
        }

        private void renderTasks(LocalDate date) {
            updateHdrDate(date);
            System.out.println("Selected date: " + selectedDate.getDayOfMonth());

            highTasks.getChildren().clear();
            regularTasks.getChildren().clear();
            doneTasks.getChildren().clear();

            List<Task> tasks = getDayTasks(date, tasklist);

            updateTaskCount(tasks.size());

            for (Task task : tasks) new TaskBox(task);
        }

        private class TaskBox extends VBox {
            private final Task task;
            private String title, content;
            private TaskPriority priority;
            private final Label tasktopic = new Label();
            private final Label priorityLabel = new Label("Done");
            private final Label taskContent = new Label();
            private final ContextMenu contextMenu = new ContextMenu();
            private final HBox hbox = new HBox();

            public TaskBox(Task associatedTask) {
                this.task = associatedTask;
                this.title = task.getTitle();
                this.content = task.getContent();
                this.priority = task.getPriority();

                tasktopic.getStyleClass().add("task-topic");
                tasktopic.setText(this.title);
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                priorityLabel.getStyleClass().add("task-priority");

                hbox.getChildren().addAll(tasktopic, spacer, priorityLabel);
                hbox.setSpacing(4);
                this.getStyleClass().add("task-element");
                this.getChildren().add(hbox);
                if (!this.content.isEmpty()) {
                    taskContent.setText(this.content);
                    VBox contentVBox = new VBox(this.taskContent);
                    this.getChildren().add(contentVBox);
                }

                MenuItem urgMenu = new MenuItem("High");
                urgMenu.setOnAction(e -> changeStatus(TaskPriority.HIGH));
                MenuItem regMenu = new MenuItem("Normal");
                regMenu.setOnAction(e -> changeStatus(TaskPriority.REG));
                MenuItem doneMenu = new MenuItem("Done");
                doneMenu.setOnAction(e -> changeStatus(TaskPriority.DONE));
                MenuItem deleteMenu = new MenuItem("Delete");
                deleteMenu.setOnAction(e -> deleteTask());

                contextMenu.getItems().addAll(urgMenu, regMenu, doneMenu, deleteMenu);
                contextMenu.setAutoHide(true);

                this.setOnContextMenuRequested(e -> {
                    contextMenu.show(this, e.getScreenX(), e.getScreenY());
                    e.consume();
                });

                if (priority == TaskPriority.HIGH) {
                    priorityLabel.setText("Urgent");
                    highTasks.getChildren().add(this);
                } else if (priority == TaskPriority.DONE) {
                    priorityLabel.setText("Done");
                    doneTasks.getChildren().add(this);
                } else {
                    priorityLabel.setText("");
                    regularTasks.getChildren().add(this);
                }
            }

            public void changeStatus(TaskPriority priority) {
                contextMenu.hide();
                if (this.priority == priority) {
                    return;
                }
                if (priority == TaskPriority.DONE) {
                    if (this.priority == TaskPriority.REG) {
                        regularTasks.getChildren().remove(this);
                    } else {
                        highTasks.getChildren().remove(this);
                    }
                    this.priority = priority;
                    priorityLabel.setText("Done");
                    doneTasks.getChildren().add(this);
                } else if (priority == TaskPriority.HIGH) {
                    if (this.priority == TaskPriority.REG) {
                        regularTasks.getChildren().remove(this);
                    } else {
                        doneTasks.getChildren().remove(this);
                    }
                    this.priority = priority;
                    priorityLabel.setText("Urgent");
                    highTasks.getChildren().add(this);
                } else {
                    if (this.priority == TaskPriority.DONE) {
                        doneTasks.getChildren().remove(this);
                    } else {
                        highTasks.getChildren().remove(this);
                    }
                    this.priority = priority;
                    priorityLabel.setText("");
                    regularTasks.getChildren().add(this);
                }

                this.task.setPriority(priority);

                FileStorage.saveTasks(tasklist);
            }

            public void deleteTask() {
                contextMenu.hide();
                if (this.priority == TaskPriority.HIGH) {
                    highTasks.getChildren().remove(this);
                } else if (this.priority == TaskPriority.REG) {
                    regularTasks.getChildren().remove(this);
                } else {
                    doneTasks.getChildren().remove(this);
                }

                currTasks.remove(this.task);
                tasklist.remove(this.task);

                updateTaskCount(currTasks.size());


                FileStorage.saveTasks(tasklist);

                if (currTasks.isEmpty()) populateCalendar(currentCalendarDate, updateEventList(tasklist));
            }
        }
    }
