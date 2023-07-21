package view.panels;

import org.jdatepicker.JDatePicker;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import controller.UASController;
import java.util.Stack;
import model.UASFactory;
import model.dto.ClassDTO;
import model.dto.CourseDTO;
import model.dto.Response;
import model.dto.StudentDTO;
import model.dto.TeacherCourseDTO;
import model.dto.TeacherCourseViewDTO;

public class AddAttendance extends JPanel {

    UASController controllerObj;
    private JLabel classLabel;
    private JLabel courseLabel;
    private JComboBox<String> classComboBox;
    private JComboBox<String> courseComboBox;
    private JLabel dateLabel;
    private JDatePicker datePicker;
    private JLabel timeSlotLabel;
    private JTextField timeSlotField;
    private JLabel remarksLabel;
    private JTextField remarksField;
    private JTable studentTable;
    private JButton addAttendanceButton;
    private JCheckBox checkAllCheckBox;
    private DefaultTableModel tableModel;
    ArrayList<StudentDTO> studentList;

    public AddAttendance() {
        controllerObj = UASFactory.getUASControllerInstance();
        
        initializeComponents();
        initializeStudentTable();
        setupLayout();
        addListeners();
    }

    private void initializeComponents() {

        classLabel = new JLabel("Class:");
        courseLabel = new JLabel("Course:");
        ArrayList<ClassDTO> list = controllerObj.getClassesByTeacherID(new Response());

        Stack<String> cl = new Stack<>();
        for (ClassDTO c : list) {
            cl.push(c.getClassId());
        }
        classComboBox = new JComboBox<>(cl);
        courseComboBox = new JComboBox<>();
        String selectedClass = (String) classComboBox.getSelectedItem();
        ClassDTO classObj = new ClassDTO();
        classObj.setClassId(selectedClass);
        ArrayList<TeacherCourseViewDTO> list1 = controllerObj.getCoursesByClassIDTeacherID(classObj, new Response());

        // Clear the existing items from the courseComboBox
        courseComboBox.removeAllItems();

        // Add the new items to the courseComboBox
        for (TeacherCourseViewDTO c : list1) {
            courseComboBox.addItem( c.getCourseCode());
        }
        dateLabel = new JLabel("Date:");
        datePicker = new JDatePicker(new Date());

        timeSlotLabel = new JLabel("Time Slot:");
        timeSlotField = new JTextField(10);
        remarksLabel = new JLabel("Remarks:");
        remarksField = new JTextField(20);

        // Set current time slot
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Date currentDate = new Date();
        String timeString = timeFormat.format(currentDate);

        timeSlotField.setText(timeString);

        
        addAttendanceButton = new JButton("Add Attendance");
    }

    private void initializeStudentTable() {

        // Initialize the table model with column names and set it to the JTable
        tableModel = new DefaultTableModel(new Object[]{"Name", "RegNo", "Present"}, 0);
         studentTable = new JTable(tableModel);
        studentTable.setModel(tableModel);

        // Set checkbox renderer for the "Present" column
        studentTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JCheckBox checkBox = new JCheckBox();
                checkBox.setSelected((boolean) value);
                return checkBox;
            }
        });
    }

    private void updateStudentTable(ArrayList<StudentDTO> studentList) {
        checkAllCheckBox.setSelected(true); 
        // Clear the existing table data
        tableModel.setRowCount(0);

        // Populate the table with the student list and checkboxes
        for (StudentDTO student : studentList) {
            Object[] rowData = new Object[]{student.getName(), student.getRegNo(), true};
            tableModel.addRow(rowData);
        }
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        setOpaque(false); // Make the panel transparent

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add course label and combo box
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(classLabel, gbc);

        gbc.gridx = 1;
        add(classComboBox, gbc);
        //Add course
        gbc.gridx = 0;
        gbc.gridy++;
        add(courseLabel, gbc);
        gbc.gridx = 1;
        add(courseComboBox, gbc);
        // Add date label and date picker
        gbc.gridx = 0;
        gbc.gridy++;
        add(dateLabel, gbc);

        gbc.gridx = 1;
        add(datePicker, gbc);

        // Add time slot label and text field
        gbc.gridx = 0;
        gbc.gridy++;
        add(timeSlotLabel, gbc);

        gbc.gridx = 1;
        add(timeSlotField, gbc);

        // Add remarks label and text field
        gbc.gridx = 0;
        gbc.gridy++;
        add(remarksLabel, gbc);

        gbc.gridx = 1;

        add(remarksField, gbc);

        // Add check/uncheck all checkbox
        checkAllCheckBox = new JCheckBox("Check/Uncheck All");
        checkAllCheckBox.setSelected(true); 
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        add(checkAllCheckBox, gbc);

        // Add student table
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane scrollPane = new JScrollPane(studentTable);
        add(scrollPane, gbc);

        // Add add attendance button
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.anchor = GridBagConstraints.CENTER;
        add(addAttendanceButton, gbc);
    }

    private void addListeners() {
        classComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedClass = (String) classComboBox.getSelectedItem();
                ClassDTO classObj = new ClassDTO();
                classObj.setClassId(selectedClass);
                ArrayList<TeacherCourseViewDTO> list = controllerObj.getCoursesByClassIDTeacherID(classObj, new Response());

                // Clear the existing items from the courseComboBox
                courseComboBox.removeAllItems();

                // Add the new items to the courseComboBox
                for (TeacherCourseViewDTO c : list) {
                    courseComboBox.addItem( c.getCourseCode());
                }
            }
        });
        courseComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedClass = (String) classComboBox.getSelectedItem();
                String selectedCourse = (String) courseComboBox.getSelectedItem();
                ClassDTO classObj = new ClassDTO();
                classObj.setClassId(selectedClass);
                CourseDTO course = new CourseDTO();
                course.setCourseCode(selectedCourse);

                studentList = controllerObj.getStudentByClassIDCourseCode(course, classObj, new Response());

                // Debug print statements
                System.out.println("Selected Class: " + selectedClass);
                System.out.println("Selected Course: " + selectedCourse);
                for (StudentDTO s : studentList) {
                    System.out.println(s.getName());
                }

                // Update the JTable with the student list and checkboxes
                updateStudentTable(studentList);
            }
        });

        checkAllCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isChecked = checkAllCheckBox.isSelected();
                DefaultTableModel tableModel = (DefaultTableModel) studentTable.getModel();
                int rowCount = tableModel.getRowCount();
                for (int i = 0; i < rowCount; i++) {
                    tableModel.setValueAt(isChecked, i, 2);
                }
            }
        });

        addAttendanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedClass = (String) classComboBox.getSelectedItem();
                Date selectedDate = (Date) datePicker.getModel().getValue();
                String timeSlot = timeSlotField.getText();
                String remarks = remarksField.getText();
                System.out.println(selectedDate.toString());

                // Get attendance information from the table
                DefaultTableModel tableModel = (DefaultTableModel) studentTable.getModel();
                int rowCount = tableModel.getRowCount();

                for (int i = 0; i < rowCount; i++) {
                    String name = (String) tableModel.getValueAt(i, 0);
                    String regNo = (String) tableModel.getValueAt(i, 1);
                    boolean isPresent = (Boolean) tableModel.getValueAt(i, 2);

                    // Perform attendance saving logic here
                    System.out.println("Name: " + name + ", RegNo: " + regNo + ", Present: " + isPresent);
                }

                // Clear input fields
                timeSlotField.setText("");
                remarksField.setText("");

                // Clear table selection
                studentTable.clearSelection();
            }
        });
    }

}
